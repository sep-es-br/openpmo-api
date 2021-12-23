package br.gov.es.openpmo.dto.workpack;

public class ToggleDto extends PropertyDto {

  private boolean value;

  public boolean isValue() {
    return this.value;
  }

  public void setValue(final boolean value) {
    this.value = value;
  }
}
