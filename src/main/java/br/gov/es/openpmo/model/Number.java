package br.gov.es.openpmo.model;

import java.util.Objects;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Number extends Property {

	private Double value;

	@Relationship(value = "IS_DRIVEN_BY")
	private NumberModel driver;

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public NumberModel getDriver() {
		return driver;
	}

	public void setDriver(NumberModel driver) {
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
		Number number = (Number) o;
		return Objects.equals(driver, number.driver);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), driver);
	}
}
