package br.gov.es.openpmo.model.properties.models;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class TextModel extends PropertyModel {

  private Long min;
  private Long max;
  private String defaultValue;

  public Long getMin() {
    return this.min;
  }

  public void setMin(final Long min) {
    this.min = min;
  }

  public Long getMax() {
    return this.max;
  }

  public void setMax(final Long max) {
    this.max = max;
  }

  public String getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final String defaultValue) {
    this.defaultValue = defaultValue;
  }

}
