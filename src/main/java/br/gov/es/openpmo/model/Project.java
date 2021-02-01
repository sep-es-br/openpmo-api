package br.gov.es.openpmo.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Project extends Workpack {

    @Relationship(value = "IS_INSTANCE_BY")
    private ProjectModel instance;

    public ProjectModel getInstance() {
        return instance;
    }

    public void setInstance(ProjectModel instance) {
        this.instance = instance;
    }
}
