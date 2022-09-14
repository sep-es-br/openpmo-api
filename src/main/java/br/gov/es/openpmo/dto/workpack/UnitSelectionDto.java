package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.office.UnitMeasure;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.properties.UnitSelection;

import java.util.Optional;

public class UnitSelectionDto extends PropertyDto {

  private Long selectedValue;

  public static UnitSelectionDto of(final Property property) {
    final UnitSelectionDto unitSelectionDto = new UnitSelectionDto();
    unitSelectionDto.setId(property.getId());
    unitSelectionDto.setIdPropertyModel(property.getPropertyModelId());
    unitSelectionDto.setSelectedValue(Optional.of((UnitSelection) property)
                                        .map(UnitSelection::getValue)
                                        .map(UnitMeasure::getId)
                                        .orElse(null));
    return unitSelectionDto;
  }

  public Long getSelectedValue() {
    return this.selectedValue;
  }

  public void setSelectedValue(final Long selectedValue) {
    this.selectedValue = selectedValue;
  }

  @Override
  public String getType() {
    return "UnitSelection";
  }

  @Override
  public void setType(final String type) {
    this.type = type;
  }

}
