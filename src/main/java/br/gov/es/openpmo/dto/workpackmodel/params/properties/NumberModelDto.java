package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.model.properties.models.NumberModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;

public class NumberModelDto extends PropertyModelDto {

  private Double defaultValue;
  private Double min;
  private Double max;
  private Integer precision;

  public static NumberModelDto of(final PropertyModel propertyModel) {
    final NumberModelDto instance = (NumberModelDto) PropertyModelDto.of(propertyModel, NumberModelDto::new);
    instance.setPrecision(((NumberModel) propertyModel).getPrecision());
    instance.setMin(((NumberModel) propertyModel).getMin());
    instance.setMax(((NumberModel) propertyModel).getMax());
    instance.setDefaultValue(((NumberModel) propertyModel).getDefaultValue());
    return instance;
  }


  public Double getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final Double defaultValue) {
    this.defaultValue = defaultValue;
  }

  public Double getMin() {
    return this.min;
  }

  public void setMin(final Double min) {
    this.min = min;
  }

  public Double getMax() {
    return this.max;
  }

  public void setMax(final Double max) {
    this.max = max;
  }

  public Integer getPrecision() {
    return this.precision;
  }

  public void setPrecision(final Integer precision) {
    this.precision = precision;
  }

}
