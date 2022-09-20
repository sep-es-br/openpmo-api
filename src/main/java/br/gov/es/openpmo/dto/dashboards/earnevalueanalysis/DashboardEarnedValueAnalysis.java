package br.gov.es.openpmo.dto.dashboards.earnevalueanalysis;

import java.util.List;

public class DashboardEarnedValueAnalysis {

  private List<EarnedValueByStep> earnedValueByStep;

  private List<PerformanceIndexes> performanceIndexes;

  public DashboardEarnedValueAnalysis(
    final List<EarnedValueByStep> earnedValueByStep,
    final List<PerformanceIndexes> performanceIndexes
  ) {
    this.earnedValueByStep = earnedValueByStep;
    this.performanceIndexes = performanceIndexes;
  }

  public List<EarnedValueByStep> getEarnedValueByStep() {
    return this.earnedValueByStep;
  }

  public void setEarnedValueByStep(final List<EarnedValueByStep> earnedValueByStep) {
    this.earnedValueByStep = earnedValueByStep;
  }

  public List<PerformanceIndexes> getPerformanceIndexes() {
    return this.performanceIndexes;
  }

  public void setPerformanceIndexes(final List<PerformanceIndexes> performanceIndexes) {
    this.performanceIndexes = performanceIndexes;
  }

}
