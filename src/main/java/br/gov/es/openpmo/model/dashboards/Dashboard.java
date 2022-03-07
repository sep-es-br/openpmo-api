package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Dashboard extends Entity {

    private String tripleConstraint;

    private String earnedValueAnalysis;

    @Relationship("BELONGS_TO")
    private Workpack workpack;

    public String getTripleConstraint() {
        return tripleConstraint;
    }

    public void setTripleConstraint(String tripleConstraint) {
        this.tripleConstraint = tripleConstraint;
    }

    public String getEarnedValueAnalysis() {
        return earnedValueAnalysis;
    }

    public void setEarnedValueAnalysis(String earnedValueAnalysis) {
        this.earnedValueAnalysis = earnedValueAnalysis;
    }

    public Workpack getWorkpack() {
        return workpack;
    }

    public void setWorkpack(Workpack workpack) {
        this.workpack = workpack;
    }

}
