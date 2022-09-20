package br.gov.es.openpmo.dto.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.MilestoneDataChart;
import br.gov.es.openpmo.dto.dashboards.RiskDataChart;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetStakeholderResponse;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetTotalizers;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.DashboardEarnedValueAnalysis;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.TripleConstraintDataChart;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DashboardResponse {

  private final RiskDataChart risk;

  private final MilestoneDataChart milestone;

  private final List<TripleConstraintDataChart> tripleConstraint;

  @JsonUnwrapped
  private final DatasheetResponse datasheet;

  private final DashboardEarnedValueAnalysis earnedValueAnalysis;

  @JsonCreator
  public DashboardResponse(
    final RiskDataChart risk,
    final MilestoneDataChart milestone,
    final List<TripleConstraintDataChart> tripleConstraint,
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

  public List<TripleConstraintDataChart> getTripleConstraint() {
    return this.tripleConstraint;
  }

  public DashboardEarnedValueAnalysis getEarnedValueAnalysis() {
    return this.earnedValueAnalysis;
  }

  @JsonIgnore
  public DatasheetTotalizers getTotalizers() {
    return Optional.ofNullable(this.getDatasheet())
      .map(DatasheetResponse::getDatasheetTotalizers)
      .orElse(null);
  }

  public DatasheetResponse getDatasheet() {
    return this.datasheet;
  }

  @JsonIgnore
  public Set<DatasheetStakeholderResponse> getStakeholders() {
    return Optional.ofNullable(this.getDatasheet())
      .map(DatasheetResponse::getStakeholders)
      .orElse(Collections.emptySet());
  }

}
