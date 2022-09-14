package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.properties.Text;

public class TextDto extends PropertyDto {

  private String value;

  public static TextDto of(final Property property) {
    final TextDto textDto = new TextDto();
    textDto.setId(property.getId());
    textDto.setIdPropertyModel(property.getPropertyModelId());
    textDto.setValue(((Text) property).getValue());
    return textDto;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  @Override
  public String getType() {
    return "Text";
  }

  @Override
  public void setType(final String type) {
    this.type = type;
  }

}
