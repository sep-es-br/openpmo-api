package br.gov.es.openpmo.dto.indicators.period;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class PeriodGoalDto {

    @NotNull
    @NotEmpty
    private String period;

    @NotNull
    @NotEmpty
    private Double expectedValue;

    @NotNull
    @NotEmpty
    private Double achievedValue;

    @NotNull
    @NotEmpty
    private String lastUpdate;

    @NotNull
    @NotEmpty
    private String justification;

    public PeriodGoalDto() {
    }

    public PeriodGoalDto(String period, Double expectedValue , Double achievedValue, String lastUpdate, String justification) {
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
