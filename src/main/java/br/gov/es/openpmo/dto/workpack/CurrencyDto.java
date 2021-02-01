package br.gov.es.openpmo.dto.workpack;

import java.math.BigDecimal;

public class CurrencyDto extends PropertyDto {

	private BigDecimal value;

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}
}
