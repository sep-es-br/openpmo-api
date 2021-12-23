package br.gov.es.openpmo.dto.workpack;

public class UnitSelectionDto extends PropertyDto {

  private Long selectedValue;

  public Long getSelectedValue() {
    return this.selectedValue;
  }

  public void setSelectedValue(final Long selectedValue) {
    this.selectedValue = selectedValue;
  }
}
