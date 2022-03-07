package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.tripleconstraint.CostDataChart;

import java.math.BigDecimal;

public class CostData {

    private BigDecimal actualValue;

    private BigDecimal variation;

    private BigDecimal plannedValue;

    private BigDecimal foreseenValue;

    public static CostData of(CostDataChart from) {
        if (from == null) {
            return null;
        }

        final CostData to = new CostData();
        to.setActualValue(from.getActualValue());
        to.setVariation(from.getVariation());
        to.setPlannedValue(from.getPlannedValue());
        to.setForeseenValue(from.getForeseenValue());
        return to;
    }

    public BigDecimal getActualValue() {
        return actualValue;
    }

    public void setActualValue(BigDecimal actualValue) {
        this.actualValue = actualValue;
    }

    public BigDecimal getVariation() {
        return variation;
    }

    public void setVariation(BigDecimal variation) {
        this.variation = variation;
    }

    public BigDecimal getPlannedValue() {
        return plannedValue;
    }

    public void setPlannedValue(BigDecimal plannedValue) {
        this.plannedValue = plannedValue;
    }

    public BigDecimal getForeseenValue() {
        return foreseenValue;
    }

    public void setForeseenValue(BigDecimal foreseenValue) {
        this.foreseenValue = foreseenValue;
    }

    public CostDataChart getResponse() {
        final CostDataChart costDataChart = new CostDataChart();
        costDataChart.setActualValue(this.actualValue);
        costDataChart.setForeseenValue(this.foreseenValue);
        costDataChart.setPlannedValue(this.plannedValue);
        costDataChart.setVariation(this.variation);
        return costDataChart;
    }

}
