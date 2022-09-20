package br.gov.es.openpmo.service.filters;

import br.gov.es.openpmo.dto.filter.CustomFilterCreateRequest;
import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import br.gov.es.openpmo.dto.filter.CustomFilterRulesDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.CustomFilterEnum;
import br.gov.es.openpmo.model.filter.Rules;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.CustomFilterRepository;
import br.gov.es.openpmo.service.actors.GetPersonById;
import br.gov.es.openpmo.service.workpack.WorkpackModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.CUSTOM_FILTER_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.ID_NOT_NULL;

@Service
public class CustomFilterService {

  private final CustomFilterRepository customFilterRepository;
  private final GetPersonById getPersonById;
  private final RulesService rulesService;
  private final WorkpackModelService workpackModelService;

  @Autowired
  public CustomFilterService(
    final CustomFilterRepository customFilterRepository,
    final GetPersonById getPersonById,
    final RulesService rulesService,
    final WorkpackModelService workpackModelService
  ) {
    this.customFilterRepository = customFilterRepository;
    this.getPersonById = getPersonById;
    this.rulesService = rulesService;
    this.workpackModelService = workpackModelService;
  }

  @Transactional
  public CustomFilterDto create(
    final CustomFilterCreateRequest customFilterCreateRequest
  ) {
    Objects.requireNonNull(customFilterCreateRequest.getIdUser());

    WorkpackModel workpackModel = null;

    if(customFilterCreateRequest.getIdWorkPackModel() != null) {
      workpackModel = this.workpackModelService.findById(customFilterCreateRequest.getIdWorkPackModel());
    }

    final Person person = this.getPersonById.execute(customFilterCreateRequest.getIdUser());

    final CustomFilter entity = new CustomFilter(
      customFilterCreateRequest.getRequest().getName(),
      customFilterCreateRequest.getCustomFilterEnum(),
      customFilterCreateRequest.getRequest().getFavorite(),
      customFilterCreateRequest.getRequest().getSortByDirection(),
      customFilterCreateRequest.getRequest().getSortBy(),
      workpackModel,
      person
    );

    this.customFilterRepository.save(entity);

    final List<Rules> rules = this.createRules(entity, customFilterCreateRequest.getRequest());

    return buildCustomFilterDto(entity, new HashSet<>(rules));
  }

  private static CustomFilterDto buildCustomFilterDto(
    final CustomFilter customFilter,
    final Collection<? extends Rules> rules
  ) {
    final CustomFilterDto customFilterResponse = new CustomFilterDto();
    customFilterResponse.setFavorite(customFilter.isFavorite());
    customFilterResponse.setId(customFilter.getId());
    customFilterResponse.setName(customFilter.getName());
    customFilterResponse.setSortBy(customFilter.getSortBy());
    customFilterResponse.setSortByDirection(customFilter.getDirection());
    customFilterResponse.setRules(buildCustomFilterRulesDto(rules));
    return customFilterResponse;
  }

  private List<Rules> createRules(
    final CustomFilter entity,
    final CustomFilterDto request
  ) {
    return this.rulesService.createAll(entity, request.getRules());
  }

  private static List<CustomFilterRulesDto> buildCustomFilterRulesDto(final Collection<? extends Rules> rules) {
    return Optional.ofNullable(rules)
      .map(notNullRules -> notNullRules.stream()
        .map(rule -> {
          final CustomFilterRulesDto dto = new CustomFilterRulesDto();
          dto.setPropertyName(rule.getPropertyName());
          dto.setOperator(rule.getRelationalOperator());
          dto.setValue(rule.getValue());
          dto.setLogicOperator(rule.getLogicOperator());
          dto.setId(rule.getId());
          return dto;
        })
        .collect(Collectors.toList())).orElse(new ArrayList<>());
  }

  public CustomFilterDto update(
    @Valid final CustomFilterDto request,
    final CustomFilterEnum customFilterEnum,
    final Long idWorkPackModel,
    final Long idPerson
  ) {
    final CustomFilter customFilterToUpdate = this.findById(request.getId(), idPerson);

    WorkpackModel workpackModel = null;

    if(idWorkPackModel != null) {
      workpackModel = this.workpackModelService.findById(idWorkPackModel);
    }

    customFilterToUpdate.update(
      request,
      customFilterEnum,
      workpackModel
    );

    this.customFilterRepository.save(customFilterToUpdate);

    this.updateRulesRelationship(customFilterToUpdate, request.getRules());

    return buildCustomFilterDto(customFilterToUpdate, customFilterToUpdate.getRules());
  }

  private void updateRulesRelationship(
    final CustomFilter entity,
    final Collection<? extends CustomFilterRulesDto> rules
  ) {
    this.rulesService.update(entity, rules);
  }

  public CustomFilter findById(
    final Long idCustomFilter,
    final Long idPerson
  ) {
    return this.customFilterRepository.findByIdAndPersonId(idCustomFilter, idPerson)
      .orElseThrow(() -> new NegocioException(CUSTOM_FILTER_NOT_FOUND));
  }

  public List<CustomFilterDto> getCombo(
    final CustomFilterEnum customFilterEnum,
    final Long idPerson
  ) {
    final List<CustomFilter> customFilters = this.customFilterRepository.findByType(
      customFilterEnum,
      idPerson
    );
    return customFilters.stream()
      .map(filter -> {
        final Set<Rules> rules = filter.getRules();
        return buildCustomFilterDto(filter, rules);
      })
      .collect(Collectors.toList());
  }

  public CustomFilterDto getById(
    final Long id,
    final Long idPerson
  ) {
    Objects.requireNonNull(idPerson, ID_NOT_NULL);
    final CustomFilter customFilter = this.findById(id, idPerson);
    final Set<Rules> rules = customFilter.getRules();
    return buildCustomFilterDto(customFilter, rules);
  }

  public void delete(
    final Long id,
    final Long idPerson
  ) {
    final CustomFilter customFilter = this.findById(id, idPerson);
    this.rulesService.deleteByCustomFilter(customFilter);
    this.customFilterRepository.delete(customFilter);
  }

  public List<CustomFilterDto> getCombo(
    final Long workpackModelId,
    final CustomFilterEnum customFilterEnum,
    final Long idPerson
  ) {

    final List<CustomFilter> customFilters = this.customFilterRepository.findByWorkpackModelIdAndType(
      workpackModelId,
      customFilterEnum,
      idPerson
    );

    return customFilters.stream()
      .map(filter -> {
        final Set<Rules> rules = filter.getRules();
        return buildCustomFilterDto(filter, rules);
      })
      .collect(Collectors.toList());
  }

}
