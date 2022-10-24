package br.gov.es.openpmo.model.properties.models;

import org.springframework.data.neo4j.core.schema.Node;

import java.math.BigDecimal;

@Node
public class CurrencyModel extends PropertyModel {

  private BigDecimal defaultValue;

  public BigDecimal getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final BigDecimal defaultValue) {
    this.defaultValue = defaultValue;
  }

}
