package br.gov.es.openpmo.service.ui;

import br.gov.es.openpmo.dto.menu.BreadcrumbDto;
import br.gov.es.openpmo.dto.workpack.WorkpackDetailDto;
import br.gov.es.openpmo.dto.workpack.WorkpackName;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.relations.BelongsTo;
import br.gov.es.openpmo.model.relations.IsLinkedTo;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_LINKED_NOT_FOUND;

@Service
public class WorkpackBreadcrumbService {

  private final BreadcrumbWorkpackHelper breadcrumbWorkpackHelper;
  private final BreadcrumbPlanHelper breadcrumbPlanHelper;
  private final BreadcrumbWorkpackLinkedHelper breadcrumbWorkpackLinkedHelper;
  private final BreadcrumbWorkpackModelHelper breadcrumbWorkpackModelHelper;


  @Autowired
  public WorkpackBreadcrumbService(
    final BreadcrumbWorkpackHelper breadcrumbWorkpackHelper,
    final BreadcrumbPlanHelper breadcrumbPlanHelper,
    final BreadcrumbWorkpackLinkedHelper breadcrumbWorkpackLinkedHelper,
    final BreadcrumbWorkpackModelHelper breadcrumbWorkpackModelHelper
  ) {
    this.breadcrumbWorkpackHelper = breadcrumbWorkpackHelper;
    this.breadcrumbPlanHelper = breadcrumbPlanHelper;
    this.breadcrumbWorkpackLinkedHelper = breadcrumbWorkpackLinkedHelper;
    this.breadcrumbWorkpackModelHelper = breadcrumbWorkpackModelHelper;
  }

  private static List<BreadcrumbDto> reverseBreadcrumbs(final List<BreadcrumbDto> breadcrumbs) {
    Collections.reverse(breadcrumbs);
    return breadcrumbs;
  }

  private static BreadcrumbDto fromOffice(final Office office) {
    return new BreadcrumbDto(
      office.getId(),
      office.getName(),
      office.getFullName(),
      "office"
    );
  }

  private static BreadcrumbDto fromPlan(final Plan plan) {
    return new BreadcrumbDto(
      plan.getId(),
      plan.getName(),
      plan.getFullName(),
      "plan"
    );
  }

  private static boolean isInPlan(final Workpack workpack, final Long idPlan) {
    return workpack.getBelongsTo().stream().anyMatch(belongsTo -> belongsTo.getIdPlan().equals(idPlan));
  }

  private static BreadcrumbDto fromEmptyWokpack(final Workpack workpack) {
    return new BreadcrumbDto(
      workpack.getId(),
      "",
      "",
      workpack.getClass().getSimpleName()
    );
  }

  public Collection<BreadcrumbDto> buildWorkpackHierarchyAsBreadcrumb(final Long idWorkpack, final Long idPlan) {
    final Workpack workpack = this.findWorkpackAndParentsById(idWorkpack);

    final Optional<BelongsTo> maybeBelongsTo = this.maybeFindBelongsToBy(idPlan, workpack);

    if(maybeBelongsTo.isPresent()) {
      final BelongsTo belongsTo = maybeBelongsTo.get();
      return this.workpackBelongsToPlan(idWorkpack, idPlan, belongsTo.getLinked());
    }
    else {
      return this.maybeBelongsToInParent(idWorkpack, idPlan);
    }
  }

  private Optional<BelongsTo> maybeFindBelongsToBy(final Long idPlan, final Workpack workpack) {
    return workpack.getBelongsTo().stream()
      .filter(belongsTo -> idPlan.equals(belongsTo.getIdPlan()))
      .findFirst();
  }

  private Collection<BreadcrumbDto> maybeBelongsToInParent(final Long idWorkpack, final Long idPlan) {
    final List<BreadcrumbDto> breadcrumbs = new ArrayList<>(0);
    final IsLinkedTo linkedToRelation = this.findWorkpackParentLinked(idWorkpack, idPlan);
    final Workpack workpackParentLinked = linkedToRelation.getWorkpack();
    final WorkpackModel workpackModelLinked = this.breadcrumbWorkpackModelHelper.findById(linkedToRelation.getWorkpackModelId());
    this.addChildreOfWorkpackLinked(breadcrumbs, workpackModelLinked, workpackParentLinked.getChildren());
    reverseBreadcrumbs(breadcrumbs);
    breadcrumbs.add(this.fromLinkedToRelation(
      linkedToRelation,
      workpackParentLinked
    ));
    this.addWorkpackParentLinkedToBreadcrumb(workpackParentLinked.getParent(), breadcrumbs, idPlan);
    this.addOfficeAndPlanBreadcrumbDto(idPlan, breadcrumbs);
    reverseBreadcrumbs(breadcrumbs);
    return breadcrumbs;
  }

  private void addChildreOfWorkpackLinked(
    final List<? super BreadcrumbDto> breadcrumbs,
    final WorkpackModel workpackModelLinked,
    final Iterable<? extends Workpack> children
  ) {
    for(final Workpack workpack : children) {
      this.addWorkpackLinkedChildren(breadcrumbs, workpackModelLinked, workpack);
    }
  }

  private void addWorkpackLinkedChildren(
    final List<? super BreadcrumbDto> breadcrumbs,
    final WorkpackModel workpackModelLinked,
    final Workpack workpack
  ) {
    final WorkpackModel instance = workpack.getWorkpackModelInstance();

    final Optional<WorkpackModel> maybeModelLinkedEquivalent = this.maybeFindWorkpackModelLinkedWithSameType(
      workpackModelLinked,
      instance
    );

    if(!maybeModelLinkedEquivalent.isPresent()) {
      throw new NegocioException(ApplicationMessage.WORKPACK_MODEL_TYPE_MISMATCH);
    }

    final BreadcrumbDto workpackBreadcrumbItem = fromEmptyWokpack(workpack);
    final WorkpackName nameAndFullname = this.breadcrumbWorkpackHelper.findWorkpackNameAndFullname(
      workpack.getId()
    ).orElseGet(WorkpackName::empty);
    workpackBreadcrumbItem.setName(nameAndFullname.getName());
    workpackBreadcrumbItem.setFullName(nameAndFullname.getFullName());
    workpackBreadcrumbItem.setModelName(maybeModelLinkedEquivalent.get().getModelName());
    workpackBreadcrumbItem.setIdWorkpackModelLinked(maybeModelLinkedEquivalent.get().getId());

    breadcrumbs.add(workpackBreadcrumbItem);

    if(workpack.getChildren() != null) {
      this.addChildreOfWorkpackLinked(breadcrumbs, maybeModelLinkedEquivalent.get(), workpack.getChildren());
    }
  }

  private Optional<WorkpackModel> maybeFindWorkpackModelLinkedWithSameType(
    final WorkpackModel workpackModelLinked,
    final WorkpackModel instance
  ) {
    return workpackModelLinked.getChildren().stream()
      .filter(modelLinked -> modelLinked.hasSameType(instance) && modelLinked.getModelName().equals(instance.getModelName()))
      .findFirst();
  }

  private BreadcrumbDto fromLinkedToRelation(
    final IsLinkedTo linkedToRelation,
    final Workpack workpackParentLinked
  ) {
    final BreadcrumbDto workpackBreadcrumbItem = fromEmptyWokpack(workpackParentLinked);

    final WorkpackName nameAndFullname = this.breadcrumbWorkpackHelper.findWorkpackNameAndFullname(
      linkedToRelation.getWorkpack().getId()
    ).orElseGet(WorkpackName::empty);

    workpackBreadcrumbItem.setName(nameAndFullname.getName());
    workpackBreadcrumbItem.setFullName(nameAndFullname.getFullName());
    workpackBreadcrumbItem.setModelName(linkedToRelation.getWorkpackModel().getModelName());
    workpackBreadcrumbItem.setIdWorkpackModelLinked(linkedToRelation.getWorkpackModelId());

    return workpackBreadcrumbItem;
  }

  private IsLinkedTo findWorkpackParentLinked(final Long idWorkpack, final Long idPlan) {
    return this.breadcrumbWorkpackLinkedHelper.findWorkpackParentLinked(
      idWorkpack,
      idPlan
    ).orElseThrow(() -> new NegocioException(WORKPACK_LINKED_NOT_FOUND));
  }

  private Collection<BreadcrumbDto> workpackBelongsToPlan(final Long idWorkpack, final Long idPlan, final boolean linked) {
    final List<BreadcrumbDto> breadcrumbs = new ArrayList<>(0);
    final Workpack workpack = this.findWorkpackAndParentsById(idWorkpack);
    if(linked) {
      this.addLinkedWorkpackToBreadcrumb(workpack, breadcrumbs, idPlan);
    }
    else {
      this.addWorkpackToBreadcrumb(workpack, breadcrumbs, idPlan);
    }
    this.addOfficeAndPlanBreadcrumbDto(idPlan, breadcrumbs);
    return reverseBreadcrumbs(breadcrumbs);
  }

  private void addLinkedWorkpackToBreadcrumb(
    final Workpack workpack,
    final List<BreadcrumbDto> breadcrumbs,
    final Long idPlan
  ) {
    breadcrumbs.add(this.fromLinkedWorkpack(workpack, idPlan));
    if(workpack.hasParent()) {
      this.addWorkpackParentLinkedToBreadcrumb(workpack.getParent(), breadcrumbs, idPlan);
    }
  }

  private Workpack findWorkpackAndParentsById(final Long idWorkpack) {
    return this.breadcrumbWorkpackHelper.findByIdWithParent(idWorkpack);
  }

  private void addOfficeAndPlanBreadcrumbDto(final Long idPlan, final Collection<? super BreadcrumbDto> breadcrumbs) {
    final Plan plan = this.findPlanById(idPlan);
    final Office office = plan.getOffice();
    final BreadcrumbDto breadcrumbPlanDto = fromPlan(plan);
    final BreadcrumbDto breadcrumbOfficeDto = fromOffice(office);
    breadcrumbs.add(breadcrumbPlanDto);
    breadcrumbs.add(breadcrumbOfficeDto);
  }

  private Plan findPlanById(final Long idPlan) {
    return this.breadcrumbPlanHelper.findById(idPlan);
  }

  private BreadcrumbDto fromLinkedWorkpack(final Workpack workpack, final Long idPlan) {
    final BreadcrumbDto breadcrumbDto = fromEmptyWokpack(workpack);

    final WorkpackName nameAndFullname = this.breadcrumbWorkpackHelper.findWorkpackNameAndFullname(
      workpack.getId()
    ).orElseGet(WorkpackName::empty);

    breadcrumbDto.setName(nameAndFullname.getName());
    breadcrumbDto.setFullName(nameAndFullname.getFullName());

    final WorkpackModel workpackModelLinked = this.breadcrumbWorkpackLinkedHelper.findWorkpackModelLinkedByWorkpackAndPlan(
      workpack.getId(),
      idPlan
    );

    breadcrumbDto.setModelName(workpackModelLinked.getModelName());
    return breadcrumbDto;
  }

  private WorkpackDetailDto getWorkpackDetailDto(final Workpack workpack) {
    return this.breadcrumbWorkpackHelper.getWorkpackDetailDto(workpack);
  }

  private BreadcrumbDto fromWorkpack(final Workpack workpack) {
    final BreadcrumbDto breadcrumbDto = fromEmptyWokpack(workpack);
    breadcrumbDto.setType(workpack.getClass().getSimpleName());

    final WorkpackDetailDto detailDto = this.getWorkpackDetailDto(workpack);

    if(detailDto.getModel() != null && detailDto.getModel().getProperties() != null) {

      final WorkpackName nameAndFullname = this.breadcrumbWorkpackHelper.findWorkpackNameAndFullname(
        workpack.getId()
      ).orElseGet(WorkpackName::empty);

      breadcrumbDto.setName(nameAndFullname.getName());
      breadcrumbDto.setFullName(nameAndFullname.getFullName());
      breadcrumbDto.setModelName(detailDto.getModel().getModelName());
    }
    return breadcrumbDto;
  }


  private void addWorkpackToBreadcrumb(final Workpack workpack, final List<BreadcrumbDto> breadcrumbs, final Long idPlan) {
    final boolean isInSamePlan = isInPlan(workpack, idPlan);
    if(isInSamePlan) {
      breadcrumbs.add(this.fromWorkpack(workpack));
      if(workpack.getParent() != null) {
        this.addWorkpackParentLinkedToBreadcrumb(workpack.getParent(), breadcrumbs, idPlan);
      }
    }
  }

  private void addWorkpackParentLinkedToBreadcrumb(
    final Iterable<? extends Workpack> parent,
    final List<BreadcrumbDto> breadcrumbs,
    final Long idPlan
  ) {
    for(final Workpack workpack : parent) {
      this.addWorkpackToBreadcrumb(workpack, breadcrumbs, idPlan);
    }
  }
}
