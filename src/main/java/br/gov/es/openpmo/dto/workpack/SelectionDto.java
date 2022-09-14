package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.properties.Selection;

public class SelectionDto extends PropertyDto {

  private String value;

  public static SelectionDto of(final Property property) {
    final SelectionDto selectionDto = new SelectionDto();
    selectionDto.setId(property.getId());
    selectionDto.setIdPropertyModel(property.getPropertyModelId());
    selectionDto.setValue(((Selection) property).getValue());
    return selectionDto;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  @Override
  public String getType() {
    return "Selection";
  }

  @Override
  public void setType(final String type) {
    this.type = type;
  }

}
