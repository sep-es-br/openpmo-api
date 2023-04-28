package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.costaccount.CostAccountDto;
import br.gov.es.openpmo.dto.plan.PlanDto;
import br.gov.es.openpmo.dto.workpack.*;
import br.gov.es.openpmo.dto.workpackshared.WorkpackSharedDto;
import br.gov.es.openpmo.enumerator.Session;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.office.Locality;
import br.gov.es.openpmo.model.office.UnitMeasure;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.properties.Currency;
import br.gov.es.openpmo.model.properties.Date;
import br.gov.es.openpmo.model.properties.Integer;
import br.gov.es.openpmo.model.properties.Number;
import br.gov.es.openpmo.model.properties.*;
import br.gov.es.openpmo.model.properties.models.*;
import br.gov.es.openpmo.model.relations.BelongsTo;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.model.workpacks.Organizer;
import br.gov.es.openpmo.model.workpacks.Portfolio;
import br.gov.es.openpmo.model.workpacks.Program;
import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.DeliverableModel;
import br.gov.es.openpmo.model.workpacks.models.MilestoneModel;
import br.gov.es.openpmo.model.workpacks.models.OrganizerModel;
import br.gov.es.openpmo.model.workpacks.models.PortfolioModel;
import br.gov.es.openpmo.model.workpacks.models.ProgramModel;
import br.gov.es.openpmo.model.workpacks.models.ProjectModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.BelongsToRepository;
import br.gov.es.openpmo.repository.CustomFilterRepository;
import br.gov.es.openpmo.repository.MilestoneRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.custom.filters.FindAllWorkpackByParentUsingCustomFilter;
import br.gov.es.openpmo.repository.custom.filters.FindAllWorkpackUsingCustomFilter;
import br.gov.es.openpmo.service.actors.OrganizationService;
import br.gov.es.openpmo.service.dashboards.v2.IAsyncDashboardService;
import br.gov.es.openpmo.service.journals.JournalDeleter;
import br.gov.es.openpmo.service.office.LocalityService;
import br.gov.es.openpmo.service.office.UnitMeasureService;
import br.gov.es.openpmo.service.office.plan.PlanService;
import br.gov.es.openpmo.service.properties.PropertyModelService;
import br.gov.es.openpmo.service.properties.PropertyService;
import br.gov.es.openpmo.service.ui.BreadcrumbWorkpackHelper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.*;
import static br.gov.es.openpmo.utils.PropertyInstanceTypeDeprecated.*;
import static java.lang.Boolean.TRUE;

@Service
public class WorkpackService implements BreadcrumbWorkpackHelper {

  private static final String TYPE_NAME_PORTFOLIO = "br.gov.es.openpmo.model.workpacks.Portfolio";

  private static final String TYPE_NAME_PROGRAM = "br.gov.es.openpmo.model.workpacks.Program";

  private static final String TYPE_NAME_ORGANIZER = "br.gov.es.openpmo.model.workpacks.Organizer";

  private static final String TYPE_NAME_DELIVERABLE = "br.gov.es.openpmo.model.workpacks.Deliverable";

  private static final String TYPE_NAME_PROJECT = "br.gov.es.openpmo.model.workpacks.Project";

  private static final String TYPE_NAME_MILESTONE = "br.gov.es.openpmo.model.workpacks.Milestone";

  private final WorkpackModelService workpackModelService;

  private final PropertyModelService propertyModelService;

  private final WorkpackRepository workpackRepository;

  private final PlanService planService;

  private final ModelMapper modelMapper;

  private final PropertyService propertyService;

  private final OrganizationService organizationService;

  private final LocalityService localityService;

  private final UnitMeasureService unitMeasureService;

  private final CustomFilterRepository customFilterRepository;

  private final FindAllWorkpackUsingCustomFilter findAllWorkpack;

  private final FindAllWorkpackByParentUsingCustomFilter findAllWorkpackByParent;

  private final BelongsToRepository belongsToRepository;

  private final MilestoneService milestoneService;

  private final JournalDeleter journalDeleter;

  private final WorkpackSorter workpackSorter;

  private final IAsyncDashboardService dashboardService;

  private final HasScheduleSessionActive hasScheduleSessionActive;

  private final MilestoneRepository milestoneRepository;

  @Autowired
  public WorkpackService(
    final WorkpackModelService workpackModelService,
    final PlanService planService,
    final ModelMapper modelMapper,
    final PropertyService propertyService,
    final PropertyModelService propertyModelService,
    final WorkpackRepository workpackRepository,
    final CustomFilterRepository customFilterRepository,
    final FindAllWorkpackByParentUsingCustomFilter findAllWorkpackByParent,
    final OrganizationService organizationService,
    final LocalityService localityService,
    final UnitMeasureService unitMeasureService,
    final FindAllWorkpackUsingCustomFilter findAllWorkpack,
    final BelongsToRepository belongsToRepository,
    final MilestoneService milestoneService,
    final JournalDeleter journalDeleter,
    final WorkpackSorter workpackSorter,
    final IAsyncDashboardService dashboardService,
    final HasScheduleSessionActive hasScheduleSessionActive,
    final MilestoneRepository milestoneRepository
  ) {
    this.workpackModelService = workpackModelService;
    this.planService = planService;
    this.modelMapper = modelMapper;
    this.propertyService = propertyService;
    this.workpackRepository = workpackRepository;
    this.propertyModelService = propertyModelService;
    this.customFilterRepository = customFilterRepository;
    this.findAllWorkpackByParent = findAllWorkpackByParent;
    this.organizationService = organizationService;
    this.localityService = localityService;
    this.unitMeasureService = unitMeasureService;
    this.findAllWorkpack = findAllWorkpack;
    this.belongsToRepository = belongsToRepository;
    this.milestoneService = milestoneService;
    this.journalDeleter = journalDeleter;
    this.workpackSorter = workpackSorter;
    this.dashboardService = dashboardService;
    this.hasScheduleSessionActive = hasScheduleSessionActive;
    this.milestoneRepository = milestoneRepository;
  }

  private static void addSharedWith(
    final Workpack workpack,
    final WorkpackDetailDto detailDto
  ) {
    if (TRUE.equals(workpack.getPublicShared())) {
      detailDto.setSharedWith(Collections.singletonList(WorkpackSharedDto.of(workpack)));
      return;
    }

    final List<WorkpackSharedDto> workpackSharedDtos = Optional.ofNullable(workpack.getSharedWith())
      .map(sharedWith -> sharedWith.stream()
        .map(WorkpackSharedDto::of)
        .collect(Collectors.toList()))
      .orElse(Collections.emptyList());

    detailDto.setSharedWith(workpackSharedDtos);
  }

  private static void validateWorkpack(final Workpack workpack) {
    final Collection<PropertyModel> models = new HashSet<>();
    switch (workpack.getClass().getTypeName()) {
      case TYPE_NAME_PORTFOLIO:
        final Portfolio portfolio = (Portfolio) workpack;
        if (portfolio.getInstance().getProperties() != null) {
          models.addAll(portfolio.getInstance().getProperties());
        }
        break;
      case TYPE_NAME_PROGRAM:
        final Program program = (Program) workpack;
        if (program.getInstance().getProperties() != null) {
          models.addAll(program.getInstance().getProperties());
        }
        break;
      case TYPE_NAME_ORGANIZER:
        final Organizer organizer = (Organizer) workpack;
        if (organizer.getInstance().getProperties() != null) {
          models.addAll(organizer.getInstance().getProperties());
        }
        break;
      case TYPE_NAME_DELIVERABLE:
        final Deliverable deliverable = (Deliverable) workpack;
        if (deliverable.getInstance().getProperties() != null) {
          models.addAll(deliverable.getInstance().getProperties());
        }
        break;
      case TYPE_NAME_PROJECT:
        final Project project = (Project) workpack;
        if (project.getInstance().getProperties() != null) {
          models.addAll(project.getInstance().getProperties());
        }
        break;
      case TYPE_NAME_MILESTONE:
        final Milestone milestone = (Milestone) workpack;
        if (milestone.getInstance().getProperties() != null) {
          models.addAll(milestone.getInstance().getProperties());
        }
        break;
    }
    models.stream().filter(m -> m.getSession() == Session.PROPERTIES && m.isActive())
      .forEach(m -> validateProperty(
        workpack,
        m
      ));

  }

  private static void validateProperty(
    final Workpack workpack,
    final PropertyModel propertyModel
  ) {
    boolean propertyModelFound = false;
    if (workpack.getProperties() != null && !workpack.getProperties().isEmpty()) {
      for (final Property property : workpack.getProperties()) {
        switch (property.getClass().getTypeName()) {
          case TYPE_MODEL_NAME_INTEGER:
            final Integer integer = (Integer) property;
            if (integer.getDriver().getId().equals(propertyModel.getId())) {
              propertyModelFound = true;
              if (integer.getDriver().isRequired() && integer.getValue() == null) {
                throw new NegocioException(
                  PROPERTY_VALUE_NOT_NULL + "$" + propertyModel.getLabel());
              }
              if (integer.getDriver().getMin() != null
                  && integer.getDriver().getMin() > integer.getValue()) {
                throw new NegocioException(
                  PROPERTY_VALUE_NOT_MIN + "$" + propertyModel.getLabel());
              }
              if (integer.getDriver().getMax() != null
                  && integer.getDriver().getMax() < integer.getValue()) {
                throw new NegocioException(
                  PROPERTY_VALUE_NOT_MAX + "$" + propertyModel.getLabel());
              }
            }
            break;
          case TYPE_MODEL_NAME_TEXT:
            final Text text = (Text) property;
            if (text.getDriver().getId().equals(propertyModel.getId())) {
              propertyModelFound = true;
              if (text.getDriver().isRequired()
                  && (text.getValue() == null || text.getValue().isEmpty())) {
                throw new NegocioException(
                  PROPERTY_VALUE_NOT_EMPTY + "$" + propertyModel.getLabel());
              }
              if (text.getDriver().getMin() != null
                  && text.getDriver().getMin() > text.getValue().length()) {
                throw new NegocioException(
                  PROPERTY_VALUE_NOT_MIN + "$" + propertyModel.getLabel());
              }
              if (text.getDriver().getMax() != null
                  && text.getDriver().getMax() < text.getValue().length()) {
                throw new NegocioException(
                  PROPERTY_VALUE_NOT_MAX + "$" + propertyModel.getLabel());
              }
            }
            break;
          case TYPE_MODEL_NAME_DATE:
            final Date date = (Date) property;
            if (date.getDriver().getId().equals(propertyModel.getId())) {
              propertyModelFound = true;
              if (date.getDriver().isRequired() && date.getValue() == null) {
                throw new NegocioException(
                  PROPERTY_VALUE_NOT_NULL + "$" + propertyModel.getLabel());
              }
              if (date.getDriver().getMin() != null
                  && date.getDriver().getMin().isAfter(date.getValue())) {
                throw new NegocioException(
                  PROPERTY_VALUE_NOT_MIN + "$" + propertyModel.getLabel());
              }
              if (date.getDriver().getMax() != null
                  && date.getDriver().getMax().isBefore(date.getValue())) {
                throw new NegocioException(
                  PROPERTY_VALUE_NOT_MAX + "$" + propertyModel.getLabel());
              }
            }
            break;
          case TYPE_MODEL_NAME_TOGGLE:
            final Toggle toggle = (Toggle) property;
            if (toggle.getDriver().getId().equals(propertyModel.getId())) {
              propertyModelFound = true;
            }
            break;
          case TYPE_MODEL_NAME_UNIT_SELECTION:
            final UnitSelection unitSelection = (UnitSelection) property;
            if (unitSelection.getDriver().getId().equals(propertyModel.getId())) {
              propertyModelFound = true;
              if (unitSelection.getDriver().isRequired() && unitSelection.getValue() == null) {
                throw new NegocioException(
                  PROPERTY_VALUE_NOT_NULL + "$" + propertyModel.getLabel());
              }
            }
            break;
          case TYPE_MODEL_NAME_SELECTION:
            final Selection selection = (Selection) property;
            if (selection.getDriver().getId().equals(propertyModel.getId())) {
              propertyModelFound = true;
              if (selection.getDriver().isRequired()
                  && (selection.getValue() == null || selection.getValue().isEmpty())) {
                throw new NegocioException(
                  PROPERTY_VALUE_NOT_NULL + "$" + propertyModel.getLabel());
              }
            }
            break;
          case TYPE_MODEL_NAME_TEXT_AREA:
            final TextArea textArea = (TextArea) property;
            if (textArea.getDriver().getId().equals(propertyModel.getId())) {
              propertyModelFound = true;
              if (textArea.getDriver().isRequired()
                  && (textArea.getValue() == null || textArea.getValue().isEmpty())) {
                throw new NegocioException(
                  PROPERTY_VALUE_NOT_EMPTY + "$" + propertyModel.getLabel());
              }
              if (textArea.getDriver().getMin() != null
                  && textArea.getDriver().getMin() > textArea.getValue().length()) {
                throw new NegocioException(
                  PROPERTY_VALUE_NOT_MIN + "$" + propertyModel.getLabel());
              }
              if (textArea.getDriver().getMax() != null
                  && textArea.getDriver().getMax() < textArea.getValue().length()) {
                throw new NegocioException(
                  PROPERTY_VALUE_NOT_MAX + "$" + propertyModel.getLabel());
              }
            }
            break;
          case TYPE_MODEL_NAME_NUMBER:
            final Number decimal = (Number) property;
            if (decimal.getDriver().getId().equals(propertyModel.getId())) {
              propertyModelFound = true;
              if (decimal.getDriver().isRequired() && decimal.getValue() == null) {
                throw new NegocioException(PROPERTY_VALUE_NOT_NULL + "$" + propertyModel.getLabel());
              }
              if (decimal.getDriver().getMin() != null
                  && decimal.getDriver().getMin() > decimal.getValue()) {
                throw new NegocioException(PROPERTY_VALUE_NOT_MIN + "$" + propertyModel.getLabel());
              }
              if (decimal.getDriver().getMax() != null
                  && decimal.getDriver().getMax() < decimal.getValue()) {
                throw new NegocioException(PROPERTY_VALUE_NOT_MAX + "$" + propertyModel.getLabel());
              }
            }
            break;
          case TYPE_MODEL_NAME_CURRENCY:
            final Currency currency = (Currency) property;
            if (currency.getDriver().getId().equals(propertyModel.getId())) {
              propertyModelFound = true;
              if (currency.getDriver().isRequired() && currency.getValue() == null) {
                throw new NegocioException(PROPERTY_VALUE_NOT_NULL + "$" + propertyModel.getLabel());
              }
            }
            break;
          case TYPE_MODEL_NAME_LOCALITY_SELECTION:
            final LocalitySelection localitySelection = (LocalitySelection) property;
            if (localitySelection.getDriver().getId().equals(propertyModel.getId())) {
              propertyModelFound = true;
              if (localitySelection.getDriver().isRequired() && (localitySelection.getValue() == null
                                                                 || localitySelection.getValue().isEmpty())) {
                throw new NegocioException(PROPERTY_VALUE_NOT_NULL + "$" + propertyModel.getLabel());
              }
            }
            break;
          case TYPE_MODEL_NAME_ORGANIZATION_SELECTION:
            final OrganizationSelection organizationSelection = (OrganizationSelection) property;
            if (organizationSelection.getDriver().getId().equals(propertyModel.getId())) {
              propertyModelFound = true;
              if (organizationSelection.getDriver().isRequired()
                  && (organizationSelection.getValue() == null
                      || organizationSelection.getValue().isEmpty())) {
                throw new NegocioException(PROPERTY_VALUE_NOT_NULL + "$" + propertyModel.getLabel());
              }
            }
            break;
        }
        if (propertyModelFound) {
          break;
        }
      }
    }
    if (!propertyModelFound && propertyModel.isRequired()) {
      throw new NegocioException(PROPERTY_REQUIRED_NOT_FOUND + "$" + propertyModel.getLabel());
    }
  }

  private static Set<Property> extractGroupedPropertyIfExists(final Collection<? extends Property> propertiesToDelete) {
    return propertiesToDelete.stream().filter(Group.class::isInstance).collect(Collectors.toSet());
  }

  public List<Workpack> findAllUsingParent(
    final Long idPlan,
    final Long idPlanModel,
    final Long idWorkpackModel,
    final Long idWorkpackParent,
    final Long idFilter,
    final boolean workpackLinked
  ) {

    if (idFilter == null) {
      return this.findAllUsingParent(
        idPlan,
        idWorkpackModel,
        idWorkpackParent,
        workpackLinked
      );
    }

    final CustomFilter filter = this.findCustomFilterById(idFilter);

    final Map<String, Object> params = new HashMap<>();
    params.put(
      "idPlan",
      idPlan
    );
    params.put(
      "idPlanModel",
      idPlanModel
    );
    params.put(
      "idWorkPackModel",
      idWorkpackModel
    );
    params.put(
      "idWorkPackParent",
      idWorkpackParent
    );

    final List<Workpack> workpacks = this.findAllWorkpackByParent.execute(
      filter,
      params
    );

    return this.workpackSorter.sort(new WorkpackSorter.WorkpackSorterRequest(
      idPlan,
      idPlanModel,
      idWorkpackModel,
      idFilter,
      workpacks
    ));
  }

  public List<Workpack> findAllUsingParent(
    final Long idPlan,
    final Long idWorkPackModel,
    final Long idWorkPackParent,
    final boolean workpackLinked
  ) {
    if (workpackLinked) {
      return this.workpackRepository.findAllUsingParentLinked(
        idWorkPackModel,
        idWorkPackParent,
        idPlan
      );
    }

    return this.workpackRepository.findAllUsingParent(
      idWorkPackModel,
      idWorkPackParent,
      idPlan
    );
  }

  private CustomFilter findCustomFilterById(final Long idFilter) {
    return this.customFilterRepository.findByIdWithRelationships(idFilter)
      .orElseThrow(() -> new NegocioException(CUSTOM_FILTER_NOT_FOUND));
  }

  public WorkpackModel getWorkpackModelById(final Long idWorkpackModel) {
    return this.workpackModelService.findById(idWorkpackModel);
  }

  public List<Workpack> findAll(
    final Long idPlan,
    final Long idPlanModel,
    final Long idWorkpackModel,
    final Long idFilter
  ) {
    if (idFilter == null) {
      return this.findAll(
        idPlan,
        idPlanModel,
        idWorkpackModel
      );
    }

    final CustomFilter filter = this.findCustomFilterById(idFilter);

    final Map<String, Object> params = new HashMap<>();
    params.put(
      "idPlan",
      idPlan
    );
    params.put(
      "idPlanModel",
      idPlanModel
    );
    params.put(
      "idWorkPackModel",
      idWorkpackModel
    );

    final List<Workpack> workpacks = this.findAllWorkpack.execute(
      filter,
      params
    );
    return this.workpackSorter.sort(new WorkpackSorter.WorkpackSorterRequest(
      idPlan,
      idPlanModel,
      idWorkpackModel,
      idFilter,
      workpacks
    ));
  }

  public List<Workpack> findAll(
    final Long idPlan,
    final Long idPlanModel,
    final Long idWorkPackModel
  ) {
    return this.workpackRepository.findAll(
      idPlan,
      idPlanModel,
      idWorkPackModel
    );
  }

  public void saveDefault(final Workpack workpack) {
    this.save(workpack);
  }

  private Workpack save(final Workpack workpack) {
    return this.workpackRepository.save(workpack);
  }

  public EntityDto save(
    final Workpack workpack,
    final Long idPlan,
    final Long idParent
  ) {
    this.ifNewWorkpackAddRelationship(
      workpack,
      idPlan,
      idParent
    );
    validateWorkpack(workpack);
    this.save(workpack);
    return EntityDto.of(workpack);
  }

  private void ifNewWorkpackAddRelationship(
    final Workpack workpack,
    final Long idPlan,
    final Long idParent
  ) {
    if (idPlan != null) {
      final BelongsTo belongsTo = new BelongsTo();
      belongsTo.setLinked(false);
      belongsTo.setPlan(this.planService.findById(idPlan));
      belongsTo.setWorkpack(workpack);
      this.belongsToRepository.save(belongsTo);
    }
    if (idParent != null) {
      final Workpack parent = this.findById(idParent);
      parent.addChildren(workpack);
      this.workpackRepository.save(parent);
    }
  }

  public Workpack findById(final Long id) {
    return this.workpackRepository.findByIdWorkpack(id)
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
  }

  public Workpack update(final Workpack workpack) {
    final Long workpackId = workpack.getId();
    final Workpack workpackUpdate = this.findById(workpackId);
    final Set<Property> propertiesToUpdate = workpackUpdate.getProperties();
    final Set<Property> properties = workpack.getProperties();
    this.verifyForPropertiesToDelete(
      propertiesToUpdate,
      properties
    );
    final Predicate<Long> dateHasChanged = this.verifyForPropertiesToUpdate(
      workpack,
      () -> workpackUpdate.setProperties(new HashSet<>()),
      propertiesToUpdate,
      properties
    );
    validateWorkpack(workpackUpdate);
    final Workpack savedWorkpack = this.save(workpackUpdate);
    if (workpack instanceof Milestone) {
      final boolean concluded = milestoneRepository.isConcluded(workpackId);
      final boolean onActualBaseline = milestoneRepository.isOnActualBaseline(workpackId);
      savedWorkpack.setReasonRequired(!concluded && onActualBaseline && dateHasChanged.test(workpackId));
    } else {
      savedWorkpack.setReasonRequired(false);
    }
    savedWorkpack.setNewDate(workpack.getNewDate());
    savedWorkpack.setPreviousDate(workpack.getPreviousDate());
    return savedWorkpack;
  }

  private Predicate<Long> verifyForPropertiesToUpdate(
    final Workpack workpack,
    final Runnable createPropertyList,
    final Collection<Property> propertiesToUpdate,
    final Collection<? extends Property> properties
  ) {
    if (properties == null || properties.isEmpty()) {
      return alwaysFalse -> false;
    }
    Predicate<Long> hasAnyDateChanged = alwaysFalse -> false;
    for (final Property property : properties) {
      if (property.getId() == null) {
        if (propertiesToUpdate == null) {
          createPropertyList.run();
        }
        Objects.requireNonNull(
          propertiesToUpdate,
          "Propriedades para atualizar são nulas!"
        );
        propertiesToUpdate.add(property);
        continue;
      }
      if (propertiesToUpdate != null) {
        final Optional<Property> maybeProperty = propertiesToUpdate.stream()
          .filter(p -> p.getId() != null && p.getId().equals(property.getId()))
          .findFirst();
        if (maybeProperty.isPresent()) {
          final Property propertyToUpdate = maybeProperty.get();
          final Predicate<Long> hasDateChanged = this.loadPropertyUpdate(
            workpack,
            propertyToUpdate,
            property
          );
          hasAnyDateChanged = hasAnyDateChanged.or(hasDateChanged);
        }
      }
    }
    return hasAnyDateChanged;
  }

  private void verifyForPropertiesToDelete(
    final Collection<? extends Property> propertiesToUpdate,
    final Collection<? extends Property> properties
  ) {
    if (propertiesToUpdate != null && !propertiesToUpdate.isEmpty()) {
      final Set<Property> propertiesToDelete = propertiesToUpdate.stream()
        .filter(property -> properties == null || properties.stream()
          .noneMatch(p -> p.getId() != null && p.getId().equals(property.getId())))
        .collect(Collectors.toSet());
      if (!propertiesToDelete.isEmpty()) {
        this.verifyForGroupedPropertiesToDelete(propertiesToDelete);
        this.propertyService.delete(propertiesToDelete);
      }
    }
  }

  private Predicate<Long> loadPropertyUpdate(
    final Workpack workpack,
    final Property propertyToUpdate,
    final Property property
  ) {
    if (!property.getClass().getTypeName().equals(propertyToUpdate.getClass().getTypeName())) {
      throw new NegocioException(PROPERTY_UPDATE_TYPE_ERROR);
    }
    Predicate<Long> hasDataChanged = alwaysFalse -> false;
    switch (propertyToUpdate.getClass().getTypeName()) {
      case TYPE_MODEL_NAME_INTEGER:
        final HasValue<Long> integerUpdate = (Integer) propertyToUpdate;
        final Integer integer = (Integer) property;
        integerUpdate.setValue(integer.getValue());
        break;
      case TYPE_MODEL_NAME_TEXT:
        final HasValue<String> textUpdate = (Text) propertyToUpdate;
        final Text text = (Text) property;
        textUpdate.setValue(text.getValue());
        break;
      case TYPE_MODEL_NAME_DATE:
        final HasValue<LocalDateTime> dateUpdate = (Date) propertyToUpdate;
        final Date date = (Date) property;
        final LocalDateTime newDate = date.getValue();
        final LocalDateTime previousDate = dateUpdate.getValue();
        if (workpack != null) {
          workpack.setNewDate(newDate.toLocalDate());
          workpack.setPreviousDate(previousDate.toLocalDate());
        }
        final boolean dateChanged =
          !previousDate.toLocalDate().isEqual(newDate.toLocalDate()) && !LocalDate.now().isEqual(newDate.toLocalDate());
        hasDataChanged = idWorkpack ->
          dateChanged &&
          milestoneRepository.hasBaselineDateChanged(
            idWorkpack,
            newDate
          );
        dateUpdate.setValue(newDate);
        break;
      case TYPE_MODEL_NAME_TOGGLE:
        final HasValue<Boolean> toggleUpdate = (Toggle) propertyToUpdate;
        final Toggle toggle = (Toggle) property;
        toggleUpdate.setValue(toggle.getValue());
        break;
      case TYPE_MODEL_NAME_UNIT_SELECTION:
        final HasValue<UnitMeasure> unitSelectionUpdate = (UnitSelection) propertyToUpdate;
        final UnitSelection unitSelection = (UnitSelection) property;
        unitSelectionUpdate.setValue(unitSelection.getValue());
        break;
      case TYPE_MODEL_NAME_SELECTION:
        final HasValue<String> selectionUpdate = (Selection) propertyToUpdate;
        final Selection selection = (Selection) property;
        selectionUpdate.setValue(selection.getValue());
        break;
      case TYPE_MODEL_NAME_TEXT_AREA:
        final HasValue<String> textAreaUpdate = (TextArea) propertyToUpdate;
        final TextArea textArea = (TextArea) property;
        textAreaUpdate.setValue(textArea.getValue());
        break;
      case TYPE_MODEL_NAME_NUMBER:
        final HasValue<Double> decimalUpdate = (Number) propertyToUpdate;
        final Number decimal = (Number) property;
        decimalUpdate.setValue(decimal.getValue());
        break;
      case TYPE_MODEL_NAME_CURRENCY:
        final HasValue<BigDecimal> currencyUpdate = (Currency) propertyToUpdate;
        final Currency currency = (Currency) property;
        currencyUpdate.setValue(currency.getValue());
        break;
      case TYPE_MODEL_NAME_LOCALITY_SELECTION:
        final HasValue<Set<Locality>> localitySelectionUpdate = (LocalitySelection) propertyToUpdate;
        final LocalitySelection localitySelection = (LocalitySelection) property;
        localitySelectionUpdate.setValue(localitySelection.getValue());
        break;
      case TYPE_MODEL_NAME_ORGANIZATION_SELECTION:
        final HasValue<Set<Organization>> organizationSelectionUpdate = (OrganizationSelection) propertyToUpdate;
        final OrganizationSelection organizationSelection = (OrganizationSelection) property;
        organizationSelectionUpdate.setValue(organizationSelection.getValue());
        break;
      case TYPE_MODEL_NAME_GROUP:
        final Group groupToUpdate = (Group) propertyToUpdate;
        final Group group = (Group) property;
        final Set<Property> groupedPropertiesToUpdate = groupToUpdate.getGroupedProperties();
        final Set<Property> groupedProperties = group.getGroupedProperties();
        this.verifyForPropertiesToDelete(
          groupedPropertiesToUpdate,
          groupedProperties
        );
        this.verifyForPropertiesToUpdate(
          null,
          () -> groupToUpdate.setGroupedProperties(new HashSet<>()),
          groupedPropertiesToUpdate,
          groupedProperties
        );
        break;
    }
    return hasDataChanged;
  }

  public Workpack findByIdDefault(final Long id) {
    return this.workpackRepository.findById(id)
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
  }

  public Workpack findByIdWithParent(final Long id) {
    return this.workpackRepository.findByIdWithParent(id)
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
  }

  public WorkpackDetailDto getWorkpackDetailDto(final Workpack workpack) {
    Set<WorkpackDetailDto> children = null;
    Set<Workpack> child = null;
    Set<Property> propertySet = null;
    List<? extends PropertyDto> properties = null;
    if (workpack.getChildren() != null) {
      child = new HashSet<>(workpack.getChildren());
      children = this.getChildren(workpack.getChildren());
    }
    Set<CostAccountDto> costs = null;
    if (workpack.getCosts() != null) {
      costs = new HashSet<>();
      for (final CostAccount costAccount : workpack.getCosts()) {
        costs.add(CostAccountDto.of(costAccount));
      }
    }
    if (workpack.getProperties() != null && !(workpack.getProperties()).isEmpty()) {
      properties = this.getPropertiesDto(workpack.getProperties());
      propertySet = new HashSet<>(workpack.getProperties());
      workpack.setProperties(null);
    }
    workpack.setChildren(null);
    final WorkpackDetailDto detailDto = this.convertWorkpackDetailDto(workpack);
    if (detailDto != null) {
      final PlanDto plan = this.findNotLinkedBelongsTo(workpack);
      detailDto.setPlan(plan);
      detailDto.setChildren(children);
      detailDto.setCosts(costs);
      detailDto.setProperties(properties);
      addSharedWith(
        workpack,
        detailDto
      );
    }
    workpack.setChildren(child);
    workpack.setProperties(propertySet);
    return detailDto;
  }

  public Optional<WorkpackName> findWorkpackNameAndFullname(final Long idWorkpack) {
    return this.workpackRepository.findWorkpackNameAndFullname(idWorkpack);
  }

  private WorkpackDetailDto convertWorkpackDetailDto(final Workpack workpack) {
    WorkpackModel workpackModel = null;
    WorkpackDetailDto workpackDetailDto = null;
    final String typeName = workpack.getClass().getTypeName();
    switch (typeName) {
      case TYPE_NAME_PORTFOLIO:
        workpackModel = ((Portfolio) workpack).getInstance();
        workpackDetailDto = PortfolioDetailDto.of(workpack);
        break;
      case TYPE_NAME_PROGRAM:
        workpackModel = ((Program) workpack).getInstance();
        workpackDetailDto = ProgramDetailDto.of(workpack);
        break;
      case TYPE_NAME_ORGANIZER:
        workpackModel = ((Organizer) workpack).getInstance();
        workpackDetailDto = OrganizerDetailDto.of(workpack);
        break;
      case TYPE_NAME_DELIVERABLE:
        workpackModel = ((Deliverable) workpack).getInstance();
        workpackDetailDto = DeliverableDetailDto.of(workpack);
        break;
      case TYPE_NAME_PROJECT:
        workpackModel = ((Project) workpack).getInstance();
        workpackDetailDto = ProjectDetailDto.of(workpack);
        break;
      case TYPE_NAME_MILESTONE:
        workpackModel = ((Milestone) workpack).getInstance();
        workpackDetailDto = MilestoneDetailDto.of(workpack);
        this.milestoneService.addDate(
          workpack.getId(),
          (MilestoneDetailDto) workpackDetailDto
        );
        this.milestoneService.addStatus(
          workpack.getId(),
          (MilestoneDetailDto) workpackDetailDto
        );
        break;
    }
    if (workpackDetailDto != null) {
      this.applyBaselineStatus(
        typeName,
        workpackDetailDto
      );
      workpackDetailDto.setCanceled(workpack.isCanceled());
      workpackDetailDto.setCancelable(this.isCancelable(workpack));
      workpackDetailDto.setCanBeDeleted(this.workpackRepository.canBeDeleted(workpack.getId()));
      workpackDetailDto.setHasScheduleSectionActive(this.hasScheduleSectionActive(workpack));
      if (workpackModel != null) {
        workpackDetailDto.setModel(this.workpackModelService.getWorkpackModelDetailDto(workpackModel));
      }
      return workpackDetailDto;
    }
    return null;
  }

  private boolean hasScheduleSectionActive(final Workpack workpack) {
    return this.hasScheduleSessionActive.execute(workpack.getId());
  }

  private boolean isCancelable(final Workpack workpack) {
    if (workpack.isProject()) {
      return false;
    }
    if (workpack.isCanceled()) {
      return false;
    }
    if (this.workpackRepository.hasChildrenWithActiveBaseline(workpack.getId())) {
      return false;
    }
    return this.workpackRepository.isPresentInBaseline(workpack.getId());
  }

  private void applyBaselineStatus(
    final String type,
    final WorkpackDetailDto workpackDetailDto
  ) {
    workpackDetailDto.setHasActiveBaseline(false);
    workpackDetailDto.setPendingBaseline(false);
    final Long idWorkpack = workpackDetailDto.getId();
    if (type.equals(TYPE_NAME_PROJECT)) {
      final boolean hasActiveBaseline = this.workpackRepository.hasActiveBaseline(idWorkpack);
      final boolean pendingBaseline = this.workpackRepository.hasProposedBaseline(idWorkpack);
      final boolean cancelPropose = this.workpackRepository.hasCancelPropose(idWorkpack);
      workpackDetailDto.setHasActiveBaseline(hasActiveBaseline);
      workpackDetailDto.setPendingBaseline(pendingBaseline);
      workpackDetailDto.setCancelPropose(cancelPropose);
      if (hasActiveBaseline) {
        this.workpackRepository.findActiveBaseline(idWorkpack)
          .map(Baseline::getName)
          .ifPresent(workpackDetailDto::setActiveBaselineName);
      }
      return;
    }
    final List<Baseline> activeBaselines =
      type.equals(TYPE_NAME_DELIVERABLE) || type.equals(TYPE_NAME_MILESTONE)
        ? this.workpackRepository.findActiveBaselineFromProjectChildren(idWorkpack)
        : this.workpackRepository.findActiveBaselineFromProjectParent(idWorkpack);
    activeBaselines.stream().findFirst().ifPresent(activeBaseline -> {
      workpackDetailDto.setActiveBaselineName(activeBaseline.getName());
      workpackDetailDto.setHasActiveBaseline(true);
    });
  }

  public void delete(final Workpack workpack) {
    if (workpack.getChildren() != null && !(workpack.getChildren()).isEmpty()) {
      throw new NegocioException(WORKPACK_DELETE_RELATIONSHIP_ERROR);
    }
    if (this.hasSnapshot(workpack)) {
      this.updateWorkpackDeleteStatus(workpack);
      return;
    }
    this.verifyForGroupedPropertiesToDelete(workpack.getProperties());
    this.journalDeleter.deleteJournalsByWorkpackId(workpack.getId());
    this.workpackRepository.delete(workpack);
  }

  private boolean hasSnapshot(final Workpack workpack) {
    return this.workpackRepository.hasSnapshot(workpack.getId());
  }

  private void updateWorkpackDeleteStatus(final Workpack workpack) {
    workpack.setDeleted(true);
    this.workpackRepository.save(workpack);
  }

  private void verifyForGroupedPropertiesToDelete(final Collection<? extends Property> propertiesToDelete) {
    final Set<Property> groupedProperties = extractGroupedPropertyIfExists(propertiesToDelete);
    if (!groupedProperties.isEmpty()) {
      final Set<Property> groupedPropertiesToDelete = new HashSet<>();
      for (final Property property : groupedProperties) {
        groupedPropertiesToDelete.addAll(((Group) property).getGroupedProperties());
      }
      this.propertyService.delete(groupedPropertiesToDelete);
    }
  }

  public Workpack getWorkpack(final WorkpackParamDto workpackParamDto) {
    Workpack workpack = null;
    Set<Property> properties = null;
    if (workpackParamDto.getProperties() != null && !workpackParamDto.getProperties().isEmpty()) {
      properties = this.getProperties(workpackParamDto.getProperties());
    }
    workpackParamDto.setProperties(null);
    final WorkpackModel workpackModel = this.workpackModelService.findById(workpackParamDto.getIdWorkpackModel());
    switch (workpackParamDto.getClass().getTypeName()) {
      case "br.gov.es.openpmo.dto.workpack.PortfolioParamDto":
        workpack = this.modelMapper.map(
          workpackParamDto,
          Portfolio.class
        );
        ((Portfolio) workpack).setInstance((PortfolioModel) workpackModel);
        break;
      case "br.gov.es.openpmo.dto.workpack.ProgramParamDto":
        workpack = this.modelMapper.map(
          workpackParamDto,
          Program.class
        );
        ((Program) workpack).setInstance((ProgramModel) workpackModel);
        break;
      case "br.gov.es.openpmo.dto.workpack.OrganizerParamDto":
        workpack = this.modelMapper.map(
          workpackParamDto,
          Organizer.class
        );
        ((Organizer) workpack).setInstance((OrganizerModel) workpackModel);
        break;
      case "br.gov.es.openpmo.dto.workpack.DeliverableParamDto":
        workpack = this.modelMapper.map(
          workpackParamDto,
          Deliverable.class
        );
        ((Deliverable) workpack).setInstance((DeliverableModel) workpackModel);
        break;
      case "br.gov.es.openpmo.dto.workpack.ProjectParamDto":
        workpack = this.modelMapper.map(
          workpackParamDto,
          Project.class
        );
        ((Project) workpack).setInstance((ProjectModel) workpackModel);
        break;
      case "br.gov.es.openpmo.dto.workpack.MilestoneParamDto":
        workpack = this.modelMapper.map(
          workpackParamDto,
          Milestone.class
        );
        ((Milestone) workpack).setInstance((MilestoneModel) workpackModel);
        break;
    }
    if (workpack != null) {
      workpack.setProperties(properties);
    }
    return workpack;
  }

  public WorkpackDetailDto getWorkpackDetailDto(
    final Workpack workpack,
    final Long idPlan
  ) {
    final WorkpackDetailDto workpackDetailDto = this.getWorkpackDetailDto(workpack);
    if (idPlan != null) {
      final Plan plan = this.planService.findById(idPlan);
      workpackDetailDto.setPlan(PlanDto.of(plan));
    }
    return workpackDetailDto;
  }

  private PlanDto findNotLinkedBelongsTo(final Workpack workpack) {
    final Plan plan = this.planService.findNotLinkedBelongsTo(workpack.getId());
    return PlanDto.of(plan);
  }

  private List<? extends PropertyDto> getPropertiesDto(final Collection<? extends Property> properties) {
    if (properties != null && !properties.isEmpty()) {
      final List<PropertyDto> list = new ArrayList<>();
      properties.parallelStream().forEach(property -> {
        final String typeName = property.getClass().getTypeName();
        switch (typeName) {
          case TYPE_MODEL_NAME_INTEGER:
            final IntegerDto integerDto = IntegerDto.of(property);
            if (((Integer) property).getDriver() != null) {
              integerDto.setIdPropertyModel(((Integer) property).getDriver().getId());
            }
            list.add(integerDto);
            break;
          case TYPE_MODEL_NAME_TEXT:
            final TextDto textDto = TextDto.of(property);
            if (((Text) property).getDriver() != null) {
              textDto.setIdPropertyModel(((Text) property).getDriver().getId());
            }
            list.add(textDto);
            break;
          case TYPE_MODEL_NAME_DATE:
            final DateDto dateDto = DateDto.of(property);
            if (((Date) property).getDriver() != null) {
              dateDto.setIdPropertyModel(((Date) property).getDriver().getId());
            }
            list.add(dateDto);
            break;
          case TYPE_MODEL_NAME_TOGGLE:
            final ToggleDto toggleDto = ToggleDto.of(property);
            if (((Toggle) property).getDriver() != null) {
              toggleDto.setIdPropertyModel(((Toggle) property).getDriver().getId());
            }
            list.add(toggleDto);
            break;
          case TYPE_MODEL_NAME_UNIT_SELECTION:
            final UnitSelectionDto unitSelectionDto = UnitSelectionDto.of(property);
            if (((UnitSelection) property).getDriver() != null) {
              unitSelectionDto.setIdPropertyModel(((UnitSelection) property).getDriver().getId());
            }
            if (((UnitSelection) property).getValue() != null) {
              unitSelectionDto.setSelectedValue(((UnitSelection) property).getValue().getId());
            }
            list.add(unitSelectionDto);
            break;
          case TYPE_MODEL_NAME_SELECTION:
            final SelectionDto selectionDto = SelectionDto.of(property);
            if (((Selection) property).getDriver() != null) {
              selectionDto.setIdPropertyModel(((Selection) property).getDriver().getId());
            }
            list.add(selectionDto);
            break;
          case TYPE_MODEL_NAME_TEXT_AREA:
            final TextAreaDto textAreaDto = TextAreaDto.of(property);
            if (((TextArea) property).getDriver() != null) {
              textAreaDto.setIdPropertyModel(((TextArea) property).getDriver().getId());
            }
            list.add(textAreaDto);
            break;
          case TYPE_MODEL_NAME_NUMBER:
            final NumberDto numberDto = NumberDto.of(property);
            if (((Number) property).getDriver() != null) {
              numberDto.setIdPropertyModel(((Number) property).getDriver().getId());
            }
            list.add(numberDto);
            break;
          case TYPE_MODEL_NAME_CURRENCY:
            final CurrencyDto currencyDto = CurrencyDto.of(property);
            if (((Currency) property).getDriver() != null) {
              currencyDto.setIdPropertyModel(((Currency) property).getDriver().getId());
            }
            list.add(currencyDto);
            break;
          case TYPE_MODEL_NAME_LOCALITY_SELECTION:
            final LocalitySelectionDto localitySelectionDto = LocalitySelectionDto.of(property);
            if (((LocalitySelection) property).getDriver() != null) {
              localitySelectionDto.setIdPropertyModel(((LocalitySelection) property).getDriver().getId());
            }
            final Set<Locality> localities = ((LocalitySelection) property).getValue();
            if (localities != null) {
              localitySelectionDto.setSelectedValues(new HashSet<>());
              localities.forEach(o -> localitySelectionDto.getSelectedValues().add(o.getId()));
            }
            list.add(localitySelectionDto);
            break;
          case TYPE_MODEL_NAME_ORGANIZATION_SELECTION:
            final OrganizationSelectionDto organizationSelectionDto =
              OrganizationSelectionDto.of(property);
            if (((OrganizationSelection) property).getDriver() != null) {
              organizationSelectionDto
                .setIdPropertyModel(((OrganizationSelection) property).getDriver().getId());
            }
            final Set<Organization> organizations = ((OrganizationSelection) property).getValue();
            if (organizations != null) {
              organizationSelectionDto.setSelectedValues(new HashSet<>());
              organizations.forEach(o -> organizationSelectionDto.getSelectedValues().add(o.getId()));
            }
            list.add(organizationSelectionDto);
            break;
          case TYPE_MODEL_NAME_GROUP:
            final GroupDto groupDto = (GroupDto) GroupDto.of(property);
            if (((Group) property).getDriver() != null) {
              groupDto.setIdPropertyModel(((Group) property).getDriver().getId());
            }
            final List<? extends PropertyDto> groupedPropertiesDto = this
              .getPropertiesDto(((Group) property).getGroupedProperties());
            groupDto.setGroupedProperties(groupedPropertiesDto);
            list.add(groupDto);
            break;
        }
      });
      return list;
    }
    return null;
  }

  public Set<WorkpackDetailDto> getChildren(final Collection<? extends Workpack> childrens) {
    if (childrens != null && !childrens.isEmpty()) {
      final Set<WorkpackDetailDto> workpacksDetail = new HashSet<>();
      childrens.parallelStream().forEach(workpack -> {
        Set<WorkpackDetailDto> childrenChild = null;
        Set<Workpack> child = null;
        Set<Property> propertySet = null;
        List<? extends PropertyDto> properties = null;
        if (workpack.getChildren() != null && !workpack.getChildren().isEmpty()) {
          child = new HashSet<>(workpack.getChildren());
          childrenChild = this.getChildren(workpack.getChildren());
        }
        workpack.setChildren(null);
        if (workpack.getProperties() != null && !(workpack.getProperties()).isEmpty()) {
          properties = this.getPropertiesDto(workpack.getProperties());
          propertySet = new HashSet<>(workpack.getProperties());
          workpack.setProperties(null);
        }
        final WorkpackDetailDto detailDto = this.convertWorkpackDetailDto(workpack);
        if (detailDto != null) {
          detailDto.setChildren(childrenChild);
          detailDto.setProperties(properties);
          addSharedWith(
            workpack,
            detailDto
          );
          workpacksDetail.add(detailDto);
        }
        workpack.setProperties(propertySet);
        workpack.setChildren(child);
      });

      return workpacksDetail;
    }
    return null;
  }

  public Set<Property> getProperties(final Iterable<? extends PropertyDto> properties) {
    final Set<Property> propertiesExtracted = new HashSet<>();
    properties.forEach(property -> this.extractProperty(
      propertiesExtracted,
      property
    ));
    return propertiesExtracted;
  }

  private void extractProperty(
    final Collection<? super Property> properties,
    final PropertyDto propertyDto
  ) {
    if (propertyDto.getIdPropertyModel() == null) {
      throw new NegocioException(PROPERTY_RELATIONSHIP_MODEL_NOT_FOUND);
    }
    final PropertyModel propertyModel = this.propertyModelService.findById(propertyDto.getIdPropertyModel());
    switch (propertyDto.getClass().getTypeName()) {
      case "br.gov.es.openpmo.dto.workpack.IntegerDto":
        final Integer integer = this.modelMapper.map(
          propertyDto,
          Integer.class
        );
        integer.setDriver((IntegerModel) propertyModel);
        properties.add(integer);
        break;
      case "br.gov.es.openpmo.dto.workpack.TextDto":
        final Text text = this.modelMapper.map(
          propertyDto,
          Text.class
        );
        text.setDriver((TextModel) propertyModel);
        properties.add(text);
        break;
      case "br.gov.es.openpmo.dto.workpack.DateDto":
        final Date date = this.modelMapper.map(
          propertyDto,
          Date.class
        );
        date.setDriver((DateModel) propertyModel);
        properties.add(date);
        break;
      case "br.gov.es.openpmo.dto.workpack.ToggleDto":
        final Toggle toggle = this.modelMapper.map(
          propertyDto,
          Toggle.class
        );
        toggle.setDriver((ToggleModel) propertyModel);
        properties.add(toggle);
        break;
      case "br.gov.es.openpmo.dto.workpack.UnitSelectionDto":
        final UnitSelection unitSelection = this.modelMapper.map(
          propertyDto,
          UnitSelection.class
        );
        unitSelection.setDriver((UnitSelectionModel) propertyModel);
        final Long selectedValue = ((UnitSelectionDto) propertyDto).getSelectedValue();
        if (selectedValue != null) {
          unitSelection.setValue(this.unitMeasureService.findById(selectedValue));
        }
        properties.add(unitSelection);
        break;
      case "br.gov.es.openpmo.dto.workpack.SelectionDto":
        final Selection selection = this.modelMapper.map(
          propertyDto,
          Selection.class
        );
        selection.setDriver((SelectionModel) propertyModel);
        properties.add(selection);
        break;
      case "br.gov.es.openpmo.dto.workpack.TextAreaDto":
        final TextArea textArea = this.modelMapper.map(
          propertyDto,
          TextArea.class
        );
        textArea.setDriver((TextAreaModel) propertyModel);
        properties.add(textArea);
        break;
      case "br.gov.es.openpmo.dto.workpack.NumberDto":
        final Number number = this.modelMapper.map(
          propertyDto,
          Number.class
        );
        number.setDriver((NumberModel) propertyModel);
        properties.add(number);
        break;
      case "br.gov.es.openpmo.dto.workpack.CurrencyDto":
        final Currency currency = this.modelMapper.map(
          propertyDto,
          Currency.class
        );
        currency.setDriver((CurrencyModel) propertyModel);
        properties.add(currency);
        break;
      case "br.gov.es.openpmo.dto.workpack.LocalitySelectionDto":
        final LocalitySelection localitySelection = this.modelMapper.map(
          propertyDto,
          LocalitySelection.class
        );
        localitySelection.setDriver((LocalitySelectionModel) propertyModel);
        final Set<Long> localitySelectedValues = ((LocalitySelectionDto) propertyDto).getSelectedValues();
        if (localitySelectedValues != null && localitySelectedValues.stream().anyMatch(Objects::nonNull)) {
          localitySelection.setValue(new HashSet<>());
          localitySelectedValues.stream()
            .filter(Objects::nonNull)
            .forEach(id -> localitySelection.getValue().add(this.localityService.findById(id)));
        }
        properties.add(localitySelection);
        break;
      case "br.gov.es.openpmo.dto.workpack.OrganizationSelectionDto":
        final OrganizationSelection organizationSelection = this.modelMapper.map(
          propertyDto,
          OrganizationSelection.class
        );
        organizationSelection.setDriver((OrganizationSelectionModel) propertyModel);
        final Set<Long> organizationSelectedValues = ((OrganizationSelectionDto) propertyDto).getSelectedValues();
        if (organizationSelectedValues != null && organizationSelectedValues.stream().anyMatch(Objects::nonNull)) {
          organizationSelection.setValue(new HashSet<>());
          organizationSelectedValues.stream()
            .filter(Objects::nonNull)
            .forEach(id -> organizationSelection.getValue().add(this.organizationService.findById(id)));
        }
        properties.add(organizationSelection);
        break;
      case "br.gov.es.openpmo.dto.workpack.GroupDto":
        final GroupDto groupDto = (GroupDto) propertyDto;
        final GroupModel groupModel = (GroupModel) propertyModel;
        final Group group = new Group();
        group.setId(groupDto.getId());
        group.setDriver(groupModel);
        final Set<Property> groupedProperties = new HashSet<>();
        groupDto.getGroupedProperties().forEach(dto -> this.extractProperty(
          groupedProperties,
          dto
        ));
        group.setGroupedProperties(groupedProperties);
        properties.add(group);
        break;
    }
  }

  public Set<Long> findAllWorkpacksWithPermissions(
    final Long idPlan,
    final Long idUser
  ) {
    return this.workpackRepository.findAllWorkpacksWithPermissions(
      idPlan,
      idUser
    );
  }

  public Workpack cancel(final Long idWorkpack) {
    final Workpack workpack = this.workpackRepository.findByIdWithChildren(idWorkpack)
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
    final Set<Workpack> workpacks = new HashSet<>();
    this.addWorkpackRecursively(
      workpack,
      workpacks
    );
    workpacks.forEach(w -> w.setCanceled(true));
    this.workpackRepository.saveAll(workpacks);
    final Set<Workpack> parents = new HashSet<>();
    workpacks.stream().map(Entity::getId).map(this.workpackRepository::findParentsById).forEach(parents::addAll);
    parents.stream().map(Entity::getId).forEach(this.dashboardService::calculate);
    return workpack;
  }

  void addWorkpackRecursively(
    final Workpack workpack,
    final Set<Workpack> workpacks
  ) {
    workpacks.add(workpack);
    final Set<Workpack> children = workpack.getChildren();
    if (children == null || children.isEmpty()) {
      return;
    }
    for (final Workpack child : children) {
      this.addWorkpackRecursively(
        child,
        workpacks
      );
    }
  }

  public void restore(final Long idWorkpack) {
    final Workpack workpack = this.workpackRepository.findByIdWithChildren(idWorkpack)
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
    if (!workpack.isRestaurable()) {
      return;
    }
    final Set<Workpack> workpacks = new HashSet<>();
    this.addWorkpackRecursively(
      workpack,
      workpacks
    );
    workpacks.forEach(w -> w.setCanceled(false));
    this.workpackRepository.saveAll(workpacks);
    final Set<Workpack> parents = new HashSet<>();
    workpacks.stream().map(Entity::getId).map(this.workpackRepository::findParentsById).forEach(parents::addAll);
    parents.stream().map(Entity::getId).forEach(this.dashboardService::calculate);
  }

  public WorkpackModel findWorkpackModelLinked(
    final Long idWorkpack,
    final Long idPlan
  ) {
    return this.workpackRepository.findWorkpackModeLinkedByWorkpackAndPlan(
        idWorkpack,
        idPlan
      )
      .orElseThrow(() -> new NegocioException(WORKPACKMODEL_NOT_FOUND));
  }

  public Set<Workpack> findAllByIdPlan(final Long idPlan) {
    return this.workpackRepository.findAllByPlanWithProperties(idPlan);
  }

  public void calculateDashboard(final Workpack workpack) {
    final Long id = workpack.getId();
    final Set<Workpack> parents = this.workpackRepository.findParentsById(id);
    parents.stream().map(Entity::getId).forEach(this.dashboardService::calculate);
  }

}
