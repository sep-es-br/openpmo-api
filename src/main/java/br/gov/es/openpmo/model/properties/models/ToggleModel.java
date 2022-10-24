package br.gov.es.openpmo.model.properties.models;

import org.springframework.data.neo4j.core.schema.Node;

@Node
public class ToggleModel extends PropertyModel {

  private boolean defaultValue;

  public boolean isDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final boolean defaultValue) {
    this.defaultValue = defaultValue;
  }

}
