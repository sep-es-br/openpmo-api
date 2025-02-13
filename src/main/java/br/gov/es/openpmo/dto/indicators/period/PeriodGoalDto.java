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

    public PeriodGoalDto() {
    }

    public PeriodGoalDto(String period, Double value) {
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
