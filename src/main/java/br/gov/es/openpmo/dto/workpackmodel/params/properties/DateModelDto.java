package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.model.properties.models.DateModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;

import java.time.LocalDateTime;

public class DateModelDto extends PropertyModelDto {

  private LocalDateTime min;
  private LocalDateTime max;
  private LocalDateTime defaultValue;

  public static DateModelDto of(final PropertyModel propertyModel) {
    final DateModelDto instance = (DateModelDto) PropertyModelDto.of(propertyModel, DateModelDto::new);
    instance.setDefaultValue(((DateModel) propertyModel).getDefaultValue());
    instance.setMax(((DateModel) propertyModel).getMax());
    instance.setMin(((DateModel) propertyModel).getMax());
    return instance;
  }

  public LocalDateTime getMin() {
    return this.min;
  }

  public void setMin(final LocalDateTime min) {
    this.min = min;
  }

  public LocalDateTime getMax() {
    return this.max;
  }

  public void setMax(final LocalDateTime max) {
    this.max = max;
  }

  public LocalDateTime getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final LocalDateTime defaultValue) {
    this.defaultValue = defaultValue;
  }

}
