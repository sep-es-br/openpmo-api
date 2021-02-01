package br.gov.es.openpmo.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Program extends Workpack {

    @Relationship(value = "IS_INSTANCE_BY")
    private ProgramModel instance;

    public ProgramModel getInstance() {
        return instance;
    }

    public void setInstance(ProgramModel instance) {
        this.instance = instance;
    }
}
