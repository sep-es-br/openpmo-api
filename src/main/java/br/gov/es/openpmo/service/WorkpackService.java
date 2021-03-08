package br.gov.es.openpmo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import br.gov.es.openpmo.dto.costaccount.CostAccountDto;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.workpack.CurrencyDto;
import br.gov.es.openpmo.dto.workpack.DateDto;
import br.gov.es.openpmo.dto.workpack.DeliverableDetailDto;
import br.gov.es.openpmo.dto.workpack.IntegerDto;
import br.gov.es.openpmo.dto.workpack.LocalitySelectionDto;
import br.gov.es.openpmo.dto.workpack.MilestoneDetailDto;
import br.gov.es.openpmo.dto.workpack.NumberDto;
import br.gov.es.openpmo.dto.workpack.OrganizationSelectionDto;
import br.gov.es.openpmo.dto.workpack.OrganizerDetailDto;
import br.gov.es.openpmo.dto.workpack.PortfolioDetailDto;
import br.gov.es.openpmo.dto.workpack.ProgramDetailDto;
import br.gov.es.openpmo.dto.workpack.ProjectDetailDto;
import br.gov.es.openpmo.dto.workpack.PropertyDto;
import br.gov.es.openpmo.dto.workpack.SelectionDto;
import br.gov.es.openpmo.dto.workpack.TextAreaDto;
import br.gov.es.openpmo.dto.workpack.TextDto;
import br.gov.es.openpmo.dto.workpack.ToggleDto;
import br.gov.es.openpmo.dto.workpack.UnitSelectionDto;
import br.gov.es.openpmo.dto.workpack.WorkpackDetailDto;
import br.gov.es.openpmo.dto.workpack.WorkpackParamDto;
import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.CostAccount;
import br.gov.es.openpmo.model.Currency;
import br.gov.es.openpmo.model.CurrencyModel;
import br.gov.es.openpmo.model.Date;
import br.gov.es.openpmo.model.DateModel;
import br.gov.es.openpmo.model.Deliverable;
import br.gov.es.openpmo.model.DeliverableModel;
import br.gov.es.openpmo.model.Integer;
import br.gov.es.openpmo.model.IntegerModel;
import br.gov.es.openpmo.model.LocalitySelection;
import br.gov.es.openpmo.model.LocalitySelectionModel;
import br.gov.es.openpmo.model.Milestone;
import br.gov.es.openpmo.model.MilestoneModel;
import br.gov.es.openpmo.model.Number;
import br.gov.es.openpmo.model.NumberModel;
import br.gov.es.openpmo.model.Office;
import br.gov.es.openpmo.model.OrganizationSelection;
import br.gov.es.openpmo.model.OrganizationSelectionModel;
import br.gov.es.openpmo.model.Organizer;
import br.gov.es.openpmo.model.OrganizerModel;
import br.gov.es.openpmo.model.Person;
import br.gov.es.openpmo.model.Plan;
import br.gov.es.openpmo.model.Portfolio;
import br.gov.es.openpmo.model.PortfolioModel;
import br.gov.es.openpmo.model.Program;
import br.gov.es.openpmo.model.ProgramModel;
import br.gov.es.openpmo.model.Project;
import br.gov.es.openpmo.model.ProjectModel;
import br.gov.es.openpmo.model.Property;
import br.gov.es.openpmo.model.PropertyModel;
import br.gov.es.openpmo.model.Selection;
import br.gov.es.openpmo.model.SelectionModel;
import br.gov.es.openpmo.model.Text;
import br.gov.es.openpmo.model.TextArea;
import br.gov.es.openpmo.model.TextAreaModel;
import br.gov.es.openpmo.model.TextModel;
import br.gov.es.openpmo.model.Toggle;
import br.gov.es.openpmo.model.ToggleModel;
import br.gov.es.openpmo.model.UnitSelection;
import br.gov.es.openpmo.model.UnitSelectionModel;
import br.gov.es.openpmo.model.Workpack;
import br.gov.es.openpmo.model.WorkpackModel;
import br.gov.es.openpmo.model.domain.Session;
import br.gov.es.openpmo.model.relations.CanAccessOffice;
import br.gov.es.openpmo.model.relations.CanAccessPlan;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;

@Service
public class WorkpackService {

    private final WorkpackModelService workpackModelService;
    private final PropertyModelService propertyModelService;
    private final WorkpackRepository workpackRepository;
    private final PlanService planService;
    private final ModelMapper modelMapper;
    private final PropertyService propertyService;

    @Autowired
    private CostAccountService costAccountService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private LocalityService localityService;

    @Autowired
    private UnitMeasureService unitMeasureService;

    @Autowired
    private PersonService personService;

    @Autowired
    private PlanPermissionService planPermissionService;

    @Autowired
    private OfficePermissionService officePermissionService;

    private static final String TYPE_NAME_PORTFOLIO = "br.gov.es.openpmo.model.Portfolio";
    private static final String TYPE_NAME_PROGRAM = "br.gov.es.openpmo.model.Program";
    private static final String TYPE_NAME_ORGANIZER = "br.gov.es.openpmo.model.Organizer";
    private static final String TYPE_NAME_DELIVERABLE = "br.gov.es.openpmo.model.Deliverable";
    private static final String TYPE_NAME_PROJECT = "br.gov.es.openpmo.model.Project";
    private static final String TYPE_NAME_MILESTONE = "br.gov.es.openpmo.model.Milestone";

    public static final String TYPE_NAME_INTEGER = "br.gov.es.openpmo.model.Integer";
    public static final String TYPE_NAME_TEXT = "br.gov.es.openpmo.model.Text";
    public static final String TYPE_NAME_DATE = "br.gov.es.openpmo.model.Date";
    public static final String TYPE_NAME_TOGGLE = "br.gov.es.openpmo.model.Toggle";
    public static final String TYPE_NAME_UNIT_SELECTION = "br.gov.es.openpmo.model.UnitSelection";
    public static final String TYPE_NAME_SELECTION = "br.gov.es.openpmo.model.Selection";
    public static final String TYPE_NAME_TEXT_AREA = "br.gov.es.openpmo.model.TextArea";
    public static final String TYPE_NAME_NUMBER = "br.gov.es.openpmo.model.Number";
    public static final String TYPE_NAME_CURRENCY = "br.gov.es.openpmo.model.Currency";
    public static final String TYPE_NAME_LOCALITY_SELECTION = "br.gov.es.openpmo.model.LocalitySelection";
    public static final String TYPE_NAME_ORGANIZATION_SELECTION = "br.gov.es.openpmo.model.OrganizationSelection";

    @Autowired
    public WorkpackService(WorkpackModelService workpackModelService, PlanService planService, ModelMapper modelMapper,
            PropertyService propertyService, PropertyModelService propertyModelService,
            WorkpackRepository workpackRepository) {
        this.workpackModelService = workpackModelService;
        this.planService = planService;
        this.modelMapper = modelMapper;
        this.propertyService = propertyService;
        this.workpackRepository = workpackRepository;
        this.propertyModelService = propertyModelService;
    }

    public List<Workpack> findAll(Long idPlan, Long idPlanModel, Long idWorkPackModel, Long idWorkPackParent) {
        return workpackRepository.findAll(idPlan, idPlanModel, idWorkPackModel, idWorkPackParent);
    }

    public List<Workpack> findAll(Long idPlan, Long idPlanModel, Long idWorkPackModel) {
        return workpackRepository.findAll(idPlan, idPlanModel, idWorkPackModel);
    }

    public WorkpackModel getWorkpackModelById(Long idWorkpackModel) {
        return workpackModelService.findById(idWorkpackModel);
    }
    public WorkpackModel getWorkpackModelByParent(Long idWorkpackParent) {
        Workpack workpack = findById(idWorkpackParent);
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
            workpackModel = workpackModelService.findById(workpackModel.getId());
        }
        return workpackModel;
    }

    public int compare (Object a, Object b) {
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
        return -1;
    }

    public Object getValueProperty(Workpack workpack, PropertyModel sortBy) {
        Property property = getPropertyByModel(workpack, sortBy);
        return getValueProperty(property);
    }

    public Object getValueProperty(Property property) {
        Object object = null;
        if (property != null) {
            switch (property.getClass().getTypeName()) {
                case TYPE_NAME_INTEGER:
                    Integer integer = (Integer) property;
                    object = integer.getValue();
                    break;
                case TYPE_NAME_TEXT:
                    Text text = (Text) property;
                    object = text.getValue();
                    break;
                case TYPE_NAME_DATE:
                    Date date = (Date) property;
                    object = date.getValue();
                    break;
                case TYPE_NAME_TOGGLE:
                    Toggle toggle = (Toggle) property;
                    object = toggle.isValue();
                    break;
                case TYPE_NAME_UNIT_SELECTION:
                    UnitSelection unitSelection = (UnitSelection) property;
                    object = unitSelection.getValue();
                    break;
                case TYPE_NAME_SELECTION:
                    Selection selection = (Selection) property;
                    object = selection.getValue();
                    break;
                case TYPE_NAME_TEXT_AREA:
                    TextArea textArea = (TextArea) property;
                    object = textArea.getValue();
                    break;
                case TYPE_NAME_NUMBER:
                    Number decimal = (Number) property;
                    object = decimal.getValue();
                    break;
                case TYPE_NAME_CURRENCY:
                    Currency currency = (Currency) property;
                    object = currency.getValue();
                    break;
                case TYPE_NAME_LOCALITY_SELECTION:
                    LocalitySelection localitySelection = (LocalitySelection) property;
                    object = localitySelection.getValue();
                    break;
                case TYPE_NAME_ORGANIZATION_SELECTION:
                    OrganizationSelection organizationSelection = (OrganizationSelection) property;
                    object = organizationSelection.getValue();
                    break;
            }

        }
        return object;
    }

    private Property getPropertyByModel(Workpack workpack, PropertyModel propertyModel) {
        if ( workpack == null || CollectionUtils.isEmpty(workpack.getProperties())) {
            return null;
        }
        for (Property property : workpack.getProperties()) {
            switch (property.getClass().getTypeName()) {
                case TYPE_NAME_INTEGER:
                    Integer integer = (Integer) property;
                    if (integer.getDriver() != null && integer.getDriver().getId().equals(propertyModel.getId())) {
                        return integer;
                    }
                    break;
                case TYPE_NAME_TEXT:
                    Text text = (Text) property;
                    if (text.getDriver() != null && text.getDriver().getId().equals(propertyModel.getId())) {
                        return text;
                    }
                    break;
                case TYPE_NAME_DATE:
                    Date date = (Date) property;
                    if (date.getDriver() != null && date.getDriver().getId().equals(propertyModel.getId())) {
                        return date;
                    }
                    break;
                case TYPE_NAME_TOGGLE:
                    Toggle toggle = (Toggle) property;
                    if (toggle.getDriver() != null && toggle.getDriver().getId().equals(propertyModel.getId())) {
                        return toggle;
                    }
                    break;
                case TYPE_NAME_UNIT_SELECTION:
                    UnitSelection unitSelection = (UnitSelection) property;
                    if (unitSelection.getDriver() != null && unitSelection.getDriver().getId().equals(propertyModel.getId())) {
                        return unitSelection;
                    }
                    break;
                case TYPE_NAME_SELECTION:
                    Selection selection = (Selection) property;
                    if (selection.getDriver() != null && selection.getDriver().getId().equals(propertyModel.getId())) {
                        return selection;
                    }
                    break;
                case TYPE_NAME_TEXT_AREA:
                    TextArea textArea = (TextArea) property;
                    if (textArea.getDriver() != null && textArea.getDriver().getId().equals(propertyModel.getId())) {
                        return textArea;
                    }
                    break;
                case TYPE_NAME_NUMBER:
                    Number decimal = (Number) property;
                    if (decimal.getDriver() != null && decimal.getDriver().getId().equals(propertyModel.getId())) {
                        return decimal;
                    }
                    break;
                case TYPE_NAME_CURRENCY:
                    Currency currency = (Currency) property;
                    if (currency.getDriver() != null && currency.getDriver().getId().equals(propertyModel.getId())) {
                        return currency;
                    }
                    break;
                case TYPE_NAME_LOCALITY_SELECTION:
                    LocalitySelection localitySelection = (LocalitySelection) property;
                    if (localitySelection.getDriver() != null && localitySelection.getDriver().getId().equals(propertyModel.getId())) {
                        return localitySelection;
                    }
                    break;
                case TYPE_NAME_ORGANIZATION_SELECTION:
                    OrganizationSelection organizationSelection = (OrganizationSelection) property;
                    if (organizationSelection.getDriver() != null
                        && organizationSelection.getDriver().getId().equals(propertyModel.getId())) {
                        return organizationSelection;
                    }
                    break;

            }
        }
        return null;
    }

    public Workpack save(Workpack workpack) {
        if (workpack.getId() == null) {
            if (workpack.getIdPlan() != null) {
                workpack.setPlan(planService.findById(workpack.getIdPlan()));
            }
            if (workpack.getIdParent() != null) {
                workpack.setParent(findById(workpack.getIdParent()));
            }
        }
        validateWorkpack(workpack);
        return workpackRepository.save(workpack);
    }

    public Workpack update(Workpack workpack) {
        Workpack workpackUpdate = findById(workpack.getId());

        if (workpackUpdate.getProperties() != null && !workpackUpdate.getProperties().isEmpty()) {
            Set<Property> propertyDelete = workpackUpdate.getProperties().stream()
                    .filter(property -> workpack.getProperties() == null || workpack.getProperties().stream()
                            .noneMatch(p -> p.getId() != null && p.getId().equals(property.getId())))
                    .collect(Collectors.toSet());
            if (!propertyDelete.isEmpty()) {
                propertyService.delete(propertyDelete);
            }

        }

        if (!CollectionUtils.isEmpty(workpack.getProperties())) {
            for (Property property : workpack.getProperties()) {
                if (property.getId() == null) {
                    if (workpackUpdate.getProperties() == null) {
                        workpackUpdate.setProperties(new HashSet<>());
                    }
                    workpackUpdate.getProperties().add(property);
                    continue;
                }
                if (workpackUpdate.getProperties() != null) {
                    workpackUpdate.getProperties().stream().filter(
                        p -> p.getId() != null && p.getId().equals(property.getId())).findFirst().ifPresent(
                        propertyUpdate -> loadPropertyUpdate(propertyUpdate, property));
                }
            }
        }

        if (!CollectionUtils.isEmpty(workpackUpdate.getCosts())) {
            Set<CostAccount> costsDelete = workpackUpdate.getCosts().stream()
                    .filter(cost -> workpack.getCosts() == null || workpack.getCosts().stream()
                            .noneMatch(p -> p.getId() != null && p.getId().equals(cost.getId())))
                    .collect(Collectors.toSet());
            if (!costsDelete.isEmpty()) {
                costAccountService.delete(costsDelete);
            }
            workpackUpdate.getCosts().removeAll(costsDelete);
            if (!CollectionUtils.isEmpty(workpackUpdate.getCosts())) {
                for (CostAccount cost : workpackUpdate.getCosts()) {
                    if (!CollectionUtils.isEmpty(cost.getProperties())) {
                        CostAccount costAccountParam = workpack.getCosts().stream()
                                .filter(c -> c.getId() != null && c.getId().equals(cost.getId())).findFirst()
                                .orElse(null);
                        if (costAccountParam != null) {
                            Set<Property> propertyDelete = cost.getProperties().stream()
                                    .filter(property -> costAccountParam.getProperties() == null
                                            || costAccountParam.getProperties().stream().noneMatch(
                                                    p -> p.getId() != null && p.getId().equals(property.getId())))
                                    .collect(Collectors.toSet());
                            if (!propertyDelete.isEmpty()) {
                                propertyService.delete(propertyDelete);
                            }
                        }

                    }
                }
            }
        }



        if (!CollectionUtils.isEmpty(workpack.getCosts())) {
            for (CostAccount cost : workpack.getCosts()) {
                if (cost.getId() == null) {
                    if (workpackUpdate.getCosts() == null) {
                        workpackUpdate.setCosts(new HashSet<>());
                    }
                    workpackUpdate.getCosts().add(cost);
                    continue;
                }
                if (!CollectionUtils.isEmpty(cost.getProperties())) {
                    CostAccount costAccountUpdate = workpackUpdate.getCosts().stream()
                            .filter(c -> c.getId().equals(cost.getId())).findFirst().orElse(null);
                    if (costAccountUpdate != null) {
                        for (Property property : cost.getProperties()) {
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
                                        .ifPresent(propertyUpdate -> loadPropertyUpdate(propertyUpdate, property));
                            }
                        }

                    }
                }
            }
        }

        validateWorkpack(workpackUpdate);
        return workpackRepository.save(workpackUpdate);
    }

    private void validateWorkpack(Workpack workpack) {
        Set<PropertyModel> models = new HashSet<>();
        switch (workpack.getClass().getTypeName()) {
            case TYPE_NAME_PORTFOLIO:
                Portfolio portfolio = (Portfolio) workpack;
                if (portfolio.getInstance().getProperties() != null) {
                    models.addAll(portfolio.getInstance().getProperties());
                }
                break;
            case TYPE_NAME_PROGRAM:
                Program program = (Program) workpack;
                if (program.getInstance().getProperties() != null) {
                    models.addAll(program.getInstance().getProperties());
                }
                break;
            case TYPE_NAME_ORGANIZER:
                Organizer organizer = (Organizer) workpack;
                if (organizer.getInstance().getProperties() != null) {
                    models.addAll(organizer.getInstance().getProperties());
                }
                break;
            case TYPE_NAME_DELIVERABLE:
                Deliverable deliverable = (Deliverable) workpack;
                if (deliverable.getInstance().getProperties() != null) {
                    models.addAll(deliverable.getInstance().getProperties());
                }
                break;
            case TYPE_NAME_PROJECT:
                Project project = (Project) workpack;
                if (project.getInstance().getProperties() != null) {
                    models.addAll(project.getInstance().getProperties());
                }
                break;
            case TYPE_NAME_MILESTONE:
                Milestone milestone = (Milestone) workpack;
                if (milestone.getInstance().getProperties() != null) {
                    models.addAll(milestone.getInstance().getProperties());
                }
                break;
        }
        models.stream().filter(m -> Session.PROPERTIES.equals(m.getSession()) && m.isActive())
              .forEach(m -> validateProperty(workpack, m));

    }

    private void validateProperty(Workpack workpack, PropertyModel propertyModel) {
        boolean propertyModelFound = false;
        if (workpack.getProperties() != null && !workpack.getProperties().isEmpty()) {
            for (Property property : workpack.getProperties()) {
                switch (property.getClass().getTypeName()) {
                    case TYPE_NAME_INTEGER:
                        Integer integer = (Integer) property;
                        if (integer.getDriver().getId().equals(propertyModel.getId())) {
                            propertyModelFound = true;
                            if (integer.getDriver().isRequired() && integer.getValue() == null) {
                                throw new NegocioException(
                                        ApplicationMessage.PROPERTY_VALUE_NOT_NULL + "$" + propertyModel.getLabel());
                            }
                            if (integer.getDriver().getMin() != null && integer.getDriver().getMin() > integer.getValue()) {
                                throw new NegocioException(
                                        ApplicationMessage.PROPERTY_VALUE_NOT_MIN + "$" + propertyModel.getLabel());
                            }
                            if (integer.getDriver().getMax() != null && integer.getDriver().getMax() < integer.getValue()) {
                                throw new NegocioException(
                                        ApplicationMessage.PROPERTY_VALUE_NOT_MAX + "$" + propertyModel.getLabel());
                            }
                        }
                        break;
                    case TYPE_NAME_TEXT:
                        Text text = (Text) property;
                        if (text.getDriver().getId().equals(propertyModel.getId())) {
                            propertyModelFound = true;
                            if (text.getDriver().isRequired()
                                    && (text.getValue() == null || text.getValue().isEmpty())) {
                                throw new NegocioException(
                                        ApplicationMessage.PROPERTY_VALUE_NOT_EMPTY + "$" + propertyModel.getLabel());
                            }
                            if (text.getDriver().getMin() != null && text.getDriver().getMin() > text.getValue().length()) {
                                throw new NegocioException(
                                        ApplicationMessage.PROPERTY_VALUE_NOT_MIN + "$" + propertyModel.getLabel());
                            }
                            if (text.getDriver().getMax() != null && text.getDriver().getMax() < text.getValue().length()) {
                                throw new NegocioException(
                                        ApplicationMessage.PROPERTY_VALUE_NOT_MAX + "$" + propertyModel.getLabel());
                            }
                        }
                        break;
                    case TYPE_NAME_DATE:
                        Date date = (Date) property;
                        if (date.getDriver().getId().equals(propertyModel.getId())) {
                            propertyModelFound = true;
                            if (date.getDriver().isRequired() && date.getValue() == null) {
                                throw new NegocioException(
                                        ApplicationMessage.PROPERTY_VALUE_NOT_NULL + "$" + propertyModel.getLabel());
                            }
                            if (date.getDriver().getMin() != null && date.getDriver().getMin().isAfter(date.getValue())) {
                                throw new NegocioException(
                                        ApplicationMessage.PROPERTY_VALUE_NOT_MIN + "$" + propertyModel.getLabel());
                            }
                            if (date.getDriver().getMax() != null && date.getDriver().getMax().isBefore(date.getValue())) {
                                throw new NegocioException(
                                        ApplicationMessage.PROPERTY_VALUE_NOT_MAX + "$" + propertyModel.getLabel());
                            }
                        }
                        break;
                    case TYPE_NAME_TOGGLE:
                        Toggle toggle = (Toggle) property;
                        if (toggle.getDriver().getId().equals(propertyModel.getId())) {
                            propertyModelFound = true;
                        }
                        break;
                    case TYPE_NAME_UNIT_SELECTION:
                        UnitSelection unitSelection = (UnitSelection) property;
                        if (unitSelection.getDriver().getId().equals(propertyModel.getId())) {
                            propertyModelFound = true;
                            if (unitSelection.getDriver().isRequired() && unitSelection.getValue() == null) {
                                throw new NegocioException(
                                        ApplicationMessage.PROPERTY_VALUE_NOT_NULL + "$" + propertyModel.getLabel());
                            }
                        }
                        break;
                    case TYPE_NAME_SELECTION:
                        Selection selection = (Selection) property;
                        if (selection.getDriver().getId().equals(propertyModel.getId())) {
                            propertyModelFound = true;
                            if (selection.getDriver().isRequired()
                                    && (selection.getValue() == null || selection.getValue().isEmpty())) {
                                throw new NegocioException(
                                        ApplicationMessage.PROPERTY_VALUE_NOT_NULL + "$" + propertyModel.getLabel());
                            }
                        }
                        break;
                    case TYPE_NAME_TEXT_AREA:
                        TextArea textArea = (TextArea) property;
                        if (textArea.getDriver().getId().equals(propertyModel.getId())) {
                            propertyModelFound = true;
                            if (textArea.getDriver().isRequired()
                                    && (textArea.getValue() == null || textArea.getValue().isEmpty())) {
                                throw new NegocioException(
                                        ApplicationMessage.PROPERTY_VALUE_NOT_EMPTY + "$" + propertyModel.getLabel());
                            }
                            if (textArea.getDriver().getMin() != null && textArea.getDriver().getMin() > textArea.getValue().length()) {
                                throw new NegocioException(
                                        ApplicationMessage.PROPERTY_VALUE_NOT_MIN + "$" + propertyModel.getLabel());
                            }
                            if (textArea.getDriver().getMax() != null && textArea.getDriver().getMax() < textArea.getValue().length()) {
                                throw new NegocioException(
                                        ApplicationMessage.PROPERTY_VALUE_NOT_MAX + "$" + propertyModel.getLabel());
                            }
                        }
                        break;
                    case TYPE_NAME_NUMBER:
                        Number decimal = (Number) property;
                        if (decimal.getDriver().getId().equals(propertyModel.getId())) {
                            propertyModelFound = true;
                            if (decimal.getDriver().isRequired() && decimal.getValue() == null) {
                                throw new NegocioException(
                                        ApplicationMessage.PROPERTY_VALUE_NOT_NULL + "$" + propertyModel.getLabel());
                            }
                            if (decimal.getDriver().getMin() != null && decimal.getDriver().getMin() > decimal.getValue()) {
                                throw new NegocioException(
                                        ApplicationMessage.PROPERTY_VALUE_NOT_MIN + "$" + propertyModel.getLabel());
                            }
                            if (decimal.getDriver().getMax() != null && decimal.getDriver().getMax() < decimal.getValue()) {
                                throw new NegocioException(
                                        ApplicationMessage.PROPERTY_VALUE_NOT_MAX + "$" + propertyModel.getLabel());
                            }
                        }
                        break;
                    case TYPE_NAME_CURRENCY:
                        Currency currency = (Currency) property;
                        if (currency.getDriver().getId().equals(propertyModel.getId())) {
                            propertyModelFound = true;
                            if (currency.getDriver().isRequired() && currency.getValue() == null) {
                                throw new NegocioException(
                                        ApplicationMessage.PROPERTY_VALUE_NOT_NULL + "$" + propertyModel.getLabel());
                            }
                        }
                        break;
                    case TYPE_NAME_LOCALITY_SELECTION:
                        LocalitySelection localitySelection = (LocalitySelection) property;
                        if (localitySelection.getDriver().getId().equals(propertyModel.getId())) {
                            propertyModelFound = true;
                            if (localitySelection.getDriver().isRequired() && (localitySelection.getValue() == null
                                    || localitySelection.getValue().isEmpty())) {
                                throw new NegocioException(
                                        ApplicationMessage.PROPERTY_VALUE_NOT_NULL + "$" + propertyModel.getLabel());
                            }
                        }
                        break;
                    case TYPE_NAME_ORGANIZATION_SELECTION:
                        OrganizationSelection organizationSelection = (OrganizationSelection) property;
                        if (organizationSelection.getDriver().getId().equals(propertyModel.getId())) {
                            propertyModelFound = true;
                            if (organizationSelection.getDriver().isRequired()
                                    && (organizationSelection.getValue() == null
                                            || organizationSelection.getValue().isEmpty())) {
                                throw new NegocioException(
                                        ApplicationMessage.PROPERTY_VALUE_NOT_NULL + "$" + propertyModel.getLabel());
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
            throw new NegocioException(ApplicationMessage.PROPERTY_REQUIRED_NOT_FOUND + "$" + propertyModel.getLabel());
        }
    }

    private void loadPropertyUpdate(Property propertyUpdate, Property property) {
        if (!property.getClass().getTypeName().equals(propertyUpdate.getClass().getTypeName())) {
            throw new NegocioException(ApplicationMessage.PROPERTY_UPDATE_TYPE_ERROR);
        }
        switch (propertyUpdate.getClass().getTypeName()) {
            case TYPE_NAME_INTEGER:
                Integer integerUpdate = (Integer) propertyUpdate;
                Integer integer = (Integer) property;
                integerUpdate.setValue(integer.getValue());
                break;
            case TYPE_NAME_TEXT:
                Text textUpdate = (Text) propertyUpdate;
                Text text = (Text) property;
                textUpdate.setValue(text.getValue());

                break;
            case TYPE_NAME_DATE:
                Date dateUpdate = (Date) propertyUpdate;
                Date date = (Date) property;
                dateUpdate.setValue(date.getValue());
                break;
            case TYPE_NAME_TOGGLE:
                Toggle toggleUpdate = (Toggle) propertyUpdate;
                Toggle toggle = (Toggle) property;
                toggleUpdate.setValue(toggle.isValue());
                break;
            case TYPE_NAME_UNIT_SELECTION:
                UnitSelection unitSelectionUpdate = (UnitSelection) propertyUpdate;
                UnitSelection unitSelection = (UnitSelection) property;
                unitSelectionUpdate.setValue(unitSelection.getValue());
                break;
            case TYPE_NAME_SELECTION:
                Selection selectionUpdate = (Selection) propertyUpdate;
                Selection selection = (Selection) property;
                selectionUpdate.setValue(selection.getValue());
                break;
            case TYPE_NAME_TEXT_AREA:
                TextArea textAreaUpdate = (TextArea) propertyUpdate;
                TextArea textArea = (TextArea) property;
                textAreaUpdate.setValue(textArea.getValue());
                break;
            case TYPE_NAME_NUMBER:
                Number decimalUpdate = (Number) propertyUpdate;
                Number decimal = (Number) property;
                decimalUpdate.setValue(decimal.getValue());
                break;
            case TYPE_NAME_CURRENCY:
                Currency currencyUpdate = (Currency) propertyUpdate;
                Currency currency = (Currency) property;
                currencyUpdate.setValue(currency.getValue());
                break;
            case TYPE_NAME_LOCALITY_SELECTION:
                LocalitySelection localitySelectionUpdate = (LocalitySelection) propertyUpdate;
                LocalitySelection localitySelection = (LocalitySelection) property;
                localitySelectionUpdate.setValue(localitySelection.getValue());
                break;
            case TYPE_NAME_ORGANIZATION_SELECTION:
                OrganizationSelection organizationSelectionUpdate = (OrganizationSelection) propertyUpdate;
                OrganizationSelection organizationSelection = (OrganizationSelection) property;
                organizationSelectionUpdate.setValue(organizationSelection.getValue());
                break;
        }

    }

    public Workpack findById(Long id) {
        return workpackRepository.findByIdWorkpack(id)
                .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
    }

    public Workpack findByIdWithParent(Long id) {
        return workpackRepository.findByIdWithParent(id)
                                 .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
    }

    public void delete(Workpack workpack) {
        if (!CollectionUtils.isEmpty(workpack.getChildren())) {
            throw new NegocioException(ApplicationMessage.WORKPACK_DELETE_REALATIONSHIP_ERROR);
        }
        workpackRepository.delete(workpack);
    }

    public Workpack getWorkpack(WorkpackParamDto workpackParamDto) {
        Workpack workpack = null;
        Set<Property> properties = null;
        if (workpackParamDto.getProperties() != null && !workpackParamDto.getProperties().isEmpty()) {
            properties = getProperties(workpackParamDto.getProperties());
        }
        workpackParamDto.setProperties(null);
        WorkpackModel workpackModel = workpackModelService.findById(workpackParamDto.getIdWorkpackModel());

        switch (workpackParamDto.getClass().getTypeName()) {
            case "br.gov.es.openpmo.dto.workpack.PortfolioParamDto":
                workpack = modelMapper.map(workpackParamDto, Portfolio.class);
                ((Portfolio) workpack).setInstance((PortfolioModel) workpackModel);
                break;
            case "br.gov.es.openpmo.dto.workpack.ProgramParamDto":
                workpack = modelMapper.map(workpackParamDto, Program.class);
                ((Program) workpack).setInstance((ProgramModel) workpackModel);
                break;
            case "br.gov.es.openpmo.dto.workpack.OrganizerParamDto":
                workpack = modelMapper.map(workpackParamDto, Organizer.class);
                ((Organizer) workpack).setInstance((OrganizerModel) workpackModel);
                break;
            case "br.gov.es.openpmo.dto.workpack.DeliverableParamDto":
                workpack = modelMapper.map(workpackParamDto, Deliverable.class);
                ((Deliverable) workpack).setInstance((DeliverableModel) workpackModel);
                break;
            case "br.gov.es.openpmo.dto.workpack.ProjectParamDto":
                workpack = modelMapper.map(workpackParamDto, Project.class);
                ((Project) workpack).setInstance((ProjectModel) workpackModel);
                break;
            case "br.gov.es.openpmo.dto.workpack.MilestoneParamDto":
                workpack = modelMapper.map(workpackParamDto, Milestone.class);
                ((Milestone) workpack).setInstance((MilestoneModel) workpackModel);
                break;
        }
        if (workpack != null) {
            workpack.setProperties(properties);
        }
        return workpack;
    }

    public WorkpackDetailDto getWorkpackDetailDto(Workpack workpack) {
        Set<WorkpackDetailDto> children = null;
        Set<Workpack> child = null;
        Set<Property> propertySet = null;
        List<? extends PropertyDto> properties = null;
        if (workpack.getChildren() != null) {
            child = new HashSet<>(workpack.getChildren());
            children = getChildren(workpack.getChildren());
        }
        Set<CostAccountDto> costs = null;
        if (workpack.getCosts() != null) {
            costs = new HashSet<>();
            for (CostAccount costAccount : workpack.getCosts()) {
                costs.add(modelMapper.map(costAccount, CostAccountDto.class));
            }
        }
        if (!CollectionUtils.isEmpty(workpack.getProperties())) {
            properties = getPropertiesDto(workpack.getProperties());
            propertySet = new HashSet<>(workpack.getProperties());
            workpack.setProperties(null);
        }
        workpack.setChildren(null);
        WorkpackDetailDto detailDto = convertWorkpackDetailDto(workpack);
        if (detailDto != null) {
            detailDto.setChildren(children);
            detailDto.setCosts(costs);
            detailDto.setProperties(properties);
        }
        workpack.setChildren(child);
        workpack.setProperties(propertySet);
        return detailDto;
    }

    public List<? extends PropertyDto> getPropertiesDto(Set<Property> properties) {
        if (!CollectionUtils.isEmpty(properties)) {
            List<PropertyDto> list =  new ArrayList<>();
            properties.forEach(property -> {
                switch (property.getClass().getTypeName()) {
                    case TYPE_NAME_INTEGER:
                        IntegerDto integerDto = modelMapper.map(property, IntegerDto.class);
                        if (((Integer)property).getDriver() != null) {
                            integerDto.setIdPropertyModel(((Integer)property).getDriver().getId());
                        }
                        list.add(integerDto);
                        break;
                    case TYPE_NAME_TEXT:
                        TextDto textDto = modelMapper.map(property, TextDto.class);
                        if (((Text)property).getDriver() != null) {
                            textDto.setIdPropertyModel(((Text)property).getDriver().getId());
                        }
                        list.add(textDto);
                        break;
                    case TYPE_NAME_DATE:
                        DateDto dateDto = modelMapper.map(property, DateDto.class);
                        if (((Date)property).getDriver() != null) {
                            dateDto.setIdPropertyModel(((Date)property).getDriver().getId());
                        }
                        list.add(dateDto);
                        break;
                    case TYPE_NAME_TOGGLE:
                        ToggleDto toggleDto = modelMapper.map(property, ToggleDto.class);
                        if (((Toggle)property).getDriver() != null) {
                            toggleDto.setIdPropertyModel(((Toggle)property).getDriver().getId());
                        }
                        list.add(toggleDto);
                        break;
                    case TYPE_NAME_UNIT_SELECTION:
                        UnitSelectionDto unitSelectionDto = modelMapper.map(property, UnitSelectionDto.class);
                        if (((UnitSelection)property).getDriver() != null) {
                            unitSelectionDto.setIdPropertyModel(((UnitSelection)property).getDriver().getId());
                        }
                        if (((UnitSelection) property).getValue() != null) {
                            unitSelectionDto.setSelectedValue(((UnitSelection) property).getValue().getId());
                        }
                        list.add(unitSelectionDto);
                        break;
                    case TYPE_NAME_SELECTION:
                        SelectionDto selectionDto = modelMapper.map(property, SelectionDto.class);
                        if (((Selection)property).getDriver() != null) {
                            selectionDto.setIdPropertyModel(((Selection)property).getDriver().getId());
                        }
                        list.add(selectionDto);
                        break;
                    case TYPE_NAME_TEXT_AREA:
                        TextAreaDto textAreaDto = modelMapper.map(property, TextAreaDto.class);
                        if (((TextArea)property).getDriver() != null) {
                            textAreaDto.setIdPropertyModel(((TextArea)property).getDriver().getId());
                        }
                        list.add(textAreaDto);
                        break;
                    case TYPE_NAME_NUMBER:
                        NumberDto numberDto = modelMapper.map(property, NumberDto.class);
                        if (((Number)property).getDriver() != null) {
                            numberDto.setIdPropertyModel(((Number)property).getDriver().getId());
                        }
                        list.add(numberDto);
                        break;
                    case TYPE_NAME_CURRENCY:
                        CurrencyDto currencyDto = modelMapper.map(property, CurrencyDto.class);
                        if (((Currency)property).getDriver() != null) {
                            currencyDto.setIdPropertyModel(((Currency)property).getDriver().getId());
                        }
                        list.add(currencyDto);
                        break;
                    case TYPE_NAME_LOCALITY_SELECTION:
                        LocalitySelectionDto localitySelectionDto = modelMapper.map(property, LocalitySelectionDto.class);
                        if (((LocalitySelection)property).getDriver() != null) {
                            localitySelectionDto.setIdPropertyModel(((LocalitySelection)property).getDriver().getId());
                        }
                        if (((LocalitySelection) property).getValue() != null) {
                            localitySelectionDto.setSelectedValues(new HashSet<>());
                            ((LocalitySelection) property).getValue().forEach(o -> localitySelectionDto.getSelectedValues().add(o.getId()));
                        }
                        list.add(localitySelectionDto);
                        break;
                    case TYPE_NAME_ORGANIZATION_SELECTION:
                        OrganizationSelectionDto organizationSelectionDto = modelMapper.map(property, OrganizationSelectionDto.class);
                        if (((OrganizationSelection)property).getDriver() != null) {
                            organizationSelectionDto.setIdPropertyModel(((OrganizationSelection)property).getDriver().getId());
                        }
                        if (((OrganizationSelection) property).getValue() != null) {
                            organizationSelectionDto.setSelectedValues(new HashSet<>());
                            ((OrganizationSelection) property).getValue().forEach(o -> organizationSelectionDto.getSelectedValues().add(o.getId()));
                        }
                        list.add(organizationSelectionDto);
                        break;
                }
            });
            return list;
        }
        return null;
    }

    private WorkpackDetailDto convertWorkpackDetailDto(Workpack workpack) {
        WorkpackModel workpackModel = null;
        WorkpackDetailDto workpackDetailDto = null;
        switch (workpack.getClass().getTypeName()) {
            case TYPE_NAME_PORTFOLIO:
                workpackModel = ((Portfolio) workpack).getInstance();
                workpackDetailDto = modelMapper.map(workpack, PortfolioDetailDto.class);
                break;
            case TYPE_NAME_PROGRAM:
                workpackModel = ((Program) workpack).getInstance();
                workpackDetailDto = modelMapper.map(workpack, ProgramDetailDto.class);
                break;
            case TYPE_NAME_ORGANIZER:
                workpackModel = ((Organizer) workpack).getInstance();
                workpackDetailDto = modelMapper.map(workpack, OrganizerDetailDto.class);
                break;
            case TYPE_NAME_DELIVERABLE:
                workpackModel = ((Deliverable) workpack).getInstance();
                workpackDetailDto = modelMapper.map(workpack, DeliverableDetailDto.class);
                break;
            case TYPE_NAME_PROJECT:
                workpackModel = ((Project) workpack).getInstance();
                workpackDetailDto = modelMapper.map(workpack, ProjectDetailDto.class);
                break;
            case TYPE_NAME_MILESTONE:
                workpackModel = ((Milestone) workpack).getInstance();
                workpackDetailDto = modelMapper.map(workpack, MilestoneDetailDto.class);
                break;
        }
        if (workpackDetailDto != null) {
            if (workpackModel != null) {
                workpackDetailDto.setModel(workpackModelService.getWorkpackModelDetailDto(workpackModel));
            }
            return workpackDetailDto;
        }
        return null;
    }

    public Set<WorkpackDetailDto> getChildren(Set<Workpack> childrens) {
        if (childrens != null && !childrens.isEmpty()) {
            Set<WorkpackDetailDto> set = new HashSet<>();
            childrens.forEach(w -> {
                Set<WorkpackDetailDto> childrenChild = null;
                Set<Workpack> child = null;
                Set<Property> propertySet = null;
                List<? extends PropertyDto> properties = null;
                if (w.getChildren() != null && !w.getChildren().isEmpty()) {
                    child = new HashSet<>(w.getChildren());
                    childrenChild = getChildren(w.getChildren());
                }
                w.setChildren(null);
                if (!CollectionUtils.isEmpty(w.getProperties())) {
                    properties = getPropertiesDto(w.getProperties());
                    propertySet = new HashSet<>(w.getProperties());
                    w.setProperties(null);
                }
                WorkpackDetailDto detailDto = convertWorkpackDetailDto(w);
                if (detailDto != null) {
                    detailDto.setChildren(childrenChild);
                    detailDto.setProperties(properties);
                    set.add(detailDto);
                }
                w.setProperties(propertySet);
                w.setChildren(child);
            });

            return set;
        }
        return null;
    }

    public Set<Property> getProperties(List<? extends PropertyDto> properties) {
        Set<Property> setProperties = new HashSet<>();
        properties.forEach(p -> {
            if (p.getIdPropertyModel() == null) {
                throw new NegocioException(ApplicationMessage.PROPERTY_REALATIONSHIP_MODEL_NOT_FOUND);
            }
            PropertyModel propertyModel = propertyModelService.findById(p.getIdPropertyModel());
            switch (p.getClass().getTypeName()) {
                case "br.gov.es.openpmo.dto.workpack.IntegerDto":
                    Integer integer = modelMapper.map(p, Integer.class);
                    integer.setDriver((IntegerModel) propertyModel);
                    setProperties.add(integer);
                    break;
                case "br.gov.es.openpmo.dto.workpack.TextDto":
                    Text text = modelMapper.map(p, Text.class);
                    text.setDriver((TextModel) propertyModel);
                    setProperties.add(text);
                    break;
                case "br.gov.es.openpmo.dto.workpack.DateDto":
                    Date date = modelMapper.map(p, Date.class);
                    date.setDriver((DateModel) propertyModel);
                    setProperties.add(date);
                    break;
                case "br.gov.es.openpmo.dto.workpack.ToggleDto":
                    Toggle toggle = modelMapper.map(p, Toggle.class);
                    toggle.setDriver((ToggleModel) propertyModel);
                    setProperties.add(toggle);
                    break;
                case "br.gov.es.openpmo.dto.workpack.UnitSelectionDto":
                    UnitSelection unitSelection = modelMapper.map(p, UnitSelection.class);
                    unitSelection.setDriver((UnitSelectionModel) propertyModel);
                    if (((UnitSelectionDto) p).getSelectedValue() != null) {
                        unitSelection.setValue(unitMeasureService.findById(((UnitSelectionDto) p).getSelectedValue()));
                    }
                    setProperties.add(unitSelection);
                    break;
                case "br.gov.es.openpmo.dto.workpack.SelectionDto":
                    Selection selection = modelMapper.map(p, Selection.class);
                    selection.setDriver((SelectionModel) propertyModel);
                    setProperties.add(selection);

                    break;
                case "br.gov.es.openpmo.dto.workpack.TextAreaDto":
                    TextArea textArea = modelMapper.map(p, TextArea.class);
                    textArea.setDriver((TextAreaModel) propertyModel);
                    setProperties.add(textArea);
                    break;
                case "br.gov.es.openpmo.dto.workpack.NumberDto":
                    Number number = modelMapper.map(p, Number.class);
                    number.setDriver((NumberModel) propertyModel);
                    setProperties.add(number);
                    break;
                case "br.gov.es.openpmo.dto.workpack.CurrencyDto":
                    Currency currency = modelMapper.map(p, Currency.class);
                    currency.setDriver((CurrencyModel) propertyModel);
                    setProperties.add(currency);
                    break;
                case "br.gov.es.openpmo.dto.workpack.LocalitySelectionDto":
                    LocalitySelection localitySelection = modelMapper.map(p, LocalitySelection.class);
                    localitySelection.setDriver((LocalitySelectionModel) propertyModel);
                    if (((LocalitySelectionDto) p).getSelectedValues() != null) {
                        localitySelection.setValue(new HashSet<>());
                        ((LocalitySelectionDto) p).getSelectedValues().forEach(id ->
                           localitySelection.getValue().add(localityService.findById(id))
                        );
                    }
                    setProperties.add(localitySelection);
                    break;
                case "br.gov.es.openpmo.dto.workpack.OrganizationSelectionDto":
                    OrganizationSelection organizationSelection = modelMapper.map(p, OrganizationSelection.class);
                    organizationSelection.setDriver((OrganizationSelectionModel) propertyModel);
                    if (((OrganizationSelectionDto) p).getSelectedValues() != null) {
                        organizationSelection.setValue(new HashSet<>());
                        ((OrganizationSelectionDto) p).getSelectedValues().forEach(id ->
                            organizationSelection.getValue().add(organizationService.findById(id))
                        );
                    }
                    setProperties.add(organizationSelection);
                    break;
            }
        });
        return setProperties;
    }

    public Set<Workpack> findAllByPlanWithProperties(Long idPlan) {
        return workpackRepository.findAllByPlanWithProperties(idPlan);
    }

    public List<WorkpackDetailDto> chekPermission(List<WorkpackDetailDto> workpackList, Long idUser, Long idPlan) {
        Person person = personService.findById(idUser);
        if (person.isAdministrator()) {
            return workpackList;
        }
        Plan plan = planService.findById(idPlan);
        List<PermissionDto> permissionsOffice = getOfficePermissionDto(plan.getOffice(), person);
        List<PermissionDto> permissionsPlan = getPlanPermissionDto(idPlan, idUser);

        Set<Workpack> workpacks = workpackRepository.findAll(idPlan);
        for (Iterator<WorkpackDetailDto> it = workpackList.iterator(); it.hasNext();) {
            WorkpackDetailDto workpackDetailDto = it.next();
            Workpack workpack = getWorkpack(workpacks, workpackDetailDto.getId());
            if (workpack != null) {
                WorkpackDetailDto detailDto = getWorkpackDetailDto(workpack);
                if (detailDto.getModel().isStakeholderSessionActive()) {
                    List<PermissionDto> permissions = getPermissionDtoWorkpack(workpack, idUser);
                    permissions = getPermissions(permissions, permissionsPlan, permissionsOffice);
                    if ((permissions.isEmpty())) {
                        it.remove();
                        continue;
                    }
                    workpackDetailDto.setPermissions(permissions);
                    continue;
                }
                List<PermissionDto> permissions = getPermissionDtoWorkpackParent(workpack, idUser);
                permissions = getPermissions(permissions, permissionsPlan, permissionsOffice);

                if (permissions.isEmpty()) {
                    it.remove();
                    continue;
                }
                workpackDetailDto.setPermissions(permissions);
            }
        }

        return workpackList;
    }

    private List<PermissionDto> getPermissions(List<PermissionDto> permissions, List<PermissionDto> permissionsPlan,
                                               List<PermissionDto> permissionsOffice) {
        if (permissions != null && permissions.stream().anyMatch(c -> PermissionLevelEnum.EDIT.equals(c.getLevel()))) {
            return permissions;
        }
        if (permissionsPlan.stream().anyMatch(c -> PermissionLevelEnum.EDIT.equals(c.getLevel()))) {
            return permissionsPlan;
        }
        if (permissionsOffice.stream().anyMatch(c -> PermissionLevelEnum.EDIT.equals(c.getLevel()))) {
            return permissionsOffice;
        }
        if (permissions != null && !permissions.isEmpty()) {
            return permissions;
        }
        return CollectionUtils.isEmpty(permissionsPlan) ? permissionsOffice : permissionsPlan;
    }

    private Workpack getWorkpack(Set<Workpack> workpacks, Long id) {
        Workpack workpack = workpacks.stream().filter(w -> w.getId().equals(id)).findFirst()
                                     .orElse(null);
        if (workpack != null) {
            return workpack;
        }
        for (Workpack w : workpacks) {
            if (w.getChildren() != null) {
                workpack = getWorkpack(w.getChildren(), id);
                if (workpack != null) {
                    return workpack;
                }
            }
        }
        return null;
    }

    public List<PermissionDto> getOfficePermissionDto(Office office, Person person) {
        List<CanAccessOffice> canAccessOffices = officePermissionService.findByOfficeAndPerson(office, person);
        return canAccessOffices.stream().map(c -> {
            PermissionDto permissionDto = new PermissionDto();
            permissionDto.setRole(c.getPermitedRole());
            permissionDto.setLevel(c.getPermissionLevel());
            permissionDto.setId(c.getId());
            return permissionDto;
        }).collect(Collectors.toList());
    }

    private List<PermissionDto> getPlanPermissionDto(Long idPlan, Long idUser) {
        List<CanAccessPlan> canAccessPlan = planPermissionService.findByIdPlan(idPlan);
        return canAccessPlan.stream().filter(c -> c.getPerson().getId().equals(idUser)).map(c -> {
            PermissionDto permissionDto = new PermissionDto();
            permissionDto.setRole(c.getPermitedRole());
            permissionDto.setLevel(c.getPermissionLevel());
            permissionDto.setId(c.getId());
            return permissionDto;
        }).collect(Collectors.toList());
    }

    public List<PermissionDto> getPermissionDto(WorkpackDetailDto workpackDetailDto, Long idUser) {
        Person person = personService.findById(idUser);
        Plan plan = planService.findById(workpackDetailDto.getPlan().getId());
        List<PermissionDto> permissionsOffice = getOfficePermissionDto(plan.getOffice(), person);
        List<PermissionDto> permissionsPlan = getPlanPermissionDto(workpackDetailDto.getPlan().getId(), idUser);

        Set<Workpack> workpacks = workpackRepository.findAll(workpackDetailDto.getPlan().getId());
        Workpack workpack = getWorkpack(workpacks, workpackDetailDto.getId());
        if (workpack != null) {
            WorkpackDetailDto detailDto = getWorkpackDetailDto(workpack);
            if (detailDto.getModel().isStakeholderSessionActive()) {
                List<PermissionDto> permissions = getPermissionDtoWorkpack(workpack, idUser);
                return getPermissions(permissions, permissionsPlan, permissionsOffice);
            }
            List<PermissionDto> permissions = getPermissionDtoWorkpackParent(workpack, idUser);
            return getPermissions(permissions, permissionsPlan, permissionsOffice);
        }
        return null;
    }

    private List<PermissionDto> getPermissionDtoWorkpack(Workpack workpack, Long idUser) {
        if (workpack.getCanAccess() != null) {
            return workpack.getCanAccess().stream()
                           .filter(c -> c.getPerson().getId().equals(idUser)).map(c -> {
                    PermissionDto permissionDto = new PermissionDto();
                    permissionDto.setId(c.getId());
                    permissionDto.setLevel(c.getPermissionLevel());
                    permissionDto.setRole(c.getPermitedRole());
                    return permissionDto;
                }).collect(Collectors.toList());
        }
        return null;
    }

    private List<PermissionDto> getPermissionDtoWorkpackParent(Workpack workpack, Long idUser) {
        WorkpackDetailDto detailDto = getWorkpackDetailDto(workpack);
        if (detailDto.getModel().isStakeholderSessionActive()) {
            return getPermissionDtoWorkpack(workpack, idUser);
        }
        if (workpack.getParent() != null) {
            return getPermissionDtoWorkpackParent(workpack.getParent(), idUser);
        }
        return null;
    }

}
