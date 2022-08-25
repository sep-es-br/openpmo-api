package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.properties.models.TextAreaModel;

public class TextAreaModelDto extends PropertyModelDto {

  private String defaultValue;
  private Integer min;
  private Integer max;
  private Integer rows;


  public static TextAreaModelDto of(final PropertyModel propertyModel) {
    final TextAreaModelDto instance = (TextAreaModelDto) PropertyModelDto.of(propertyModel, TextAreaModelDto::new);
    instance.setMin(((TextAreaModel) propertyModel).getMin());
    instance.setMax(((TextAreaModel) propertyModel).getMax());
    instance.setDefaultValue(((TextAreaModel) propertyModel).getDefaultValue());
    instance.setRows(((TextAreaModel) propertyModel).getRows());
    return instance;
  }

  public String getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public Integer getMin() {
    return this.min;
  }

  public void setMin(final Integer min) {
    this.min = min;
  }

  public Integer getMax() {
    return this.max;
  }

  public void setMax(final Integer max) {
    this.max = max;
  }

  public Integer getRows() {
    return this.rows;
  }

  public void setRows(final Integer rows) {
    this.rows = rows;
  }

}
