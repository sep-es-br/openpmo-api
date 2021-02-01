package br.gov.es.openpmo.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Organizer extends Workpack {

    @Relationship(value = "IS_INSTANCE_BY")
    private OrganizerModel instance;

    public OrganizerModel getInstance() {
        return instance;
    }

    public void setInstance(OrganizerModel instance) {
        this.instance = instance;
    }
}
