package br.gov.es.openpmo.model;

import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class MilestoneModel extends WorkpackModel {

    @Relationship(value = "IS_INSTANCE_BY", direction = Relationship.INCOMING)
    private Set<Milestone> instances;

    public Set<Milestone> getInstances() {
        return instances;
    }

    public void setInstances(Set<Milestone> instances) {
        this.instances = instances;
    }
}
