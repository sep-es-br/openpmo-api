package br.gov.es.openpmo.model;

import java.util.Objects;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class TextArea extends Property {

	private String value;

	@Relationship(value = "IS_DRIVEN_BY")
	private TextAreaModel driver;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public TextAreaModel getDriver() {
		return driver;
	}

	public void setDriver(TextAreaModel driver) {
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
		TextArea textArea = (TextArea) o;
		return Objects.equals(driver, textArea.driver);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), driver);
	}
}
