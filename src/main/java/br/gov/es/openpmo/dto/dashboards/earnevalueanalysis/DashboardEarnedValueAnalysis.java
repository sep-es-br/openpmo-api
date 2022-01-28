package br.gov.es.openpmo.dto.dashboards.earnevalueanalysis;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DashboardEarnedValueAnalysis {

  private final List<EarnedValueByStep> earnedValueByStep;

  @JsonUnwrapped
  private final EarnedValueAnalysisVariables variables;

  private final CostPerformanceIndex costPerformanceIndex;

  private final SchedulePerformanceIndex schedulePerformanceIndex;

  private final BigDecimal estimatesAtCompletion;

  private final BigDecimal estimateToComplete;

  public DashboardEarnedValueAnalysis(
      final List<EarnedValueByStep> earnedValueByStep,
      final CostPerformanceIndex costPerformanceIndex,
      final SchedulePerformanceIndex schedulePerformanceIndex,
      final EarnedValueAnalysisVariables variables,
      final BigDecimal estimatesAtCompletion,
      final BigDecimal estimateToComplete
  ) {
    this.earnedValueByStep = Optional.ofNullable(earnedValueByStep)
      .map(Collections::unmodifiableList)
      .orElse(Collections.emptyList());
    this.costPerformanceIndex = costPerformanceIndex;
    this.schedulePerformanceIndex = schedulePerformanceIndex;
    this.variables = variables;
    this.estimatesAtCompletion = estimatesAtCompletion;
    this.estimateToComplete = estimateToComplete;
  }

  public List<EarnedValueByStep> getEarnedValueByStep() {
    return this.earnedValueByStep;
  }

  public CostPerformanceIndex getCostPerformanceIndex() {
    return this.costPerformanceIndex;
  }

  public SchedulePerformanceIndex getSchedulePerformanceIndex() {
    return this.schedulePerformanceIndex;
  }

  public BigDecimal getEstimatesAtCompletion() {
    return this.estimatesAtCompletion;
  }

  public BigDecimal getEstimateToComplete() {
    return this.estimateToComplete;
  }

  public EarnedValueAnalysisVariables getVariables() {
    return this.variables;
  }

}
