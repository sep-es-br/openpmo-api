package br.gov.es.openpmo.service.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.service.office.plan.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.dto.menu.BreadcrumbDto;
import br.gov.es.openpmo.dto.menu.WorkpackResultDto;
import br.gov.es.openpmo.utils.ApplicationCacheUtil;

@Service
public class WorkpackBreadcrumbService {

  private final ApplicationCacheUtil cacheUtil;
  private final PlanService planService;


  @Autowired
  public WorkpackBreadcrumbService(
    final ApplicationCacheUtil cacheUtil,
    final PlanService planService) {
    this.cacheUtil = cacheUtil;
    this.planService = planService;
  }

  private static List<BreadcrumbDto> reverseBreadcrumbs(final List<BreadcrumbDto> breadcrumbs) {
    Collections.reverse(breadcrumbs);
    return breadcrumbs;
  }


  private static BreadcrumbDto fromWorkpackMenuResult(WorkpackResultDto workpack) {
    BreadcrumbDto dto = new BreadcrumbDto(
        workpack.getId(),
        workpack.getName(),
        workpack.getFullName(),
        workpack.getType());
    dto.setModelName(workpack.getModelName());
    if (Boolean.TRUE.equals(workpack.getLinked())) {
      dto.setIdWorkpackModelLinked(workpack.getIdWorkpackModel());
    }
    return dto;
  }

  public Collection<BreadcrumbDto> buildWorkpackHierarchyAsBreadcrumb(
    final Long idWorkpack,
    final Long idPlan
  ) {
    List<BreadcrumbDto> result = new ArrayList<>(0);
    List<WorkpackResultDto> list = cacheUtil.getListWorkpackResultDtoByPlan(idPlan);

    WorkpackResultDto actual = list.stream().filter(w -> w.getId().equals(idWorkpack)).findFirst().orElse(null);
    if (actual != null) {
      addBreadcrumb(actual, list, result);
    }
    addPlanOffice(result, idPlan);
    return reverseBreadcrumbs(result);
  }

  private void addPlanOffice(List<BreadcrumbDto> result, final Long idPlan) {
    final Plan plan = this.planService.findById(idPlan);
    final BreadcrumbDto planBread = new BreadcrumbDto(
            plan.getId(),
            plan.getName(),
            plan.getFullName(),
            "plan"
    );
    result.add(planBread);
    final BreadcrumbDto officeBread = new BreadcrumbDto(
            plan.getOffice().getId(),
            plan.getOffice().getName(),
            plan.getOffice().getFullName(),
            "office"
    );
    result.add(officeBread);
  }

  private void addBreadcrumb(WorkpackResultDto actual, List<WorkpackResultDto> list, List<BreadcrumbDto> result) {
    result.add(fromWorkpackMenuResult(actual));
    if (actual.getIdParent() != null) {
      addParent(actual.getIdParent(), list, result);
    }
  }

  private void addParent(Long idParent, List<WorkpackResultDto> list, List<BreadcrumbDto> result) {
    WorkpackResultDto parent = list.stream().filter(w -> w.getId().equals(idParent)).findFirst().orElse(null);
    if (parent != null) {
      addBreadcrumb(parent, list, result);
    }
  }

}
