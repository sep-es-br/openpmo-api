package br.gov.es.openpmo.model;

import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class PortfolioModel extends WorkpackModel {

    @Relationship(value = "IS_INSTANCE_BY", direction = Relationship.INCOMING)
    private Set<Portfolio> instances;

    public Set<Portfolio> getInstances() {
        return instances;
    }

    public void setInstances(Set<Portfolio> instances) {
        this.instances = instances;
    }
}
