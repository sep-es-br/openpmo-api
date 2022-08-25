package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.properties.models.ToggleModel;

public class ToggleModelDto extends PropertyModelDto {

  private boolean defaultValue;

  public static ToggleModelDto of(final PropertyModel propertyModel) {
    final ToggleModelDto instance = (ToggleModelDto) PropertyModelDto.of(propertyModel, ToggleModelDto::new);
    instance.setDefaultValue(((ToggleModel) propertyModel).isDefaultValue());
    return instance;
  }


  public boolean isDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final boolean defaultValue) {
    this.defaultValue = defaultValue;
  }

}
