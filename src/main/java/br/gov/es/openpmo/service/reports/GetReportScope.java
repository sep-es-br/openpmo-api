package br.gov.es.openpmo.service.reports;

import br.gov.es.openpmo.dto.reports.ReportScope;
import br.gov.es.openpmo.dto.reports.ReportScopeItem;
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

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
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

    final boolean canReadOrEdit = analyzePermission(fetchPermissionFunction.apply(idPlan));

    log.debug("Criando ReportScope a partir do idPlan={}", idPlan);
    final ReportScope scope = ReportScope.of(plan, canReadOrEdit);

    final Set<Workpack> workpacks = plan.getWorkpacks();

    log.debug("Navegando na estrutura dos {} workpacks filho(s) diretos de idPlan={}", workpacks.size(), plan.getId());
    final Collection<ReportScopeItem> children = this.addScope(workpacks, fetchPermissionFunction);

    scope.addChildren(children);

    return scope;
  }

  private Function<Long, CanAccessDataResponse> fetchPermission(final String authorization) {
    return idWorkpack -> this.canAccessData.execute(idWorkpack, authorization);
  }

  private Collection<ReportScopeItem> addScope(
    final Collection<? extends Workpack> workpacks,
    final Function<Long, CanAccessDataResponse> fetchPermissions
  ) {
    log.debug("Iniciando processamento de {} workpack(s)", workpacks.size());
    final Collection<ReportScopeItem> items = new LinkedList<>();

    final Map<Long, CanAccessDataResponse> permissionByWorkpackId = workpacks.parallelStream()
      .collect(Collectors.toMap(Workpack::getId, w -> fetchPermissions.apply(w.getId())));

    for (final Workpack workpack : workpacks) {

      log.debug("Criando ReportScopeItem a partir do workpackId={}", workpack.getId());
      final boolean canReadOrEdit = analyzePermission(permissionByWorkpackId.get(workpack.getId()));
      final ReportScopeItem item = ReportScopeItem.of(workpack, canReadOrEdit);
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

      item.addChildren(this.addScope(children, fetchPermissions));

    }

    return items.stream()
      .sorted(Comparator.comparing(ReportScopeItem::getName))
      .collect(Collectors.toList());
  }

  private Plan getPlanStructure(final Long idPlan) {
    return this.planRepository.findFirstLevelStructurePlanById(idPlan)
      .orElseThrow(() -> new RegistroNaoEncontradoException(ApplicationMessage.PLAN_NOT_FOUND));
  }

}
