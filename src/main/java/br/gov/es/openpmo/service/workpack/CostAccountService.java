package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.configuration.properties.AppProperties;
import br.gov.es.openpmo.dto.costaccount.CostAccountDto;
import br.gov.es.openpmo.dto.costaccount.CostAccountStoreDto;
import br.gov.es.openpmo.dto.costaccount.CostAccountUpdateDto;
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
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.budget.PlanoOrcamentario;
import br.gov.es.openpmo.model.budget.UnidadeOrcamentaria;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.properties.Currency;
import br.gov.es.openpmo.model.properties.Date;
import br.gov.es.openpmo.model.properties.Integer;
import br.gov.es.openpmo.model.properties.LocalitySelection;
import br.gov.es.openpmo.model.properties.Number;
import br.gov.es.openpmo.model.properties.OrganizationSelection;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.properties.Selection;
import br.gov.es.openpmo.model.properties.Text;
import br.gov.es.openpmo.model.properties.TextArea;
import br.gov.es.openpmo.model.properties.Toggle;
import br.gov.es.openpmo.model.properties.UnitSelection;
import br.gov.es.openpmo.model.relations.Consumes;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.CostAccountModel;
import br.gov.es.openpmo.repository.*;
import br.gov.es.openpmo.repository.custom.filters.FindAllCostAccountUsingCustomFilter;
import br.gov.es.openpmo.service.reports.models.GetPropertyModelDtoFromEntity;
import br.gov.es.openpmo.utils.ApplicationCacheUtil;
import br.gov.es.openpmo.utils.ApplicationMessage;
import br.gov.es.openpmo.utils.TextSimilarityScore;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
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
import java.util.Set;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.COST_ACCOUNT_DELETE_RELATIONSHIP_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.COST_ACCOUNT_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.CUSTOM_FILTER_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;
import static br.gov.es.openpmo.utils.PropertyInstanceTypeDeprecated.TYPE_MODEL_NAME_CURRENCY;
import static br.gov.es.openpmo.utils.PropertyInstanceTypeDeprecated.TYPE_MODEL_NAME_DATE;
import static br.gov.es.openpmo.utils.PropertyInstanceTypeDeprecated.TYPE_MODEL_NAME_INTEGER;
import static br.gov.es.openpmo.utils.PropertyInstanceTypeDeprecated.TYPE_MODEL_NAME_LOCALITY_SELECTION;
import static br.gov.es.openpmo.utils.PropertyInstanceTypeDeprecated.TYPE_MODEL_NAME_NUMBER;
import static br.gov.es.openpmo.utils.PropertyInstanceTypeDeprecated.TYPE_MODEL_NAME_ORGANIZATION_SELECTION;
import static br.gov.es.openpmo.utils.PropertyInstanceTypeDeprecated.TYPE_MODEL_NAME_SELECTION;
import static br.gov.es.openpmo.utils.PropertyInstanceTypeDeprecated.TYPE_MODEL_NAME_TEXT;
import static br.gov.es.openpmo.utils.PropertyInstanceTypeDeprecated.TYPE_MODEL_NAME_TEXT_AREA;
import static br.gov.es.openpmo.utils.PropertyInstanceTypeDeprecated.TYPE_MODEL_NAME_TOGGLE;
import static br.gov.es.openpmo.utils.PropertyInstanceTypeDeprecated.TYPE_MODEL_NAME_UNIT_SELECTION;

@Service
public class CostAccountService {

  private final CostAccountRepository costAccountRepository;

  private final ConsumesRepository consumesRepository;

  private final WorkpackRepository workpackRepository;

  private final CustomFilterRepository customFilterRepository;

  private final CostAccountSorter costAccountSorter;

  private final FindAllCostAccountUsingCustomFilter findAllCostAccount;

  private final AppProperties appProperties;

  private final TextSimilarityScore textSimilarityScore;

  private final GetPropertyModelDtoFromEntity getPropertyModelDtoFromEntity;

  private final WorkpackService workpackService;

  private final ModelMapper modelMapper;

  private final CostAccountModelRepository costAccountModelRepository;

  private final PropertyRepository propertyRepository;

  private final ApplicationCacheUtil applicationCacheUtil;

  private final UnidadeOrcamentariaRepository unidadeOrcamentariaRepository;

  private final PlanoOrcamentarioRepository planoOrcamentarioRepository;

  @Autowired
  public CostAccountService(
    final CostAccountRepository costAccountRepository,
    final ConsumesRepository consumesRepository,
    final WorkpackRepository workpackRepository,
    final CustomFilterRepository customFilterRepository,
    final FindAllCostAccountUsingCustomFilter findAllCostAccount,
    final AppProperties appProperties,
    final TextSimilarityScore textSimilarityScore,
    final CostAccountSorter costAccountSorter,
    final GetPropertyModelDtoFromEntity getPropertyModelDtoFromEntity,
    final WorkpackService workpackService,
    final ModelMapper modelMapper,
    final CostAccountModelRepository costAccountModelRepository,
    final ApplicationCacheUtil applicationCacheUtil,
    final PropertyRepository propertyRepository,
    final UnidadeOrcamentariaRepository unidadeOrcamentariaRepository,
    final PlanoOrcamentarioRepository planoOrcamentarioRepository
  ) {
    this.costAccountRepository = costAccountRepository;
    this.consumesRepository = consumesRepository;
    this.workpackRepository = workpackRepository;
    this.customFilterRepository = customFilterRepository;
    this.costAccountSorter = costAccountSorter;
    this.findAllCostAccount = findAllCostAccount;
    this.appProperties = appProperties;
    this.textSimilarityScore = textSimilarityScore;
    this.getPropertyModelDtoFromEntity = getPropertyModelDtoFromEntity;
    this.workpackService = workpackService;
    this.modelMapper = modelMapper;
    this.costAccountModelRepository = costAccountModelRepository;
    this.applicationCacheUtil = applicationCacheUtil;
    this.propertyRepository = propertyRepository;
    this.unidadeOrcamentariaRepository = unidadeOrcamentariaRepository;
    this.planoOrcamentarioRepository = planoOrcamentarioRepository;
  }

  public List<CostAccountDto> findAllByIdWorkpack(
    final Long idWorkpack,
    final Long idFilter,
    final String term
  ) {

    if (idFilter == null) {
      return this.findAllByIdWorkpack(idWorkpack).stream().map(this::mapToDto)
        .filter(dto -> StringUtils.isBlank(term) || this.filterBySimilarity(term, dto))
        .collect(Collectors.toList());
    }

    final CustomFilter filter = this.customFilterRepository
      .findById(idFilter)
      .orElseThrow(() -> new NegocioException(CUSTOM_FILTER_NOT_FOUND));

    final Map<String, Object> params = new HashMap<>();
    params.put("idWorkpack", idWorkpack);

    final List<CostAccount> costAccounts = this.findAllCostAccount.execute(
        filter,
        params
      ).stream()
      .flatMap(workpack -> this.fetchCostAccountFromWorkpack((Workpack) workpack).stream())
      .collect(Collectors.toList());

    final List<CostAccount> costAccountsSorted = this.costAccountSorter.sort(
      new CostAccountSorter.CostAccountSorterRequest(
        idFilter,
        costAccounts
      )
    );

    return costAccountsSorted
      .stream()
      .map(this::mapToDto)
      .filter(dto -> StringUtils.isBlank(term) || this.filterBySimilarity(term, dto))
      .collect(Collectors.toList());
  }

  public CostAccount save(final CostAccount costAccount) {
    return this.costAccountRepository.save(costAccount);
  }

  public CostAccount findById(final Long id) {
    CostAccount costAccount = this.costAccountRepository.findByIdWithPropertyModel(id)
      .orElseThrow(() -> new NegocioException(COST_ACCOUNT_NOT_FOUND));

    Optional<UnidadeOrcamentaria> uoOpt = unidadeOrcamentariaRepository.findByIdCostAccount(id);
    uoOpt.ifPresent(costAccount::setUnidadeOrcamentaria);

    Optional<PlanoOrcamentario> poOpt = planoOrcamentarioRepository.findByIdCostAccout(id);
    poOpt.ifPresent(costAccount::setPlanoOrcamentario);

    return costAccount;
  }

  public CostAccountDto findByIdAsDto(final Long id) {
    final CostAccount costAccount = this.findById(id);
    final CostAccountDto costAccountDto = mapToDto(costAccount);

    this.maybeSetWorkpackNameData(
      costAccountDto,
      costAccount.getWorkpackId()
    );

    if (costAccount.getProperties() != null && !(costAccount.getProperties()).isEmpty()) {
      costAccountDto.setProperties(
        this.getPropertiesDto(
          costAccount.getProperties(),
          costAccountDto
        ));
    }
    if (costAccount.getWorkpack() != null) {
      costAccountDto.setIdWorkpack(costAccount.getWorkpack().getId());
    }

    return costAccountDto;
  }

  public void delete(final Iterable<? extends CostAccount> costAccounts) {
    this.costAccountRepository.deleteAll(costAccounts);
  }

  public void delete(final CostAccount costAccount) {
    final List<Consumes> consumes = this.consumesRepository.findAllByIdCostAccount(costAccount.getId());
    if (!consumes.isEmpty()) {
      throw new NegocioException(COST_ACCOUNT_DELETE_RELATIONSHIP_ERROR);
    }
    this.costAccountRepository.delete(costAccount);
  }

  public CostDto getCost(
    final Long id,
    final Long idWorkpack
  ) {
    if (this.workpackRepository.findById(idWorkpack).isPresent()) {
      BigDecimal actual = BigDecimal.ZERO;
      BigDecimal planed = BigDecimal.ZERO;
      final List<Consumes> consumes = this.consumesRepository.findAllByIdAndWorkpack(
        id,
        idWorkpack
      );
      for (final Consumes consume : consumes) {
        if (consume.getActualCost() != null) {
          actual = actual.add(consume.getActualCost());
        }
        if (consume.getPlannedCost() != null) {
          planed = planed.add(consume.getPlannedCost());
        }
      }
      return new CostDto(
        idWorkpack,
        planed,
        actual
      );
    }
    return null;
  }

  private List<CostAccount> findAllByIdWorkpack(final Long idWorkpack) {
    List<Long> idsWorkpakWithParents = applicationCacheUtil.getListIdWorkpackWithParent(idWorkpack);
    return this.costAccountRepository.findAllByWorkpackId(idsWorkpakWithParents);
  }

  private List<? extends PropertyDto> getPropertiesDto(
    final Collection<? extends Property> properties,
    final CostAccountDto costAccountDto
  ) {
    if (properties == null || properties.isEmpty()) {
      return null;
    }
    costAccountDto.setModels(new ArrayList<>());
    final List<PropertyDto> list = new ArrayList<>();
    properties.forEach(property -> {
      switch (property.getClass().getTypeName()) {
        case TYPE_MODEL_NAME_INTEGER:
          final IntegerDto integerDto = IntegerDto.of(property);
          if (((Integer) property).getDriver() != null) {
            integerDto.setIdPropertyModel(((Integer) property).getDriver().getId());
            costAccountDto.getModels().add(this.getPropertyModelDtoFromEntity.execute(((Integer) property).getDriver()));
          }
          list.add(integerDto);
          break;
        case TYPE_MODEL_NAME_TEXT:
          final TextDto textDto = TextDto.of(property);
          if (((Text) property).getDriver() != null) {
            textDto.setIdPropertyModel(((Text) property).getDriver().getId());
            costAccountDto.getModels().add(this.getPropertyModelDtoFromEntity.execute(((Text) property).getDriver()));
          }
          list.add(textDto);
          break;
        case TYPE_MODEL_NAME_DATE:
          final DateDto dateDto = DateDto.of(property);
          if (((Date) property).getDriver() != null) {
            dateDto.setIdPropertyModel(((Date) property).getDriver().getId());
            costAccountDto.getModels().add(this.getPropertyModelDtoFromEntity.execute(((Date) property).getDriver()));
          }
          list.add(dateDto);
          break;
        case TYPE_MODEL_NAME_TOGGLE:
          final ToggleDto toggleDto = ToggleDto.of(property);
          if (((Toggle) property).getDriver() != null) {
            toggleDto.setIdPropertyModel(((Toggle) property).getDriver().getId());
            costAccountDto.getModels().add(this.getPropertyModelDtoFromEntity.execute(((Toggle) property).getDriver()));
          }
          list.add(toggleDto);
          break;
        case TYPE_MODEL_NAME_UNIT_SELECTION:
          final UnitSelectionDto unitSelectionDto = UnitSelectionDto.of(property);
          if (((UnitSelection) property).getDriver() != null) {
            unitSelectionDto.setIdPropertyModel(((UnitSelection) property).getDriver().getId());
            costAccountDto.getModels().add(this.getPropertyModelDtoFromEntity.execute(((UnitSelection) property).getDriver()));
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
            costAccountDto.getModels().add(this.getPropertyModelDtoFromEntity.execute(((Selection) property).getDriver()));
          }
          list.add(selectionDto);
          break;
        case TYPE_MODEL_NAME_TEXT_AREA:
          final TextAreaDto textAreaDto = TextAreaDto.of(property);
          if (((TextArea) property).getDriver() != null) {
            textAreaDto.setIdPropertyModel(((TextArea) property).getDriver().getId());
            costAccountDto.getModels().add(this.getPropertyModelDtoFromEntity.execute(((TextArea) property).getDriver()));
          }
          list.add(textAreaDto);
          break;
        case TYPE_MODEL_NAME_NUMBER:
          final NumberDto numberDto = NumberDto.of(property);
          if (((Number) property).getDriver() != null) {
            numberDto.setIdPropertyModel(((Number) property).getDriver().getId());
            costAccountDto.getModels().add(this.getPropertyModelDtoFromEntity.execute(((Number) property).getDriver()));
          }
          list.add(numberDto);
          break;
        case TYPE_MODEL_NAME_CURRENCY:
          final CurrencyDto currencyDto = CurrencyDto.of(property);
          if (((Currency) property).getDriver() != null) {
            currencyDto.setIdPropertyModel(((Currency) property).getDriver().getId());
            costAccountDto.getModels().add(this.getPropertyModelDtoFromEntity.execute(((Currency) property).getDriver()));
          }
          list.add(currencyDto);
          break;
        case TYPE_MODEL_NAME_LOCALITY_SELECTION:
          final LocalitySelectionDto localitySelectionDto = LocalitySelectionDto.of(property);
          if (((LocalitySelection) property).getDriver() != null) {
            localitySelectionDto.setIdPropertyModel(((LocalitySelection) property).getDriver().getId());
            costAccountDto.getModels().add(this.getPropertyModelDtoFromEntity.execute(((LocalitySelection) property).getDriver()));
          }
          if (((LocalitySelection) property).getValue() != null) {
            localitySelectionDto.setSelectedValues(new HashSet<>());
            ((LocalitySelection) property).getValue().forEach(o -> localitySelectionDto.getSelectedValues().add(o.getId()));
          }
          list.add(localitySelectionDto);
          break;
        case TYPE_MODEL_NAME_ORGANIZATION_SELECTION:
          final OrganizationSelectionDto organizationSelectionDto = OrganizationSelectionDto.of(property);
          if (((OrganizationSelection) property).getDriver() != null) {
            organizationSelectionDto.setIdPropertyModel(((OrganizationSelection) property).getDriver().getId());
            costAccountDto.getModels().add(this.getPropertyModelDtoFromEntity.execute(((OrganizationSelection) property).getDriver()));
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

  private boolean filterBySimilarity(final String term, final CostAccountDto dto) {
    final double score = this.textSimilarityScore.execute(dto.getWorkpackModelName() + dto.getWorkpackModelFullName(), term);
    return score > this.appProperties.getSearchCutOffScore();
  }

  private CostAccountDto mapToDto(final CostAccount costAccount) {
    final CostAccountDto dto = CostAccountDto.of(costAccount);
    final Long workpackId = costAccount.getWorkpackId();
    this.maybeSetWorkpackNameData(
      dto,
      workpackId
    );
    if (costAccount.getWorkpack() != null) {
      dto.setIdWorkpack(workpackId);
    }
    if (costAccount.getProperties() != null && !(costAccount.getProperties()).isEmpty()) {
      dto.setProperties(this.getPropertiesDto(
        costAccount.getProperties(),
        dto
      ));
    }
    final Long id = costAccount.getId();
    final CostDto cost = this.getCost(
      id,
      workpackId
    );
    cost.setIdWorkpack(null);
    cost.setLimit(this.costAccountRepository.findCostAccountLimitById(id));
    dto.setCostAccountAllocation(cost);
    return dto;
  }

  private void maybeSetWorkpackNameData(
    final CostAccountDto dto,
    final Long workpackId
  ) {
    final Optional<Workpack> workpack = this.workpackRepository.findById(workpackId, 0);
    if (workpack.isPresent()) {
      dto.setWorkpackModelName(workpack.get().getName());
      dto.setWorkpackModelFullName(workpack.get().getFullName());
    }
  }

  private List<CostAccount> fetchCostAccountFromWorkpack(final Workpack workpack) {
    final List<CostAccount> costs = new ArrayList<>();
    if (workpack.getCosts() != null && !(workpack.getCosts()).isEmpty()) {
      costs.addAll(workpack.getCosts());
    }
    if (workpack.getParent() != null) {
      costs.addAll(this.fetchCostAccountFromWorkpackParent(workpack.getParent()));
    }
    return costs;
  }

  private List<CostAccount> fetchCostAccountFromWorkpackParent(final Iterable<? extends Workpack> parent) {
    final List<CostAccount> costs = new ArrayList<>();
    for (final Workpack workpack : parent) {
      costs.addAll(this.fetchCostAccountFromWorkpack(workpack));
    }
    return costs;
  }

  public CostAccount getCostAccount(final Object cost) {

    Set<Property> properties = null;
    Long idCostAccount = null;
    Long idCostAccountModel = null;
    Workpack workpack = null;
    UnidadeOrcamentaria unidadeOrcamentaria;
    PlanoOrcamentario planoOrcamentario;

    if (cost instanceof CostAccountStoreDto) {

      final CostAccountStoreDto store = (CostAccountStoreDto) cost;
      final List<? extends PropertyDto> propertyDtos = store.getProperties();

      if (propertyDtos != null && !propertyDtos.isEmpty()) {
        properties = this.workpackService.getProperties(store.getProperties());
        idCostAccountModel = store.getIdCostAccountModel();
        store.setProperties(null);

        workpack = this.workpackRepository.findById(store.getIdWorkpack(), 0)
                .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
      }

      unidadeOrcamentaria = store.getUnidadeOrcamentaria();
      planoOrcamentario = store.getPlanoOrcamentario();

    } else {
      idCostAccount = ((CostAccountUpdateDto) cost).getId();
      final CostAccountUpdateDto update = (CostAccountUpdateDto) cost;
      final List<? extends PropertyDto> propertyDtos = update.getProperties();

      if (propertyDtos != null && !propertyDtos.isEmpty()) {
        properties = this.workpackService.getProperties(update.getProperties());
        idCostAccountModel = update.getIdCostAccountModel();
        update.setProperties(null);
      }

      unidadeOrcamentaria = update.getUnidadeOrcamentaria();
      planoOrcamentario = update.getPlanoOrcamentario();
    }

    CostAccount costAccount;
    if (idCostAccount == null) {
      costAccount = this.modelMapper.map(cost, CostAccount.class);
      costAccount.setWorkpack(workpack);
      costAccount.setUnidadeOrcamentaria(unidadeOrcamentaria);
      costAccount.setPlanoOrcamentario(planoOrcamentario);
    } else {
      costAccount = this.findById(idCostAccount);

      if (costAccount.getUnidadeOrcamentaria() != null) {
        unidadeOrcamentariaRepository.deleteById(costAccount.getUnidadeOrcamentaria().getId());
      }

      if (costAccount.getPlanoOrcamentario() != null) {
        planoOrcamentarioRepository.deleteById(costAccount.getPlanoOrcamentario().getId());
      }
    }

    costAccount.setPlanoOrcamentario(planoOrcamentario);
    costAccount.setUnidadeOrcamentaria(unidadeOrcamentaria);

    if (unidadeOrcamentaria != null) {
      if (unidadeOrcamentaria.getPlanoOrcamentario() == null) {
        unidadeOrcamentaria.setPlanoOrcamentario(new HashSet<>());
      }
      unidadeOrcamentaria.getPlanoOrcamentario().add(planoOrcamentario);
    }

    costAccount.setProperties(properties);

    final CostAccountModel costAccountModel = this.costAccountModelRepository.findById(idCostAccountModel, 0)
            .orElseThrow(() -> new RegistroNaoEncontradoException(ApplicationMessage.COST_ACCOUNT_MODEL_NOT_FOUND));
    costAccount.setInstance(costAccountModel);

    costAccount = this.save(costAccount);

    if (properties != null && !properties.isEmpty()) {
      for (Property property : properties) {
        property.setCostAccount(costAccount);
      }
      this.propertyRepository.saveAll(properties);
    }

    return costAccount;
  }


}
