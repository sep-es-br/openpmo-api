package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.costaccount.CostAccountDto;
import br.gov.es.openpmo.dto.costaccount.CostDto;
import br.gov.es.openpmo.dto.workpack.*;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.properties.Integer;
import br.gov.es.openpmo.model.properties.Number;
import br.gov.es.openpmo.model.properties.*;
import br.gov.es.openpmo.model.relations.Consumes;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.ConsumesRepository;
import br.gov.es.openpmo.repository.CostAccountRepository;
import br.gov.es.openpmo.repository.CustomFilterRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.custom.filters.FindAllCostAccountUsingCustomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.COST_ACCOUNT_DELETE_RELATIONSHIP_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.COST_ACCOUNT_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.CUSTOM_FILTER_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;
import static br.gov.es.openpmo.utils.PropertyInstanceTypeDeprecated.*;

@Service
public class CostAccountService {

  private final CostAccountRepository costAccountRepository;
  private final ConsumesRepository consumesRepository;
  private final WorkpackRepository workpackRepository;
  private final CustomFilterRepository customFilterRepository;
  private final WorkpackModelService workpackModelService;
  private final FindAllCostAccountUsingCustomFilter findAllCostAccount;

  @Autowired
  public CostAccountService(
    final CostAccountRepository costAccountRepository,
    final ConsumesRepository consumesRepository,
    final WorkpackRepository workpackRepository,
    final CustomFilterRepository customFilterRepository,
    final WorkpackModelService workpackModelService,
    final FindAllCostAccountUsingCustomFilter findAllCostAccount
  ) {
    this.costAccountRepository = costAccountRepository;
    this.consumesRepository = consumesRepository;
    this.workpackRepository = workpackRepository;
    this.customFilterRepository = customFilterRepository;
    this.workpackModelService = workpackModelService;
    this.findAllCostAccount = findAllCostAccount;
  }

  public List<CostAccountDto> findAllByIdWorkpack(
    final Long idWorkpack,
    final Long idFilter
  ) {

    if(idFilter == null) {
      return this.findAllByIdWorkpack(idWorkpack)
        .stream()
        .map(this::mapToDto)
        .collect(Collectors.toList());
    }

    final CustomFilter filter = this.customFilterRepository
      .findById(idFilter)
      .orElseThrow(() -> new NegocioException(CUSTOM_FILTER_NOT_FOUND));

    final Map<String, Object> params = new HashMap<>();
    params.put("idWorkpack", idWorkpack);

    return this.findAllCostAccount.execute(filter, params)
      .stream()
      .map(CostAccount.class::cast)
      .map(this::mapToDto)
      .collect(Collectors.toList());
  }

  private CostAccountDto mapToDto(final CostAccount costAccount) {
    final CostAccountDto dto = CostAccountDto.withoutRelations(costAccount);
    this.maybeSetWorkpackNameData(dto, costAccount.getWorkpackId());
    if(costAccount.getWorkpack() != null) {
      dto.setIdWorkpack(costAccount.getWorkpackId());
    }
    if(costAccount.getProperties() != null && !(costAccount.getProperties()).isEmpty()) {
      dto.setProperties(this.getPropertiesDto(costAccount.getProperties(), dto));
    }
    return dto;
  }

  private void maybeSetWorkpackNameData(
    final CostAccountDto dto,
    final Long workpackId
  ) {
    final Optional<WorkpackName> maybeWorkpackName = this.maybeGetWorkpackNameData(workpackId);
    if(maybeWorkpackName.isPresent()) {
      final WorkpackName workpackName = maybeWorkpackName.get();
      dto.setWorkpackModelName(workpackName.getName());
      dto.setWorkpackModelFullName(workpackName.getFullName());
    }
  }

  private Optional<WorkpackName> maybeGetWorkpackNameData(final Long workpackId) {
    return this.workpackRepository.findWorkpackNameAndFullname(workpackId);
  }

  public List<CostAccount> findAllByIdWorkpack(
    final Long idWorkpack
  ) {
    final Workpack workpack = this.costAccountRepository.findWorkpackWithCosts(idWorkpack)
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
    return this.fetchCostAccountFromWorkpack(workpack);
  }

  private List<CostAccount> fetchCostAccountFromWorkpack(final Workpack workpack) {
    final List<CostAccount> costs = new ArrayList<>();
    if(workpack.getCosts() != null && !(workpack.getCosts()).isEmpty()) {
      costs.addAll(workpack.getCosts());
    }
    if(workpack.getParent() != null) {
      costs.addAll(this.fetchCostAccountFromWorkpackParent(workpack.getParent()));
    }
    return costs;
  }

  private List<CostAccount> fetchCostAccountFromWorkpackParent(final Iterable<? extends Workpack> parent) {
    final List<CostAccount> costs = new ArrayList<>();
    for(final Workpack workpack : parent) {
      costs.addAll(this.fetchCostAccountFromWorkpack(workpack));
    }
    return costs;
  }

  public CostAccount save(final CostAccount costAccount) {
    return this.costAccountRepository.save(costAccount);
  }

  public CostAccount findById(final Long id) {
    return this.costAccountRepository.findByIdWithPropertyModel(id).orElseThrow(
      () -> new NegocioException(COST_ACCOUNT_NOT_FOUND));
  }

  public CostAccountDto findByIdAsDto(final Long id) {
    final CostAccount costAccount = this.findById(id);
    final CostAccountDto costAccountDto = CostAccountDto.withoutRelations(costAccount);

    this.maybeSetWorkpackNameData(costAccountDto, costAccount.getWorkpackId());

    if(costAccount.getProperties() != null && !(costAccount.getProperties()).isEmpty()) {
      costAccountDto.setProperties(
        this.getPropertiesDto(costAccount.getProperties(), costAccountDto));
    }
    if(costAccount.getWorkpack() != null) {
      costAccountDto.setIdWorkpack(costAccount.getWorkpack().getId());
    }
    return costAccountDto;
  }

  public void delete(final Iterable<? extends CostAccount> costAccounts) {
    this.costAccountRepository.deleteAll(costAccounts);
  }

  public void delete(final CostAccount costAccount) {
    final List<Consumes> consumes = this.consumesRepository.findAllByIdCostAccount(costAccount.getId());
    if(!consumes.isEmpty()) {
      throw new NegocioException(COST_ACCOUNT_DELETE_RELATIONSHIP_ERROR);
    }
    this.costAccountRepository.delete(costAccount);
  }

  public CostDto getCost(
    final Long id,
    final Long idWorkpack
  ) {
    if(this.workpackRepository.findById(idWorkpack).isPresent()) {
      BigDecimal actual = BigDecimal.ZERO;
      BigDecimal planed = BigDecimal.ZERO;
      final List<Consumes> consumes = this.consumesRepository.findAllByIdAndWorkpack(id, idWorkpack);
      for(final Consumes consume : consumes) {
        if(consume.getActualCost() != null) {
          actual = actual.add(consume.getActualCost());
        }
        if(consume.getPlannedCost() != null) {
          planed = planed.add(consume.getPlannedCost());
        }
      }
      return new CostDto(idWorkpack, planed, actual);
    }
    return null;
  }

  public List<? extends PropertyDto> getPropertiesDto(
    final Collection<? extends Property> properties,
    final CostAccountDto costAccountDto
  ) {
    if(properties == null || properties.isEmpty()) {return null;}
    costAccountDto.setModels(new ArrayList<>());
    final List<PropertyDto> list = new ArrayList<>();
    properties.forEach(property -> {
      switch(property.getClass().getTypeName()) {
        case TYPE_MODEL_NAME_INTEGER:
          final IntegerDto integerDto = IntegerDto.of(property);
          if(((Integer) property).getDriver() != null) {
            integerDto.setIdPropertyModel(((Integer) property).getDriver().getId());
            costAccountDto.getModels().add(this.workpackModelService.getPropertyModelDto(((Integer) property).getDriver()));
          }
          list.add(integerDto);
          break;
        case TYPE_MODEL_NAME_TEXT:
          final TextDto textDto = TextDto.of(property);
          if(((Text) property).getDriver() != null) {
            textDto.setIdPropertyModel(((Text) property).getDriver().getId());
            costAccountDto.getModels().add(this.workpackModelService.getPropertyModelDto(((Text) property).getDriver()));
          }
          list.add(textDto);
          break;
        case TYPE_MODEL_NAME_DATE:
          final DateDto dateDto = DateDto.of(property);
          if(((Date) property).getDriver() != null) {
            dateDto.setIdPropertyModel(((Date) property).getDriver().getId());
            costAccountDto.getModels().add(this.workpackModelService.getPropertyModelDto(((Date) property).getDriver()));
          }
          list.add(dateDto);
          break;
        case TYPE_MODEL_NAME_TOGGLE:
          final ToggleDto toggleDto = ToggleDto.of(property);
          if(((Toggle) property).getDriver() != null) {
            toggleDto.setIdPropertyModel(((Toggle) property).getDriver().getId());
            costAccountDto.getModels().add(this.workpackModelService.getPropertyModelDto(((Toggle) property).getDriver()));
          }
          list.add(toggleDto);
          break;
        case TYPE_MODEL_NAME_UNIT_SELECTION:
          final UnitSelectionDto unitSelectionDto = UnitSelectionDto.of(property);
          if(((UnitSelection) property).getDriver() != null) {
            unitSelectionDto.setIdPropertyModel(((UnitSelection) property).getDriver().getId());
            costAccountDto.getModels().add(this.workpackModelService.getPropertyModelDto(((UnitSelection) property).getDriver()));
          }
          if(((UnitSelection) property).getValue() != null) {
            unitSelectionDto.setSelectedValue(((UnitSelection) property).getValue().getId());
          }
          list.add(unitSelectionDto);
          break;
        case TYPE_MODEL_NAME_SELECTION:
          final SelectionDto selectionDto = SelectionDto.of(property);
          if(((Selection) property).getDriver() != null) {
            selectionDto.setIdPropertyModel(((Selection) property).getDriver().getId());
            costAccountDto.getModels().add(this.workpackModelService.getPropertyModelDto(((Selection) property).getDriver()));
          }
          list.add(selectionDto);
          break;
        case TYPE_MODEL_NAME_TEXT_AREA:
          final TextAreaDto textAreaDto = TextAreaDto.of(property);
          if(((TextArea) property).getDriver() != null) {
            textAreaDto.setIdPropertyModel(((TextArea) property).getDriver().getId());
            costAccountDto.getModels().add(this.workpackModelService.getPropertyModelDto(((TextArea) property).getDriver()));
          }
          list.add(textAreaDto);
          break;
        case TYPE_MODEL_NAME_NUMBER:
          final NumberDto numberDto = NumberDto.of(property);
          if(((Number) property).getDriver() != null) {
            numberDto.setIdPropertyModel(((Number) property).getDriver().getId());
            costAccountDto.getModels().add(this.workpackModelService.getPropertyModelDto(((Number) property).getDriver()));
          }
          list.add(numberDto);
          break;
        case TYPE_MODEL_NAME_CURRENCY:
          final CurrencyDto currencyDto = CurrencyDto.of(property);
          if(((Currency) property).getDriver() != null) {
            currencyDto.setIdPropertyModel(((Currency) property).getDriver().getId());
            costAccountDto.getModels().add(this.workpackModelService.getPropertyModelDto(((Currency) property).getDriver()));
          }
          list.add(currencyDto);
          break;
        case TYPE_MODEL_NAME_LOCALITY_SELECTION:
          final LocalitySelectionDto localitySelectionDto = LocalitySelectionDto.of(property);
          if(((LocalitySelection) property).getDriver() != null) {
            localitySelectionDto.setIdPropertyModel(((LocalitySelection) property).getDriver().getId());
            costAccountDto.getModels().add(this.workpackModelService.getPropertyModelDto(((LocalitySelection) property).getDriver()));
          }
          if(((LocalitySelection) property).getValue() != null) {
            localitySelectionDto.setSelectedValues(new HashSet<>());
            ((LocalitySelection) property).getValue().forEach(o -> localitySelectionDto.getSelectedValues().add(o.getId()));
          }
          list.add(localitySelectionDto);
          break;
        case TYPE_MODEL_NAME_ORGANIZATION_SELECTION:
          final OrganizationSelectionDto organizationSelectionDto = OrganizationSelectionDto.of(property);
          if(((OrganizationSelection) property).getDriver() != null) {
            organizationSelectionDto.setIdPropertyModel(((OrganizationSelection) property).getDriver().getId());
            costAccountDto.getModels().add(this.workpackModelService.getPropertyModelDto(((OrganizationSelection) property).getDriver()));
          }
          if(((OrganizationSelection) property).getValue() != null) {
            organizationSelectionDto.setSelectedValues(new HashSet<>());
            ((OrganizationSelection) property).getValue().forEach(o -> organizationSelectionDto.getSelectedValues().add(o.getId()));
          }
          list.add(organizationSelectionDto);
          break;
      }
    });
    return list;
  }

}
