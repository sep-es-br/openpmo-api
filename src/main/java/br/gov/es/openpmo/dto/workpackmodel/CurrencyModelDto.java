package br.gov.es.openpmo.dto.workpackmodel;

import java.math.BigDecimal;

public class CurrencyModelDto extends PropertyModelDto {

	private BigDecimal defaultValue;

	public BigDecimal getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(BigDecimal defaultValue) {
		this.defaultValue = defaultValue;
	}

}
