package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import javax.validation.constraints.NotBlank;

public class SelectionModelDto extends PropertyModelDto {

  @NotBlank
  private String defaultValue;
  @NotBlank
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
