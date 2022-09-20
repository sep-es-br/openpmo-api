package br.gov.es.openpmo.service.ui;


import br.gov.es.openpmo.dto.menu.BreadcrumbDto;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.service.office.plan.PlanModelService;
import br.gov.es.openpmo.service.workpack.WorkpackModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class WorkpackModelBreadcrumbService {

  private final WorkpackModelService workpackModelService;
  private final PlanModelService planModelService;


  @Autowired
  public WorkpackModelBreadcrumbService(
    final WorkpackModelService workpackModelService,
    final PlanModelService planModelService
  ) {
    this.workpackModelService = workpackModelService;
    this.planModelService = planModelService;
  }

  private static BreadcrumbDto planModelAsBreadcrumbItem(final PlanModel planModel) {
    return new BreadcrumbDto(
      planModel.getId(),
      planModel.getName(),
      planModel.getFullName(),
      "strategy"
    );
  }

  private static BreadcrumbDto officeASBreadcrumbItem(final Office office) {
    return new BreadcrumbDto(
      office.getId(),
      office.getName(),
      office.getFullName(),
      "strategies"
    );
  }

  private static BreadcrumbDto workpackModelAsBreadcrumbItem(final WorkpackModel workpackModel) {
    return new BreadcrumbDto(
      workpackModel.getId(),
      workpackModel.getModelName(),
      workpackModel.getModelNameInPlural(),
      workpackModel.getClass().getSimpleName()
    );
  }

  private static List<BreadcrumbDto> reverse(final List<BreadcrumbDto> breadcrumbs) {
    Collections.reverse(breadcrumbs);
    return breadcrumbs;
  }

  private static void addPlanModel(
    final Collection<? super BreadcrumbDto> breadcrumbs,
    final PlanModel planModel
  ) {
    final BreadcrumbDto breadcrumbPlanModelItem = planModelAsBreadcrumbItem(planModel);
    breadcrumbs.add(breadcrumbPlanModelItem);
  }

  private static void addOfficeOfPlanModel(
    final Collection<? super BreadcrumbDto> breadcrumbs,
    final PlanModel planModel
  ) {
    final Office office = planModel.getOffice();
    final BreadcrumbDto breadcrumbOfficeItem = officeASBreadcrumbItem(office);
    breadcrumbs.add(breadcrumbOfficeItem);
  }

  public List<BreadcrumbDto> buildWorkpackModelHierarchyAsBreadcrumb(final Long idWorkpackModel) {
    final List<BreadcrumbDto> breadcrumbs = new ArrayList<>(0);

    final WorkpackModel workpackModel = this.findWorkpackModelById(idWorkpackModel);

    breadcrumbs.add(workpackModelAsBreadcrumbItem(workpackModel));

    this.ifHasParentAdd(breadcrumbs, workpackModel);

    this.addPlanModelBreadcrumb(breadcrumbs, workpackModel.getIdPlanModel());
    return reverse(breadcrumbs);
  }

  private void ifHasParentAdd(
    final List<BreadcrumbDto> breadcrumbs,
    final WorkpackModel workpackModel
  ) {
    if(workpackModel.hasParent()) {
      this.addWorkpackModelParentBreadcrumb(workpackModel.getParent(), breadcrumbs);
    }
  }

  private WorkpackModel findWorkpackModelById(final Long idWorkpackModel) {
    return this.workpackModelService.findByIdWithParents(idWorkpackModel);
  }

  private void addWorkpackModelParentBreadcrumb(
    final Iterable<? extends WorkpackModel> workpackModel,
    final List<BreadcrumbDto> breadcrumbs
  ) {
    workpackModel.forEach(model -> {
      breadcrumbs.add(workpackModelAsBreadcrumbItem(model));
      if(model.getParent() != null) {
        this.addWorkpackModelParentBreadcrumb(model.getParent(), breadcrumbs);
      }
    });
  }

  private void addPlanModelBreadcrumb(
    final Collection<? super BreadcrumbDto> breadcrumbs,
    final Long idPlanModel
  ) {
    final PlanModel planModel = this.findPlanModelById(idPlanModel);
    addPlanModel(breadcrumbs, planModel);
    addOfficeOfPlanModel(breadcrumbs, planModel);
  }

  private PlanModel findPlanModelById(final Long idPlanModel) {
    return this.planModelService.findById(idPlanModel);
  }

}
