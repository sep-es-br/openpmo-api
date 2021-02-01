package br.gov.es.openpmo.model;

import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class OrganizerModel extends WorkpackModel {

    @Relationship(value = "IS_INSTANCE_BY", direction = Relationship.INCOMING)
    private Set<Organizer> instances;

    public Set<Organizer> getInstances() {
        return instances;
    }

    public void setInstances(Set<Organizer> instances) {
        this.instances = instances;
    }
}
