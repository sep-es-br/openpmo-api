package br.gov.es.openpmo.dto.baselines.ccbmemberview;

public class TripleConstraintOutput {

  private final BaselineCostDetail costDetail;

  private final BaselineScheduleDetail scheduleDetail;

  private final BaselineScopeDetail scopeDetail;

  public TripleConstraintOutput(
    final BaselineCostDetail costDetail,
    final BaselineScheduleDetail scheduleDetail,
    final BaselineScopeDetail scopeDetail
  ) {
    this.costDetail = costDetail;
    this.scheduleDetail = scheduleDetail;
    this.scopeDetail = scopeDetail;
  }

  public TripleConstraintOutput() {
    this.costDetail = new BaselineCostDetail();
    this.scheduleDetail = new BaselineScheduleDetail();
    this.scopeDetail = new BaselineScopeDetail();
  }

  public void addCostDetail(final CostDetailItem item) {
    this.costDetail.addDetail(item);
  }

  public BaselineCostDetail getCostDetail() {
    return this.costDetail;
  }

  public BaselineScheduleDetail getScheduleDetail() {
    return this.scheduleDetail;
  }

  public BaselineScopeDetail getScopeDetail() {
    return this.scopeDetail;
  }

  public void addScheduleDetail(final ScheduleDetailItem item) {
    this.scheduleDetail.addScheduleItem(item);
  }

  public void addScopeDetail(final ScopeDetailItem scopeItem) {
    this.scopeDetail.addDetail(scopeItem);
  }

}
