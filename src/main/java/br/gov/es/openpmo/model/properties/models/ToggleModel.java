package br.gov.es.openpmo.model.properties.models;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class ToggleModel extends PropertyModel {

  private boolean defaultValue;

  public boolean isDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final boolean defaultValue) {
    this.defaultValue = defaultValue;
  }

}
