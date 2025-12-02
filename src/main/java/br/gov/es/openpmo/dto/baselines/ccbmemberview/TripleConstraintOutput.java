package br.gov.es.openpmo.dto.baselines.ccbmemberview;

import java.util.ArrayList;
import java.util.List;

import br.gov.es.openpmo.model.office.UnitMeasure;

public class TripleConstraintOutput {
  private final BaselineCostDetail costDetail;

  private final BaselineScheduleDetail scheduleDetail;

  private final BaselineScopeDetail scopeDetail;

  private final List<TripleConstraintBreakdown> tripleConstraintBreakdown;

  private final List<UnitMeasure> officeUnitMeasureConfig;

  public TripleConstraintOutput(
    final BaselineCostDetail costDetail,
    final BaselineScheduleDetail scheduleDetail,
    final BaselineScopeDetail scopeDetail,
    final List<TripleConstraintBreakdown> tripleConstraintBreakdown,
    final List<UnitMeasure> officeUnitMeasureConfig
  ) {
    this.costDetail = costDetail;
    this.scheduleDetail = scheduleDetail;
    this.scopeDetail = scopeDetail;
    this.tripleConstraintBreakdown = tripleConstraintBreakdown;
    this.officeUnitMeasureConfig = officeUnitMeasureConfig;
  }

  public TripleConstraintOutput() {
    this.costDetail = new BaselineCostDetail();
    this.scheduleDetail = new BaselineScheduleDetail();
    this.scopeDetail = new BaselineScopeDetail();
    this.tripleConstraintBreakdown = new ArrayList<TripleConstraintBreakdown>();
    this.officeUnitMeasureConfig = new ArrayList<UnitMeasure>();
  }

  public BaselineCostDetail getCostDetail() {
    return this.costDetail;
  }

  public void addCostDetail(final CostDetailItem item) {
    this.costDetail.addDetail(item);
  }

  public BaselineScheduleDetail getScheduleDetail() {
    return this.scheduleDetail;
  }

  public void addScheduleDetail(final ScheduleDetailItem item) {
    this.scheduleDetail.addScheduleItem(item);
  }

  public BaselineScopeDetail getScopeDetail() {
    return this.scopeDetail;
  }

  public void addScopeDetail(final ScopeDetailItem scopeItem) {
    this.scopeDetail.addDetail(scopeItem);
  }

  public List<TripleConstraintBreakdown> getTripleConstraintBreakdown() {
    return this.tripleConstraintBreakdown;
  }

  public List<UnitMeasure> getOfficeUnitMeasureConfig() {
    return this.officeUnitMeasureConfig;
  }
}
