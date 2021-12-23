package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.relations.BelongsTo;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.BelongsToRepository;
import br.gov.es.openpmo.repository.PlanRepository;
import br.gov.es.openpmo.repository.PropertyModelRepository;
import br.gov.es.openpmo.repository.PropertyRepository;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
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

  @Autowired
  public PasteToWorkpackService(
    final WorkpackRepository workpackRepository,
    final BelongsToRepository belongsToRepository,
    final PlanRepository planRepository,
    final WorkpackModelRepository workpackModelRepository,
    final PropertyRepository propertyRepository,
    final PropertyModelRepository propertyModelRepository
  ) {
    this.workpackRepository = workpackRepository;
    this.belongsToRepository = belongsToRepository;
    this.planRepository = planRepository;
    this.workpackModelRepository = workpackModelRepository;
    this.propertyRepository = propertyRepository;
    this.propertyModelRepository = propertyModelRepository;
  }

  private static boolean areWorkpackModelsCompatible(
    final WorkpackModel model,
    final WorkpackModel other
  ) {
    return model.hasSameType(other) && model.hasSameName(other);
  }

  private static <T> Collection<T> copySet(final Collection<? extends T> collection) {
    return new HashSet<>(collection);
  }

  private static <T> void validateIfIsEmpty(final Collection<T> workpacks) {
    if(!workpacks.isEmpty()) {
      throw new NegocioException(ApplicationMessage.WORKPACK_PASTE_ERROR);
    }
  }

  private static boolean arePropertyModelsCompatible(
    final PropertyModel model,
    final PropertyModel other
  ) {
    return model.hasSameType(other) && model.hasSameName(other);
  }

  private static <T> boolean allEmpty(final Collection<? extends T> objects) {
    return Objects.isNull(objects) || objects.isEmpty();
  }

  private void handleProperties(
    final Workpack workpack,
    final Collection<? extends Property> properties,
    final Collection<? extends PropertyModel> propertyModels
  ) {
    if(allEmpty(properties)) {
      return;
    }

    if(allEmpty(propertyModels)) {
      throw new NegocioException(ApplicationMessage.WORKPACK_PASTE_ERROR);
    }

    if(properties.size() > propertyModels.size()) {
      throw new NegocioException(ApplicationMessage.WORKPACK_PASTE_ERROR);
    }

    final Collection<Property> fromCopy = copySet(properties);
    final Collection<PropertyModel> toCopy = copySet(propertyModels);

    final Iterator<Property> fromIterator = fromCopy.iterator();
    final Iterator<PropertyModel> toIterator = toCopy.iterator();

    while(fromIterator.hasNext() && toIterator.hasNext()) {
      this.handlePasteAllProperties(fromIterator, toIterator);
    }

    this.deleteRemainingProperties(workpack, fromIterator);
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
    this.validateParent(idWorkpack, idParentFrom);

    final Workpack workpack = this.getWorkpack(idWorkpack);
    final Plan plan = this.getPlan(idPlanTo);
    final WorkpackModel workpackModel = this.getWorkpackModel(idWorkpackModelTo);

    if(idParentTo != null) {
      this.deleteIsInRelationship(idParentFrom, workpack);
      this.createIsInRelationship(idParentTo, workpack);
    }

    this.handlePasteWorkpack(workpack, workpackModel, plan);
  }

  private void createIsInRelationship(final Long idParentTo, final Workpack workpack) {
    this.workpackRepository.createIsInRelationshipByWorkpackIdAndParentId(workpack.getId(), idParentTo);
  }

  private void deleteIsInRelationship(final Long idParentFrom, final Workpack workpack) {
    this.workpackRepository.deleteIsInRelationshipByWorkpackIdAndParentId(workpack.getId(), idParentFrom);
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
    this.deletePropertyModelRelationshipByPropertyId(property);
    this.createRelationshipByPropertyIdAndModelId(property, propertyModel);
  }

  private void createRelationshipByPropertyIdAndModelId(final Property property, final PropertyModel propertyModel) {
    this.propertyModelRepository.createRelationshipByPropertyIdAndModelId(property.getId(), propertyModel.getId());
  }

  private void deletePropertyModelRelationshipByPropertyId(final Property property) {
    this.propertyModelRepository.deleteRelationshipByPropertyId(property.getId());
  }

  private void deleteWorkpackModelRelationshipByWorkpackId(final Workpack workpack) {
    this.workpackModelRepository.deleteRelationshipByWorkpackId(workpack.getId());
  }

  private void deleteRemainingProperties(final Workpack workpack, final Iterator<? extends Property> fromIterator) {
    fromIterator.forEachRemaining(property -> this.deleteFeaturesRelationship(workpack, property));
  }

  private void deleteFeaturesRelationship(final Workpack workpack, final Property property) {
    this.propertyRepository.deleteFeaturesRelationshipByPropertyIdAndWorkpackId(property.getId(), workpack.getId());
  }

  private void handlePasteAllProperties(
    final Iterator<? extends Property> fromIterator,
    final Iterator<? extends PropertyModel> toIterator
  ) {
    final Property property = fromIterator.next();

    final PropertyModel propertyModelFrom = this.getPropertyModel(property);
    final PropertyModel propertyModelTo = toIterator.next();

    if(arePropertyModelsCompatible(propertyModelFrom, propertyModelTo)) {
      this.handlePasteProperty(property, propertyModelFrom);
      fromIterator.remove();
      toIterator.remove();
    }
  }

  private void handleChildren(
    final Collection<? extends Workpack> workpacks,
    final Collection<? extends WorkpackModel> workpackModels,
    final Plan plan
  ) {
    if(allEmpty(workpacks)) {
      return;
    }

    if(allEmpty(workpackModels)) {
      throw new NegocioException(ApplicationMessage.WORKPACK_PASTE_ERROR);
    }

    if(workpacks.size() > workpackModels.size()) {
      throw new NegocioException(ApplicationMessage.WORKPACK_PASTE_ERROR);
    }

    final Collection<Workpack> fromCopy = copySet(workpacks);
    final Collection<WorkpackModel> toCopy = copySet(workpackModels);

    final Iterator<Workpack> fromIterator = fromCopy.iterator();
    final Iterator<WorkpackModel> toIterator = toCopy.iterator();

    while(fromIterator.hasNext() && toIterator.hasNext()) {
      this.handlePasteAllWorkpacks(fromIterator, toIterator, plan);
    }

    validateIfIsEmpty(fromCopy);
  }

  private void handlePasteAllWorkpacks(
    final Iterator<? extends Workpack> fromIterator,
    final Iterator<? extends WorkpackModel> toIterator,
    final Plan plan
  ) {
    final Workpack workpack = fromIterator.next();

    final WorkpackModel workpackModelFrom = this.getWorkpackModel(workpack);
    final WorkpackModel workpackModelTo = toIterator.next();

    if(areWorkpackModelsCompatible(workpackModelFrom, workpackModelTo)) {
      this.handlePasteWorkpack(workpack, workpackModelTo, plan);
      fromIterator.remove();
      toIterator.remove();
    }
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
    this.deleteWorkpackModelRelationshipByWorkpackId(workpack);
    this.createRelationshipByWorkpackIdAndModelId(workpack, workpackModel);
  }

  private void createRelationshipByWorkpackIdAndModelId(final Workpack workpack, final WorkpackModel workpackModel) {
    this.workpackModelRepository.createRelationshipByWorkpackIdAndModelId(workpack.getId(), workpackModel.getId());
  }

  private void handlePlan(final Workpack workpack, final Plan plan) {
    this.deleteBelongsToByWorkpackId(workpack);

    final BelongsTo belongsTo = new BelongsTo();

    belongsTo.setWorkpack(workpack);
    belongsTo.setPlan(plan);
    belongsTo.setLinked(false);

    this.saveBelongsTo(belongsTo);
  }

  private void saveBelongsTo(final BelongsTo belongsTo) {
    this.belongsToRepository.save(belongsTo);
  }

  private void deleteBelongsToByWorkpackId(final Workpack workpack) {
    this.belongsToRepository.deleteByWorkpackId(workpack.getId());
  }

  private Workpack getWorkpack(final Long idWorkpack) {
    return this.workpackRepository.findWithPropertiesAndModelAndChildrenById(idWorkpack)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
  }

  private void validateParent(final Long idWorkpack, final Long idParentFrom) {
    if(idParentFrom != null && !this.isWorkpackInParent(idWorkpack, idParentFrom)) {
      throw new NegocioException(ApplicationMessage.WORKPACK_PASTE_ERROR);
    }
  }

  private boolean isWorkpackInParent(final Long idWorkpack, final Long idParentFrom) {
    return this.workpackRepository.isWorkpackInParent(idWorkpack, idParentFrom);
  }

  private void validateModel(final Long idWorkpack, final Long idWorkpackModelFrom) {
    if(!this.isWorkpackInstanceByModel(idWorkpack, idWorkpackModelFrom)) {
      throw new NegocioException(ApplicationMessage.WORKPACK_PASTE_ERROR);
    }
  }

  private boolean isWorkpackInstanceByModel(final Long idWorkpack, final Long idWorkpackModelFrom) {
    return this.workpackModelRepository.isWorkpackInstanceByModel(idWorkpack, idWorkpackModelFrom);
  }

  private void validatePlan(final Long idWorkpack, final Long idPlanFrom) {
    if(!this.workpackBelongsToPlan(idWorkpack, idPlanFrom)) {
      throw new NegocioException(ApplicationMessage.WORKPACK_PASTE_ERROR);
    }
  }

  private boolean workpackBelongsToPlan(final Long idWorkpack, final Long idPlanFrom) {
    return this.belongsToRepository.workpackBelongsToPlan(idWorkpack, idPlanFrom);
  }

  private WorkpackModel getWorkpackModel(final Long idWorkpackModel) {
    return this.workpackModelRepository.findByIdWorkpackWithChildren(idWorkpackModel)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACKMODEL_NOT_FOUND));
  }

}
