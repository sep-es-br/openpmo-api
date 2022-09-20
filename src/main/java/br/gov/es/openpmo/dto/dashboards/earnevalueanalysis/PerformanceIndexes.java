package br.gov.es.openpmo.dto.dashboards.earnevalueanalysis;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.YearMonth;

public class PerformanceIndexes {

  private final BigDecimal actualCost;

  private final BigDecimal plannedValue;

  private final BigDecimal earnedValue;

  private final BigDecimal estimatesAtCompletion;

  private final BigDecimal estimateToComplete;

  private final CostPerformanceIndex costPerformanceIndex;

  private final SchedulePerformanceIndex schedulePerformanceIndex;

  @JsonFormat(pattern = "yyyy-MM")
  private final YearMonth date;

  public PerformanceIndexes(
    final BigDecimal actualCost,
    final BigDecimal plannedValue,
    final BigDecimal earnedValue,
    final BigDecimal estimatesAtCompletion,
    final BigDecimal estimateToComplete,
    final CostPerformanceIndex costPerformanceIndex,
    final SchedulePerformanceIndex schedulePerformanceIndex,
    final YearMonth date
  ) {
    this.actualCost = actualCost;
    this.plannedValue = plannedValue;
    this.earnedValue = earnedValue;
    this.estimatesAtCompletion = estimatesAtCompletion;
    this.estimateToComplete = estimateToComplete;
    this.costPerformanceIndex = costPerformanceIndex;
    this.schedulePerformanceIndex = schedulePerformanceIndex;
    this.date = date;
  }

  public BigDecimal getActualCost() {
    return this.actualCost;
  }

  public BigDecimal getPlannedValue() {
    return this.plannedValue;
  }

  public BigDecimal getEarnedValue() {
    return this.earnedValue;
  }

  public BigDecimal getEstimatesAtCompletion() {
    return this.estimatesAtCompletion;
  }

  public BigDecimal getEstimateToComplete() {
    return this.estimateToComplete;
  }

  public CostPerformanceIndex getCostPerformanceIndex() {
    return this.costPerformanceIndex;
  }

  public SchedulePerformanceIndex getSchedulePerformanceIndex() {
    return this.schedulePerformanceIndex;
  }

  public YearMonth getDate() {
    return this.date;
  }

}
