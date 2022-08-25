package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.model.properties.models.IntegerModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;

public class IntegerModelDto extends PropertyModelDto {

  private Long min;
  private Long max;
  private Long defaultValue;

  public static IntegerModelDto of(final PropertyModel propertyModel) {
    final IntegerModelDto instance = (IntegerModelDto) PropertyModelDto.of(propertyModel, IntegerModelDto::new);
    instance.setMin(((IntegerModel) propertyModel).getMin());
    instance.setMax(((IntegerModel) propertyModel).getMax());
    instance.setDefaultValue(((IntegerModel) propertyModel).getDefaultValue());
    return instance;
  }


  public Long getMin() {
    return this.min;
  }

  public void setMin(final Long min) {
    this.min = min;
  }

  public Long getMax() {
    return this.max;
  }

  public void setMax(final Long max) {
    this.max = max;
  }

  public Long getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final Long defaultValue) {
    this.defaultValue = defaultValue;
  }

}
