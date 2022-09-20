package br.gov.es.openpmo.model.properties.models;

import org.neo4j.ogm.annotation.NodeEntity;

import java.time.LocalDateTime;

@NodeEntity
public class DateModel extends PropertyModel {

  private LocalDateTime min;
  private LocalDateTime max;
  private LocalDateTime defaultValue;

  public LocalDateTime getMin() {
    return this.min;
  }

  public void setMin(final LocalDateTime min) {
    this.min = min;
  }

  public LocalDateTime getMax() {
    return this.max;
  }

  public void setMax(final LocalDateTime max) {
    this.max = max;
  }

  public LocalDateTime getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final LocalDateTime defaultValue) {
    this.defaultValue = defaultValue;
  }

}
