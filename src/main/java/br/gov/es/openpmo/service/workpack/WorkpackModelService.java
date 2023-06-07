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
import br.gov.es.openpmo.dto.workpackmodel.params.properties.*;
import br.gov.es.openpmo.enumerator.Session;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.properties.models.*;
import br.gov.es.openpmo.model.workpacks.models.DeliverableModel;
import br.gov.es.openpmo.model.workpacks.models.MilestoneModel;
import br.gov.es.openpmo.model.workpacks.models.OrganizerModel;
import br.gov.es.openpmo.model.workpacks.models.PortfolioModel;
import br.gov.es.openpmo.model.workpacks.models.ProgramModel;
import br.gov.es.openpmo.model.workpacks.models.ProjectModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import br.gov.es.openpmo.service.actors.OrganizationService;
import br.gov.es.openpmo.service.office.DomainService;
import br.gov.es.openpmo.service.office.LocalityService;
import br.gov.es.openpmo.service.office.UnitMeasureService;
import br.gov.es.openpmo.service.office.plan.PlanModelService;
import br.gov.es.openpmo.service.properties.PropertyModelService;
import br.gov.es.openpmo.service.ui.BreadcrumbWorkpackModelHelper;
import br.gov.es.openpmo.utils.ApplicationMessage;
import br.gov.es.openpmo.utils.WorkpackModelInstanceType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.PROPERTY_MODEL_DELETE_RELATIONSHIP_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.PROPERTY_UPDATE_TYPE_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACKMODEL_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_MODEL_MILESTONE_DELIVERABLE_PROGRAM_ERROR;

@Service
public class WorkpackModelService implements BreadcrumbWorkpackModelHelper {

  private static final String TYPE_NAME_MODEL_PORTFOLIO = "br.gov.es.openpmo.model.workpacks.models.PortfolioModel";

  private static final String TYPE_NAME_MODEL_PROGRAM = "br.gov.es.openpmo.model.workpacks.models.ProgramModel";

  private static final String TYPE_NAME_MODEL_ORGANIZER = "br.gov.es.openpmo.model.workpacks.models.OrganizerModel";

  private static final String TYPE_NAME_MODEL_DELIVERABLE = "br.gov.es.openpmo.model.workpacks.models.DeliverableModel";

  private static final String TYPE_NAME_MODEL_PROJECT = "br.gov.es.openpmo.model.workpacks.models.ProjectModel";

  private static final String TYPE_NAME_MODEL_MILESTONE = "br.gov.es.openpmo.model.workpacks.models.MilestoneModel";

  private static final String TYPE_NAME_MODEL_INTEGER = "br.gov.es.openpmo.model.properties.models.IntegerModel";

  private static final String TYPE_NAME_MODEL_TEXT = "br.gov.es.openpmo.model.properties.models.TextModel";

  private static final String TYPE_NAME_MODEL_DATE = "br.gov.es.openpmo.model.properties.models.DateModel";

  private static final String TYPE_NAME_MODEL_TOGGLE = "br.gov.es.openpmo.model.properties.models.ToggleModel";

  private static final String TYPE_NAME_MODEL_UNIT_SELECTION = "br.gov.es.openpmo.model.properties.models" +
                                                               ".UnitSelectionModel";

  private static final String TYPE_NAME_MODEL_SELECTION = "br.gov.es.openpmo.model.properties.models.SelectionModel";

  private static final String TYPE_NAME_MODEL_TEXT_AREA = "br.gov.es.openpmo.model.properties.models.TextAreaModel";

  private static final String TYPE_NAME_MODEL_NUMBER = "br.gov.es.openpmo.model.properties.models.NumberModel";

  private static final String TYPE_NAME_MODEL_CURRENCY = "br.gov.es.openpmo.model.properties.models.CurrencyModel";

  private static final String TYPE_NAME_MODEL_LOCALITY_SELECTION
    = "br.gov.es.openpmo.model.properties.models.LocalitySelectionModel";

  private static final String TYPE_NAME_MODEL_ORGANIZATION_SELECTION
    = "br.gov.es.openpmo.model.properties.models.OrganizationSelectionModel";

  private static final String TYPE_NAME_MODEL_GROUP = "br.gov.es.openpmo.model.properties.models.GroupModel";

  private static final String PACKAGE_DTO = "br.gov.es.openpmo.dto.workpackmodel.params";

  private static final String PACKAGE_PROPERTIES_DTO = "br.gov.es.openpmo.dto.workpackmodel.params.properties";

  private final WorkpackModelRepository workpackModelRepository;

  private final PlanModelService planModelService;

  private final ModelMapper modelMapper;

  private final PropertyModelService propertyModelService;

  private final DomainService domainService;

  private final LocalityService localityService;

  private final OrganizationService organizationService;

  private final UnitMeasureService unitMeasureService;

  private final ParentWorkpackTypeVerifier verifier;

  @Autowired
  public WorkpackModelService(
    final WorkpackModelRepository workpackModelRepository,
    final PlanModelService planModelService,
    final ModelMapper modelMapper,
    final PropertyModelService propertyModelService,
    final DomainService domainService,
    final LocalityService localityService,
    final OrganizationService organizationService,
    final UnitMeasureService unitMeasureService,
    final ParentWorkpackTypeVerifier verifier
  ) {
    this.workpackModelRepository = workpackModelRepository;
    this.planModelService = planModelService;
    this.modelMapper = modelMapper;
    this.propertyModelService = propertyModelService;
    this.domainService = domainService;
    this.localityService = localityService;
    this.organizationService = organizationService;
    this.unitMeasureService = unitMeasureService;
    this.verifier = verifier;
  }

  private static Set<PropertyModel> extractGroupedPropertyIfExists(final Set<PropertyModel> propertiesToDelete) {
    return Optional.ofNullable(propertiesToDelete)
      .map(properties -> properties.stream()
        .filter(GroupModel.class::isInstance)
        .collect(Collectors.toSet()))
      .orElse(new HashSet<>());
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
    if (workpackModel.getId() == null) {
      workpackModel.setPlanModel(this.planModelService.findById(workpackModel.getIdPlanModel()));
      this.ifHasParentIdCreateAsChild(workpackModel, idParent);
    }
    return this.workpackModelRepository.save(workpackModel);
  }

  private void ifHasParentIdCreateAsChild(
    final WorkpackModel workpackModel,
    final Long idParent
  ) {
    if (idParent != null) {
      final WorkpackModel parent = this.findById(idParent);
      workpackModel.addParent(parent);
    }
  }

  public WorkpackModel findById(final Long id) {
    return this.workpackModelRepository.findAllByIdWorkpackModel(id)
      .orElseThrow(() -> new NegocioException(WORKPACKMODEL_NOT_FOUND));
  }

  public List<WorkpackModel> findAll(final Long idPlanModel) {
    return this.workpackModelRepository.findAllByIdPlanModel(idPlanModel)
      .stream().sorted(Comparator.comparing(WorkpackModel::getPositionOrElseZero))
      .collect(Collectors.toList());
  }

  @Transactional
  public WorkpackModel update(final WorkpackModel workpackModel) {
    final WorkpackModel workpackModelUpdate = this.findById(workpackModel.getId());

    final Set<PropertyModel> properties = workpackModel.getProperties();
    final Set<PropertyModel> propertiesToUpdate = workpackModelUpdate.getProperties();

    if (workpackModelUpdate.sortByWasChanged(workpackModel.getSortBy())) {
      final PropertyModel oldSortProperty = propertiesToUpdate.stream()
        .filter(PropertyModel::isSortProperty)
        .findFirst()
        .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_SORTER_PROPERTY_NOT_FOUND));
      oldSortProperty.setSorts(null);

      final PropertyModel newSortProperty = propertiesToUpdate.stream()
        .filter(p -> p.getId().equals(workpackModel.getSortBy().getId()))
        .findFirst()
        .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_SORTER_PROPERTY_NOT_FOUND));

      newSortProperty.setSorts(workpackModelUpdate);
    }

    workpackModelUpdate.updateFields(workpackModel);


    this.verifyForPropertiesToDelete(properties, propertiesToUpdate);
    this.verifyForPropertiesToUpdate(
      () -> workpackModelUpdate.setProperties(new HashSet<>()),
      properties,
      propertiesToUpdate
    );

    return this.workpackModelRepository.save(workpackModelUpdate);
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
      if (workpackModelParamDto.getSortBy() != null && workpackModel.getProperties() != null && !(workpackModel.getProperties()).isEmpty()) {
        workpackModel.setSortBy(
          workpackModel.getProperties().stream()
            .filter(p -> p.getSession() == Session.PROPERTIES)
            .filter(p -> p.getName() != null && p.getName().equals(workpackModelParamDto.getSortBy()))
            .findFirst().orElse(null)
        );
      }
    }
    return workpackModel;
  }

  public WorkpackModelDetailDto getWorkpackModelDetailDto(final WorkpackModel workpackModel) {
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
    if (workpackModel.getProperties() != null && !(workpackModel.getProperties()).isEmpty()) {
      properties = this.getPropertyModelDto(workpackModel);
    }
    if (workpackModel.getSortBy() != null) {
      sortBy = this.getPropertyModelDto(workpackModel.getSortBy());
      workpackModel.setSortBy(null);
    }
    final WorkpackModelDetailDto detailDto = this.convertWorkpackModelDetailDto(workpackModel);
    if (detailDto != null) {
      detailDto.dashboardConfiguration(workpackModel);
      detailDto.setChildren(children);
      detailDto.setParent(parent);
      detailDto.setProperties(properties);
      detailDto.setSortBy(sortBy);
    }
    if (detailDto != null && detailDto.getChildren() != null) {
      final LinkedHashSet<WorkpackModelDetailDto> sortedChildren = detailDto.getChildren().stream()
        .sorted(Comparator.comparing(WorkpackModelDetailDto::getPositionOrElseZero)
                  .thenComparing(WorkpackModelDetailDto::getModelName))
        .collect(Collectors.toCollection(LinkedHashSet::new));
      detailDto.setChildren(sortedChildren);
    }
    return detailDto;
  }

  public PropertyModelDto getPropertyModelDto(final PropertyModel propertyModel) {
    switch (propertyModel.getClass().getTypeName()) {
      case TYPE_NAME_MODEL_INTEGER:
        return IntegerModelDto.of(propertyModel);
      case TYPE_NAME_MODEL_TEXT:
        return TextModelDto.of(propertyModel);
      case TYPE_NAME_MODEL_DATE:
        return DateModelDto.of(propertyModel);
      case TYPE_NAME_MODEL_TOGGLE:
        return ToggleModelDto.of(propertyModel);
      case TYPE_NAME_MODEL_UNIT_SELECTION:
        final UnitSelectionModelDto unitDto = UnitSelectionModelDto.of(propertyModel);
        final UnitSelectionModel unitModel = (UnitSelectionModel) propertyModel;
        if (unitModel.getDefaultValue() != null) {
          unitDto.setDefaults(unitModel.getDefaultValue().getId());
        }
        return unitDto;
      case TYPE_NAME_MODEL_SELECTION:
        return SelectionModelDto.of(propertyModel);
      case TYPE_NAME_MODEL_TEXT_AREA:
        return TextAreaModelDto.of(propertyModel);
      case TYPE_NAME_MODEL_NUMBER:
        return NumberModelDto.of(propertyModel);
      case TYPE_NAME_MODEL_CURRENCY:
        return CurrencyModelDto.of(propertyModel);
      case TYPE_NAME_MODEL_LOCALITY_SELECTION:
        final LocalitySelectionModelDto localityDto = LocalitySelectionModelDto.of(propertyModel);
        final LocalitySelectionModel localityModel = (LocalitySelectionModel) propertyModel;
        if (localityModel.getDefaultValue() != null && !(localityModel.getDefaultValue()).isEmpty()) {
          localityDto.setDefaults(new ArrayList<>());
          localityModel.getDefaultValue().forEach(l -> localityDto.getDefaults().add(l.getId()));
        }
        if (localityModel.getDomain() != null) {
          localityDto.setIdDomain(localityModel.getDomain().getId());
        }
        return localityDto;
      case TYPE_NAME_MODEL_ORGANIZATION_SELECTION:
        final OrganizationSelectionModelDto organizationDto = OrganizationSelectionModelDto.of(propertyModel);
        final OrganizationSelectionModel organizationModel = (OrganizationSelectionModel) propertyModel;
        if (organizationModel.getDefaultValue() != null && !(organizationModel.getDefaultValue().isEmpty())) {
          organizationDto.setDefaults(new ArrayList<>());
          organizationModel.getDefaultValue().forEach(
            l -> organizationDto.getDefaults().add(l.getId()));
        }
        return organizationDto;
      case TYPE_NAME_MODEL_GROUP:
        final GroupModelDto groupModelDto = GroupModelDto.of(propertyModel);
        final GroupModel groupModel = (GroupModel) propertyModel;
        final List<PropertyModelDto> groupedProperties = new ArrayList<>();
        final Set<PropertyModel> properties = groupModel.getGroupedProperties();
        if (properties != null && !properties.isEmpty()) {
          properties.forEach(property -> groupedProperties.add(this.getPropertyModelDto(property)));
        }
        groupModelDto.setGroupedProperties(groupedProperties);
        return groupModelDto;
      default:
        return null;
    }
  }

  public WorkpackModelDto getWorkpackModelDto(final WorkpackModel workpackModel) {
    PropertyModelDto sortBy = null;
    if (workpackModel.getSortBy() != null) {
      sortBy = this.getPropertyModelDto(workpackModel.getSortBy());
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
      final Set<WorkpackModelDetailDto> set = new HashSet<>();
      childrens.parallelStream().forEach(w -> {
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
    final PropertyModel propertyModel = this.propertyModelService.findById(idPropertyModel);
    if (!this.isCanDeleteProperty(idPropertyModel)) {
      throw new NegocioException(PROPERTY_MODEL_DELETE_RELATIONSHIP_ERROR);
    }
    final Collection<PropertyModel> properties = new HashSet<>();
    properties.add(propertyModel);
    this.propertyModelService.delete(properties);
  }

  public boolean isCanDeleteProperty(final Long idPropertyModel) {
    return this.propertyModelService.canDeleteProperty(idPropertyModel);
  }

  private void verifyForPropertiesToUpdate(
    final Runnable createPropertyList,
    final Collection<? extends PropertyModel> properties,
    final Collection<PropertyModel> propertiesToUpdate
  ) {
    if (CollectionUtils.isEmpty(properties)) return;
    for (final PropertyModel propertyModel : properties) {
      if (propertyModel.getId() == null) {
        if (propertiesToUpdate == null) {
          createPropertyList.run();
        }
        propertiesToUpdate.add(propertyModel);
        continue;
      }
      if (propertiesToUpdate != null) {
        propertiesToUpdate.stream()
          .filter(p -> p.getId() != null && p.getId().equals(propertyModel.getId()))
          .findFirst()
          .ifPresent(propertyModelUpdate -> this.loadPropertyUpdate(propertyModelUpdate, propertyModel)
          );
      }
    }
  }

  private void verifyForPropertiesToDelete(
    final Collection<PropertyModel> properties,
    final Collection<PropertyModel> propertiesToUpdate
  ) {
    if (propertiesToUpdate != null && !propertiesToUpdate.isEmpty()) {
      final Predicate<PropertyModel> findPropertiesToDelete = propertyModel -> properties == null ||
                                                                               properties.stream().noneMatch(p -> p.getId() != null && p.getId().equals(
                                                                                 propertyModel.getId()));
      final Set<PropertyModel> propertiesToDelete = propertiesToUpdate.stream()
        .filter(findPropertiesToDelete)
        .collect(Collectors.toSet());
      if (!propertiesToDelete.isEmpty()) {
        for (final PropertyModel propertyModel : propertiesToDelete) {
          if (!this.isCanDeleteProperty(propertyModel.getId())) {
            throw new NegocioException(PROPERTY_MODEL_DELETE_RELATIONSHIP_ERROR);
          }
        }
        this.verifyForGroupedPropertiesToDelete(propertiesToDelete);
        this.propertyModelService.delete(propertiesToDelete);
      }
    }
  }

  private void loadPropertyUpdate(
    final PropertyModel propertyModelUpdate,
    final PropertyModel propertyModel
  ) {
    if (!propertyModel.getClass().getTypeName().equals(propertyModelUpdate.getClass().getTypeName())) {
      throw new NegocioException(PROPERTY_UPDATE_TYPE_ERROR);
    }
    propertyModelUpdate.setActive(propertyModel.isActive());
    propertyModelUpdate.setFullLine(propertyModel.isFullLine());
    propertyModelUpdate.setLabel(propertyModel.getLabel());
    propertyModelUpdate.setRequired(propertyModel.isRequired());
    propertyModelUpdate.setSortIndex(propertyModel.getSortIndex());
    propertyModelUpdate.setName(propertyModel.getName());
    propertyModelUpdate.setSession(propertyModel.getSession());

    switch (propertyModelUpdate.getClass().getTypeName()) {
      case TYPE_NAME_MODEL_INTEGER:
        final IntegerModel integerModelUpdate = (IntegerModel) propertyModelUpdate;
        final IntegerModel integerModel = (IntegerModel) propertyModel;
        integerModelUpdate.setMax(integerModel.getMax());
        integerModelUpdate.setMin(integerModel.getMin());
        integerModelUpdate.setDefaultValue(integerModel.getDefaultValue());
        break;
      case TYPE_NAME_MODEL_TEXT:
        final TextModel textModelUpdate = (TextModel) propertyModelUpdate;
        final TextModel textModel = (TextModel) propertyModel;
        textModelUpdate.setMax(textModel.getMax());
        textModelUpdate.setMin(textModel.getMin());
        textModelUpdate.setDefaultValue(textModel.getDefaultValue());
        break;
      case TYPE_NAME_MODEL_DATE:
        final DateModel dateModelUpdate = (DateModel) propertyModelUpdate;
        final DateModel dateModel = (DateModel) propertyModel;
        dateModelUpdate.setMax(dateModel.getMax());
        dateModelUpdate.setMin(dateModel.getMin());
        dateModelUpdate.setDefaultValue(dateModel.getDefaultValue());
        break;
      case TYPE_NAME_MODEL_TOGGLE:
        final ToggleModel toggleModelUpdate = (ToggleModel) propertyModelUpdate;
        final ToggleModel toggleModel = (ToggleModel) propertyModel;
        toggleModelUpdate.setDefaultValue(toggleModel.isDefaultValue());
        break;
      case TYPE_NAME_MODEL_UNIT_SELECTION:
        final UnitSelectionModel unitSelectionModelUpdate = (UnitSelectionModel) propertyModelUpdate;
        final UnitSelectionModel unitSelectionModel = (UnitSelectionModel) propertyModel;
        unitSelectionModelUpdate.setDefaultValue(unitSelectionModel.getDefaultValue());
        break;
      case TYPE_NAME_MODEL_SELECTION:
        final SelectionModel selectionModelUpdate = (SelectionModel) propertyModelUpdate;
        final SelectionModel selectionModel = (SelectionModel) propertyModel;
        selectionModelUpdate.setMultipleSelection(selectionModel.isMultipleSelection());
        selectionModelUpdate.setPossibleValues(selectionModel.getPossibleValues());
        selectionModelUpdate.setDefaultValue(selectionModel.getDefaultValue());
        break;
      case TYPE_NAME_MODEL_TEXT_AREA:
        final TextAreaModel textAreaModelUpdate = (TextAreaModel) propertyModelUpdate;
        final TextAreaModel textAreaModel = (TextAreaModel) propertyModel;
        textAreaModelUpdate.setMax(textAreaModel.getMax());
        textAreaModelUpdate.setMin(textAreaModel.getMin());
        textAreaModelUpdate.setDefaultValue(textAreaModel.getDefaultValue());
        textAreaModelUpdate.setRows(textAreaModel.getRows());
        break;
      case TYPE_NAME_MODEL_NUMBER:
        final NumberModel decimalModelUpdate = (NumberModel) propertyModelUpdate;
        final NumberModel decimalModel = (NumberModel) propertyModel;
        decimalModelUpdate.setMax(decimalModel.getMax());
        decimalModelUpdate.setMin(decimalModel.getMin());
        decimalModelUpdate.setDefaultValue(decimalModel.getDefaultValue());
        decimalModelUpdate.setPrecision(decimalModel.getPrecision());
        break;
      case TYPE_NAME_MODEL_CURRENCY:
        final CurrencyModel currencyModelUpdate = (CurrencyModel) propertyModelUpdate;
        final CurrencyModel currencyModel = (CurrencyModel) propertyModel;
        currencyModelUpdate.setDefaultValue(currencyModel.getDefaultValue());
        break;
      case TYPE_NAME_MODEL_LOCALITY_SELECTION:
        final LocalitySelectionModel localitySelectionModelUpdate = (LocalitySelectionModel) propertyModelUpdate;
        final LocalitySelectionModel localitySelectionModel = (LocalitySelectionModel) propertyModel;
        localitySelectionModelUpdate.setMultipleSelection(localitySelectionModel.isMultipleSelection());
        localitySelectionModelUpdate.setDefaultValue(localitySelectionModel.getDefaultValue());
        localitySelectionModelUpdate.setDomain(localitySelectionModel.getDomain());
        break;
      case TYPE_NAME_MODEL_ORGANIZATION_SELECTION:
        final OrganizationSelectionModel organizationSelectionModelUpdate =
          (OrganizationSelectionModel) propertyModelUpdate;
        final OrganizationSelectionModel organizationSelectionModel = (OrganizationSelectionModel) propertyModel;
        organizationSelectionModelUpdate.setMultipleSelection(organizationSelectionModel.isMultipleSelection());
        organizationSelectionModelUpdate.setDefaultValue(organizationSelectionModel.getDefaultValue());
        break;
      case TYPE_NAME_MODEL_GROUP:
        final GroupModel groupModelUpdate = (GroupModel) propertyModelUpdate;
        final GroupModel groupModel = (GroupModel) propertyModel;

        final Set<PropertyModel> groupedProperties = groupModel.getGroupedProperties();
        final Set<PropertyModel> groupedPropertiesToUpdate = groupModelUpdate.getGroupedProperties();

        this.verifyForPropertiesToDelete(groupedProperties, groupedPropertiesToUpdate);
        this.verifyForPropertiesToUpdate(
          () -> groupModelUpdate.setGroupedProperties(new HashSet<>()),
          groupedProperties,
          groupedPropertiesToUpdate
        );
        break;
    }

  }

  private void verifyForGroupedPropertiesToDelete(final Set<PropertyModel> propertiesToDelete) {
    final Set<PropertyModel> groupedProperties =
      WorkpackModelService.extractGroupedPropertyIfExists(propertiesToDelete);
    if (!groupedProperties.isEmpty()) {
      final Collection<PropertyModel> groupedPropertiesToDelete = new HashSet<>();
      for (final PropertyModel property : groupedProperties) {
        groupedPropertiesToDelete.addAll(((GroupModel) property).getGroupedProperties());
      }
      this.propertyModelService.delete(groupedPropertiesToDelete);
    }
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

  private List<? extends PropertyModelDto> getPropertyModelDto(final WorkpackModel workpackModel) {
    if (workpackModel.getProperties() != null && !(workpackModel.getProperties()).isEmpty()) {
      final List<PropertyModelDto> list = new ArrayList<>();
      workpackModel.getProperties()
        .forEach(propertyModel -> list.add(this.getPropertyModelDto(propertyModel)));
      list.sort(Comparator.comparing(PropertyModelDto::getSortIndex));
      return list;
    }
    return Collections.emptyList();
  }

  private WorkpackModelDetailDto convertWorkpackModelDetailDto(final WorkpackModel workpackModel) {
    PropertyModelDto sortBy = null;
    if (workpackModel.getSortBy() != null) {
      sortBy = this.getPropertyModelDto(workpackModel.getSortBy());
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
    final Set<PropertyModel> propertyModels = new HashSet<>();
    workpackModelParamDto.getProperties().forEach(property -> this.extractToModel(propertyModels, property));
    return propertyModels;
  }

  public void extractToModel(
    final Collection<? super PropertyModel> propertyModels,
    final PropertyModelDto property
  ) {
    switch (property.getClass().getTypeName()) {
      case PACKAGE_PROPERTIES_DTO + ".IntegerModelDto":
        propertyModels.add(this.modelMapper.map(property, IntegerModel.class));
        break;
      case PACKAGE_PROPERTIES_DTO + ".TextModelDto":
        propertyModels.add(this.modelMapper.map(property, TextModel.class));
        break;
      case PACKAGE_PROPERTIES_DTO + ".DateModelDto":
        propertyModels.add(this.modelMapper.map(property, DateModel.class));
        break;
      case PACKAGE_PROPERTIES_DTO + ".ToggleModelDto":
        propertyModels.add(this.modelMapper.map(property, ToggleModel.class));
        break;
      case PACKAGE_PROPERTIES_DTO + ".UnitSelectionModelDto":
        final UnitSelectionModel unitSelectionModel = this.modelMapper.map(property, UnitSelectionModel.class);
        final UnitSelectionModelDto unitSelectionDto = (UnitSelectionModelDto) property;
        if (unitSelectionDto.getDefaults() != null) {
          unitSelectionModel.setDefaultValue(this.unitMeasureService.findById(unitSelectionDto.getDefaults()));
        }
        propertyModels.add(unitSelectionModel);
        break;
      case PACKAGE_PROPERTIES_DTO + ".SelectionModelDto":
        propertyModels.add(this.modelMapper.map(property, SelectionModel.class));
        break;
      case PACKAGE_PROPERTIES_DTO + ".TextAreaModelDto":
        propertyModels.add(this.modelMapper.map(property, TextAreaModel.class));
        break;
      case PACKAGE_PROPERTIES_DTO + ".NumberModelDto":
        final NumberModel numberModel = this.modelMapper.map(
          property,
          NumberModel.class
        );
        final Integer precision = numberModel.getPrecision();
        if (precision == null) {
          numberModel.setPrecision(3);
        } else if (precision < 1 || precision > 6) {
          throw new NegocioException(ApplicationMessage.PRECISION_OUT_OF_RANGE);
        }
        propertyModels.add(numberModel);
        break;
      case PACKAGE_PROPERTIES_DTO + ".CurrencyModelDto":
        propertyModels.add(this.modelMapper.map(property, CurrencyModel.class));
        break;
      case PACKAGE_PROPERTIES_DTO + ".LocalitySelectionModelDto":
        final LocalitySelectionModel localitySelectionModel = this.modelMapper.map(
          property,
          LocalitySelectionModel.class
        );
        final LocalitySelectionModelDto localityDto = (LocalitySelectionModelDto) property;
        if (localityDto.getIdDomain() != null) {
          localitySelectionModel.setDomain(this.domainService.findById(localityDto.getIdDomain()));
        }
        if (localityDto.getDefaults() != null && !(localityDto.getDefaults()).isEmpty()) {
          localitySelectionModel.setDefaultValue(new HashSet<>());
          localityDto.getDefaults().forEach(
            l -> localitySelectionModel.getDefaultValue().add(this.localityService.findById(l)));
        }
        propertyModels.add(localitySelectionModel);
        break;
      case PACKAGE_PROPERTIES_DTO + ".OrganizationSelectionModelDto":
        final OrganizationSelectionModel organizationSelectionModel = this.modelMapper.map(
          property,
          OrganizationSelectionModel.class
        );
        final OrganizationSelectionModelDto organizationDto = (OrganizationSelectionModelDto) property;
        if (organizationDto.getDefaults() != null && !(organizationDto.getDefaults()).isEmpty()) {
          organizationSelectionModel.setDefaultValue(new HashSet<>());
          organizationDto.getDefaults().forEach(
            o -> organizationSelectionModel.getDefaultValue().add(this.organizationService.findById(o)));
        }
        propertyModels.add(organizationSelectionModel);
        break;
      case PACKAGE_PROPERTIES_DTO + ".GroupModelDto":
        final GroupModel groupModel = this.modelMapper.map(property, GroupModel.class);
        final GroupModelDto groupModelDto = (GroupModelDto) property;

        final Set<PropertyModel> groupedProperties = new HashSet<>();

        groupModelDto.getGroupedProperties().forEach(p -> this.extractToModel(groupedProperties, p));
        groupModel.setGroupedProperties(groupedProperties);

        propertyModels.add(groupModel);

        break;
    }
  }

}
