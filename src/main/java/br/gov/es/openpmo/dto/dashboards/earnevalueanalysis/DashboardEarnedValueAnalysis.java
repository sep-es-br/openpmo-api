package br.gov.es.openpmo.dto.dashboards.earnevalueanalysis;

import java.util.List;

public class DashboardEarnedValueAnalysis {

  private List<EarnedValueByStep> earnedValueByStep;



  public DashboardEarnedValueAnalysis(
    final List<EarnedValueByStep> earnedValueByStep
  ) {
    this.earnedValueByStep = earnedValueByStep;
  }

  public List<EarnedValueByStep> getEarnedValueByStep() {
    return this.earnedValueByStep;
  }

  public void setEarnedValueByStep(final List<EarnedValueByStep> earnedValueByStep) {
    this.earnedValueByStep = earnedValueByStep;
  }

}
