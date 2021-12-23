package br.gov.es.openpmo.dto.workpack;

public class IntegerDto extends PropertyDto {

  private Long value;

  public Long getValue() {
    return this.value;
  }

  public void setValue(final Long value) {
    this.value = value;
  }
}
