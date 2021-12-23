package br.gov.es.openpmo.dto.schedule;

import java.math.BigDecimal;

public class CostSchedule {
  private Long id;
  private BigDecimal plannedCost;
  private BigDecimal actualCost;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public BigDecimal getPlannedCost() {
    return this.plannedCost;
  }

  public void setPlannedCost(final BigDecimal plannedCost) {
    this.plannedCost = plannedCost;
  }

  public BigDecimal getActualCost() {
    return this.actualCost;
  }

  public void setActualCost(final BigDecimal actualCost) {
    this.actualCost = actualCost;
  }
}
