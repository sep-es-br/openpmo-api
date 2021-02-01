package br.gov.es.openpmo.dto.schedule;

import java.math.BigDecimal;

public class CostSchedule {
    private Long id;
    private BigDecimal plannedCost;
    private BigDecimal actualCost;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPlannedCost() {
        return plannedCost;
    }

    public void setPlannedCost(BigDecimal plannedCost) {
        this.plannedCost = plannedCost;
    }

    public BigDecimal getActualCost() {
        return actualCost;
    }

    public void setActualCost(BigDecimal actualCost) {
        this.actualCost = actualCost;
    }
}
