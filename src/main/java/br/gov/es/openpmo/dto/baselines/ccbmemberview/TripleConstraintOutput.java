package br.gov.es.openpmo.dto.baselines.ccbmemberview;

import java.util.ArrayList;
import java.util.List;

public class TripleConstraintOutput {
  private final BaselineCostDetail costDetail;

  private final BaselineScheduleDetail scheduleDetail;

  private final BaselineScopeDetail scopeDetail;

  private final List<TripleConstraintBreakdown> tripleConstraintBreakdown;

  public TripleConstraintOutput(
    final BaselineCostDetail costDetail,
    final BaselineScheduleDetail scheduleDetail,
    final BaselineScopeDetail scopeDetail,
    final List<TripleConstraintBreakdown> tripleConstraintBreakdown
  ) {
    this.costDetail = costDetail;
    this.scheduleDetail = scheduleDetail;
    this.scopeDetail = scopeDetail;
    this.tripleConstraintBreakdown = tripleConstraintBreakdown;
  }

  public TripleConstraintOutput() {
    this.costDetail = new BaselineCostDetail();
    this.scheduleDetail = new BaselineScheduleDetail();
    this.scopeDetail = new BaselineScopeDetail();
    this.tripleConstraintBreakdown = new ArrayList<TripleConstraintBreakdown>();
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

  public List<TripleConstraintBreakdown> getTripleConstraintBreakdown() {
    return this.tripleConstraintBreakdown;
  }

}
