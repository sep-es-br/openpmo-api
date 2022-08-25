package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.model.properties.models.CurrencyModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;

import java.math.BigDecimal;

public class CurrencyModelDto extends PropertyModelDto {

  private BigDecimal defaultValue;

  public static CurrencyModelDto of(final PropertyModel propertyModel) {
    final CurrencyModelDto instance = (CurrencyModelDto) PropertyModelDto.of(propertyModel, CurrencyModelDto::new);
    instance.setDefaultValue(((CurrencyModel) propertyModel).getDefaultValue());
    return instance;
  }


  public BigDecimal getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final BigDecimal defaultValue) {
    this.defaultValue = defaultValue;
  }

}
