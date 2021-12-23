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
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import br.gov.es.openpmo.utils.WorkpackModelInstanceType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SharedPlanModelService {

  private final PlanModelRepository planModelRepository;
  private final WorkpackModelRepository workpackModelRepository;
  private final OfficeRepository officeRepository;

  @Autowired
  public SharedPlanModelService(
    final PlanModelRepository planModelRepository,
    final WorkpackModelRepository workpackModelRepository,
    final OfficeRepository officeRepository
  ) {
    this.planModelRepository = planModelRepository;
    this.workpackModelRepository = workpackModelRepository;
    this.officeRepository = officeRepository;
  }

  private static void copyPropertiesToPlanModel(final PlanModel source, final PlanModel target) {
    BeanUtils.copyProperties(source, target);
    target.setId(null);
    target.setPublicShared(false);
  }

  private static void clearShared(final PlanModel planModel) {
    Optional.ofNullable(planModel.getSharedWith()).ifPresent(Set::clear);
  }

  private static WorkpackModel copyPropertiesToWorkpackModel(final WorkpackModel workpackModel) {
    final WorkpackModel newModel = WorkpackModelInstanceType.createFrom(workpackModel);

    copyBeanProperties(workpackModel, newModel);
    copyChildren(workpackModel, newModel);

    return newModel;
  }

  private static void copyChildren(final WorkpackModel workpackModel, final WorkpackModel newModel) {
    final Set<WorkpackModel> child = workpackModel.getChildren();

    if(child != null) {
      child.stream()
        .map(SharedPlanModelService::copyPropertiesToWorkpackModel)
        .forEach(model -> copyWorkpackModelChild(newModel, model));
    }
  }

  private static void copyBeanProperties(final WorkpackModel workpackModel, final WorkpackModel newModel) {
    BeanUtils.copyProperties(workpackModel, newModel);
    newModel.setId(null);
    newModel.setIdPlanModel(null);
    newModel.setPlanModel(null);
    newModel.setChildren(new HashSet<>());
    cleanUpPropertiesModel(newModel);
  }

  private static void cleanUpPropertiesModel(final WorkpackModel newModel) {
    newModel.getProperties().forEach(property -> {
      property.setId(null);
      if(property instanceof GroupModel) {
        cleanUpGroupedProperty(((GroupModel) property).getGroupedProperties());
      }
    });
  }

  private static void cleanUpGroupedProperty(final Iterable<? extends PropertyModel> groupedProperties) {
    for(final PropertyModel property : groupedProperties) {
      property.setId(null);
    }
  }

  private static void copyWorkpackModelChild(final WorkpackModel newModel, final WorkpackModel model) {
    model.setParent(new HashSet<>(Collections.singletonList(newModel)));
    newModel.getChildren().add(model);
  }

  public EntityDto createFromShared(final Long idOffice, final Long idPlanModelShared) {
    final PlanModel planModel = new PlanModel();
    final PlanModel sharingPlanModel = this.findById(idPlanModelShared);

    copyPropertiesToPlanModel(sharingPlanModel, planModel);
    clearShared(planModel);

    final Office office = this.findOffice(idOffice);
    planModel.setOffice(office);

    final Long idPlanModel = this.savePlanModel(planModel);

    final List<WorkpackModel> workpacks = this.copyWorkpackModels(sharingPlanModel);
    this.setPlanModelToWorkpackModels(planModel, workpacks);

    return new EntityDto(idPlanModel);
  }

  public PlanModel findById(final Long id) {
    return this.maybeFind(id)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PLAN_MODEL_NOT_FOUND));
  }

  public Office findOffice(final Long idOffice) {
    return this.maybeFindOffice(idOffice)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.OFFICE_NOT_FOUND));
  }

  public Long savePlanModel(final PlanModel planModel) {
    final PlanModel save = this.save(planModel);
    return save.getId();
  }

  private List<WorkpackModel> copyWorkpackModels(final PlanModel other) {
    final Set<WorkpackModel> workpackModels = this.findWorkPackModelsByIdPlanModel(other.getId());

    return workpackModels.stream()
      .map(SharedPlanModelService::copyPropertiesToWorkpackModel)
      .collect(Collectors.toList());
  }

  private void setPlanModelToWorkpackModels(final PlanModel planModel, final Iterable<? extends WorkpackModel> workpackModels) {
    workpackModels.forEach(workpackModel -> this.setPlanModelToWorkpackModel(planModel, workpackModel));
  }

  private Optional<PlanModel> maybeFind(final Long idPlanModel) {
    return this.planModelRepository.findById(idPlanModel);
  }

  private Optional<Office> maybeFindOffice(final Long idOffice) {
    return this.officeRepository.findById(idOffice);
  }

  private PlanModel save(final PlanModel planModel) {
    return this.planModelRepository.save(planModel);
  }

  private Set<WorkpackModel> findWorkPackModelsByIdPlanModel(final Long idPlanModel) {
    return this.workpackModelRepository.findAllByIdPlanModelWithChildren(idPlanModel);
  }

  private void setPlanModelToWorkpackModel(final PlanModel planModel, final WorkpackModel workpackModel) {
    workpackModel.setId(null);
    workpackModel.setIdPlanModel(planModel.getId());
    workpackModel.setPlanModel(planModel);
    workpackModel.getChildren().forEach(child -> this.setPlanModelToWorkpackModel(planModel, child));

    this.saveWorkpackModel(workpackModel);
  }

  private void saveWorkpackModel(final WorkpackModel workpackModel) {
    this.workpackModelRepository.save(workpackModel);
  }
}
