package br.gov.es.openpmo.dto.indicators.period;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class PeriodGoalDto {

    @NotNull
    @NotEmpty
    private String period;

    @NotNull
    @NotEmpty
    private Double value;

    @NotNull
    @NotEmpty
    private String lastUpdate;

    @NotNull
    @NotEmpty
    private String justification;

    public PeriodGoalDto() {
    }

    public PeriodGoalDto(String period, Double value, String lastUpdate, String justification) {
        this.period = period;
        this.value = value;
        this.lastUpdate = lastUpdate;
        this.justification = justification;
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
