package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.relations.BelongsTo;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.*;
import br.gov.es.openpmo.service.dashboards.v2.IAsyncDashboardService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

@Service
public class PasteToWorkpackService {

  private final WorkpackRepository workpackRepository;

  private final BelongsToRepository belongsToRepository;

  private final PlanRepository planRepository;

  private final WorkpackModelRepository workpackModelRepository;

  private final PropertyRepository propertyRepository;

  private final PropertyModelRepository propertyModelRepository;

  private final IAsyncDashboardService dashboardService;

  @Autowired
  public PasteToWorkpackService(
    final WorkpackRepository workpackRepository,
    final BelongsToRepository belongsToRepository,
    final PlanRepository planRepository,
    final WorkpackModelRepository workpackModelRepository,
    final PropertyRepository propertyRepository,
    final PropertyModelRepository propertyModelRepository,
    IAsyncDashboardService dashboardService
  ) {
    this.workpackRepository = workpackRepository;
    this.belongsToRepository = belongsToRepository;
    this.planRepository = planRepository;
    this.workpackModelRepository = workpackModelRepository;
    this.propertyRepository = propertyRepository;
    this.propertyModelRepository = propertyModelRepository;
    this.dashboardService = dashboardService;
  }

  private static boolean areWorkpackModelsCompatible(
    final WorkpackModel model,
    final WorkpackModel other
  ) {
    return model.hasSameType(other);
  }

  private static <T> void validateIfIsEmpty(final Collection<T> workpacks) {
    if (!workpacks.isEmpty()) {
      throw new NegocioException(ApplicationMessage.WORKPACK_PASTE_ERROR);
    }
  }

  private static <T> boolean allEmpty(final Collection<? extends T> objects) {
    return Objects.isNull(objects) || objects.isEmpty();
  }

  private void handleProperties(
    final Workpack workpack,
    final Collection<? extends Property> properties,
    final Collection<? extends PropertyModel> propertyModels
  ) {
    if (allEmpty(properties)) {
      return;
    }

    if (allEmpty(propertyModels)) {
      throw new NegocioException(ApplicationMessage.WORKPACK_PASTE_ERROR);
    }

    if (properties.size() > propertyModels.size()) {
      throw new NegocioException(ApplicationMessage.WORKPACK_PASTE_ERROR);
    }

    Iterable<Property> fromCopy = new HashSet<>(properties);
    Iterator<Property> fromIterator = fromCopy.iterator();

    while (fromIterator.hasNext()) {
      Property property = fromIterator.next();
      PropertyModel propertyModelFrom = this.getPropertyModel(property);

      Iterable<PropertyModel> toCopy = new HashSet<>(propertyModels);
      for (PropertyModel propertyModelTo : toCopy) {
        if (propertyModelFrom.isCompatibleWith(propertyModelTo)) {
          this.handlePasteProperty(property, propertyModelTo);
          fromIterator.remove();
          break;
        }
      }
    }

    for (Property property : fromCopy) {
      this.propertyRepository
        .deleteFeaturesRelationshipByPropertyIdAndWorkpackId(workpack.getId(), property.getId());
    }
  }

  public void pastesWorkpackTo(
    final Long idWorkpack,
    final Long idPlanFrom,
    final Long idParentFrom,
    final Long idWorkpackModelFrom,
    final Long idPlanTo,
    final Long idParentTo,
    final Long idWorkpackModelTo
  ) {
    this.validatePlan(idWorkpack, idPlanFrom);
    this.validateModel(idWorkpack, idWorkpackModelFrom);

    final Workpack workpack = this.getWorkpack(idWorkpack);
    final Plan plan = this.getPlan(idPlanTo);
    final WorkpackModel workpackModel = this.getWorkpackModel(idWorkpackModelTo);

    if (idParentFrom != null) {
      this.validateParent(idWorkpack, idParentFrom);
      this.workpackRepository.deleteIsInRelationshipByWorkpackIdAndParentId(idWorkpack, idParentFrom);
    }

    if (idParentTo != null) {
      this.workpackRepository.createIsInRelationship(idWorkpack, idParentTo);
    }

    this.handlePasteWorkpack(workpack, workpackModel, plan);

    if (idParentFrom != null) {
      this.calculateDashboard(idParentFrom);
    }

    this.calculateDashboard(idWorkpack);
  }

  private void calculateDashboard(Long workpackId) {
    this.workpackRepository.findAllInHierarchy(workpackId)
      .forEach(dashboardService::calculate);
  }

  private Plan getPlan(final Long idPlan) {
    return this.planRepository.findById(idPlan)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PLAN_NOT_FOUND));
  }

  private void handlePasteWorkpack(final Workpack workpack, final WorkpackModel workpackModel, final Plan plan) {
    this.handlePlan(workpack, plan);
    this.handleWorkpackModel(workpack, workpackModel);
    this.handleProperties(workpack, workpack.getProperties(), workpackModel.getProperties());
    this.handleChildren(workpack.getChildren(), workpackModel.getChildren(), plan);
  }

  private void handlePasteProperty(final Property property, final PropertyModel propertyModel) {
    this.propertyModelRepository.deleteRelationshipByPropertyId(property.getId());
    this.propertyModelRepository.createRelationshipByPropertyIdAndModelId(property.getId(), propertyModel.getId());
  }

  private void handleChildren(
    final Collection<? extends Workpack> workpacks,
    final Collection<? extends WorkpackModel> workpackModels,
    final Plan plan
  ) {
    if (allEmpty(workpacks)) {
      return;
    }

    if (allEmpty(workpackModels)) {
      throw new NegocioException(ApplicationMessage.WORKPACK_PASTE_ERROR);
    }

    if (workpacks.size() > workpackModels.size()) {
      throw new NegocioException(ApplicationMessage.WORKPACK_PASTE_ERROR);
    }

    final Collection<Workpack> fromCopy = new HashSet<>(workpacks);
    final Iterator<Workpack> fromIterator = fromCopy.iterator();

    while (fromIterator.hasNext()) {
      Workpack workpack = fromIterator.next();
      WorkpackModel workpackModelFrom = this.getWorkpackModel(workpack);

      Iterable<WorkpackModel> toCopy = new HashSet<>(workpackModels);
      for (WorkpackModel workpackModelTo : toCopy) {
        if (areWorkpackModelsCompatible(workpackModelFrom, workpackModelTo)) {
          this.handlePasteWorkpack(workpack, workpackModelTo, plan);
          fromIterator.remove();
          break;
        }
      }
    }

    validateIfIsEmpty(fromCopy);
  }

  private WorkpackModel getWorkpackModel(final Workpack workpack) {
    return this.workpackModelRepository.findByIdWorkpack(workpack.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACKMODEL_NOT_FOUND));
  }

  private PropertyModel getPropertyModel(final Property property) {
    return this.propertyModelRepository.findByIdProperty(property.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PROPERTY_MODEL_NOT_FOUND));
  }

  private void handleWorkpackModel(final Workpack workpack, final WorkpackModel workpackModel) {
    this.workpackModelRepository.deleteRelationshipByWorkpackId(workpack.getId());
    this.workpackModelRepository.createRelationshipByWorkpackIdAndModelId(workpack.getId(), workpackModel.getId());
  }

  private void handlePlan(final Workpack workpack, final Plan plan) {
    this.belongsToRepository.deleteByWorkpackId(workpack.getId());
    final BelongsTo belongsTo = new BelongsTo();
    belongsTo.setWorkpack(workpack);
    belongsTo.setPlan(plan);
    belongsTo.setLinked(false);
    this.belongsToRepository.save(belongsTo, 0);
  }

  private Workpack getWorkpack(final Long idWorkpack) {
    return this.workpackRepository.findWithPropertiesAndModelAndChildrenById(idWorkpack)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
  }

  private void validateParent(final Long idWorkpack, final Long idParentFrom) {
    if (idParentFrom != null && !this.workpackRepository.isWorkpackInParent(idWorkpack, idParentFrom)) {
      throw new NegocioException(ApplicationMessage.WORKPACK_PASTE_ERROR);
    }
  }

  private void validateModel(final Long idWorkpack, final Long idWorkpackModelFrom) {
    if (!this.workpackModelRepository.isWorkpackInstanceByModel(idWorkpack, idWorkpackModelFrom)) {
      throw new NegocioException(ApplicationMessage.WORKPACK_PASTE_ERROR);
    }
  }

  private void validatePlan(final Long idWorkpack, final Long idPlanFrom) {
    if (!this.belongsToRepository.workpackBelongsToPlan(idWorkpack, idPlanFrom)) {
      throw new NegocioException(ApplicationMessage.WORKPACK_PASTE_ERROR);
    }
  }

  private WorkpackModel getWorkpackModel(final Long idWorkpackModel) {
    return this.workpackModelRepository.findByIdWorkpackWithChildren(idWorkpackModel)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACKMODEL_NOT_FOUND));
  }

}
