package br.gov.es.openpmo.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import br.gov.es.openpmo.dto.costaccount.CostAccountDto;
import br.gov.es.openpmo.dto.costaccount.CostDto;
import br.gov.es.openpmo.dto.workpack.CurrencyDto;
import br.gov.es.openpmo.dto.workpack.DateDto;
import br.gov.es.openpmo.dto.workpack.IntegerDto;
import br.gov.es.openpmo.dto.workpack.LocalitySelectionDto;
import br.gov.es.openpmo.dto.workpack.NumberDto;
import br.gov.es.openpmo.dto.workpack.OrganizationSelectionDto;
import br.gov.es.openpmo.dto.workpack.PropertyDto;
import br.gov.es.openpmo.dto.workpack.SelectionDto;
import br.gov.es.openpmo.dto.workpack.TextAreaDto;
import br.gov.es.openpmo.dto.workpack.TextDto;
import br.gov.es.openpmo.dto.workpack.ToggleDto;
import br.gov.es.openpmo.dto.workpack.UnitSelectionDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.CostAccount;
import br.gov.es.openpmo.model.Currency;
import br.gov.es.openpmo.model.Date;
import br.gov.es.openpmo.model.Integer;
import br.gov.es.openpmo.model.LocalitySelection;
import br.gov.es.openpmo.model.Number;
import br.gov.es.openpmo.model.OrganizationSelection;
import br.gov.es.openpmo.model.Property;
import br.gov.es.openpmo.model.Selection;
import br.gov.es.openpmo.model.Text;
import br.gov.es.openpmo.model.TextArea;
import br.gov.es.openpmo.model.Toggle;
import br.gov.es.openpmo.model.UnitSelection;
import br.gov.es.openpmo.model.Workpack;
import br.gov.es.openpmo.model.relations.Consumes;
import br.gov.es.openpmo.repository.ConsumesRepository;
import br.gov.es.openpmo.repository.CostAccountRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;

@Service
public class CostAccountService {

    private final CostAccountRepository costAccountRepository;
    private final ConsumesRepository consumesRepository;
    private final WorkpackRepository workpackRepository;
    private final ModelMapper modelMapper;
    private final WorkpackModelService workpackModelService;

    @Autowired
    public CostAccountService(CostAccountRepository costAccountRepository,
                              ConsumesRepository consumesRepository,
                              WorkpackRepository workpackRepository,
                              ModelMapper modelMapper,
                              WorkpackModelService workpackModelService) {
        this.costAccountRepository = costAccountRepository;
        this.consumesRepository = consumesRepository;
        this.workpackRepository = workpackRepository;
        this.modelMapper = modelMapper;
        this.workpackModelService = workpackModelService;
    }

    public List<CostAccount> findAllByIdWorkpack(Long idWorkpack) {
        Workpack workpack = costAccountRepository.findWorkpackWithCosts(idWorkpack)
                                     .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
        return getCostAccount(workpack);
    }

    private List<CostAccount> getCostAccount(Workpack workpack) {
        List<CostAccount> costs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(workpack.getCosts())) {
            costs.addAll(workpack.getCosts());
        }
        if (workpack.getParent() != null) {
            costs.addAll(getCostAccount(workpack.getParent()));
        }
        return costs;
    }

    public CostAccount save(CostAccount costAccount) {
        return costAccountRepository.save(costAccount);
    }

    public CostAccount findById(Long id) {
        return costAccountRepository.findByIdWithPropertyModel(id).orElseThrow(
            () -> new NegocioException(ApplicationMessage.COST_ACCOUNT_NOT_FOUND));
    }

    public void delete(Set<CostAccount> costAccounts) {
        costAccountRepository.deleteAll(costAccounts);
    }

    public void delete(CostAccount costAccount) {
        List<Consumes> consumes = consumesRepository.findAllByIdCostAccount(costAccount.getId());
        if (!consumes.isEmpty()) {
            throw new NegocioException(ApplicationMessage.COST_ACCOUNT_DELETE_RELATIONSHIP_ERROR);
        }
        costAccountRepository.delete(costAccount);
    }

    public CostDto getCost(Long id, Long idWorkpack) {
        if (workpackRepository.findById(idWorkpack).isPresent()) {
            BigDecimal actual = BigDecimal.ZERO;
            BigDecimal planed = BigDecimal.ZERO;
            List<Consumes> consumes = consumesRepository.findAllByIdAndWorkpack(id, idWorkpack);
            for (Consumes consume : consumes) {
                if (consume.getActualCost() != null) {
                    actual = actual.add(consume.getActualCost());
                }
                if (consume.getPlannedCost() != null) {
                    planed = planed.add(consume.getPlannedCost());
                }
            }
            return new CostDto(idWorkpack, planed, actual);
        }
        return null;
    }

    public List<? extends PropertyDto> getPropertiesDto(Set<Property> properties, CostAccountDto costAccountDto) {
        if (!CollectionUtils.isEmpty(properties)) {
            costAccountDto.setModels(new ArrayList<>());
            List<PropertyDto> list =  new ArrayList<>();
            properties.forEach(property -> {
                switch (property.getClass().getTypeName()) {
                    case WorkpackService.TYPE_NAME_INTEGER:
                        IntegerDto integerDto = modelMapper.map(property, IntegerDto.class);
                        if (((Integer)property).getDriver() != null) {
                            integerDto.setIdPropertyModel(((Integer)property).getDriver().getId());
                            costAccountDto.getModels().add(workpackModelService.getPropertyModelDto(((Integer)property).getDriver()));
                        }
                        list.add(integerDto);
                        break;
                    case WorkpackService.TYPE_NAME_TEXT:
                        TextDto textDto = modelMapper.map(property, TextDto.class);
                        if (((Text)property).getDriver() != null) {
                            textDto.setIdPropertyModel(((Text)property).getDriver().getId());
                            costAccountDto.getModels().add(workpackModelService.getPropertyModelDto(((Text)property).getDriver()));
                        }
                        list.add(textDto);
                        break;
                    case WorkpackService.TYPE_NAME_DATE:
                        DateDto dateDto = modelMapper.map(property, DateDto.class);
                        if (((Date)property).getDriver() != null) {
                            dateDto.setIdPropertyModel(((Date)property).getDriver().getId());
                            costAccountDto.getModels().add(workpackModelService.getPropertyModelDto(((Date)property).getDriver()));
                        }
                        list.add(dateDto);
                        break;
                    case WorkpackService.TYPE_NAME_TOGGLE:
                        ToggleDto toggleDto = modelMapper.map(property, ToggleDto.class);
                        if (((Toggle)property).getDriver() != null) {
                            toggleDto.setIdPropertyModel(((Toggle)property).getDriver().getId());
                            costAccountDto.getModels().add(workpackModelService.getPropertyModelDto(((Toggle)property).getDriver()));
                        }
                        list.add(toggleDto);
                        break;
                    case WorkpackService.TYPE_NAME_UNIT_SELECTION:
                        UnitSelectionDto unitSelectionDto = modelMapper.map(property, UnitSelectionDto.class);
                        if (((UnitSelection)property).getDriver() != null) {
                            unitSelectionDto.setIdPropertyModel(((UnitSelection)property).getDriver().getId());
                            costAccountDto.getModels().add(workpackModelService.getPropertyModelDto(((UnitSelection)property).getDriver()));
                        }
                        if (((UnitSelection) property).getValue() != null) {
                            unitSelectionDto.setSelectedValue(((UnitSelection) property).getValue().getId());
                        }
                        list.add(unitSelectionDto);
                        break;
                    case WorkpackService.TYPE_NAME_SELECTION:
                        SelectionDto selectionDto = modelMapper.map(property, SelectionDto.class);
                        if (((Selection)property).getDriver() != null) {
                            selectionDto.setIdPropertyModel(((Selection)property).getDriver().getId());
                            costAccountDto.getModels().add(workpackModelService.getPropertyModelDto(((Selection)property).getDriver()));
                        }
                        list.add(selectionDto);
                        break;
                    case WorkpackService.TYPE_NAME_TEXT_AREA:
                        TextAreaDto textAreaDto = modelMapper.map(property, TextAreaDto.class);
                        if (((TextArea)property).getDriver() != null) {
                            textAreaDto.setIdPropertyModel(((TextArea)property).getDriver().getId());
                            costAccountDto.getModels().add(workpackModelService.getPropertyModelDto(((TextArea)property).getDriver()));
                        }
                        list.add(textAreaDto);
                        break;
                    case WorkpackService.TYPE_NAME_NUMBER:
                        NumberDto numberDto = modelMapper.map(property, NumberDto.class);
                        if (((Number)property).getDriver() != null) {
                            numberDto.setIdPropertyModel(((Number)property).getDriver().getId());
                            costAccountDto.getModels().add(workpackModelService.getPropertyModelDto(((Number)property).getDriver()));
                        }
                        list.add(numberDto);
                        break;
                    case WorkpackService.TYPE_NAME_CURRENCY:
                        CurrencyDto currencyDto = modelMapper.map(property, CurrencyDto.class);
                        if (((Currency)property).getDriver() != null) {
                            currencyDto.setIdPropertyModel(((Currency)property).getDriver().getId());
                            costAccountDto.getModels().add(workpackModelService.getPropertyModelDto(((Currency)property).getDriver()));
                        }
                        list.add(currencyDto);
                        break;
                    case WorkpackService.TYPE_NAME_LOCALITY_SELECTION:
                        LocalitySelectionDto localitySelectionDto = modelMapper.map(property, LocalitySelectionDto.class);
                        if (((LocalitySelection)property).getDriver() != null) {
                            localitySelectionDto.setIdPropertyModel(((LocalitySelection)property).getDriver().getId());
                            costAccountDto.getModels().add(workpackModelService.getPropertyModelDto(((LocalitySelection)property).getDriver()));
                        }
                        if (((LocalitySelection) property).getValue() != null) {
                            localitySelectionDto.setSelectedValues(new HashSet<>());
                            ((LocalitySelection) property).getValue().forEach(o -> localitySelectionDto.getSelectedValues().add(o.getId()));
                        }
                        list.add(localitySelectionDto);
                        break;
                    case WorkpackService.TYPE_NAME_ORGANIZATION_SELECTION:
                        OrganizationSelectionDto organizationSelectionDto = modelMapper.map(property, OrganizationSelectionDto.class);
                        if (((OrganizationSelection)property).getDriver() != null) {
                            organizationSelectionDto.setIdPropertyModel(((OrganizationSelection)property).getDriver().getId());
                            costAccountDto.getModels().add(workpackModelService.getPropertyModelDto(((OrganizationSelection)property).getDriver()));
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
}
