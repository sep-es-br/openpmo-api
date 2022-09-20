package br.gov.es.openpmo.dto.dashboards;

import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.DashboardEarnedValueAnalysis;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.TripleConstraintDataChart;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class DashboardDataResponse {

  private final RiskDataChart risk;

  private final MilestoneDataChart milestone;

  private final TripleConstraintDataChart tripleConstraint;

  @JsonUnwrapped
  private final DatasheetResponse datasheet;

  private final DashboardEarnedValueAnalysis earnedValueAnalysis;

  @JsonCreator
  public DashboardDataResponse(
    final RiskDataChart risk,
    final MilestoneDataChart milestone,
    final TripleConstraintDataChart tripleConstraint,
    final DatasheetResponse datasheet,
    final DashboardEarnedValueAnalysis earnedValueAnalysis
  ) {
    this.risk = risk;
    this.milestone = milestone;
    this.datasheet = datasheet;
    this.earnedValueAnalysis = earnedValueAnalysis;
    this.tripleConstraint = tripleConstraint;
  }

  public RiskDataChart getRisk() {
    return this.risk;
  }

  public MilestoneDataChart getMilestone() {
    return this.milestone;
  }

  public TripleConstraintDataChart getTripleConstraint() {
    return this.tripleConstraint;
  }

  public DatasheetResponse getDatasheet() {
    return this.datasheet;
  }

  public DashboardEarnedValueAnalysis getEarnedValueAnalysis() {
    return this.earnedValueAnalysis;
  }

}
