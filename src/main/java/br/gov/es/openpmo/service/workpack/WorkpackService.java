package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.costaccount.CostAccountDto;
import br.gov.es.openpmo.dto.plan.PlanDto;
import br.gov.es.openpmo.dto.workpack.*;
import br.gov.es.openpmo.dto.workpackshared.WorkpackSharedDto;
import br.gov.es.openpmo.enumerator.Session;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.properties.Currency;
import br.gov.es.openpmo.model.properties.Date;
import br.gov.es.openpmo.model.properties.Integer;
import br.gov.es.openpmo.model.properties.Number;
import br.gov.es.openpmo.model.properties.*;
import br.gov.es.openpmo.model.properties.models.*;
import br.gov.es.openpmo.model.relations.BelongsTo;
import br.gov.es.openpmo.model.workpacks.*;
import br.gov.es.openpmo.model.workpacks.models.*;
import br.gov.es.openpmo.repository.BelongsToRepository;
import br.gov.es.openpmo.repository.CustomFilterRepository;
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
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.*;
import static br.gov.es.openpmo.utils.PropertyModelInstanceType.*;
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

    private final CostAccountService costAccountService;

    private final OrganizationService organizationService;

    private final LocalityService localityService;

    private final UnitMeasureService unitMeasureService;

    private final CustomFilterRepository customFilterRepository;

    private final FindAllWorkpackUsingCustomFilter findAllWorkpack;

    private final FindAllWorkpackByParentUsingCustomFilter findAllWorkpackByParent;

    private final BelongsToRepository belongsToRepository;

    private final MilestoneService milestoneService;

    private final JournalDeleter journalDeleter;

    private final IAsyncDashboardService dashboardService;

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
            final CostAccountService costAccountService,
            final OrganizationService organizationService,
            final LocalityService localityService,
            final UnitMeasureService unitMeasureService,
            final FindAllWorkpackUsingCustomFilter findAllWorkpack,
            final BelongsToRepository belongsToRepository,
            final MilestoneService milestoneService,
            final JournalDeleter journalDeleter,
            IAsyncDashboardService dashboardService
    ) {
        this.workpackModelService = workpackModelService;
        this.planService = planService;
        this.modelMapper = modelMapper;
        this.propertyService = propertyService;
        this.workpackRepository = workpackRepository;
        this.propertyModelService = propertyModelService;
        this.customFilterRepository = customFilterRepository;
        this.findAllWorkpackByParent = findAllWorkpackByParent;
        this.costAccountService = costAccountService;
        this.organizationService = organizationService;
        this.localityService = localityService;
        this.unitMeasureService = unitMeasureService;
        this.findAllWorkpack = findAllWorkpack;
        this.belongsToRepository = belongsToRepository;
        this.milestoneService = milestoneService;
        this.journalDeleter = journalDeleter;
        this.dashboardService = dashboardService;
    }

    private static void addSharedWith(final Workpack workpack, final WorkpackDetailDto detailDto) {
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

    public static int compare(final Object a, final Object b) {
        if (a instanceof String && b instanceof String) {
            return ((String) a).compareTo((String) b);
        }
        if (a instanceof LocalDateTime && b instanceof LocalDateTime) {
            return ((LocalDateTime) a).compareTo((LocalDateTime) b);
        }
        if (a instanceof BigDecimal && b instanceof BigDecimal) {
            return ((BigDecimal) a).compareTo((BigDecimal) b);
        }
        if (a instanceof java.lang.Integer && b instanceof java.lang.Integer) {
            return ((java.lang.Integer) a).compareTo((java.lang.Integer) b);
        }
        if (a instanceof Long && b instanceof Long) {
            return ((Long) a).compareTo((Long) b);
        }
        if (a instanceof Boolean && b instanceof Boolean) {
            return ((Boolean) a).compareTo((Boolean) b);
        }
        if (a instanceof Double && b instanceof Double) {
            return ((Double) a).compareTo((Double) b);
        }
        return -1;
    }

    public static Object getValueProperty(final Workpack workpack, final PropertyModel sortBy) {
        final Property property = getPropertyByModel(workpack, sortBy);
        return getValueProperty(property);
    }

    private static Property getPropertyByModel(final Workpack workpack, final PropertyModel propertyModel) {
        if (workpack == null || CollectionUtils.isEmpty(workpack.getProperties())) {
            return null;
        }
        for (final Property property : workpack.getProperties()) {
            switch (property.getClass().getTypeName()) {
                case TYPE_MODEL_NAME_INTEGER:
                    final Integer integer = (Integer) property;
                    if (integer.getDriver() != null && integer.getDriver().getId().equals(propertyModel.getId())) {
                        return integer;
                    }
                    break;
                case TYPE_MODEL_NAME_TEXT:
                    final Text text = (Text) property;
                    if (text.getDriver() != null && text.getDriver().getId().equals(propertyModel.getId())) {
                        return text;
                    }
                    break;
                case TYPE_MODEL_NAME_DATE:
                    final Date date = (Date) property;
                    if (date.getDriver() != null && date.getDriver().getId().equals(propertyModel.getId())) {
                        return date;
                    }
                    break;
                case TYPE_MODEL_NAME_TOGGLE:
                    final Toggle toggle = (Toggle) property;
                    if (toggle.getDriver() != null && toggle.getDriver().getId().equals(propertyModel.getId())) {
                        return toggle;
                    }
                    break;
                case TYPE_MODEL_NAME_UNIT_SELECTION:
                    final UnitSelection unitSelection = (UnitSelection) property;
                    if (unitSelection.getDriver() != null
                            && unitSelection.getDriver().getId().equals(propertyModel.getId())) {
                        return unitSelection;
                    }
                    break;
                case TYPE_MODEL_NAME_SELECTION:
                    final Selection selection = (Selection) property;
                    if (selection.getDriver() != null && selection.getDriver().getId().equals(propertyModel.getId())) {
                        return selection;
                    }
                    break;
                case TYPE_MODEL_NAME_TEXT_AREA:
                    final TextArea textArea = (TextArea) property;
                    if (textArea.getDriver() != null && textArea.getDriver().getId().equals(propertyModel.getId())) {
                        return textArea;
                    }
                    break;
                case TYPE_MODEL_NAME_NUMBER:
                    final Number decimal = (Number) property;
                    if (decimal.getDriver() != null && decimal.getDriver().getId().equals(propertyModel.getId())) {
                        return decimal;
                    }
                    break;
                case TYPE_MODEL_NAME_CURRENCY:
                    final Currency currency = (Currency) property;
                    if (currency.getDriver() != null && currency.getDriver().getId().equals(propertyModel.getId())) {
                        return currency;
                    }
                    break;
                case TYPE_MODEL_NAME_LOCALITY_SELECTION:
                    final LocalitySelection localitySelection = (LocalitySelection) property;
                    if (localitySelection.getDriver() != null
                            && localitySelection.getDriver().getId().equals(propertyModel.getId())) {
                        return localitySelection;
                    }
                    break;
                case TYPE_MODEL_NAME_ORGANIZATION_SELECTION:
                    final OrganizationSelection organizationSelection = (OrganizationSelection) property;
                    if (organizationSelection.getDriver() != null
                            && organizationSelection.getDriver().getId().equals(propertyModel.getId())) {
                        return organizationSelection;
                    }
                    break;

            }
        }
        return null;
    }

    public static Object getValueProperty(final Property property) {
        Object object = null;
        if (property != null) {
            switch (property.getClass().getTypeName()) {
                case TYPE_MODEL_NAME_INTEGER:
                    final Integer integer = (Integer) property;
                    object = integer.getValue();
                    break;
                case TYPE_MODEL_NAME_TEXT:
                    final Text text = (Text) property;
                    object = text.getValue();
                    break;
                case TYPE_MODEL_NAME_DATE:
                    final Date date = (Date) property;
                    object = date.getValue();
                    break;
                case TYPE_MODEL_NAME_TOGGLE:
                    final Toggle toggle = (Toggle) property;
                    object = toggle.getValue();
                    break;
                case TYPE_MODEL_NAME_UNIT_SELECTION:
                    final UnitSelection unitSelection = (UnitSelection) property;
                    object = unitSelection.getValue();
                    break;
                case TYPE_MODEL_NAME_SELECTION:
                    final Selection selection = (Selection) property;
                    object = selection.getValue();
                    break;
                case TYPE_MODEL_NAME_TEXT_AREA:
                    final TextArea textArea = (TextArea) property;
                    object = textArea.getValue();
                    break;
                case TYPE_MODEL_NAME_NUMBER:
                    final Number decimal = (Number) property;
                    object = decimal.getValue();
                    break;
                case TYPE_MODEL_NAME_CURRENCY:
                    final Currency currency = (Currency) property;
                    object = currency.getValue();
                    break;
                case TYPE_MODEL_NAME_LOCALITY_SELECTION:
                    final LocalitySelection localitySelection = (LocalitySelection) property;
                    object = localitySelection.getValue();
                    break;
                case TYPE_MODEL_NAME_ORGANIZATION_SELECTION:
                    final OrganizationSelection organizationSelection = (OrganizationSelection) property;
                    object = organizationSelection.getValue();
                    break;
            }

        }
        return object;
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
                .forEach(m -> validateProperty(workpack, m));

    }

    private static void validateProperty(final Workpack workpack, final PropertyModel propertyModel) {
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
            return this.findAllUsingParent(idPlan, idWorkpackModel, idWorkpackParent, workpackLinked);
        }

        final CustomFilter filter = this.findCustomFilterById(idFilter);

        final Map<String, Object> params = new HashMap<>();
        params.put("idPlan", idPlan);
        params.put("idPlanModel", idPlanModel);
        params.put("idWorkPackModel", idWorkpackModel);
        params.put("idWorkPackParent", idWorkpackParent);

        return this.findAllWorkpackByParent.execute(filter, params);
    }

    public List<Workpack> findAllUsingParent(
            final Long idPlan,
            final Long idWorkPackModel,
            final Long idWorkPackParent,
            final boolean workpackLinked
    ) {
        if (workpackLinked) {
            return this.workpackRepository.findAllUsingParentLinked(idWorkPackModel, idWorkPackParent, idPlan);
        }

        return this.workpackRepository.findAllUsingParent(idWorkPackModel, idWorkPackParent, idPlan);
    }

    private CustomFilter findCustomFilterById(final Long idFilter) {
        return this.customFilterRepository.findByIdWithRelationships(idFilter)
                .orElseThrow(() -> new NegocioException(CUSTOM_FILTER_NOT_FOUND));
    }

    public WorkpackModel getWorkpackModelById(final Long idWorkpackModel) {
        return this.workpackModelService.findById(idWorkpackModel);
    }

    public List<Workpack> findAll(final Long idPlan, final Long idPlanModel, final Long idWorkpackModel, final Long idFilter) {
        if (idFilter == null) {
            return this.findAll(idPlan, idPlanModel, idWorkpackModel);
        }

        final CustomFilter filter = this.findCustomFilterById(idFilter);

        final Map<String, Object> params = new HashMap<>();
        params.put("idPlan", idPlan);
        params.put("idPlanModel", idPlanModel);
        params.put("idWorkPackModel", idWorkpackModel);

        return this.findAllWorkpack.execute(filter, params);
    }

    public List<Workpack> findAll(final Long idPlan, final Long idPlanModel, final Long idWorkPackModel) {
        return this.workpackRepository.findAll(idPlan, idPlanModel, idWorkPackModel);
    }

    public Workpack saveDefault(final Workpack workpack) {
        return this.save(workpack);
    }

    private Workpack save(final Workpack workpack) {
        return this.workpackRepository.save(workpack);
    }

    public EntityDto save(final Workpack workpack, final Long idPlan, final Long idParent) {
        this.ifNewWorkpackAddRelationship(workpack, idPlan, idParent);
        validateWorkpack(workpack);
        this.save(workpack);
        return EntityDto.of(workpack);
    }

    private void ifNewWorkpackAddRelationship(final Workpack workpack, final Long idPlan, final Long idParent) {
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

    public WorkpackModel getWorkpackModelByParent(final Long idWorkpackParent) {
        final Workpack workpack = this.findById(idWorkpackParent);
        WorkpackModel workpackModel = null;
        switch (workpack.getClass().getTypeName()) {
            case TYPE_NAME_PORTFOLIO:
                workpackModel = ((Portfolio) workpack).getInstance();
                break;
            case TYPE_NAME_PROGRAM:
                workpackModel = ((Program) workpack).getInstance();
                break;
            case TYPE_NAME_ORGANIZER:
                workpackModel = ((Organizer) workpack).getInstance();
                break;
            case TYPE_NAME_DELIVERABLE:
                workpackModel = ((Deliverable) workpack).getInstance();
                break;
            case TYPE_NAME_PROJECT:
                workpackModel = ((Project) workpack).getInstance();
                break;
            case TYPE_NAME_MILESTONE:
                workpackModel = ((Milestone) workpack).getInstance();
                break;
        }
        if (workpackModel != null) {
            workpackModel = this.workpackModelService.findById(workpackModel.getId());
        }
        return workpackModel;
    }

    public Workpack update(final Workpack workpack) {
        final Workpack workpackUpdate = this.findById(workpack.getId());

        final Set<Property> propertiesToUpdate = workpackUpdate.getProperties();
        final Set<Property> properties = workpack.getProperties();

        this.verifyForPropertiesToDelete(
                propertiesToUpdate,
                properties
        );

        this.verifyForPropertiesToUpdate(
                () -> workpackUpdate.setProperties(new HashSet<>()),
                propertiesToUpdate,
                properties
        );

        if (!CollectionUtils.isEmpty(workpackUpdate.getCosts())) {
            final Set<CostAccount> costsDelete = workpackUpdate.getCosts().stream()
                    .filter(cost -> workpack.getCosts() == null || workpack.getCosts().stream()
                            .noneMatch(p -> p.getId() != null && p.getId().equals(cost.getId())))
                    .collect(Collectors.toSet());
            if (!costsDelete.isEmpty()) {
                this.costAccountService.delete(costsDelete);
            }
            workpackUpdate.getCosts().removeAll(costsDelete);
            if (!CollectionUtils.isEmpty(workpackUpdate.getCosts())) {
                for (final CostAccount cost : workpackUpdate.getCosts()) {
                    if (!CollectionUtils.isEmpty(cost.getProperties())) {
                        final CostAccount costAccountParam = workpack.getCosts().stream()
                                .filter(c -> c.getId() != null && c.getId().equals(cost.getId())).findFirst()
                                .orElse(null);
                        if (costAccountParam != null) {
                            final Set<Property> propertyDelete = cost.getProperties().stream()
                                    .filter(property -> costAccountParam.getProperties() == null
                                            || costAccountParam.getProperties().stream().noneMatch(
                                            p -> p.getId() != null && p.getId().equals(property.getId())))
                                    .collect(Collectors.toSet());
                            if (!propertyDelete.isEmpty()) {
                                this.propertyService.delete(propertyDelete);
                            }
                        }
                    }
                }
            }
        }

        if (!CollectionUtils.isEmpty(workpack.getCosts())) {
            for (final CostAccount cost : workpack.getCosts()) {
                if (cost.getId() == null) {
                    if (workpackUpdate.getCosts() == null) {
                        workpackUpdate.setCosts(new HashSet<>());
                    }
                    workpackUpdate.getCosts().add(cost);
                    continue;
                }
                if (!CollectionUtils.isEmpty(cost.getProperties())) {
                    final CostAccount costAccountUpdate = workpackUpdate.getCosts().stream()
                            .filter(c -> c.getId().equals(cost.getId())).findFirst().orElse(null);
                    if (costAccountUpdate != null) {
                        for (final Property property : cost.getProperties()) {
                            if (property.getId() == null) {
                                if (cost.getProperties() == null) {
                                    cost.setProperties(new HashSet<>());
                                }
                                costAccountUpdate.getProperties().add(property);
                                continue;
                            }
                            if (costAccountUpdate.getProperties() != null) {
                                costAccountUpdate.getProperties().stream()
                                        .filter(p -> p.getId().equals(property.getId())).findFirst()
                                        .ifPresent(propertyUpdate -> this.loadPropertyUpdate(propertyUpdate, property));
                            }
                        }

                    }
                }
            }
        }

        validateWorkpack(workpackUpdate);
        return this.save(workpackUpdate);
    }

    private void verifyForPropertiesToUpdate(
            final Runnable createPropertyList, final Collection<Property> propertiesToUpdate,
            final Collection<? extends Property> properties
    ) {
        if (!CollectionUtils.isEmpty(properties)) {
            for (final Property property : properties) {
                if (property.getId() == null) {
                    if (propertiesToUpdate == null) {
                        createPropertyList.run();
                    }
                    propertiesToUpdate.add(property);
                    continue;
                }
                if (propertiesToUpdate != null) {
                    propertiesToUpdate.stream().filter(p -> p.getId() != null && p.getId().equals(property.getId()))
                            .findFirst().ifPresent(propertyUpdate -> this.loadPropertyUpdate(propertyUpdate, property));
                }
            }
        }
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

    private void loadPropertyUpdate(final Property propertyToUpdate, final Property property) {
        if (!property.getClass().getTypeName().equals(propertyToUpdate.getClass().getTypeName())) {
            throw new NegocioException(PROPERTY_UPDATE_TYPE_ERROR);
        }
        switch (propertyToUpdate.getClass().getTypeName()) {
            case TYPE_MODEL_NAME_INTEGER:
                final Integer integerUpdate = (Integer) propertyToUpdate;
                final Integer integer = (Integer) property;
                integerUpdate.setValue(integer.getValue());
                break;
            case TYPE_MODEL_NAME_TEXT:
                final Text textUpdate = (Text) propertyToUpdate;
                final Text text = (Text) property;
                textUpdate.setValue(text.getValue());
                break;
            case TYPE_MODEL_NAME_DATE:
                final Date dateUpdate = (Date) propertyToUpdate;
                final Date date = (Date) property;
                dateUpdate.setValue(date.getValue());
                break;
            case TYPE_MODEL_NAME_TOGGLE:
                final Toggle toggleUpdate = (Toggle) propertyToUpdate;
                final Toggle toggle = (Toggle) property;
                toggleUpdate.setValue(toggle.getValue());
                break;
            case TYPE_MODEL_NAME_UNIT_SELECTION:
                final UnitSelection unitSelectionUpdate = (UnitSelection) propertyToUpdate;
                final UnitSelection unitSelection = (UnitSelection) property;
                unitSelectionUpdate.setValue(unitSelection.getValue());
                break;
            case TYPE_MODEL_NAME_SELECTION:
                final Selection selectionUpdate = (Selection) propertyToUpdate;
                final Selection selection = (Selection) property;
                selectionUpdate.setValue(selection.getValue());
                break;
            case TYPE_MODEL_NAME_TEXT_AREA:
                final TextArea textAreaUpdate = (TextArea) propertyToUpdate;
                final TextArea textArea = (TextArea) property;
                textAreaUpdate.setValue(textArea.getValue());
                break;
            case TYPE_MODEL_NAME_NUMBER:
                final Number decimalUpdate = (Number) propertyToUpdate;
                final Number decimal = (Number) property;
                decimalUpdate.setValue(decimal.getValue());
                break;
            case TYPE_MODEL_NAME_CURRENCY:
                final Currency currencyUpdate = (Currency) propertyToUpdate;
                final Currency currency = (Currency) property;
                currencyUpdate.setValue(currency.getValue());
                break;
            case TYPE_MODEL_NAME_LOCALITY_SELECTION:
                final LocalitySelection localitySelectionUpdate = (LocalitySelection) propertyToUpdate;
                final LocalitySelection localitySelection = (LocalitySelection) property;
                localitySelectionUpdate.setValue(localitySelection.getValue());
                break;
            case TYPE_MODEL_NAME_ORGANIZATION_SELECTION:
                final OrganizationSelection organizationSelectionUpdate = (OrganizationSelection) propertyToUpdate;
                final OrganizationSelection organizationSelection = (OrganizationSelection) property;
                organizationSelectionUpdate.setValue(organizationSelection.getValue());
                break;
            case TYPE_MODEL_NAME_GROUP:
                final Group groupToUpdate = (Group) propertyToUpdate;
                final Group group = (Group) property;

                final Set<Property> groupedPropertiesToUpdate = groupToUpdate.getGroupedProperties();
                final Set<Property> groupedProperties = group.getGroupedProperties();

                this.verifyForPropertiesToDelete(groupedPropertiesToUpdate, groupedProperties);
                this.verifyForPropertiesToUpdate(() -> groupToUpdate.setGroupedProperties(new HashSet<>()),
                        groupedPropertiesToUpdate, groupedProperties
                );

                break;
        }

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
                costs.add(this.modelMapper.map(costAccount, CostAccountDto.class));
            }
        }

        if (!CollectionUtils.isEmpty(workpack.getProperties())) {
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
            addSharedWith(workpack, detailDto);
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
                workpackDetailDto = this.modelMapper.map(workpack, PortfolioDetailDto.class);
                break;
            case TYPE_NAME_PROGRAM:
                workpackModel = ((Program) workpack).getInstance();
                workpackDetailDto = this.modelMapper.map(workpack, ProgramDetailDto.class);
                break;
            case TYPE_NAME_ORGANIZER:
                workpackModel = ((Organizer) workpack).getInstance();
                workpackDetailDto = this.modelMapper.map(workpack, OrganizerDetailDto.class);
                break;
            case TYPE_NAME_DELIVERABLE:
                workpackModel = ((Deliverable) workpack).getInstance();
                workpackDetailDto = this.modelMapper.map(workpack, DeliverableDetailDto.class);
                break;
            case TYPE_NAME_PROJECT:
                workpackModel = ((Project) workpack).getInstance();
                workpackDetailDto = this.modelMapper.map(workpack, ProjectDetailDto.class);
                break;
            case TYPE_NAME_MILESTONE:
                workpackModel = ((Milestone) workpack).getInstance();
                workpackDetailDto = this.modelMapper.map(workpack, MilestoneDetailDto.class);
                this.milestoneService.addDate(workpack.getId(), (MilestoneDetailDto) workpackDetailDto);
                this.milestoneService.addStatus(workpack.getId(), (MilestoneDetailDto) workpackDetailDto);
                break;
        }
        if (workpackDetailDto != null) {
            this.applyBaselineStatus(typeName, workpackDetailDto);
            if (workpackModel != null) {
                workpackDetailDto.setModel(this.workpackModelService.getWorkpackModelDetailDto(workpackModel));
            }
            return workpackDetailDto;
        }
        return null;
    }

    private void applyBaselineStatus(final String type, final WorkpackDetailDto workpackDetailDto) {
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
        }

        if (type.equals(TYPE_NAME_DELIVERABLE) || type.equals(TYPE_NAME_MILESTONE)) {
            final Optional<Baseline> maybeActiveBaseline =
                    this.workpackRepository.findActiveBaselineFromProjectChildren(idWorkpack);

            maybeActiveBaseline.ifPresent(activeBaseline -> {
                workpackDetailDto.setActiveBaselineName(activeBaseline.getName());
                workpackDetailDto.setHasActiveBaseline(true);
            });
        }
    }

    public void delete(final Workpack workpack) {
        if (!CollectionUtils.isEmpty(workpack.getChildren())) {
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
                workpack = this.modelMapper.map(workpackParamDto, Portfolio.class);
                ((Portfolio) workpack).setInstance((PortfolioModel) workpackModel);
                break;
            case "br.gov.es.openpmo.dto.workpack.ProgramParamDto":
                workpack = this.modelMapper.map(workpackParamDto, Program.class);
                ((Program) workpack).setInstance((ProgramModel) workpackModel);
                break;
            case "br.gov.es.openpmo.dto.workpack.OrganizerParamDto":
                workpack = this.modelMapper.map(workpackParamDto, Organizer.class);
                ((Organizer) workpack).setInstance((OrganizerModel) workpackModel);
                break;
            case "br.gov.es.openpmo.dto.workpack.DeliverableParamDto":
                workpack = this.modelMapper.map(workpackParamDto, Deliverable.class);
                ((Deliverable) workpack).setInstance((DeliverableModel) workpackModel);
                break;
            case "br.gov.es.openpmo.dto.workpack.ProjectParamDto":
                workpack = this.modelMapper.map(workpackParamDto, Project.class);
                ((Project) workpack).setInstance((ProjectModel) workpackModel);
                break;
            case "br.gov.es.openpmo.dto.workpack.MilestoneParamDto":
                workpack = this.modelMapper.map(workpackParamDto, Milestone.class);
                ((Milestone) workpack).setInstance((MilestoneModel) workpackModel);
                break;
        }
        if (workpack != null) {
            workpack.setProperties(properties);
        }
        return workpack;
    }

    public WorkpackDetailDto getWorkpackDetailDto(final Workpack workpack, final Long idPlan) {
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

    private List<? extends PropertyDto> getPropertiesDto(final Collection<Property> properties) {
        if (!CollectionUtils.isEmpty(properties)) {
            final List<PropertyDto> list = new ArrayList<>();
            properties.forEach(property -> {
                final String typeName = property.getClass().getTypeName();
                switch (typeName) {
                    case TYPE_MODEL_NAME_INTEGER:
                        final IntegerDto integerDto = this.modelMapper.map(property, IntegerDto.class);
                        if (((Integer) property).getDriver() != null) {
                            integerDto.setIdPropertyModel(((Integer) property).getDriver().getId());
                        }
                        list.add(integerDto);
                        break;
                    case TYPE_MODEL_NAME_TEXT:
                        final TextDto textDto = this.modelMapper.map(property, TextDto.class);
                        if (((Text) property).getDriver() != null) {
                            textDto.setIdPropertyModel(((Text) property).getDriver().getId());
                        }
                        list.add(textDto);
                        break;
                    case TYPE_MODEL_NAME_DATE:
                        final DateDto dateDto = this.modelMapper.map(property, DateDto.class);
                        if (((Date) property).getDriver() != null) {
                            dateDto.setIdPropertyModel(((Date) property).getDriver().getId());
                        }
                        list.add(dateDto);
                        break;
                    case TYPE_MODEL_NAME_TOGGLE:
                        final ToggleDto toggleDto = this.modelMapper.map(property, ToggleDto.class);
                        if (((Toggle) property).getDriver() != null) {
                            toggleDto.setIdPropertyModel(((Toggle) property).getDriver().getId());
                        }
                        list.add(toggleDto);
                        break;
                    case TYPE_MODEL_NAME_UNIT_SELECTION:
                        final UnitSelectionDto unitSelectionDto = this.modelMapper.map(property, UnitSelectionDto.class);
                        if (((UnitSelection) property).getDriver() != null) {
                            unitSelectionDto.setIdPropertyModel(((UnitSelection) property).getDriver().getId());
                        }
                        if (((UnitSelection) property).getValue() != null) {
                            unitSelectionDto.setSelectedValue(((UnitSelection) property).getValue().getId());
                        }
                        list.add(unitSelectionDto);
                        break;
                    case TYPE_MODEL_NAME_SELECTION:
                        final SelectionDto selectionDto = this.modelMapper.map(property, SelectionDto.class);
                        if (((Selection) property).getDriver() != null) {
                            selectionDto.setIdPropertyModel(((Selection) property).getDriver().getId());
                        }
                        list.add(selectionDto);
                        break;
                    case TYPE_MODEL_NAME_TEXT_AREA:
                        final TextAreaDto textAreaDto = this.modelMapper.map(property, TextAreaDto.class);
                        if (((TextArea) property).getDriver() != null) {
                            textAreaDto.setIdPropertyModel(((TextArea) property).getDriver().getId());
                        }
                        list.add(textAreaDto);
                        break;
                    case TYPE_MODEL_NAME_NUMBER:
                        final NumberDto numberDto = this.modelMapper.map(property, NumberDto.class);
                        if (((Number) property).getDriver() != null) {
                            numberDto.setIdPropertyModel(((Number) property).getDriver().getId());
                        }
                        list.add(numberDto);
                        break;
                    case TYPE_MODEL_NAME_CURRENCY:
                        final CurrencyDto currencyDto = this.modelMapper.map(property, CurrencyDto.class);
                        if (((Currency) property).getDriver() != null) {
                            currencyDto.setIdPropertyModel(((Currency) property).getDriver().getId());
                        }
                        list.add(currencyDto);
                        break;
                    case TYPE_MODEL_NAME_LOCALITY_SELECTION:
                        final LocalitySelectionDto localitySelectionDto = this.modelMapper.map(
                                property,
                                LocalitySelectionDto.class
                        );
                        if (((LocalitySelection) property).getDriver() != null) {
                            localitySelectionDto.setIdPropertyModel(((LocalitySelection) property).getDriver().getId());
                        }
                        if (((LocalitySelection) property).getValue() != null) {
                            localitySelectionDto.setSelectedValues(new HashSet<>());
                            ((LocalitySelection) property).getValue()
                                    .forEach(o -> localitySelectionDto.getSelectedValues().add(o.getId()));
                        }
                        list.add(localitySelectionDto);
                        break;
                    case TYPE_MODEL_NAME_ORGANIZATION_SELECTION:
                        final OrganizationSelectionDto organizationSelectionDto = this.modelMapper.map(
                                property,
                                OrganizationSelectionDto.class
                        );
                        if (((OrganizationSelection) property).getDriver() != null) {
                            organizationSelectionDto
                                    .setIdPropertyModel(((OrganizationSelection) property).getDriver().getId());
                        }
                        if (((OrganizationSelection) property).getValue() != null) {
                            organizationSelectionDto.setSelectedValues(new HashSet<>());
                            ((OrganizationSelection) property).getValue()
                                    .forEach(o -> organizationSelectionDto.getSelectedValues().add(o.getId()));
                        }
                        list.add(organizationSelectionDto);
                        break;
                    case TYPE_MODEL_NAME_GROUP:
                        final GroupDto groupDto = this.modelMapper.map(property, GroupDto.class);
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
            childrens.forEach(workpack -> {
                Set<WorkpackDetailDto> childrenChild = null;
                Set<Workpack> child = null;
                Set<Property> propertySet = null;
                List<? extends PropertyDto> properties = null;
                if (workpack.getChildren() != null && !workpack.getChildren().isEmpty()) {
                    child = new HashSet<>(workpack.getChildren());
                    childrenChild = this.getChildren(workpack.getChildren());
                }
                workpack.setChildren(null);
                if (!CollectionUtils.isEmpty(workpack.getProperties())) {
                    properties = this.getPropertiesDto(workpack.getProperties());
                    propertySet = new HashSet<>(workpack.getProperties());
                    workpack.setProperties(null);
                }
                final WorkpackDetailDto detailDto = this.convertWorkpackDetailDto(workpack);
                if (detailDto != null) {
                    detailDto.setChildren(childrenChild);
                    detailDto.setProperties(properties);
                    addSharedWith(workpack, detailDto);
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
        properties.forEach(property -> this.extractProperty(propertiesExtracted, property));
        return propertiesExtracted;
    }

    private void extractProperty(final Collection<? super Property> properties, final PropertyDto propertyDto) {
        if (propertyDto.getIdPropertyModel() == null) {
            throw new NegocioException(PROPERTY_RELATIONSHIP_MODEL_NOT_FOUND);
        }
        final PropertyModel propertyModel = this.propertyModelService.findById(propertyDto.getIdPropertyModel());
        switch (propertyDto.getClass().getTypeName()) {
            case "br.gov.es.openpmo.dto.workpack.IntegerDto":
                final Integer integer = this.modelMapper.map(propertyDto, Integer.class);
                integer.setDriver((IntegerModel) propertyModel);
                properties.add(integer);
                break;
            case "br.gov.es.openpmo.dto.workpack.TextDto":
                final Text text = this.modelMapper.map(propertyDto, Text.class);
                text.setDriver((TextModel) propertyModel);
                properties.add(text);
                break;
            case "br.gov.es.openpmo.dto.workpack.DateDto":
                final Date date = this.modelMapper.map(propertyDto, Date.class);
                date.setDriver((DateModel) propertyModel);
                properties.add(date);
                break;
            case "br.gov.es.openpmo.dto.workpack.ToggleDto":
                final Toggle toggle = this.modelMapper.map(propertyDto, Toggle.class);
                toggle.setDriver((ToggleModel) propertyModel);
                properties.add(toggle);
                break;
            case "br.gov.es.openpmo.dto.workpack.UnitSelectionDto":
                final UnitSelection unitSelection = this.modelMapper.map(propertyDto, UnitSelection.class);
                unitSelection.setDriver((UnitSelectionModel) propertyModel);
                if (((UnitSelectionDto) propertyDto).getSelectedValue() != null) {
                    unitSelection.setValue(
                            this.unitMeasureService.findById(((UnitSelectionDto) propertyDto).getSelectedValue()));
                }
                properties.add(unitSelection);
                break;
            case "br.gov.es.openpmo.dto.workpack.SelectionDto":
                final Selection selection = this.modelMapper.map(propertyDto, Selection.class);
                selection.setDriver((SelectionModel) propertyModel);
                properties.add(selection);

                break;
            case "br.gov.es.openpmo.dto.workpack.TextAreaDto":
                final TextArea textArea = this.modelMapper.map(propertyDto, TextArea.class);
                textArea.setDriver((TextAreaModel) propertyModel);
                properties.add(textArea);
                break;
            case "br.gov.es.openpmo.dto.workpack.NumberDto":
                final Number number = this.modelMapper.map(propertyDto, Number.class);
                number.setDriver((NumberModel) propertyModel);
                properties.add(number);
                break;
            case "br.gov.es.openpmo.dto.workpack.CurrencyDto":
                final Currency currency = this.modelMapper.map(propertyDto, Currency.class);
                currency.setDriver((CurrencyModel) propertyModel);
                properties.add(currency);
                break;
            case "br.gov.es.openpmo.dto.workpack.LocalitySelectionDto":
                final LocalitySelection localitySelection = this.modelMapper.map(propertyDto, LocalitySelection.class);
                localitySelection.setDriver((LocalitySelectionModel) propertyModel);
                if (((LocalitySelectionDto) propertyDto).getSelectedValues() != null) {
                    localitySelection.setValue(new HashSet<>());
                    ((LocalitySelectionDto) propertyDto).getSelectedValues()
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
                if (((OrganizationSelectionDto) propertyDto).getSelectedValues() != null) {
                    organizationSelection.setValue(new HashSet<>());
                    ((OrganizationSelectionDto) propertyDto).getSelectedValues()
                            .forEach(id -> organizationSelection.getValue().add(this.organizationService.findById(id)));
                }
                properties.add(organizationSelection);
                break;
            case "br.gov.es.openpmo.dto.workpack.GroupDto":
                final Group groupModel = this.modelMapper.map(propertyDto, Group.class);
                groupModel.setDriver((GroupModel) propertyModel);

                final GroupDto groupModelDto = (GroupDto) propertyDto;
                final Set<Property> groupedProperties = new HashSet<>();

                groupModelDto.getGroupedProperties().forEach(dto -> this.extractProperty(groupedProperties, dto));
                groupModel.setGroupedProperties(groupedProperties);

                properties.add(groupModel);
                break;
        }
    }

    public Set<Workpack> findAllByPlanWithProperties(final Long idPlan) {
        return this.workpackRepository.findAllByPlanWithProperties(idPlan);
    }

    public Set<Long> findAllWorkpacksWithPermissions(final Long idPlan, final Long idUser) {
        return this.workpackRepository.findAllWorkpacksWithPermissions(idPlan, idUser);
    }

    public Workpack cancel(final Long idWorkpack) {
        final Workpack workpack = this.findById(idWorkpack);

        if (this.isCancelable(workpack)) {
            this.cancelWorkpack(workpack);
        }

        return this.save(workpack);
    }

    private boolean isCancelable(final Workpack workpack) {
        return workpack.isCancelable();
    }

    private void cancelWorkpack(final Workpack workpack) {
        workpack.setCanceled(true);
    }

    public void restore(final Long idWorkpack) {
        final Workpack workpack = this.findById(idWorkpack);

        if (this.isRestaurable(workpack)) {
            this.restoreWorkpack(workpack);
        }

        this.save(workpack);
    }

    private boolean isRestaurable(final Workpack workpack) {
        return workpack.isRestaurable();
    }

    private void restoreWorkpack(final Workpack workpack) {
        workpack.setCanceled(false);
    }

    public WorkpackModel findWorkpackModelLinked(final Long idWorkpack, final Long idPlan) {
        return this.workpackRepository.findWorkpackModeLinkedByWorkpackAndPlan(idWorkpack, idPlan)
                .orElseThrow(() -> new NegocioException(WORKPACKMODEL_NOT_FOUND));
    }

    public Set<Workpack> findAllByIdPlan(final Long idPlan) {
        return this.workpackRepository.findAllByPlanWithProperties(idPlan);
    }

    public void calculateDashboard(Workpack workpack) {
        this.workpackRepository.findAscendentsId(workpack.getId())
                .forEach(this.dashboardService::calculate);
    }
}
