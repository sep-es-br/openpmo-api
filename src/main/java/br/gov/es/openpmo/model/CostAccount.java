package br.gov.es.openpmo.model;

import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class CostAccount extends Entity {

    @Relationship(type = "FEATURES", direction = Relationship.INCOMING)
    private Set<Property> properties;

    @Relationship(type = "APPLIES_TO")
    private Workpack workpack;

    public Set<Property> getProperties() {
        return properties;
    }

    public void setProperties(Set<Property> properties) {
        this.properties = properties;
    }

    public Workpack getWorkpack() {
        return workpack;
    }

    public void setWorkpack(Workpack workpack) {
        this.workpack = workpack;
    }
}
