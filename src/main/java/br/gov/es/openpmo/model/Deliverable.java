package br.gov.es.openpmo.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Deliverable extends Workpack {

    @Relationship(value = "IS_INSTANCE_BY")
    private DeliverableModel instance;

    public DeliverableModel getInstance() {
        return instance;
    }

    public void setInstance(DeliverableModel instance) {
        this.instance = instance;
    }
}
