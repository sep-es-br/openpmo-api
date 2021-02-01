package br.gov.es.openpmo.model;

import java.time.LocalDateTime;
import java.util.Objects;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Date extends Property {

	private LocalDateTime value;

	@Relationship(value = "IS_DRIVEN_BY")
	private DateModel driver;

	public LocalDateTime getValue() {
		return value;
	}

	public void setValue(LocalDateTime value) {
		this.value = value;
	}

	public DateModel getDriver() {
		return driver;
	}

	public void setDriver(DateModel driver) {
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
		Date date = (Date) o;
		return Objects.equals(driver, date.driver);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), driver);
	}
}
