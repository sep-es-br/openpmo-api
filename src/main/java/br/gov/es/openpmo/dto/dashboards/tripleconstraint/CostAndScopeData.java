package br.gov.es.openpmo.dto.dashboards.tripleconstraint;

public class CostAndScopeData {

  private final CostDataChart costDataChart;
  private final ScopeDataChart scopeDataChart;

  public CostAndScopeData(
    final CostDataChart costDataChart,
    final ScopeDataChart scopeDataChart
  ) {
    this.costDataChart = costDataChart;
    this.scopeDataChart = scopeDataChart;
  }

  public CostDataChart getCostDataChart() {
    return this.costDataChart;
  }

  public ScopeDataChart getScopeDataChart() {
    return this.scopeDataChart;
  }

}
