package br.gov.es.openpmo.service.filters;

import br.gov.es.openpmo.dto.filter.CustomFilterRulesDto;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.Rules;
import br.gov.es.openpmo.repository.RulesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RulesService {

  private final RulesRepository rulesRepository;

  @Autowired
  public RulesService(final RulesRepository rulesRepository) {
    this.rulesRepository = rulesRepository;
  }

  public void create(
    final CustomFilter customFilter,
    final CustomFilterRulesDto rule
  ) {
    final Rules entity = new Rules(
      customFilter,
      rule.getLogicOperator(),
      rule.getOperator(),
      rule.getPropertyName(),
      rule.getValue()
    );
    this.rulesRepository.save(entity);
  }

  public List<Rules> createAll(
    final CustomFilter filter,
    final List<CustomFilterRulesDto> rulesDto
  ) {
    final List<Rules> rules = rulesDto.stream().map(r ->
                                                      new Rules(
                                                        filter,
                                                        r.getLogicOperator(),
                                                        r.getOperator(),
                                                        r.getPropertyName(),
                                                        r.getValue()
                                                      )
    ).collect(Collectors.toList());
    this.rulesRepository.saveAll(rules);
    return rules;
  }

  public void deleteByCustomFilter(final CustomFilter customFilter) {
    final List<Rules> rules = this.findByCustomFilter(customFilter);
    this.rulesRepository.deleteAll(rules);
  }

  public List<Rules> findByCustomFilter(final CustomFilter customFilter) {
    return this.rulesRepository.findByCustomFilter(customFilter);
  }

  public void update(
    final CustomFilter customFilter,
    final Collection<? extends CustomFilterRulesDto> rulesDto
  ) {
    final List<Rules> rules = this.rulesRepository.findByCustomFilterId(customFilter.getId());

    this.findAndDeleteRemovedRules(rulesDto, rules, customFilter);

    rulesDto.forEach(ruleDto -> {
      final Optional<Rules> ruleFound = this.findRegisteredRuleBetween(rules, ruleDto);

      if(!ruleFound.isPresent()) {
        customFilter.getRules().add(this.createNewRule(customFilter, ruleDto));
        return;
      }

      customFilter.getRules().add(this.updateRegisteredRule(ruleDto, ruleFound.get()));
    });
  }

  private Rules updateRegisteredRule(
    final CustomFilterRulesDto ruleDto,
    final Rules ruleFound
  ) {
    ruleFound.update(ruleDto);
    this.rulesRepository.save(ruleFound);
    return ruleFound;
  }

  private Rules createNewRule(
    final CustomFilter customFilter,
    final CustomFilterRulesDto ruleDto
  ) {
    final Rules newRule = new Rules(
      customFilter,
      ruleDto.getLogicOperator(),
      ruleDto.getOperator(),
      ruleDto.getPropertyName(),
      ruleDto.getValue()
    );
    this.rulesRepository.save(newRule);
    return newRule;
  }

  private Optional<Rules> findRegisteredRuleBetween(
    final List<Rules> rules,
    final CustomFilterRulesDto ruleDto
  ) {
    return rules
      .stream()
      .filter(filtro -> filtro.getId().equals(ruleDto.getId()))
      .findFirst();
  }

  private void findAndDeleteRemovedRules(
    final Collection<? extends CustomFilterRulesDto> rulesDto,
    final Collection<? extends Rules> rules,
    final CustomFilter customFilter
  ) {
    final List<Rules> removedRules = rules.stream()
      .filter(rule -> rulesDto.stream().noneMatch(filtro -> rule.getId().equals(filtro.getId())))
      .collect(Collectors.toList());

    if(Objects.isNull(customFilter.getRules())) return;

    removedRules.forEach(r -> customFilter.getRules().remove(r));

    this.rulesRepository.deleteAll(removedRules);

  }

}
