package br.gov.es.openpmo.model;

import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class ProjectModel extends WorkpackModel {

    @Relationship(value = "IS_INSTANCE_BY", direction = Relationship.INCOMING)
    private Set<Project> instances;

    public Set<Project> getInstances() {
        return instances;
    }

    public void setInstances(Set<Project> instances) {
        this.instances = instances;
    }
}
