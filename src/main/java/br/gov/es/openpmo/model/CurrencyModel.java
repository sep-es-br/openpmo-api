package br.gov.es.openpmo.model;

import java.math.BigDecimal;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class CurrencyModel extends PropertyModel {

	private BigDecimal defaultValue;

	public BigDecimal getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(BigDecimal defaultValue) {
		this.defaultValue = defaultValue;
	}

}
