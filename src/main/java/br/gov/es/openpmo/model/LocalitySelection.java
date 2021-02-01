package br.gov.es.openpmo.model;

import java.util.Objects;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class LocalitySelection extends Property {

    @Relationship(value = "VALUES")
    private Set<Locality> value;

    @Relationship(value = "IS_DRIVEN_BY")
    private LocalitySelectionModel driver;

    public Set<Locality> getValue() {
        return value;
    }

    public void setValue(Set<Locality> value) {
        this.value = value;
    }

    public LocalitySelectionModel getDriver() {
        return driver;
    }

    public void setDriver(LocalitySelectionModel driver) {
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
        LocalitySelection that = (LocalitySelection) o;
        return Objects.equals(driver, that.driver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), driver);
    }
}
