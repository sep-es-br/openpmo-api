package br.gov.es.openpmo.dto.dashboards.earnevalueanalysis;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static java.util.Objects.isNull;

public class EarnedValueAnalysisDerivedVariables {

  private final EarnedValueAnalysisVariables variables;

  private EarnedValueAnalysisDerivedVariables(final EarnedValueAnalysisVariables variables) {
    this.variables = variables;
  }

  public static EarnedValueAnalysisDerivedVariables create(final EarnedValueAnalysisVariables variables) {
    if(variables.getPlannedValue() != null && variables.getPlannedValue().compareTo(BigDecimal.ZERO) == 0) {
      return new EarnedValueAnalysisDerivedVariables(null);
    }
    return new EarnedValueAnalysisDerivedVariables(variables);
  }

  public BigDecimal getCostVariance() {
    if(this.variables == null) return null;
    return Optional.of(this.variables)
      .map(EarnedValueAnalysisVariables::getEarnedValue)
      .map(earnedValue -> earnedValue.subtract(this.variables.getActualCost()))
      .orElse(null);
  }

  public BigDecimal getScheduleVariance() {
    if(this.variables == null) return null;
    return Optional.of(this.variables)
      .map(EarnedValueAnalysisVariables::getEarnedValue)
      .map(earnedValue -> earnedValue.subtract(this.variables.getPlannedValue()))
      .orElse(null);
  }

  public BigDecimal getSchedulePerformanceIndex() {
    if(this.variables == null) return null;
    final BigDecimal plannedValue = this.variables.getPlannedValue();
    if(isValidDivisor(plannedValue)) {
      return null;
    }
    return Optional.of(this.variables)
      .map(EarnedValueAnalysisVariables::getEarnedValue)
      .map(earnedValue -> earnedValue.divide(plannedValue, 2, RoundingMode.HALF_UP))
      .orElse(null);
  }

  private static boolean isValidDivisor(final Comparable<? super BigDecimal> divisor) {
    return isNull(divisor) || divisor.compareTo(BigDecimal.ZERO) == 0;
  }

  public BigDecimal getEstimateAtComplete() {
    if(this.variables == null) return null;
    final BigDecimal estimateAtCompletion = this.getEstimateAtCompletion();
    final BigDecimal actualCost = this.variables.getActualCost();
    if(actualCost == null) {
      return estimateAtCompletion;
    }
    return Optional.ofNullable(estimateAtCompletion)
      .map(eac -> eac.subtract(actualCost))
      .orElse(null);
  }

  public BigDecimal getEstimateAtCompletion() {
    if(this.variables == null) return null;
    final BigDecimal cpi = this.getCostPerformanceIndex();
    if(isValidDivisor(cpi)) {
      return null;
    }
    final BigDecimal budgetAtCompletion = this.variables.getPlannedValue();
    return Optional.ofNullable(budgetAtCompletion)
      .map(bac -> bac.divide(cpi, 2, RoundingMode.HALF_UP))
      .orElse(null);
  }

  public BigDecimal getCostPerformanceIndex() {
    if(this.variables == null) return null;
    final BigDecimal actualCost = this.variables.getActualCost();
    if(isValidDivisor(actualCost)) {
      return null;
    }
    return Optional.of(this.variables)
      .map(EarnedValueAnalysisVariables::getEarnedValue)
      .map(earnedValue -> earnedValue.divide(actualCost, 2, RoundingMode.HALF_UP))
      .orElse(null);
  }
}
