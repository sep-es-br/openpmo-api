package br.gov.es.openpmo.dto.workpack;

import java.math.BigDecimal;

public class CurrencyDto extends PropertyDto {

  private BigDecimal value;

  public BigDecimal getValue() {
    return this.value;
  }

  public void setValue(final BigDecimal value) {
    this.value = value;
  }
}
