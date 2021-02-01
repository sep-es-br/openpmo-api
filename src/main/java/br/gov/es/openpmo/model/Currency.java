package br.gov.es.openpmo.model;

import java.math.BigDecimal;
import java.util.Objects;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Currency extends Property {

	private BigDecimal value;

	@Relationship(value = "IS_DRIVEN_BY")
	private CurrencyModel driver;

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public CurrencyModel getDriver() {
		return driver;
	}

	public void setDriver(CurrencyModel driver) {
		this.driver = driver;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		Currency currency = (Currency) o;
		return Objects.equals(driver, currency.driver);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), driver);
	}
}
