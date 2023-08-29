package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.properties.Currency;
import br.gov.es.openpmo.model.properties.Property;

import java.math.BigDecimal;

public class CurrencyDto extends PropertyDto {

  private BigDecimal value;

  public static CurrencyDto of(final Property property) {
    final CurrencyDto currencyDto = new CurrencyDto();
    currencyDto.setId(property.getId());
    currencyDto.setIdPropertyModel(property.getPropertyModelId());
    currencyDto.setValue(((Currency) property).getValue());
    return currencyDto;
  }

  public BigDecimal getValue() {
    return this.value;
  }

  public void setValue(final BigDecimal value) {
    this.value = value;
  }

  @Override
  public String getType() {
    return "Currency";
  }

  @Override
  public void setType(final String type) {
    this.type = type;
  }

}
