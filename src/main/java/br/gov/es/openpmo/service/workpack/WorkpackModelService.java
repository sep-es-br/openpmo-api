package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpackmodel.DeliverableModelDto;
import br.gov.es.openpmo.dto.workpackmodel.MilestoneModelDto;
import br.gov.es.openpmo.dto.workpackmodel.OrganizerModelDto;
import br.gov.es.openpmo.dto.workpackmodel.PortfolioModelDto;
import br.gov.es.openpmo.dto.workpackmodel.ProgramModelDto;
import br.gov.es.openpmo.dto.workpackmodel.ProjectModelDto;
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelDto;
import br.gov.es.openpmo.dto.workpackmodel.details.DeliverableModelDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.details.MilestoneModelDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.details.OrganizerModelDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.details.PortfolioModelDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.details.ProgramModelDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.details.ProjectModelDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.details.WorkpackModelDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.params.WorkpackModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.properties.models.GroupModel;
import br.gov.es.openpmo.model.properties.models.SelectionModel;
import br.gov.es.openpmo.model.properties.models.TransversalViewSelectionModel;
import br.gov.es.openpmo.model.workpacks.models.DeliverableModel;
import br.gov.es.openpmo.model.workpacks.models.MilestoneModel;
import br.gov.es.openpmo.model.workpacks.models.OrganizerModel;
import br.gov.es.openpmo.model.workpacks.models.PortfolioModel;
import br.gov.es.openpmo.model.workpacks.models.ProgramModel;
import br.gov.es.openpmo.model.workpacks.models.ProjectModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModelClassification;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import br.gov.es.openpmo.service.office.plan.PlanModelService;
import br.gov.es.openpmo.service.properties.PropertyModelService;
import br.gov.es.openpmo.service.reports.models.GetPropertyModelDtoFromEntity;
import br.gov.es.openpmo.service.reports.models.GetPropertyModelDtosFromEntities;
import br.gov.es.openpmo.service.reports.models.GetPropertyModelFromDto;
import br.gov.es.openpmo.utils.ApplicationCacheUtil;
import br.gov.es.openpmo.utils.ApplicationMessage;
import br.gov.es.openpmo.utils.WorkpackModelInstanceType;

import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACKMODEL_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_MODEL_MILESTONE_DELIVERABLE_PROGRAM_ERROR;

@Service
public class WorkpackModelService {

  private static final String TYPE_NAME_MODEL_PORTFOLIO = "br.gov.es.openpmo.model.workpacks.models.PortfolioModel";

  private static final String TYPE_NAME_MODEL_PROGRAM = "br.gov.es.openpmo.model.workpacks.models.ProgramModel";

  private static final String TYPE_NAME_MODEL_ORGANIZER = "br.gov.es.openpmo.model.workpacks.models.OrganizerModel";

  private static final String TYPE_NAME_MODEL_DELIVERABLE = "br.gov.es.openpmo.model.workpacks.models.DeliverableModel";

  private static final String TYPE_NAME_MODEL_PROJECT = "br.gov.es.openpmo.model.workpacks.models.ProjectModel";

  private static final String TYPE_NAME_MODEL_MILESTONE = "br.gov.es.openpmo.model.workpacks.models.MilestoneModel";

  private static final String PACKAGE_DTO = "br.gov.es.openpmo.dto.workpackmodel.params";

  private final WorkpackModelRepository workpackModelRepository;

  private final PlanModelService planModelService;

  private final ModelMapper modelMapper;

  private final PropertyModelService propertyModelService;

  private final ParentWorkpackTypeVerifier verifier;

  private final GetPropertyModelFromDto getPropertyModelFromDto;

  private final GetPropertyModelDtoFromEntity getPropertyModelDtoFromEntity;

  private final GetPropertyModelDtosFromEntities getPropertyModelDtosFromEntities;

  private final UpdatePropertyModels updatePropertyModels;

  private final DeletePropertyModel deletePropertyModel;

  private final ApplicationCacheUtil cacheUtil;

  @Autowired
  public WorkpackModelService(
    final WorkpackModelRepository workpackModelRepository,
    final PlanModelService planModelService,
    final ModelMapper modelMapper,
    final PropertyModelService propertyModelService,
    final ParentWorkpackTypeVerifier verifier,
    final GetPropertyModelFromDto getPropertyModelFromDto,
    final GetPropertyModelDtoFromEntity getPropertyModelDtoFromEntity,
    final GetPropertyModelDtosFromEntities getPropertyModelDtosFromEntities,
    final UpdatePropertyModels updatePropertyModels,
    final ApplicationCacheUtil cacheUtil,
    final DeletePropertyModel deletePropertyModel
  ) {
    this.workpackModelRepository = workpackModelRepository;
    this.planModelService = planModelService;
    this.modelMapper = modelMapper;
    this.propertyModelService = propertyModelService;
    this.verifier = verifier;
    this.getPropertyModelFromDto = getPropertyModelFromDto;
    this.getPropertyModelDtoFromEntity = getPropertyModelDtoFromEntity;
    this.getPropertyModelDtosFromEntities = getPropertyModelDtosFromEntities;
    this.updatePropertyModels = updatePropertyModels;
    this.deletePropertyModel = deletePropertyModel;
    this.cacheUtil = cacheUtil;
  }

  private static void ifNotMilestoneDeliverableProgramWorkpackThrowException(
    final WorkpackModelParamDto workpackModelParamDto,
    final boolean isChildFromProgramModel
  ) {
    if (workpackModelParamDto.hasNoParent() || !isChildFromProgramModel) {
      throw new NegocioException(WORKPACK_MODEL_MILESTONE_DELIVERABLE_PROGRAM_ERROR);
    }
  }

  public WorkpackModel save(
    final WorkpackModel workpackModel,
    final Long idParent
  ) {
    this.ensureRequiredProjectStatuses(workpackModel);
    if (workpackModel.getId() == null) {
      workpackModel.setPlanModel(this.planModelService.findById(workpackModel.getIdPlanModel()));
      this.ifHasParentIdCreateAsChild(workpackModel, idParent);
    }
    WorkpackModel model = this.workpackModelRepository.save(workpackModel);
    this.cacheUtil.loadAllCache();
    return model;
  }

  @Transactional
  public WorkpackModel saveWithUses(
    final WorkpackModel workpackModel,
    final Long idParent,
    final Set<Long> idsUses
  ) {
    final WorkpackModel saved = this.save(workpackModel, idParent);
    this.replaceUsesIfProvided(saved, idsUses);
    return saved;
  }

  private void ifHasParentIdCreateAsChild(
    final WorkpackModel workpackModel,
    final Long idParent
  ) {
    if (idParent != null) {
      final WorkpackModel parent = this.findById(idParent);
      workpackModel.setClassification(parent.getClassification());
      workpackModel.addParent(parent);
    }
  }

  public WorkpackModel findById(final Long id) {
    return this.workpackModelRepository.findAllByIdWorkpackModel(id)
            .map(wpModel -> {
                wpModel.setDashboardShowDeliveryStatus(!(wpModel instanceof DeliverableModel));
                return wpModel;
            })
      .orElseThrow(() -> new NegocioException(WORKPACKMODEL_NOT_FOUND));
  }

  public WorkpackModel findByIdWithAllChildrens(final Long id) {
    return this.workpackModelRepository.findAllByIdWorkpackModelWithAllChildren(id)
            .map(wpModel -> {
                wpModel.setDashboardShowDeliveryStatus(!(wpModel instanceof DeliverableModel));
                return wpModel;
            })
            .orElseThrow(() -> new NegocioException(WORKPACKMODEL_NOT_FOUND));
  }

  public List<PropertyModel> getPropertyModels(final Long idWorkpackModel) {
    return this.workpackModelRepository.findAllPropertyModels(idWorkpackModel).stream().collect(Collectors.toList());
  }

  public List<WorkpackModel> findAll(final Long idPlanModel) {
    return this.workpackModelRepository.findAllByIdPlanModel(idPlanModel)
      .stream().sorted(Comparator.comparing(WorkpackModel::getPositionOrElseZero))
      .collect(Collectors.toList());
  }

  @Transactional
  public List<WorkpackModel> replaceUses(final Long sourceId, final Set<Long> targetIds) {
    final WorkpackModel source = this.findById(sourceId);
    if (source.getClassification() != WorkpackModelClassification.TRANSVERSAL) {
      throw new NegocioException(ApplicationMessage.WORKPACK_MODEL_USES_SOURCE_NOT_TRANSVERSAL);
    }

    final Set<Long> normalizedTargetIds = Optional.ofNullable(targetIds)
      .orElseGet(Collections::emptySet)
      .stream()
      .filter(java.util.Objects::nonNull)
      .collect(Collectors.toCollection(LinkedHashSet::new));

    if (normalizedTargetIds.contains(sourceId)) {
      throw new NegocioException(ApplicationMessage.WORKPACK_MODEL_USES_SELF);
    }

    final List<WorkpackModel> targets = normalizedTargetIds.stream()
      .map(this::findById)
      .collect(Collectors.toList());

    targets.forEach(target -> {
      if (target.getClassification() != WorkpackModelClassification.STRUCTURAL) {
        throw new NegocioException(ApplicationMessage.WORKPACK_MODEL_USES_TARGET_NOT_STRUCTURAL);
      }
      if (!this.workpackModelRepository.areInSamePlan(sourceId, target.getId())) {
        throw new NegocioException(ApplicationMessage.WORKPACK_MODEL_USES_DIFFERENT_PLAN);
      }
    });

    this.workpackModelRepository.deleteUses(sourceId);
    normalizedTargetIds.forEach(targetId -> this.workpackModelRepository.createUses(sourceId, targetId));
    return this.findUses(sourceId);
  }

  public List<WorkpackModel> findUses(final Long sourceId) {
    return this.workpackModelRepository.findUses(sourceId).stream()
      .sorted(Comparator.comparing(
        WorkpackModel::getModelName,
        Comparator.nullsLast(String::compareToIgnoreCase)
      ))
      .collect(Collectors.toList());
  }

  public void validateTransversalViewSelection(
    final Long projectModelId,
    final String selectedValues,
    final boolean multipleSelection,
    final Long idRootTransversalViewModel,
    final String previouslySelectedValues
  ) {
    if (selectedValues == null || selectedValues.trim().isEmpty()) {
      return;
    }

    final Set<String> selectedTokens = new LinkedHashSet<>();
    for (final String value : selectedValues.split(",", -1)) {
      if (value.trim().isEmpty()) {
        throw new NegocioException(ApplicationMessage.TRANSVERSAL_VIEW_SELECTION_INVALID);
      }
      selectedTokens.add(value);
    }

    final Set<Long> allowedIds = idRootTransversalViewModel == null
      ? new LinkedHashSet<>()
      : new LinkedHashSet<>(this.workpackModelRepository.findTransversalSelectionIdsForProjectModel(
        projectModelId,
        idRootTransversalViewModel
      ));
    final Set<String> previouslySelectedLegacyTokens = new LinkedHashSet<>();
    if (previouslySelectedValues != null) {
      for (final String value : previouslySelectedValues.split(",", -1)) {
        try {
          allowedIds.add(Long.valueOf(value.trim()));
        } catch (final NumberFormatException ignored) {
          previouslySelectedLegacyTokens.add(value);
        }
      }
    }

    final boolean hasInvalidSelection = selectedTokens.stream().anyMatch(value -> {
      try {
        return !allowedIds.contains(Long.valueOf(value.trim()));
      } catch (final NumberFormatException ignored) {
        return !previouslySelectedLegacyTokens.contains(value);
      }
    });
    if ((!multipleSelection && selectedTokens.size() > 1) || hasInvalidSelection) {
      throw new NegocioException(ApplicationMessage.TRANSVERSAL_VIEW_SELECTION_INVALID);
    }
  }

  @Transactional
  public WorkpackModel update(final WorkpackModel workpackModel) {
    this.ensureRequiredProjectStatuses(workpackModel);
    final WorkpackModel workpackModelUpdate = this.findById(workpackModel.getId());

    final Set<PropertyModel> properties = workpackModel.getProperties();
    final Set<PropertyModel> propertiesToUpdate = workpackModelUpdate.getProperties();

    if (workpackModel.getSortBy() != null && workpackModelUpdate.sortByWasChanged(workpackModel.getSortBy())) {
      propertiesToUpdate.stream()
        .filter(PropertyModel::isSortProperty)
        .findFirst()
        .ifPresent(oldSortProperty -> oldSortProperty.setSorts(null));

      final PropertyModel newSortProperty = propertiesToUpdate.stream()
        .filter(p -> p.getId().equals(workpackModel.getSortBy().getId()))
        .findFirst()
        .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_SORTER_PROPERTY_NOT_FOUND));

      newSortProperty.setSorts(workpackModelUpdate);
    }

    if (workpackModelUpdate.getSortBy() != null && workpackModel.getSortBy() == null) {
      workpackModelUpdate.getSortBy().setSorts(null);
    }

    if (workpackModel.getSortBy() != null) {
      workpackModelUpdate.setSortByField(null);
    }

    workpackModelUpdate.updateFields(workpackModel);


    this.updatePropertyModels.execute(properties, propertiesToUpdate);
    WorkpackModel model = this.workpackModelRepository.save(workpackModelUpdate);
    this.cacheUtil.loadAllCache();
    return model;
  }

  @Transactional
  public WorkpackModel updateWithUses(final WorkpackModel workpackModel, final Set<Long> idsUses) {
    final WorkpackModel updated = this.update(workpackModel);
    this.replaceUsesIfProvided(updated, idsUses);
    return updated;
  }

  private void replaceUsesIfProvided(final WorkpackModel workpackModel, final Set<Long> idsUses) {
    if (idsUses == null) {
      return;
    }
    if (workpackModel.getClassification() == WorkpackModelClassification.TRANSVERSAL) {
      this.replaceUses(workpackModel.getId(), idsUses);
    } else if (!idsUses.isEmpty()) {
      throw new NegocioException(ApplicationMessage.WORKPACK_MODEL_USES_SOURCE_NOT_TRANSVERSAL);
    }
  }

  private void ensureRequiredProjectStatuses(final WorkpackModel workpackModel) {
    if (!(workpackModel instanceof ProjectModel) || workpackModel.getProperties() == null) {
      return;
    }

    workpackModel.getProperties().stream()
      .filter(SelectionModel.class::isInstance)
      .map(SelectionModel.class::cast)
      .filter(property -> "Status".equals(property.getName()))
      .forEach(property -> {
        final List<String> possibleValues = Optional.ofNullable(property.getPossibleValues())
          .map(values -> java.util.Arrays.stream(values.split(","))
            .map(String::trim)
            .filter(value -> !value.isEmpty())
            .collect(Collectors.toList()))
          .orElseGet(java.util.ArrayList::new);

        if (!possibleValues.contains("Estruturação")) {
          possibleValues.add("Estruturação");
        }
        if (!possibleValues.contains("Repactuação")) {
          possibleValues.add("Repactuação");
        }

        property.setPossibleValues(String.join(",", possibleValues));
      });
  }

  public WorkpackModel getWorkpackModel(final WorkpackModelParamDto workpackModelParamDto) {
    this.validType(workpackModelParamDto);
    Set<PropertyModel> propertyModels = null;
    if (workpackModelParamDto.getProperties() != null && !workpackModelParamDto.getProperties().isEmpty()) {
      propertyModels = this.getProperties(workpackModelParamDto);
    }
    WorkpackModel workpackModel = null;
    switch (workpackModelParamDto.getClass().getTypeName()) {
      case PACKAGE_DTO + ".PortfolioModelParamDto":
        workpackModel = this.modelMapper.map(workpackModelParamDto, PortfolioModel.class);
        break;
      case PACKAGE_DTO + ".ProgramModelParamDto":
        workpackModel = this.modelMapper.map(workpackModelParamDto, ProgramModel.class);
        break;
      case PACKAGE_DTO + ".OrganizerModelParamDto":
        workpackModel = this.modelMapper.map(workpackModelParamDto, OrganizerModel.class);
        break;
      case PACKAGE_DTO + ".DeliverableModelParamDto":
        workpackModel = this.modelMapper.map(workpackModelParamDto, DeliverableModel.class);
        break;
      case PACKAGE_DTO + ".ProjectModelParamDto":
        workpackModel = this.modelMapper.map(workpackModelParamDto, ProjectModel.class);
        break;
      case PACKAGE_DTO + ".MilestoneModelParamDto":
        workpackModel = this.modelMapper.map(workpackModelParamDto, MilestoneModel.class);
        break;
    }
    if (workpackModel != null) {
      workpackModel.dashboardConfiguration(workpackModelParamDto.getDashboardConfiguration());
      workpackModel.setProperties(propertyModels);
      this.validateTransversalViewProperty(workpackModel, workpackModelParamDto.getIdPlanModel());
      if (workpackModelParamDto.getSortBy() != null) {
        if (CollectionUtils.isNotEmpty(workpackModel.getProperties())) {
          workpackModel.setSortBy(
              workpackModel.getProperties().stream()
                           .filter(p -> p.getName() != null && p.getName().equals(workpackModelParamDto.getSortBy()))
                           .findFirst().orElse(null)
          );
        }
        if (workpackModel.getSortBy() == null) {
          workpackModel.setSortByField(workpackModelParamDto.getSortBy());
        }
      }
    }
    return workpackModel;
  }

  private void validateTransversalViewProperty(final WorkpackModel workpackModel, final Long idPlanModel) {
    if (workpackModel.getProperties() == null) {
      return;
    }

    final boolean isProjectModel = workpackModel instanceof ProjectModel;
    final long rootPropertyCount = workpackModel.getProperties().stream()
      .filter(TransversalViewSelectionModel.class::isInstance)
      .count();
    final boolean nestedProperty = workpackModel.getProperties().stream()
      .filter(GroupModel.class::isInstance)
      .map(GroupModel.class::cast)
      .anyMatch(group -> group.getGroupedProperties() != null
        && group.getGroupedProperties().stream().anyMatch(this::containsTransversalViewProperty));

    if ((!isProjectModel && rootPropertyCount > 0) || rootPropertyCount > 1 || nestedProperty) {
      throw new NegocioException(ApplicationMessage.PROPERTY_MODEL_INVALID_TYPE);
    }
    workpackModel.getProperties().stream()
      .filter(TransversalViewSelectionModel.class::isInstance)
      .map(TransversalViewSelectionModel.class::cast)
      .forEach(property -> {
        final Long idViewModel = property.getIdRootTransversalViewModel();
        if (idViewModel == null && idPlanModel != null
          && this.workpackModelRepository.hasTransversalRootViewModelInPlanModel(idPlanModel)) {
          throw new NegocioException(ApplicationMessage.TRANSVERSAL_VIEW_SELECTION_INVALID);
        }
        if (idViewModel != null && (idPlanModel == null
          || !this.workpackModelRepository.isTransversalViewModelInPlanModel(idViewModel, idPlanModel))) {
          throw new NegocioException(ApplicationMessage.TRANSVERSAL_VIEW_SELECTION_INVALID);
        }
        if (idViewModel != null && property.getDefaultValue() != null && !property.getDefaultValue().trim().isEmpty()) {
          final Set<Long> defaultIds = new LinkedHashSet<>();
          try {
            for (final String value : property.getDefaultValue().split(",", -1)) {
              if (value.trim().isEmpty()) throw new NumberFormatException();
              defaultIds.add(Long.valueOf(value.trim()));
            }
          } catch (final NumberFormatException exception) {
            throw new NegocioException(ApplicationMessage.TRANSVERSAL_VIEW_SELECTION_INVALID);
          }
          if ((!property.isMultipleSelection() && defaultIds.size() > 1)
            || !new HashSet<>(this.workpackModelRepository.findTransversalViewInstanceIds(idViewModel, idPlanModel))
              .containsAll(defaultIds)) {
            throw new NegocioException(ApplicationMessage.TRANSVERSAL_VIEW_SELECTION_INVALID);
          }
        }
      });
  }

  private boolean containsTransversalViewProperty(final PropertyModel propertyModel) {
    if (propertyModel instanceof TransversalViewSelectionModel) {
      return true;
    }
    if (propertyModel instanceof GroupModel) {
      final GroupModel group = (GroupModel) propertyModel;
      return group.getGroupedProperties() != null
        && group.getGroupedProperties().stream().anyMatch(this::containsTransversalViewProperty);
    }
    return false;
  }


  public WorkpackModelDetailDto getWorkpackModelDetailWithoutChildren(final WorkpackModel workpackModel) {
    this.includeTransversalSelectionPropertyModels(workpackModel);
    List<WorkpackModelDto> parent = null;
    PropertyModelDto sortBy = null;
    if (workpackModel.getParent() != null) {
      parent = workpackModel.getParent().stream()
        .map(this::getWorkpackModelDto)
        .collect(Collectors.toList());
      workpackModel.setParent(null);
    }
    List<? extends PropertyModelDto> properties = null;
    if (workpackModel.getProperties() != null && !workpackModel.getProperties().isEmpty()) {
      properties = this.getPropertyModelDtosFromEntities.execute(workpackModel.getProperties());
    }
    if (workpackModel.getSortBy() != null) {
      sortBy = this.getPropertyModelDtoFromEntity.execute(workpackModel.getSortBy());
      workpackModel.setSortBy(null);
    }
    final WorkpackModelDetailDto detailDto = this.convertWorkpackModelDetailDto(workpackModel);
    if (detailDto != null) {
      detailDto.dashboardConfiguration(workpackModel);
      detailDto.setParent(parent);
      detailDto.setProperties(properties);
      detailDto.setSortBy(sortBy);
    }
    if (detailDto != null && detailDto.getChildren() != null) {
      final Set<WorkpackModelDetailDto> sortedChildren = detailDto.getChildren().stream()
        .sorted(Comparator.comparing(WorkpackModelDetailDto::getPositionOrElseZero)
                  .thenComparing(WorkpackModelDetailDto::getModelName))
        .collect(Collectors.toCollection(LinkedHashSet::new));
      detailDto.setChildren(sortedChildren);
    }
    return detailDto;
  }

  public WorkpackModelDetailDto getWorkpackModelDetailDto(final WorkpackModel workpackModel) {
    this.includeTransversalSelectionPropertyModels(workpackModel);
    Set<WorkpackModelDetailDto> children = null;
    List<WorkpackModelDto> parent = null;
    PropertyModelDto sortBy = null;
    if (workpackModel.getChildren() != null) {
      children = this.getChildren(workpackModel.getChildren());
      workpackModel.setChildren(null);
    }
    if (workpackModel.getParent() != null) {
      parent = workpackModel.getParent().stream()
        .map(this::getWorkpackModelDto)
        .collect(Collectors.toList());
      workpackModel.setParent(null);
    }
    List<? extends PropertyModelDto> properties = null;
    if (workpackModel.getProperties() != null && !workpackModel.getProperties().isEmpty()) {
      properties = this.getPropertyModelDtosFromEntities.execute(workpackModel.getProperties());
    }
    if (workpackModel.getSortBy() != null) {
      sortBy = this.getPropertyModelDtoFromEntity.execute(workpackModel.getSortBy());
      workpackModel.setSortBy(null);
    }
    final WorkpackModelDetailDto detailDto = this.convertWorkpackModelDetailDto(workpackModel);
    if (detailDto != null) {
      detailDto.dashboardConfiguration(workpackModel);
      detailDto.notificationsConfiguration(workpackModel); 
      detailDto.setChildren(children);
      detailDto.setParent(parent);
      detailDto.setProperties(properties);
      detailDto.setSortBy(sortBy);
      detailDto.setSortByField(workpackModel.getSortByField());
    }
    if (detailDto != null && detailDto.getChildren() != null) {
      final Set<WorkpackModelDetailDto> sortedChildren = detailDto.getChildren().stream()
        .sorted(Comparator.comparing(WorkpackModelDetailDto::getPositionOrElseZero)
                  .thenComparing(WorkpackModelDetailDto::getModelName))
        .collect(Collectors.toCollection(LinkedHashSet::new));
      detailDto.setChildren(sortedChildren);
    }
    return detailDto;
  }

  private void includeTransversalSelectionPropertyModels(final WorkpackModel workpackModel) {
    if (!(workpackModel instanceof ProjectModel) || workpackModel.getId() == null) {
      return;
    }
    final Set<PropertyModel> properties = workpackModel.getProperties() == null
      ? new LinkedHashSet<>()
      : new LinkedHashSet<>(workpackModel.getProperties());
    final Set<TransversalViewSelectionModel> transversalProperties = this.workpackModelRepository
      .findTransversalViewSelectionPropertyModels(workpackModel.getId());
    properties.addAll(transversalProperties);
    workpackModel.setProperties(properties);
  }

  public WorkpackModelDto getWorkpackModelDto(final WorkpackModel workpackModel) {
    PropertyModelDto sortBy = null;
    if (workpackModel.getSortBy() != null) {
      sortBy = this.getPropertyModelDtoFromEntity.execute(workpackModel.getSortBy());
    }
    workpackModel.setSortBy(null);
    switch (workpackModel.getClass().getTypeName()) {
      case TYPE_NAME_MODEL_PORTFOLIO:
        final PortfolioModelDto portfolioModelDto = PortfolioModelDto.of(workpackModel);
        portfolioModelDto.setSortBy(sortBy);
        return portfolioModelDto;
      case TYPE_NAME_MODEL_PROGRAM:
        final ProgramModelDto programModelDto = ProgramModelDto.of(workpackModel);
        programModelDto.setSortBy(sortBy);
        return programModelDto;
      case TYPE_NAME_MODEL_ORGANIZER:
        final OrganizerModelDto organizerModelDto = OrganizerModelDto.of(workpackModel);
        organizerModelDto.setSortBy(sortBy);
        return organizerModelDto;
      case TYPE_NAME_MODEL_DELIVERABLE:
        final DeliverableModelDto deliverableModelDto = DeliverableModelDto.of(workpackModel);
        deliverableModelDto.setSortBy(sortBy);
        return deliverableModelDto;
      case TYPE_NAME_MODEL_PROJECT:
        final ProjectModelDto projectModelDto = ProjectModelDto.of(workpackModel);
        projectModelDto.setSortBy(sortBy);
        return projectModelDto;
      case TYPE_NAME_MODEL_MILESTONE:
        final MilestoneModelDto milestoneModelDto = MilestoneModelDto.of(workpackModel);
        milestoneModelDto.setSortBy(sortBy);
        return milestoneModelDto;
      default:
        return null;
    }
  }

  public Set<WorkpackModelDetailDto> getChildren(final Collection<? extends WorkpackModel> childrens) {
    if (childrens != null && !childrens.isEmpty()) {
      final Collection<WorkpackModelDetailDto> set = new HashSet<>();
      childrens.forEach(w -> {
        Set<WorkpackModelDetailDto> childrenChild = null;
        if (w.getChildren() != null && !w.getChildren().isEmpty()) {
          childrenChild = this.getChildren(w.getChildren());
        }
        w.setParent(null);
        w.setChildren(null);
        final WorkpackModelDetailDto detailDto = this.convertWorkpackModelDetailDto(w);
        if (detailDto != null) {
          detailDto.setChildren(childrenChild);
          set.add(detailDto);
        }
      });
      return set.stream()
        .sorted(Comparator.comparing(WorkpackModelDetailDto::getPositionOrElseZero))
        .collect(Collectors.toCollection(LinkedHashSet::new));
    }
    return Collections.emptySet();
  }

  public WorkpackModel findByIdWithParents(final Long id) {
    return this.workpackModelRepository.findByIdWithParents(id)
      .orElseThrow(() -> new NegocioException(WORKPACKMODEL_NOT_FOUND));
  }

  public void deleteProperty(final Long idPropertyModel) {
    this.deletePropertyModel.execute(idPropertyModel);
  }

  public boolean isCanDeleteProperty(final Long idPropertyModel) {
    return this.propertyModelService.canDeleteProperty(idPropertyModel);
  }

  private void validType(final WorkpackModelParamDto workpackModelParamDto) {
    if (workpackModelParamDto.isDeliverableDtoOrMilestoneDto()) {
      final boolean isChildFromProgramModel = this.verifier.verify(
        workpackModelParamDto.getId(),
        WorkpackModelInstanceType.TYPE_NAME_MODEL_PROGRAM::isTypeOf
      );
      ifNotMilestoneDeliverableProgramWorkpackThrowException(workpackModelParamDto, isChildFromProgramModel);
    }
  }

  private WorkpackModelDetailDto convertWorkpackModelDetailDto(final WorkpackModel workpackModel) {
    PropertyModelDto sortBy = null;
    if (workpackModel.getSortBy() != null) {
      sortBy = this.getPropertyModelDtoFromEntity.execute(workpackModel.getSortBy());
    }
    final String typeName = workpackModel.getClass().getTypeName();
    switch (typeName) {
      case TYPE_NAME_MODEL_PORTFOLIO:
        final PortfolioModelDetailDto portfolioModelDetailDto = PortfolioModelDetailDto.of(workpackModel);
        portfolioModelDetailDto.setSortBy(sortBy);
        return portfolioModelDetailDto;
      case TYPE_NAME_MODEL_PROGRAM:
        final ProgramModelDetailDto programModelDetailDto = ProgramModelDetailDto.of(workpackModel);
        programModelDetailDto.setSortBy(sortBy);
        return programModelDetailDto;
      case TYPE_NAME_MODEL_ORGANIZER:
        final OrganizerModelDetailDto organizerModelDetailDto = OrganizerModelDetailDto.of(workpackModel);
        organizerModelDetailDto.setSortBy(sortBy);
        return organizerModelDetailDto;
      case TYPE_NAME_MODEL_DELIVERABLE:
        final DeliverableModelDetailDto deliverableModelDetailDto = DeliverableModelDetailDto.of(workpackModel);
        deliverableModelDetailDto.setSortBy(sortBy);
        return deliverableModelDetailDto;
      case TYPE_NAME_MODEL_PROJECT:
        final ProjectModelDetailDto projectModelDetailDto = ProjectModelDetailDto.of(workpackModel);
        projectModelDetailDto.setSortBy(sortBy);
        return projectModelDetailDto;
      case TYPE_NAME_MODEL_MILESTONE:
        final MilestoneModelDetailDto milestoneModelDetailDto = MilestoneModelDetailDto.of(workpackModel);
        milestoneModelDetailDto.setSortBy(sortBy);
        return milestoneModelDetailDto;
      default:
        return null;
    }
  }

  private Set<PropertyModel> getProperties(final WorkpackModelParamDto workpackModelParamDto) {
    final List<? extends PropertyModelDto> properties = workpackModelParamDto.getProperties();
    return this.getPropertyModelFromDto.execute(properties);
  }

  public Optional<WorkpackModel> getWorkpackModelByWorkpackId(Long workpackId) {
    return this.workpackModelRepository.findByWorkpackId(workpackId);
  }

  public boolean deliveryHasValidScope(Long deliveryId) {
    return this.workpackModelRepository.deliveryHasValidScope(deliveryId);
  }
}
