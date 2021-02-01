package br.gov.es.openpmo.model;

import java.util.Objects;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class UnitSelection extends Property {

    @Relationship(value = "VALUES")
	private UnitMeasure value;

    @Relationship(value = "IS_DRIVEN_BY")
    private UnitSelectionModel driver;

    public UnitMeasure getValue() {
        return value;
    }

    public void setValue(UnitMeasure value) {
        this.value = value;
    }

    public UnitSelectionModel getDriver() {
        return driver;
    }

    public void setDriver(UnitSelectionModel driver) {
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
        UnitSelection that = (UnitSelection) o;
        return Objects.equals(driver, that.driver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), driver);
    }
}
