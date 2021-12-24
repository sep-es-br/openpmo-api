package br.gov.es.openpmo.dto.workpackmodel.params.properties;

public class TextAreaModelDto extends PropertyModelDto {

  private String defaultValue;
  private Integer min;
  private Integer max;
  private Integer rows;

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
