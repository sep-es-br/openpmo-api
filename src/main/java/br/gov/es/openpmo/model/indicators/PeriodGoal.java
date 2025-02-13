package br.gov.es.openpmo.model.indicators;

import br.gov.es.openpmo.model.Entity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class PeriodGoal extends Entity {

    @Property
    private String period;

    @Property
    private Double value;

    public PeriodGoal() {

    }

    public PeriodGoal(String period, Double value) {
        this.period = period;
        this.value = value;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
