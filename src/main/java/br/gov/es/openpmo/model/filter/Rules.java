package br.gov.es.openpmo.model.filter;

import br.gov.es.openpmo.dto.filter.CustomFilterRulesDto;
import br.gov.es.openpmo.enumerator.GeneralOperatorsEnum;
import br.gov.es.openpmo.model.Entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Rules extends Entity {

  @Relationship("HAS")
  @JsonIgnoreProperties("rules")
  private CustomFilter customFilter;
  private LogicOperatorEnum logicOperator;
  private GeneralOperatorsEnum relationalOperator;
  private String propertyName;
  private String value;

  public Rules(
    final CustomFilter customFilter,
    final LogicOperatorEnum logicOperator,
    final GeneralOperatorsEnum relationalOperator,
    final String propertyName,
    final String value
  ) {
    this.customFilter = customFilter;
    this.logicOperator = logicOperator;
    this.relationalOperator = relationalOperator;
    this.propertyName = propertyName;
    this.value = value;
  }

  public Rules() {
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  public CustomFilter getCustomFilter() {
    return this.customFilter;
  }

  public void setCustomFilter(final CustomFilter customFilter) {
    this.customFilter = customFilter;
  }

  public LogicOperatorEnum getLogicOperator() {
    return this.logicOperator;
  }

  public void setLogicOperator(final LogicOperatorEnum logicOperator) {
    this.logicOperator = logicOperator;
  }

  public GeneralOperatorsEnum getRelationalOperator() {
    return this.relationalOperator;
  }

  public void setRelationalOperator(final GeneralOperatorsEnum relationalOperator) {
    this.relationalOperator = relationalOperator;
  }

  public String getPropertyName() {
    return this.propertyName;
  }

  public void setPropertyName(final String propertyName) {
    this.propertyName = propertyName;
  }

  public void update(final CustomFilterRulesDto ruleDto) {
    this.setLogicOperator(ruleDto.getLogicOperator());
    this.setValue(ruleDto.getValue());
    this.setPropertyName(ruleDto.getPropertyName());
    this.setRelationalOperator(ruleDto.getOperator());
  }

}
