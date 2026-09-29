package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.properties.TransversalViewSelection;

public class TransversalViewSelectionDto extends PropertyDto {

  private String value;

  public static TransversalViewSelectionDto of(final Property property) {
    final TransversalViewSelectionDto dto = new TransversalViewSelectionDto();
    dto.setId(property.getId());
    dto.setIdPropertyModel(property.getPropertyModelId());
    dto.setValue(((TransversalViewSelection) property).getValue());
    return dto;
  }

  @Override
  public String getType() {
    return "TransversalViewSelection";
  }

  @Override
  public void setType(final String type) {
    this.type = type;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(final String value) {
    this.value = value;
  }
}
