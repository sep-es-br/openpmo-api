package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStep;

import java.math.BigDecimal;
import java.time.YearMonth;

public class EarnedValueByStepData {

    private BigDecimal plannedValue;

    private BigDecimal actualCost;

    private BigDecimal earnedValue;

    private YearMonth date;

    public static EarnedValueByStepData of(EarnedValueByStep from) {
        if (from == null) {
            return null;
        }

        final EarnedValueByStepData to = new EarnedValueByStepData();
        to.setPlannedValue(from.getPlannedValue());
        to.setActualCost(from.getActualCost());
        to.setEarnedValue(from.getEarnedValue());
        to.setDate(from.getDate());
        return to;
    }

    public BigDecimal getPlannedValue() {
        return plannedValue;
    }

    public void setPlannedValue(BigDecimal plannedValue) {
        this.plannedValue = plannedValue;
    }

    public BigDecimal getActualCost() {
        return actualCost;
    }

    public void setActualCost(BigDecimal actualCost) {
        this.actualCost = actualCost;
    }

    public BigDecimal getEarnedValue() {
        return earnedValue;
    }

    public void setEarnedValue(BigDecimal earnedValue) {
        this.earnedValue = earnedValue;
    }

    public YearMonth getDate() {
        return date;
    }

    public void setDate(YearMonth date) {
        this.date = date;
    }

    public EarnedValueByStep getResponse() {
        final EarnedValueByStep step = new EarnedValueByStep();
        step.setActualCost(this.actualCost);
        step.setPlannedValue(this.plannedValue);
        step.setEarnedValue(this.earnedValue);
        step.setDate(this.date);
        return step;
    }
}
