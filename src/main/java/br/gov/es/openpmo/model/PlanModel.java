package br.gov.es.openpmo.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class PlanModel extends Entity {

    @Relationship(type = "IS_ADOPTED_BY")
    private Office office;

    private String name;
    private String fullName;

    public PlanModel() {
    }

    public PlanModel(Long id) {
        setId(id);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

}
