package br.gov.es.openpmo.model.indicators;

import br.gov.es.openpmo.model.Entity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class PeriodGoal extends Entity {

    private String period;

    private Double value;

    private String lastUpdate;

    @Relationship(value = "BELONGS_TO")
    private Indicator indicator;

    public PeriodGoal() {

    }

    public PeriodGoal(String period, Double value, String lastUpdate) {
        this.period = period;
        this.value = value;
        this.lastUpdate = lastUpdate;
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
}
