package br.gov.es.openpmo.dto.workpackmodel.params.properties;

public class ToggleModelDto extends PropertyModelDto {

  private boolean defaultValue;

  public boolean isDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final boolean defaultValue) {
    this.defaultValue = defaultValue;
  }

}