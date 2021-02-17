package br.gov.es.openpmo.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import br.gov.es.openpmo.dto.workpackmodel.CurrencyModelDto;
import br.gov.es.openpmo.dto.workpackmodel.DateModelDto;
import br.gov.es.openpmo.dto.workpackmodel.DeliverableModelDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.DeliverableModelDto;
import br.gov.es.openpmo.dto.workpackmodel.IntegerModelDto;
import br.gov.es.openpmo.dto.workpackmodel.LocalitySelectionModelDto;
import br.gov.es.openpmo.dto.workpackmodel.MilestoneModelDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.MilestoneModelDto;
import br.gov.es.openpmo.dto.workpackmodel.NumberModelDto;
import br.gov.es.openpmo.dto.workpackmodel.OrganizationSelectionModelDto;
import br.gov.es.openpmo.dto.workpackmodel.OrganizerModelDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.OrganizerModelDto;
import br.gov.es.openpmo.dto.workpackmodel.PortfolioModelDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.PortfolioModelDto;
import br.gov.es.openpmo.dto.workpackmodel.ProgramModelDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.ProgramModelDto;
import br.gov.es.openpmo.dto.workpackmodel.ProjectModelDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.ProjectModelDto;
import br.gov.es.openpmo.dto.workpackmodel.PropertyModelDto;
import br.gov.es.openpmo.dto.workpackmodel.SelectionModelDto;
import br.gov.es.openpmo.dto.workpackmodel.TextAreaModelDto;
import br.gov.es.openpmo.dto.workpackmodel.TextModelDto;
import br.gov.es.openpmo.dto.workpackmodel.ToggleModelDto;
import br.gov.es.openpmo.dto.workpackmodel.UnitSelectionModelDto;
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelDto;
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelParamDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.CurrencyModel;
import br.gov.es.openpmo.model.DateModel;
import br.gov.es.openpmo.model.DeliverableModel;
import br.gov.es.openpmo.model.IntegerModel;
import br.gov.es.openpmo.model.LocalitySelectionModel;
import br.gov.es.openpmo.model.MilestoneModel;
import br.gov.es.openpmo.model.NumberModel;
import br.gov.es.openpmo.model.OrganizationSelectionModel;
import br.gov.es.openpmo.model.OrganizerModel;
import br.gov.es.openpmo.model.PortfolioModel;
import br.gov.es.openpmo.model.ProgramModel;
import br.gov.es.openpmo.model.ProjectModel;
import br.gov.es.openpmo.model.PropertyModel;
import br.gov.es.openpmo.model.SelectionModel;
import br.gov.es.openpmo.model.TextAreaModel;
import br.gov.es.openpmo.model.TextModel;
import br.gov.es.openpmo.model.ToggleModel;
import br.gov.es.openpmo.model.UnitSelectionModel;
import br.gov.es.openpmo.model.WorkpackModel;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;

@Service
public class WorkpackModelService {

    private final WorkpackModelRepository workpackModelRepository;
    private final PlanModelService planModelService;
    private final ModelMapper modelMapper;
    private final PropertyModelService propertyModelService;
    private final DomainService domainService;
    private final LocalityService localityService;
    private final OrganizationService organizationService;
    private final UnitMeasureService unitMeasureService;
    private static final String TYPE_NAME_MODEL_PORTFOLIO = "br.gov.es.openpmo.model.PortfolioModel";
    private static final String TYPE_NAME_MODEL_PROGRAM = "br.gov.es.openpmo.model.ProgramModel";
    private static final String TYPE_NAME_MODEL_ORGANIZER = "br.gov.es.openpmo.model.OrganizerModel";
    private static final String TYPE_NAME_MODEL_DELIVERABLE = "br.gov.es.openpmo.model.DeliverableModel";
    private static final String TYPE_NAME_MODEL_PROJECT = "br.gov.es.openpmo.model.ProjectModel";
    private static final String TYPE_NAME_MODEL_MILESTONE = "br.gov.es.openpmo.model.MilestoneModel";

    private static final String TYPE_NAME_MODEL_INTEGER = "br.gov.es.openpmo.model.IntegerModel";
    private static final String TYPE_NAME_MODEL_TEXT = "br.gov.es.openpmo.model.TextModel";
    private static final String TYPE_NAME_MODEL_DATE = "br.gov.es.openpmo.model.DateModel";
    private static final String TYPE_NAME_MODEL_TOGGLE = "br.gov.es.openpmo.model.ToggleModel";
    private static final String TYPE_NAME_MODEL_UNIT_SELECTION = "br.gov.es.openpmo.model.UnitSelectionModel";
    private static final String TYPE_NAME_MODEL_SELECTION = "br.gov.es.openpmo.model.SelectionModel";
    private static final String TYPE_NAME_MODEL_TEXT_AREA = "br.gov.es.openpmo.model.TextAreaModel";
    private static final String TYPE_NAME_MODEL_NUMBER = "br.gov.es.openpmo.model.NumberModel";
    private static final String TYPE_NAME_MODEL_CURRENCY = "br.gov.es.openpmo.model.CurrencyModel";
    private static final String TYPE_NAME_MODEL_LOCALITY_SELECTION = "br.gov.es.openpmo.model.LocalitySelectionModel";
    private static final String TYPE_NAME_MODEL_ORGANIZATION_SELECTION = "br.gov.es.openpmo.model.OrganizationSelectionModel";

    private static final String PACKAGE_DTO = "br.gov.es.openpmo.dto.workpackmodel";

    @Autowired
    public WorkpackModelService(WorkpackModelRepository workpackModelRepository, PlanModelService planModelService,
                                ModelMapper modelMapper, PropertyModelService propertyModelService,
                                DomainService domainService, LocalityService localityService,
                                OrganizationService organizationService, UnitMeasureService unitMeasureService) {
        this.workpackModelRepository = workpackModelRepository;
        this.planModelService = planModelService;
        this.modelMapper = modelMapper;
        this.propertyModelService = propertyModelService;
        this.domainService = domainService;
        this.localityService = localityService;
        this.organizationService = organizationService;
        this.unitMeasureService = unitMeasureService;
    }

    public List<WorkpackModel> findAll(Long idPlanModel) {
        return workpackModelRepository.findAllByIdPlanModel(idPlanModel);
    }

    public WorkpackModel save(WorkpackModel workpackModel) {
        if (workpackModel.getId() == null) {
            workpackModel.setPlanModel(planModelService.findById(workpackModel.getIdPlanModel()));
            if (workpackModel.getIdParent() != null) {
                WorkpackModel parent = findById(workpackModel.getIdParent());
                workpackModel.setParent(parent);
            }
        }
        workpackModel = workpackModelRepository.save(workpackModel);
        return workpackModel;
    }

    public WorkpackModel update(WorkpackModel workpackModel) {
        WorkpackModel workpackModelUpdate = findById(workpackModel.getId());
        workpackModelUpdate.setChildWorkpackModelSessionActive(workpackModel.isChildWorkpackModelSessionActive());
        workpackModelUpdate.setModelName(workpackModel.getModelName());
        workpackModelUpdate.setCostSessionActive(workpackModel.isCostSessionActive());
        workpackModelUpdate.setScheduleSessionActive(workpackModel.isScheduleSessionActive());
        workpackModelUpdate.setPersonRoles(workpackModel.getPersonRoles());
        workpackModelUpdate.setFontIcon(workpackModel.getFontIcon());
        workpackModelUpdate.setModelNameInPlural(workpackModel.getModelNameInPlural());
        workpackModelUpdate.setOrganizationRoles(workpackModel.getOrganizationRoles());
        workpackModelUpdate.setStakeholderSessionActive(workpackModel.isStakeholderSessionActive());
        workpackModelUpdate.setSortBy(workpackModel.getSortBy());

        if (workpackModelUpdate.getProperties() != null && !workpackModelUpdate.getProperties().isEmpty()) {
            Set<PropertyModel> propertyModelDelete = workpackModelUpdate.getProperties().stream().filter(
                propertyModel -> workpackModel.getProperties() == null
                    || workpackModel.getProperties().stream().noneMatch(
                    p -> p.getId() != null && p.getId().equals(propertyModel.getId()))).collect(Collectors.toSet());
            if (!propertyModelDelete.isEmpty()) {
                for(PropertyModel propertyModel : propertyModelDelete) {
                    if (!isCanDeleteProperty(propertyModel.getId())) {
                        throw new NegocioException(ApplicationMessage.PROPERTYMODEL_DELETE_REALATIONSHIP_ERROR);
                    }
                }
                propertyModelService.delete(propertyModelDelete);
            }

        }
        if (workpackModel.getProperties() != null && !workpackModel.getProperties().isEmpty()) {
            for (PropertyModel propertyModel : workpackModel.getProperties()) {
                if (propertyModel.getId() == null) {
                    if (workpackModelUpdate.getProperties() == null) {
                        workpackModelUpdate.setProperties(new HashSet<>());
                    }
                    workpackModelUpdate.getProperties().add(propertyModel);
                    continue;
                }
                if (workpackModelUpdate.getProperties() != null) {
                    PropertyModel propertyModelUpdate = workpackModelUpdate.getProperties().stream().filter(
                        p -> p.getId() != null && p.getId().equals(propertyModel.getId())).findFirst().orElse(null);
                    if (propertyModelUpdate != null) {
                        loadPropertyUpdate(propertyModelUpdate, propertyModel);
                    }
                }
            }
        }

        return workpackModelRepository.save(workpackModelUpdate);
    }

    private void loadPropertyUpdate(PropertyModel propertyModelUpdate, PropertyModel propertyModel) {
        if (!propertyModel.getClass().getTypeName().equals(propertyModelUpdate.getClass().getTypeName())) {
            throw new NegocioException(ApplicationMessage.PROPERTY_UPDATE_TYPE_ERROR);
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
                IntegerModel integerModelUpdate = (IntegerModel) propertyModelUpdate;
                IntegerModel integerModel = (IntegerModel) propertyModel;
                integerModelUpdate.setMax(integerModel.getMax());
                integerModelUpdate.setMin(integerModel.getMin());
                integerModelUpdate.setDefaultValue(integerModel.getDefaultValue());
                break;
            case TYPE_NAME_MODEL_TEXT:
                TextModel textModelUpdate = (TextModel) propertyModelUpdate;
                TextModel textModel = (TextModel) propertyModel;
                textModelUpdate.setMax(textModel.getMax());
                textModelUpdate.setMin(textModel.getMin());
                textModelUpdate.setDefaultValue(textModel.getDefaultValue());
                break;
            case TYPE_NAME_MODEL_DATE:
                DateModel dateModelUpdate = (DateModel) propertyModelUpdate;
                DateModel dateModel = (DateModel) propertyModel;
                dateModelUpdate.setMax(dateModel.getMax());
                dateModelUpdate.setMin(dateModel.getMin());
                dateModelUpdate.setDefaultValue(dateModel.getDefaultValue());
                break;
            case TYPE_NAME_MODEL_TOGGLE:
                ToggleModel toggleModelUpdate = (ToggleModel) propertyModelUpdate;
                ToggleModel toggleModel = (ToggleModel) propertyModel;
                toggleModelUpdate.setDefaultValue(toggleModel.isDefaultValue());
                break;
            case TYPE_NAME_MODEL_UNIT_SELECTION:
                UnitSelectionModel unitSelectionModelUpdate = (UnitSelectionModel) propertyModelUpdate;
                UnitSelectionModel unitSelectionModel = (UnitSelectionModel) propertyModel;
                unitSelectionModelUpdate.setDefaultValue(unitSelectionModel.getDefaultValue());
                break;
            case TYPE_NAME_MODEL_SELECTION:
                SelectionModel selectionModelUpdate = (SelectionModel) propertyModelUpdate;
                SelectionModel selectionModel = (SelectionModel) propertyModel;
                selectionModelUpdate.setMultipleSelection(selectionModel.isMultipleSelection());
                selectionModelUpdate.setPossibleValues(selectionModel.getPossibleValues());
                selectionModelUpdate.setDefaultValue(selectionModel.getDefaultValue());
                break;
            case TYPE_NAME_MODEL_TEXT_AREA:
                TextAreaModel textAreaModelUpdate = (TextAreaModel) propertyModelUpdate;
                TextAreaModel textAreaModel = (TextAreaModel) propertyModel;
                textAreaModelUpdate.setMax(textAreaModel.getMax());
                textAreaModelUpdate.setMin(textAreaModel.getMin());
                textAreaModelUpdate.setDefaultValue(textAreaModel.getDefaultValue());
                textAreaModelUpdate.setRows(textAreaModel.getRows());
                break;
            case TYPE_NAME_MODEL_NUMBER:
                NumberModel decimalModelUpdate = (NumberModel) propertyModelUpdate;
                NumberModel decimalModel = (NumberModel) propertyModel;
                decimalModelUpdate.setMax(decimalModel.getMax());
                decimalModelUpdate.setMin(decimalModel.getMin());
                decimalModelUpdate.setDefaultValue(decimalModel.getDefaultValue());
                decimalModelUpdate.setDecimals(decimalModel.getDecimals());
                break;
            case TYPE_NAME_MODEL_CURRENCY:
                CurrencyModel currencyModelUpdate = (CurrencyModel) propertyModelUpdate;
                CurrencyModel currencyModel = (CurrencyModel) propertyModel;
                currencyModelUpdate.setDefaultValue(currencyModel.getDefaultValue());
                break;
            case TYPE_NAME_MODEL_LOCALITY_SELECTION:
                LocalitySelectionModel localitySelectionModelUpdate = (LocalitySelectionModel) propertyModelUpdate;
                LocalitySelectionModel localitySelectionModel = (LocalitySelectionModel) propertyModel;
                localitySelectionModelUpdate.setMultipleSelection(localitySelectionModel.isMultipleSelection());
                localitySelectionModelUpdate.setDefaultValue(localitySelectionModel.getDefaultValue());
                break;
            case TYPE_NAME_MODEL_ORGANIZATION_SELECTION:
                OrganizationSelectionModel organizationSelectionModelUpdate = (OrganizationSelectionModel) propertyModelUpdate;
                OrganizationSelectionModel organizationSelectionModel = (OrganizationSelectionModel) propertyModel;
                organizationSelectionModelUpdate.setMultipleSelection(organizationSelectionModel.isMultipleSelection());
                organizationSelectionModelUpdate.setDefaultValue(organizationSelectionModel.getDefaultValue());
                break;
        }

    }

    public WorkpackModel findById(Long id) {
        return workpackModelRepository.findAllByIdWorkpackModel(id).orElseThrow(
            () -> new NegocioException(ApplicationMessage.WORKPACKMODEL_NOT_FOUND));
    }

    public WorkpackModel findByIdWorkpack(Long idWorkpack) {
        return workpackModelRepository.findByIdWorkpack(idWorkpack).orElseThrow(
            () -> new NegocioException(ApplicationMessage.WORKPACKMODEL_NOT_FOUND));
    }

    public void delete(WorkpackModel workpackModel) {
        if (!isCanDelete(workpackModel)) {
            throw new NegocioException(ApplicationMessage.WORKPACKMODEL_DELETE_REALATIONSHIP_ERROR);
        }
        workpackModelRepository.delete(workpackModel);
    }

    private boolean isCanDelete(WorkpackModel workpackModel) {
        switch (workpackModel.getClass().getTypeName()) {
            case TYPE_NAME_MODEL_PORTFOLIO:
                PortfolioModel portfolioModel = (PortfolioModel) workpackModel;
                return portfolioModel.getInstances() == null || portfolioModel.getInstances().isEmpty();
            case TYPE_NAME_MODEL_PROGRAM:
                ProgramModel programModel = (ProgramModel) workpackModel;
                return programModel.getInstances() == null || programModel.getInstances().isEmpty();
            case TYPE_NAME_MODEL_ORGANIZER:
                OrganizerModel organizerModel = (OrganizerModel) workpackModel;
                return organizerModel.getInstances() == null || organizerModel.getInstances().isEmpty();
            case TYPE_NAME_MODEL_DELIVERABLE:
                DeliverableModel deliverableModel = (DeliverableModel) workpackModel;
                return deliverableModel.getInstances() == null || deliverableModel.getInstances().isEmpty();
            case TYPE_NAME_MODEL_PROJECT:
                ProjectModel projectModel = (ProjectModel) workpackModel;
                return projectModel.getInstances() == null || projectModel.getInstances().isEmpty();
            case TYPE_NAME_MODEL_MILESTONE:
                MilestoneModel milestoneModel = (MilestoneModel) workpackModel;
                return milestoneModel.getInstances() == null || milestoneModel.getInstances().isEmpty();
        }
        return true;
    }

    public WorkpackModel getWorkpackModel(WorkpackModelParamDto workpackModelParamDto) {
        validType(workpackModelParamDto);
        WorkpackModel workpackModel = null;
        Set<PropertyModel> propertyModels = null;
        if (workpackModelParamDto.getProperties() != null && !workpackModelParamDto.getProperties().isEmpty()) {
            propertyModels = getProperties(workpackModelParamDto);
        }
        switch (workpackModelParamDto.getClass().getTypeName()) {
            case PACKAGE_DTO + ".PortfolioModelParamDto":
                workpackModel = modelMapper.map(workpackModelParamDto, PortfolioModel.class);
                break;
            case PACKAGE_DTO + ".ProgramModelParamDto":
                workpackModel = modelMapper.map(workpackModelParamDto, ProgramModel.class);
                break;
            case PACKAGE_DTO + ".OrganizerModelParamDto":
                workpackModel = modelMapper.map(workpackModelParamDto, OrganizerModel.class);
                break;
            case PACKAGE_DTO + ".DeliverableModelParamDto":
                workpackModel = modelMapper.map(workpackModelParamDto, DeliverableModel.class);
                break;
            case PACKAGE_DTO + ".ProjectModelParamDto":
                workpackModel = modelMapper.map(workpackModelParamDto, ProjectModel.class);
                break;
            case PACKAGE_DTO + ".MilestoneModelParamDto":
                workpackModel = modelMapper.map(workpackModelParamDto, MilestoneModel.class);
                break;
        }
        if (workpackModel != null) {
            workpackModel.setProperties(propertyModels);
            if (workpackModelParamDto.getSortBy() != null && !CollectionUtils.isEmpty(workpackModel.getProperties())) {
                workpackModel.setSortBy(workpackModel.getProperties().stream().filter(
                    p -> p.getLabel() != null && p.getLabel().equals(
                        workpackModelParamDto.getSortBy())).findFirst().orElse(null));
            }
        }
        return workpackModel;
    }

    public WorkpackModelDetailDto getWorkpackModelDetailDto(WorkpackModel workpackModel) {
        Set<WorkpackModelDetailDto> children = null;
        WorkpackModelDto parent = null;
        PropertyModelDto sortBy = null;
        List<? extends PropertyModelDto> properties = null;
        if (workpackModel.getChildren() != null) {
            children = getChildren(workpackModel.getChildren());
            workpackModel.setChildren(null);
        }
        if (workpackModel.getParent() != null) {
            parent = getWorkpackModelDto(workpackModel.getParent());
            workpackModel.setParent(null);
        }
        if (!CollectionUtils.isEmpty(workpackModel.getProperties())) {
            properties = getPropertyModelDto(workpackModel);
        }
        if (workpackModel.getSortBy() != null) {
            sortBy = getPropertyModelDto(workpackModel.getSortBy());
            workpackModel.setSortBy(null);
        }
        WorkpackModelDetailDto detailDto = convertWorkpackModelDetailDto(workpackModel);
        if (detailDto != null) {
            detailDto.setChildren(children);
            detailDto.setParent(parent);
            detailDto.setProperties(properties);
            detailDto.setSortBy(sortBy);
        }
        return detailDto;
    }

    private List<? extends PropertyModelDto> getPropertyModelDto(WorkpackModel workpackModel) {
        if (!CollectionUtils.isEmpty(workpackModel.getProperties())) {
            List<PropertyModelDto> list = new ArrayList<>();
            workpackModel.getProperties().forEach(propertyModel -> list.add(getPropertyModelDto(propertyModel)));
            list.sort(Comparator.comparing(PropertyModelDto::getSortIndex));
            return list;
        }
        return null;
    }

    public PropertyModelDto getPropertyModelDto(PropertyModel propertyModel) {
        switch (propertyModel.getClass().getTypeName()) {
            case TYPE_NAME_MODEL_INTEGER:
                return modelMapper.map(propertyModel, IntegerModelDto.class);
            case TYPE_NAME_MODEL_TEXT:
                return modelMapper.map(propertyModel, TextModelDto.class);
            case TYPE_NAME_MODEL_DATE:
                return modelMapper.map(propertyModel, DateModelDto.class);
            case TYPE_NAME_MODEL_TOGGLE:
                return modelMapper.map(propertyModel, ToggleModelDto.class);
            case TYPE_NAME_MODEL_UNIT_SELECTION:
                UnitSelectionModelDto unitDto = modelMapper.map(propertyModel, UnitSelectionModelDto.class);
                UnitSelectionModel unitModel = (UnitSelectionModel) propertyModel;
                if (unitModel.getDefaultValue() != null) {
                    unitDto.setDefaults(unitModel.getDefaultValue().getId());
                }
                return unitDto;
            case TYPE_NAME_MODEL_SELECTION:
                return modelMapper.map(propertyModel, SelectionModelDto.class);
            case TYPE_NAME_MODEL_TEXT_AREA:
                return modelMapper.map(propertyModel, TextAreaModelDto.class);
            case TYPE_NAME_MODEL_NUMBER:
                return modelMapper.map(propertyModel, NumberModelDto.class);
            case TYPE_NAME_MODEL_CURRENCY:
                return modelMapper.map(propertyModel, CurrencyModelDto.class);
            case TYPE_NAME_MODEL_LOCALITY_SELECTION:
                LocalitySelectionModelDto localityDto = modelMapper.map(propertyModel,
                                                                        LocalitySelectionModelDto.class);
                LocalitySelectionModel localityModel = (LocalitySelectionModel) propertyModel;
                if (!CollectionUtils.isEmpty(localityModel.getDefaultValue())) {
                    localityDto.setDefaults(new ArrayList<>());
                    localityModel.getDefaultValue().forEach(l -> localityDto.getDefaults().add(l.getId()));
                }
                if (localityModel.getDomain() != null) {
                    localityDto.setIdDomain(localityModel.getDomain().getId());
                }
                return localityDto;
            case TYPE_NAME_MODEL_ORGANIZATION_SELECTION:
                OrganizationSelectionModelDto organizationDto = modelMapper.map(propertyModel,
                                                                                OrganizationSelectionModelDto.class);
                OrganizationSelectionModel organizationModel = (OrganizationSelectionModel) propertyModel;
                if (!CollectionUtils.isEmpty(organizationModel.getDefaultValue())) {
                    organizationDto.setDefaults(new ArrayList<>());
                    organizationModel.getDefaultValue().forEach(
                        l -> organizationDto.getDefaults().add(l.getId()));
                }
                return organizationDto;
            default:
                return null;
        }
    }

    public WorkpackModelDto getWorkpackModelDto(WorkpackModel workpackModel) {
        PropertyModelDto sortBy = null;
        if (workpackModel.getSortBy() != null) {
            sortBy = getPropertyModelDto(workpackModel.getSortBy());
        }
        workpackModel.setSortBy(null);
        switch (workpackModel.getClass().getTypeName()) {
            case TYPE_NAME_MODEL_PORTFOLIO:
                PortfolioModelDto portfolioModelDto = modelMapper.map(workpackModel, PortfolioModelDto.class);
                portfolioModelDto.setSortBy(sortBy);
                return portfolioModelDto;
            case TYPE_NAME_MODEL_PROGRAM:
                ProgramModelDto programModelDto = modelMapper.map(workpackModel, ProgramModelDto.class);
                programModelDto.setSortBy(sortBy);
                return programModelDto;
            case TYPE_NAME_MODEL_ORGANIZER:
                OrganizerModelDto organizerModelDto = modelMapper.map(workpackModel, OrganizerModelDto.class);
                organizerModelDto.setSortBy(sortBy);
                return organizerModelDto;
            case TYPE_NAME_MODEL_DELIVERABLE:
                DeliverableModelDto deliverableModelDto = modelMapper.map(workpackModel, DeliverableModelDto.class);
                deliverableModelDto.setSortBy(sortBy);
                return deliverableModelDto;
            case TYPE_NAME_MODEL_PROJECT:
                ProjectModelDto projectModelDto = modelMapper.map(workpackModel, ProjectModelDto.class);
                projectModelDto.setSortBy(sortBy);
                return projectModelDto;
            case TYPE_NAME_MODEL_MILESTONE:
                MilestoneModelDto milestoneModelDto = modelMapper.map(workpackModel, MilestoneModelDto.class);
                milestoneModelDto.setSortBy(sortBy);
                return milestoneModelDto;
            default:
                return null;
        }
    }

    private WorkpackModelDetailDto convertWorkpackModelDetailDto(WorkpackModel workpackModel) {
        PropertyModelDto sortBy = null;
        if (workpackModel.getSortBy() != null) {
            sortBy = getPropertyModelDto(workpackModel.getSortBy());
        }
        switch (workpackModel.getClass().getTypeName()) {
            case TYPE_NAME_MODEL_PORTFOLIO:
                PortfolioModelDetailDto portfolioModelDetailDto = modelMapper.map(workpackModel, PortfolioModelDetailDto.class);
                portfolioModelDetailDto.setSortBy(sortBy);
                return portfolioModelDetailDto;
            case TYPE_NAME_MODEL_PROGRAM:
                ProgramModelDetailDto programModelDetailDto = modelMapper.map(workpackModel, ProgramModelDetailDto.class);
                programModelDetailDto.setSortBy(sortBy);
                return programModelDetailDto;
            case TYPE_NAME_MODEL_ORGANIZER:
                OrganizerModelDetailDto organizerModelDetailDto = modelMapper.map(workpackModel, OrganizerModelDetailDto.class);
                organizerModelDetailDto.setSortBy(sortBy);
                return organizerModelDetailDto;
            case TYPE_NAME_MODEL_DELIVERABLE:
                DeliverableModelDetailDto deliverableModelDetailDto = modelMapper.map(workpackModel, DeliverableModelDetailDto.class);
                deliverableModelDetailDto.setSortBy(sortBy);
                return deliverableModelDetailDto;
            case TYPE_NAME_MODEL_PROJECT:
                ProjectModelDetailDto projectModelDetailDto = modelMapper.map(workpackModel, ProjectModelDetailDto.class);
                projectModelDetailDto.setSortBy(sortBy);
                return projectModelDetailDto;
            case TYPE_NAME_MODEL_MILESTONE:
                MilestoneModelDetailDto milestoneModelDetailDto = modelMapper.map(workpackModel, MilestoneModelDetailDto.class);
                milestoneModelDetailDto.setSortBy(sortBy);
                return milestoneModelDetailDto;
            default:
                return null;
        }
    }

    public Set<WorkpackModelDetailDto> getChildren(Set<WorkpackModel> childrens) {
        if (childrens != null && !childrens.isEmpty()) {
            Set<WorkpackModelDetailDto> set = new HashSet<>();
            childrens.forEach(w -> {
                Set<WorkpackModelDetailDto> childrenChild = null;
                if (w.getChildren() != null && !w.getChildren().isEmpty()) {
                    childrenChild = getChildren(w.getChildren());
                }
                w.setParent(null);
                w.setChildren(null);
                WorkpackModelDetailDto detailDto = convertWorkpackModelDetailDto(w);
                if (detailDto != null) {
                    detailDto.setChildren(childrenChild);
                    set.add(detailDto);
                }
            });

            return set;
        }
        return null;
    }

    private void validType(WorkpackModelParamDto workpackModelParamDto) {
        if (workpackModelParamDto.getClass().getTypeName().equals(PACKAGE_DTO + ".DeliverableModelDto")
            || workpackModelParamDto.getClass().getTypeName().equals(PACKAGE_DTO + ".MilestoneModelDto")) {
            if (workpackModelParamDto.getIdParent() == null || !isChildFromProgramModel(
                workpackModelParamDto.getIdParent())) {
                throw new NegocioException(ApplicationMessage.WORKPACKMODEL_MILESTONE_DELIVERABLE_PROGRAM_ERROR);
            }
        }
    }

    private boolean isChildFromProgramModel(Long idWorkpackModel) {
        WorkpackModel workpackModel = findById(idWorkpackModel);
        boolean child = workpackModel.getClass().getTypeName().equals(TYPE_NAME_MODEL_PROJECT);
        if (!child && workpackModel.getParent() != null) {
            child = isChildFromProgramModel(workpackModel.getParent().getId());
        }
        return child;
    }

    private Set<PropertyModel> getProperties(WorkpackModelParamDto workpackModelParamDto) {
        Set<PropertyModel> propertyModels = new HashSet<>();
        workpackModelParamDto.getProperties().forEach(p -> {
            switch (p.getClass().getTypeName()) {
                case PACKAGE_DTO + ".IntegerModelDto":
                    propertyModels.add(modelMapper.map(p, IntegerModel.class));
                    break;
                case PACKAGE_DTO + ".TextModelDto":
                    propertyModels.add(modelMapper.map(p, TextModel.class));
                    break;
                case PACKAGE_DTO + ".DateModelDto":
                    propertyModels.add(modelMapper.map(p, DateModel.class));
                    break;
                case PACKAGE_DTO + ".ToggleModelDto":
                    propertyModels.add(modelMapper.map(p, ToggleModel.class));
                    break;
                case PACKAGE_DTO + ".UnitSelectionModelDto":
                    UnitSelectionModel unitSelectionModel = modelMapper.map(p, UnitSelectionModel.class);
                    UnitSelectionModelDto unitSelectionDto = (UnitSelectionModelDto) p;
                    if (unitSelectionDto.getDefaults() != null) {
                        unitSelectionModel.setDefaultValue(unitMeasureService.findById(unitSelectionDto.getDefaults()));
                    }
                    propertyModels.add(unitSelectionModel);
                    break;
                case PACKAGE_DTO + ".SelectionModelDto":
                    propertyModels.add(modelMapper.map(p, SelectionModel.class));
                    break;
                case PACKAGE_DTO + ".TextAreaModelDto":
                    propertyModels.add(modelMapper.map(p, TextAreaModel.class));
                    break;
                case PACKAGE_DTO + ".NumberModelDto":
                    propertyModels.add(modelMapper.map(p, NumberModel.class));
                    break;
                case PACKAGE_DTO + ".CurrencyModelDto":
                    propertyModels.add(modelMapper.map(p, CurrencyModel.class));
                    break;
                case PACKAGE_DTO + ".LocalitySelectionModelDto":
                    LocalitySelectionModel localitySelectionModel = modelMapper.map(p, LocalitySelectionModel.class);
                    LocalitySelectionModelDto localityDto = (LocalitySelectionModelDto) p;
                    if (localityDto.getIdDomain() != null) {
                        localitySelectionModel.setDomain(domainService.findById(localityDto.getIdDomain()));
                    }
                    if (!CollectionUtils.isEmpty(localityDto.getDefaults())) {
                        localitySelectionModel.setDefaultValue(new HashSet<>());
                        localityDto.getDefaults().forEach(
                            l -> localitySelectionModel.getDefaultValue().add(localityService.findById(l)));
                    }
                    propertyModels.add(localitySelectionModel);
                    break;
                case PACKAGE_DTO + ".OrganizationSelectionModelDto":
                    OrganizationSelectionModel organizationSelectionModel = modelMapper.map(p,
                                                                                            OrganizationSelectionModel.class);
                    OrganizationSelectionModelDto organizationDto = (OrganizationSelectionModelDto) p;
                    if (!CollectionUtils.isEmpty(organizationDto.getDefaults())) {
                        organizationSelectionModel.setDefaultValue(new HashSet<>());
                        organizationDto.getDefaults().forEach(
                            o -> organizationSelectionModel.getDefaultValue().add(organizationService.findById(o)));
                    }
                    propertyModels.add(organizationSelectionModel);
                    break;
            }
        });
        return propertyModels;
    }

    public Boolean isParentProject(Long id) {
        Boolean isParentProject = false;
        WorkpackModel workpack = workpackModelRepository.findByIdWithParents(id);
        if (workpack != null) {
            isParentProject = isParentProject(workpack);
        }
        return isParentProject;
    }

    private Boolean isParentProject(WorkpackModel workpackModel) {
        if (workpackModel.getClass().getTypeName().equals(TYPE_NAME_MODEL_PROJECT)) {
            return true;
        }
        if (workpackModel.getParent() != null) {
            return isParentProject(workpackModel.getParent());
        }
        return false;
    }

    public Set<WorkpackModel> findAllByIdPlanModelWithChildren(Long id) {
        return workpackModelRepository.findAllByIdPlanModelWithChildren(id);
    }

    public boolean isCanDeleteProperty(Long idPropertyModel) {
        return propertyModelService.canDeleteProperty(idPropertyModel);
    }

    public void deleteProperty(Long idPropertyModel) {
        PropertyModel propertyModel = propertyModelService.findById(idPropertyModel);
        if (!isCanDeleteProperty(idPropertyModel)) {
            throw new NegocioException(ApplicationMessage.PROPERTYMODEL_DELETE_REALATIONSHIP_ERROR);
        }
        Set<PropertyModel> properties = new HashSet<>();
        properties.add(propertyModel);
        propertyModelService.delete(properties);
    }
}
