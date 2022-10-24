package br.gov.es.openpmo.model.properties.models;

import org.springframework.data.neo4j.core.schema.Node;

@Node
public class SelectionModel extends PropertyModel {

  private String defaultValue;
  private String possibleValues;
  private boolean multipleSelection;

  public String getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getPossibleValues() {
    return this.possibleValues;
  }

  public void setPossibleValues(final String possibleValues) {
    this.possibleValues = possibleValues;
  }

  public boolean isMultipleSelection() {
    return this.multipleSelection;
  }

  public void setMultipleSelection(final boolean multipleSelection) {
    this.multipleSelection = multipleSelection;
  }

}
