package br.gov.es.openpmo.model;

import java.time.LocalDate;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Plan extends Entity {

    @Relationship(type = "IS_ADOPTED_BY")
    private Office office;

    @Relationship(type = "IS_STRUCTURED_BY")
    private PlanModel planModel;

    private String name;
    private String fullName;
    private LocalDate start;
    private LocalDate finish;

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public PlanModel getPlanModel() {
        return planModel;
    }

    public void setPlanModel(PlanModel planModel) {
        this.planModel = planModel;
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

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getFinish() {
        return finish;
    }

    public void setFinish(LocalDate finish) {
        this.finish = finish;
    }
}
