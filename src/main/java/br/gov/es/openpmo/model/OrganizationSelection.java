package br.gov.es.openpmo.model;

import java.util.Objects;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class OrganizationSelection extends Property {

    @Relationship(value = "VALUES")
    private Set<Organization> value;

    @Relationship(value = "IS_DRIVEN_BY")
    private OrganizationSelectionModel driver;

    public Set<Organization> getValue() {
        return value;
    }

    public void setValue(Set<Organization> value) {
        this.value = value;
    }

    public OrganizationSelectionModel getDriver() {
        return driver;
    }

    public void setDriver(OrganizationSelectionModel driver) {
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
        OrganizationSelection that = (OrganizationSelection) o;
        return Objects.equals(driver, that.driver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), driver);
    }
}
