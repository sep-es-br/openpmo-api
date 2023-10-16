package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStep;

import java.math.BigDecimal;
import java.time.YearMonth;

public class EarnedValueByStepData {

  private Long idBaseline;

  private BigDecimal plannedValue;

  private BigDecimal actualCost;

  private BigDecimal estimatedCost;

  private BigDecimal earnedValue;

  private YearMonth date;

  public static EarnedValueByStepData of(final EarnedValueByStep from) {
    if (from == null) {
      return null;
    }

    final EarnedValueByStepData to = new EarnedValueByStepData();
    to.setIdBaseline(from.getIdBaseline());
    to.setPlannedValue(from.getPlannedValue());
    to.setActualCost(from.getActualCost());
    to.setEstimatedCost(from.getEstimatedCost());
    to.setEarnedValue(from.getEarnedValue());
    to.setDate(from.getDate());
    return to;
  }

  public static EarnedValueByStepData of(final EarnedValue from) {
    if (from == null) {
      return null;
    }

    final EarnedValueByStepData to = new EarnedValueByStepData();
    to.setIdBaseline(from.getIdBaseline());
    to.setPlannedValue(from.getPlannedValue());
    to.setActualCost(from.getActualCost());
    to.setEstimatedCost(from.getEstimatedCost());
    to.setEarnedValue(from.getEarnedValue());
    to.setDate(from.getMonth().toYearMonth());
    return to;
  }

  public Long getIdBaseline() {
    return idBaseline;
  }

  public void setIdBaseline(Long idBaseline) {
    this.idBaseline = idBaseline;
  }

  public BigDecimal getPlannedValue() {
    return this.plannedValue;
  }

  public void setPlannedValue(final BigDecimal plannedValue) {
    this.plannedValue = plannedValue;
  }

  public BigDecimal getActualCost() {
    return this.actualCost;
  }

  public void setActualCost(final BigDecimal actualCost) {
    this.actualCost = actualCost;
  }

  public BigDecimal getEstimatedCost() {
    return this.estimatedCost;
  }

  public void setEstimatedCost(final BigDecimal estimatedCost) {
    this.estimatedCost = estimatedCost;
  }

  public BigDecimal getEarnedValue() {
    return this.earnedValue;
  }

  public void setEarnedValue(final BigDecimal earnedValue) {
    this.earnedValue = earnedValue;
  }

  public YearMonth getDate() {
    return this.date;
  }

  public void setDate(final YearMonth date) {
    this.date = date;
  }

  public EarnedValueByStep getResponse() {
    final EarnedValueByStep step = new EarnedValueByStep();
    step.setIdBaseline(this.idBaseline);
    step.setActualCost(this.actualCost);
    step.setEstimatedCost(this.estimatedCost);
    step.setPlannedValue(this.plannedValue);
    step.setEarnedValue(this.earnedValue);
    step.setDate(this.date);
    return step;
  }

}
