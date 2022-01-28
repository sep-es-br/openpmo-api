package br.gov.es.openpmo.dto.dashboards.tripleconstraint;

public class TripleConstraintDataChart {

  private CostDataChart cost;
  private ScheduleDataChart schedule;
  private ScopeDataChart scope;

  public CostDataChart getCost() {
    return this.cost;
  }

  public void setCost(final CostDataChart cost) {
    this.cost = cost;
  }

  public ScheduleDataChart getSchedule() {
    return this.schedule;
  }

  public void setSchedule(final ScheduleDataChart schedule) {
    this.schedule = schedule;
  }

  public ScopeDataChart getScope() {
    return this.scope;
  }

  public void setScope(final ScopeDataChart scope) {
    this.scope = scope;
  }

  public void sumCostData(final CostDataChart cost) {
    if(this.cost == null) this.cost = new CostDataChart();
    this.cost.sumCostData(cost);
  }

  public void sumScopeData(final ScopeDataChart scopeData) {
    if(this.scope == null) this.scope = new ScopeDataChart();
    this.scope.sumScopeData(scopeData);
  }

  public void round() {
    this.cost.round();
    this.schedule.round();
    this.scope.round();
  }
}
