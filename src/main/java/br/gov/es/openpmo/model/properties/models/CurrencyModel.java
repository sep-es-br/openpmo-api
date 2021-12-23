package br.gov.es.openpmo.model.properties.models;

import org.neo4j.ogm.annotation.NodeEntity;

import java.math.BigDecimal;

@NodeEntity
public class CurrencyModel extends PropertyModel {

  private BigDecimal defaultValue;

  public BigDecimal getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final BigDecimal defaultValue) {
    this.defaultValue = defaultValue;
  }

}
