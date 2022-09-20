package br.gov.es.openpmo.service.office.plan;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.model.properties.models.GroupModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.OfficeRepository;
import br.gov.es.openpmo.repository.PlanModelRepository;
import br.gov.es.openpmo.repository.PropertyModelRepository;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static br.gov.es.openpmo.utils.WorkpackModelInstanceType.createFrom;

@Service
public class SharedPlanModelService {

  private final PlanModelRepository planModelRepository;
  private final WorkpackModelRepository workpackModelRepository;
  private final OfficeRepository officeRepository;
  private final PropertyModelRepository propertyModelRepository;

  @Autowired
  public SharedPlanModelService(
    final PlanModelRepository planModelRepository,
    final WorkpackModelRepository workpackModelRepository,
    final OfficeRepository officeRepository,
    final PropertyModelRepository propertyModelRepository
  ) {
    this.planModelRepository = planModelRepository;
    this.workpackModelRepository = workpackModelRepository;
    this.officeRepository = officeRepository;
    this.propertyModelRepository = propertyModelRepository;
  }

  private static void copyPropertiesToPlanModel(
    final PlanModel source,
    final PlanModel target
  ) {
    BeanUtils.copyProperties(source, target);

    target.setId(null);
    target.setPublicShared(false);

    final Set<Office> sharedWith = target.getSharedWith();
    if(sharedWith != null) {
      sharedWith.clear();
    }
  }

  private static void cleanUpProperty(final PropertyModel property) {
    property.setId(null);

    if(property instanceof GroupModel) {
      cleanUpGroupedProperty(((GroupModel) property).getGroupedProperties());
    }
  }

  private static void cleanUpGroupedProperty(final Iterable<? extends PropertyModel> groupedProperties) {
    for(final PropertyModel property : groupedProperties) {
      property.setId(null);
    }
  }

  private static void copyWorkpackModelChild(
    final WorkpackModel newModel,
    final WorkpackModel model
  ) {
    model.getParent().add(newModel);
    newModel.getChildren().add(model);
  }

  private void copyBeanProperties(
    final WorkpackModel workpackModel,
    final WorkpackModel newModel
  ) {
    BeanUtils.copyProperties(workpackModel, newModel);
    this.cleanUp(newModel);
  }

  private void cleanUp(final WorkpackModel newModel) {
    newModel.setId(null);
    newModel.setIdPlanModel(null);
    newModel.setPlanModel(null);
    newModel.setChildren(new HashSet<>());
    newModel.setParent(new HashSet<>());
    this.cleanUpPropertiesModel(newModel);
  }

  private void cleanUpPropertiesModel(final WorkpackModel newModel) {
    final Set<PropertyModel> properties = newModel.getProperties();

    if(properties == null) {
      newModel.setProperties(new HashSet<>());
      return;
    }

    final Set<PropertyModel> newProperties = new HashSet<>();

    for(final PropertyModel property : properties) {
      cleanUpProperty(property);
      newProperties.add(this.propertyModelRepository.save(property));
    }

    newModel.setProperties(newProperties);
  }

  private void copyChildren(
    final WorkpackModel workpackModel,
    final WorkpackModel newModel,
    final Map<Long, WorkpackModel> cache
  ) {
    final Set<WorkpackModel> children = workpackModel.getChildren();

    if(children == null || children.isEmpty()) {
      return;
    }

    for(final WorkpackModel child : children) {
      final WorkpackModel newChild = this.copyPropertiesToWorkpackModel(child, cache);
      copyWorkpackModelChild(newModel, newChild);
    }
  }

  private WorkpackModel copyPropertiesToWorkpackModel(
    final WorkpackModel workpackModel,
    final Map<Long, WorkpackModel> cache
  ) {
    final Long workpackModelId = workpackModel.getId();

    if(cache.containsKey(workpackModelId)) {
      return cache.get(workpackModelId);
    }

    final WorkpackModel newModel = this.copyWorkpackModel(workpackModel);
    this.copyChildren(workpackModel, newModel, cache);

    cache.put(workpackModelId, newModel);
    return newModel;
  }

  private WorkpackModel copyWorkpackModel(final WorkpackModel workpackModel) {
    final WorkpackModel newModel = createFrom(workpackModel);
    this.copyBeanProperties(workpackModel, newModel);
    return this.workpackModelRepository.save(newModel);
  }

  public EntityDto createFromShared(
    final Long idOffice,
    final Long idPlanModelShared
  ) {
    final PlanModel planModel = new PlanModel();
    final PlanModel sharingPlanModel = this.findById(idPlanModelShared);

    copyPropertiesToPlanModel(sharingPlanModel, planModel);
    this.setOffice(idOffice, planModel);

    final PlanModel savedPlanModel = this.planModelRepository.save(planModel);

    final Map<Long, WorkpackModel> cache = new HashMap<>();
    final List<WorkpackModel> workpacks = this.copyWorkpackModels(sharingPlanModel, cache);

    this.setPlanModelToWorkpackModels(savedPlanModel, workpacks);
    return new EntityDto(savedPlanModel.getId());
  }

  private void setOffice(
    final Long idOffice,
    final PlanModel planModel
  ) {
    final Office office = this.findOffice(idOffice);
    planModel.setOffice(office);
  }

  public PlanModel findById(final Long id) {
    return this.planModelRepository.findById(id)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PLAN_MODEL_NOT_FOUND));
  }

  public Office findOffice(final Long idOffice) {
    return this.officeRepository.findById(idOffice)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.OFFICE_NOT_FOUND));
  }

  private List<WorkpackModel> copyWorkpackModels(
    final PlanModel other,
    final Map<Long, WorkpackModel> cache
  ) {
    final Set<WorkpackModel> models =
      this.workpackModelRepository.findAllByIdPlanModelWithChildren(other.getId());

    final List<WorkpackModel> list = new ArrayList<>();

    for(final WorkpackModel model : models) {
      final WorkpackModel workpackModel = this.copyPropertiesToWorkpackModel(model, cache);
      list.add(workpackModel);
    }

    return list;
  }

  private void setPlanModelToWorkpackModels(
    final PlanModel planModel,
    final Iterable<? extends WorkpackModel> workpackModels
  ) {
    for(final WorkpackModel workpackModel : workpackModels) {
      this.setPlanModelToWorkpackModel(planModel, workpackModel);
    }
  }

  private void setPlanModelToWorkpackModel(
    final PlanModel planModel,
    final WorkpackModel workpackModel
  ) {
    if(workpackModel == null) {
      return;
    }

    workpackModel.setIdPlanModel(planModel.getId());
    workpackModel.setPlanModel(planModel);
    this.workpackModelRepository.save(workpackModel);

    for(final WorkpackModel child : workpackModel.getChildren()) {
      this.setPlanModelToWorkpackModel(planModel, child);
    }
  }

}
