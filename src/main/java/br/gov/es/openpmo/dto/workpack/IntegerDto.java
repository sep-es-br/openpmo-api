package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.properties.Integer;
import br.gov.es.openpmo.model.properties.Property;

public class IntegerDto extends PropertyDto {

  private Long value;

  public static IntegerDto of(final Property property) {
    final IntegerDto integerDto = new IntegerDto();
    integerDto.setId(property.getId());
    integerDto.setIdPropertyModel(property.getPropertyModelId());
    integerDto.setValue(((Integer) property).getValue());
    return integerDto;
  }

  public Long getValue() {
    return this.value;
  }

  public void setValue(final Long value) {
    this.value = value;
  }

  @Override
  public String getType() {
    return "Integer";
  }

  @Override
  public void setType(final String type) {
    this.type = type;
  }

}
