package br.gov.es.openpmo.dto.dashboards.v2;

import br.gov.es.openpmo.dto.MilestoneResultDto;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.ScheduleInterval;
import br.gov.es.openpmo.dto.dashboards.PerformanceIndexDto;
import br.gov.es.openpmo.dto.dashboards.RiskDataChart;
import br.gov.es.openpmo.dto.dashboards.TripleConstraintDto;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetStakeholderResponse;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetTotalizers;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStepDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DashboardResponse {

  private final RiskDataChart risk;

  private final TripleConstraintDto tripleConstraint;

  private final PerformanceIndexDto performanceIndex;

  private final ScheduleInterval scheduleInterval;

  private final MilestoneResultDto milestone;



  @JsonUnwrapped
  private final DatasheetResponse datasheet;

  private final List<EarnedValueByStepDto> earnedValueByStep;

  @JsonCreator
  public DashboardResponse(
    final RiskDataChart risk,
    final TripleConstraintDto tripleConstraint,
    final DatasheetResponse datasheet,
    final List<EarnedValueByStepDto> earnedValueByStep,
    final PerformanceIndexDto performanceIndexes,
    final MilestoneResultDto milestone,
    final ScheduleInterval scheduleInterval
  ) {
    this.risk = risk;
    this.datasheet = datasheet;
    this.earnedValueByStep = earnedValueByStep;
    this.tripleConstraint = tripleConstraint;
    this.performanceIndex = performanceIndexes;
    this.milestone = milestone;
    this.scheduleInterval = scheduleInterval;
  }

  public RiskDataChart getRisk() {
    return this.risk;
  }

  public MilestoneResultDto getMilestone() {
    return milestone;
  }

  public TripleConstraintDto getTripleConstraint() {
    return tripleConstraint;
  }

  public List<EarnedValueByStepDto> getEarnedValueByStep() {
    return this.earnedValueByStep;
  }

  public PerformanceIndexDto getPerformanceIndex() {
    return performanceIndex;
  }

  public ScheduleInterval getScheduleInterval() {
    return scheduleInterval;
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
