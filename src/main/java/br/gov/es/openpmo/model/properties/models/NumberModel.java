package br.gov.es.openpmo.model.properties.models;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class NumberModel extends PropertyModel {

  private Double defaultValue;

  private Double min;

  private Double max;

  private Integer precision;

  public Double getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final Double defaultValue) {
    this.defaultValue = defaultValue;
  }

  public Double getMin() {
    return this.min;
  }

  public void setMin(final Double min) {
    this.min = min;
  }

  public Double getMax() {
    return this.max;
  }

  public void setMax(final Double max) {
    this.max = max;
  }

  public Integer getPrecision() {
    return this.precision;
  }

  public void setPrecision(final Integer precision) {
    this.precision = precision;
  }

}
