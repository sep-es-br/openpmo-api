package br.gov.es.openpmo.model.indicators;

import br.gov.es.openpmo.model.Entity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class PeriodGoal extends Entity {

    private String period;

    private Double expectedValue;

    private Double achievedValue;

    private String lastUpdate;

    private String justification;

    @Relationship(value = "IS_DEFINED_FOR", direction = Relationship.OUTGOING)
    private Indicator indicator;

    public PeriodGoal() {

    }

    public PeriodGoal(String period, Double expectedValue, Double achievedValue, String lastUpdate, String justification) {
        this.period = period;
        this.expectedValue = expectedValue;
        this.achievedValue = achievedValue;
        this.lastUpdate = lastUpdate;
        this.justification = justification;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Double getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(Double expectedValue) {
        this.expectedValue = expectedValue;
    }

    public Double getAchievedValue() {
        return achievedValue;
    }

    public void setAchievedValue(Double achievedValue) {
        this.achievedValue = achievedValue;
    }

    public Indicator getIndicator() {
        return indicator;
    }

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    
}
