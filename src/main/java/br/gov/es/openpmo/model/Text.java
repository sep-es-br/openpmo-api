package br.gov.es.openpmo.model;

import java.util.Objects;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Text extends Property {

	private String value;

	@Relationship(value = "IS_DRIVEN_BY")
	private TextModel driver;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public TextModel getDriver() {
		return driver;
	}

	public void setDriver(TextModel driver) {
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
		Text text = (Text) o;
		return Objects.equals(driver, text.driver);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), driver);
	}
}
