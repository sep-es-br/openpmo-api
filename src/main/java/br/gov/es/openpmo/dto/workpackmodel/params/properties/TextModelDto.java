package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.properties.models.TextModel;

public class TextModelDto extends PropertyModelDto {

  private Long min;
  private Long max;
  private String defaultValue;

  public static TextModelDto of(final PropertyModel propertyModel) {
    final TextModelDto instance = (TextModelDto) PropertyModelDto.of(propertyModel, TextModelDto::new);
    instance.setMin(((TextModel) propertyModel).getMin());
    instance.setMax(((TextModel) propertyModel).getMax());
    instance.setDefaultValue(((TextModel) propertyModel).getDefaultValue());
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

  public String getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final String defaultValue) {
    this.defaultValue = defaultValue;
  }

}
