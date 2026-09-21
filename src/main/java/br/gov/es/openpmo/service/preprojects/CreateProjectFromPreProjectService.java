package br.gov.es.openpmo.service.preprojects;

import static br.gov.es.openpmo.utils.ApplicationMessage.PRE_PROJECT_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_MODEL_INVALID_TYPE;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_PARENT_PLAN_MISMATCH;

import br.gov.es.openpmo.dto.preprojects.CreateProjectFromPreProjectRequest;
import br.gov.es.openpmo.dto.workpack.GroupDto;
import br.gov.es.openpmo.dto.workpack.OrganizationSelectionDto;
import br.gov.es.openpmo.dto.workpack.ProjectParamDto;
import br.gov.es.openpmo.dto.workpack.PropertyDto;
import br.gov.es.openpmo.dto.workpack.SelectionDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.preprojects.PreProject;
import br.gov.es.openpmo.model.properties.models.GroupModel;
import br.gov.es.openpmo.model.properties.models.OrganizationSelectionModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.properties.models.SelectionModel;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.ProjectModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.PreProjectRepository;
import br.gov.es.openpmo.service.office.plan.PlanService;
import br.gov.es.openpmo.service.workpack.WorkpackModelService;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import br.gov.es.openpmo.utils.ApplicationCacheUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProjectFromPreProjectService {

  private final PreProjectRepository preProjectRepository;

  private final PlanService planService;

  private final WorkpackModelService workpackModelService;

  private final WorkpackService workpackService;

  private final ApplicationCacheUtil applicationCacheUtil;

  public CreateProjectFromPreProjectService(
    final PreProjectRepository preProjectRepository,
    final PlanService planService,
    final WorkpackModelService workpackModelService,
    final WorkpackService workpackService,
    final ApplicationCacheUtil applicationCacheUtil
  ) {
    this.preProjectRepository = preProjectRepository;
    this.planService = planService;
    this.workpackModelService = workpackModelService;
    this.workpackService = workpackService;
    this.applicationCacheUtil = applicationCacheUtil;
  }

  /**
   * Uses the selected parent workpack to determine the ProjectModel to
   * instantiate. The model id therefore never needs to be sent by the client.
   */
  @Transactional
  public Workpack create(
    final Long idPreProject,
    final CreateProjectFromPreProjectRequest request
  ) {
    final PreProject preProject = this.preProjectRepository.findByIdThin(idPreProject)
      .orElseThrow(() -> new RegistroNaoEncontradoException(PRE_PROJECT_NOT_FOUND));
    final Plan plan = this.planService.findById(request.getIdPlan());
    final ProjectModel projectModel = this.findProjectModel(request.getIdParent(), plan);

    final ProjectParamDto project = new ProjectParamDto();
    project.setIdPlan(request.getIdPlan());
    project.setIdParent(request.getIdParent());
    project.setIdWorkpackModel(projectModel.getId());
    project.setName(preProject.getName());
    project.setFullName(preProject.getFullName());
    project.setProperties(this.getProjectProperties(projectModel, preProject));

    final Workpack createdProject = this.workpackService.criarWorkpackFromPreProject(project);
    this.preProjectRepository.createOriginatedRelationship(idPreProject, createdProject.getId());
    this.applicationCacheUtil.loadAllCacheSync();
    return createdProject;
  }

  private ProjectModel findProjectModel(final Long idParent, final Plan plan) {
    final Workpack parent = this.workpackService.findById(idParent);
    final WorkpackModel parentModel = parent.getWorkpackModelInstance();
    final Set<WorkpackModel> childModels = parentModel == null ? null : parentModel.getChildren();

    final List<Long> projectModelIds = childModels == null
      ? Collections.emptyList()
      : childModels.stream()
        .filter(ProjectModel.class::isInstance)
        .map(WorkpackModel::getId)
        .collect(Collectors.toList());

    if (projectModelIds.size() != 1) {
      throw new NegocioException(WORKPACK_MODEL_INVALID_TYPE);
    }

    final WorkpackModel model = this.workpackModelService.findById(projectModelIds.get(0));
    if (!(model instanceof ProjectModel)) {
      throw new NegocioException(WORKPACK_MODEL_INVALID_TYPE);
    }
    if (
      model.getPlanModel() == null ||
      plan.getPlanModel() == null ||
      !Objects.equals(model.getPlanModel().getId(), plan.getPlanModel().getId())
    ) {
      throw new NegocioException(WORKPACK_PARENT_PLAN_MISMATCH);
    }
    return (ProjectModel) model;
  }

  private List<PropertyDto> getProjectProperties(
    final ProjectModel projectModel,
    final PreProject preProject
  ) {
    final List<PropertyDto> properties = new ArrayList<>();
    final Long idOrganization = preProject.getOrganization() == null
      ? null
      : preProject.getOrganization().getId();
    for (final PropertyModel propertyModel : projectModel.getProperties()) {
      final PropertyDto property = this.getProjectProperty(propertyModel, idOrganization);
      if (property != null) {
        properties.add(property);
      }
    }
    return properties;
  }

  private PropertyDto getProjectProperty(
    final PropertyModel propertyModel,
    final Long idOrganization
  ) {
    if (propertyModel instanceof OrganizationSelectionModel && idOrganization != null) {
      final OrganizationSelectionDto property = new OrganizationSelectionDto();
      property.setIdPropertyModel(propertyModel.getId());
      property.setSelectedValues(Collections.singleton(idOrganization));
      return property;
    }
    if (propertyModel instanceof SelectionModel && "Status".equals(propertyModel.getName())) {
      final SelectionDto property = new SelectionDto();
      property.setIdPropertyModel(propertyModel.getId());
      property.setValue("Estruturação");
      return property;
    }
    if (!(propertyModel instanceof GroupModel)) {
      return null;
    }

    final List<PropertyDto> groupedProperties = new ArrayList<>();
    final GroupModel groupModel = (GroupModel) propertyModel;
    if (groupModel.getGroupedProperties() != null) {
      for (final PropertyModel groupedProperty : groupModel.getGroupedProperties()) {
        final PropertyDto property = this.getProjectProperty(groupedProperty, idOrganization);
        if (property != null) {
          groupedProperties.add(property);
        }
      }
    }
    if (groupedProperties.isEmpty()) {
      return null;
    }

    final GroupDto group = new GroupDto();
    group.setIdPropertyModel(groupModel.getId());
    group.setGroupedProperties(groupedProperties);
    return group;
  }

}
