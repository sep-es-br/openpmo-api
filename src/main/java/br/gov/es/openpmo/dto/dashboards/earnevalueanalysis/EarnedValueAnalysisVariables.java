package br.gov.es.openpmo.dto.dashboards.earnevalueanalysis;

import java.math.BigDecimal;

public class EarnedValueAnalysisVariables {

  private BigDecimal earnedValue;

  private BigDecimal actualCost;

  private BigDecimal plannedValue;

  public EarnedValueAnalysisVariables() {
  }

  public EarnedValueAnalysisVariables(
    final BigDecimal earnedValue,
    final BigDecimal actualCost,
    final BigDecimal plannedValue
  ) {
    this.earnedValue = earnedValue;
    this.actualCost = actualCost;
    this.plannedValue = plannedValue;
  }

  public BigDecimal getEarnedValue() {
    return this.earnedValue;
  }

  public void setEarnedValue(final BigDecimal earnedValue) {
    this.earnedValue = earnedValue;
  }

  public BigDecimal getActualCost() {
    return this.actualCost;
  }

  public void setActualCost(final BigDecimal actualCost) {
    this.actualCost = actualCost;
  }

  public BigDecimal getPlannedValue() {
    return this.plannedValue;
  }

  public void setPlannedValue(final BigDecimal plannedValue) {
    this.plannedValue = plannedValue;
  }

}
