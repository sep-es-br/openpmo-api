package br.gov.es.openpmo.dto.workpackmodel.params.properties;

public class IntegerModelDto extends PropertyModelDto {

  private Long min;
  private Long max;
  private Long defaultValue;

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
