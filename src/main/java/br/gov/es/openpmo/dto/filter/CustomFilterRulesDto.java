package br.gov.es.openpmo.dto.filter;

import br.gov.es.openpmo.enumerator.GeneralOperatorsEnum;
import br.gov.es.openpmo.model.filter.LogicOperatorEnum;

public class CustomFilterRulesDto {

  private Long id;
  private String propertyName;
  private GeneralOperatorsEnum operator;
  private String value;
  private LogicOperatorEnum logicOperator;

  public String getPropertyName() {
    return this.propertyName;
  }

  public void setPropertyName(final String propertyName) {
    this.propertyName = propertyName;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public GeneralOperatorsEnum getOperator() {
    return this.operator;
  }

  public void setOperator(final GeneralOperatorsEnum operator) {
    this.operator = operator;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  public LogicOperatorEnum getLogicOperator() {
    return this.logicOperator;
  }

  public void setLogicOperator(final LogicOperatorEnum logicOperator) {
    this.logicOperator = logicOperator;
  }

}
