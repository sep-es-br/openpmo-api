package br.gov.es.openpmo.model;

import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class ProgramModel extends WorkpackModel {

    @Relationship(value = "IS_INSTANCE_BY", direction = Relationship.INCOMING)
    private Set<Program> instances;

    public Set<Program> getInstances() {
        return instances;
    }

    public void setInstances(Set<Program> instances) {
        this.instances = instances;
    }
}
