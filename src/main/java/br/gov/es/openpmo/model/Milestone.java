package br.gov.es.openpmo.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Milestone extends Workpack {

    @Relationship(value = "IS_INSTANCE_BY")
    private MilestoneModel instance;

    public MilestoneModel getInstance() {
        return instance;
    }

    public void setInstance(MilestoneModel instance) {
        this.instance = instance;
    }
}
