package br.gov.es.openpmo.dto.dashboards.earnevalueanalysis;

import java.util.List;

public class DashboardEarnedValueAnalysis {

  private List<EarnedValueByStep> earnedValueByStep;

  private List<PerformanceIndexesByStep> performanceIndexes;

  public DashboardEarnedValueAnalysis(
    final List<EarnedValueByStep> earnedValueByStep,
    final List<PerformanceIndexesByStep> performanceIndexes
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

  public List<PerformanceIndexesByStep> getPerformanceIndexes() {
    return this.performanceIndexes;
  }

  public void setPerformanceIndexes(final List<PerformanceIndexesByStep> performanceIndexes) {
    this.performanceIndexes = performanceIndexes;
  }

}
