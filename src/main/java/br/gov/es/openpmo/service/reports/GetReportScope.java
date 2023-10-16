package br.gov.es.openpmo.service.reports;

import br.gov.es.openpmo.dto.reports.ReportScope;
import br.gov.es.openpmo.dto.reports.ReportScopeItem;
import br.gov.es.openpmo.dto.reports.Scope;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.PlanRepository;
import br.gov.es.openpmo.service.permissions.canaccess.CanAccessData;
import br.gov.es.openpmo.service.permissions.canaccess.CanAccessDataResponse;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessDataResponse;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GetReportScope {

  private static final Logger log = LoggerFactory.getLogger(GetReportScope.class);

  private final PlanRepository planRepository;

  private final CanAccessData canAccessData;

  public GetReportScope(
    final PlanRepository planRepository,
    final CanAccessData canAccessData
  ) {
    this.planRepository = planRepository;
    this.canAccessData = canAccessData;
  }

  private static boolean analyzePermission(final ICanAccessDataResponse canAccess) {
    return canAccess.canEditResource() || canAccess.getRead();
  }

  public ReportScope execute(final Long idPlan, final String authorization) {
    log.info("Consultando escopo do relatório para o idPlan={}", idPlan);
    final Plan plan = this.getPlanStructure(idPlan);

    final Function<Long, CanAccessDataResponse> fetchPermissionFunction = this.fetchPermission(authorization);

    log.debug("Criando ReportScope a partir do idPlan={}", idPlan);
    final ReportScope scope = ReportScope.of(plan,  false);

    final Set<Workpack> workpacks = plan.getWorkpacks();

    log.debug("Navegando na estrutura dos {} workpacks filho(s) diretos de idPlan={}", workpacks.size(), plan.getId());
    final Instant instant0 = Instant.now();
    final Collection<ReportScopeItem> children = this.addScope(workpacks);
    final Instant instantFinal = Instant.now();

    log.info("Tempo: {}", ChronoUnit.MILLIS.between(instant0, instantFinal));
    scope.addChildren(children);

    applyPermissionsToScope(scope, fetchPermissionFunction.andThen(GetReportScope::analyzePermission));
    return scope;
  }

  private void applyPermissionsToScope(Scope scope, Function<Long, Boolean> fetchPermissionFunction) {
    if (fetchPermissionFunction.apply(scope.getId())) {
      scope.enablePermission();
      return;
    }
    scope.disablePermission();
    scope.getChildren().forEach(child -> applyPermissionsToScope(child, fetchPermissionFunction));
  }

  private Function<Long, CanAccessDataResponse> fetchPermission(final String authorization) {
    return id -> this.canAccessData.execute(id, authorization);
  }

  private Collection<ReportScopeItem> addScope(
    final Collection<? extends Workpack> workpacks
  ) {
    log.debug("Iniciando processamento de {} workpack(s)", workpacks.size());
    final Collection<ReportScopeItem> items = new LinkedList<>();
    for (final Workpack workpack : workpacks) {

      log.debug("Criando ReportScopeItem a partir do workpackId={}", workpack.getId());
      final ReportScopeItem item = ReportScopeItem.of(workpack, false);
      items.add(item);

      if (!workpack.hasChildren()) {
        log.debug(
          "Workpack workpackId={} não possui filhos, iniciando processamento do próximo Workpack",
          workpack.getId()
        );
        continue;
      }

      final Set<Workpack> children = workpack.getChildren();
      log.debug("Iniciando processamento de {} filho(s) do workpackId={}", children.size(), workpack.getId());

      item.addChildren(this.addScope(children));

    }

    return items.stream()
      .sorted(Comparator.comparing(ReportScopeItem::getName, Comparator.nullsLast(Comparator.naturalOrder())))
      .collect(Collectors.toList());
  }

  private Plan getPlanStructure(final Long idPlan) {
    return this.planRepository.findFirstLevelStructurePlanById(idPlan)
      .orElseThrow(() -> new RegistroNaoEncontradoException(ApplicationMessage.PLAN_NOT_FOUND));
  }

}
