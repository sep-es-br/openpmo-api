package br.gov.es.openpmo.dto.workpack.breakdown.structure;

import br.gov.es.openpmo.dto.dashboards.DashboardMonthDto;
import br.gov.es.openpmo.dto.dashboards.MilestoneDto;
import br.gov.es.openpmo.dto.dashboards.RiskDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.List;

public class WorkpackRepresentation {

  private Long idWorkpack;

  private String workpackName;

  private String workpackType;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private DashboardMonthDto dashboard;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<RiskDto> risks;

  @JsonUnwrapped
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private MilestoneDto milestone;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<MilestoneDto> milestones;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private ScheduleMeasureUnit unitMeasure;

  public Long getIdWorkpack() {
    return idWorkpack;
  }

  public void setIdWorkpack(Long idWorkpack) {
    this.idWorkpack = idWorkpack;
  }

  public String getWorkpackName() {
    return workpackName;
  }

  public void setWorkpackName(String workpackName) {
    this.workpackName = workpackName;
  }

  public DashboardMonthDto getDashboard() {
    return dashboard;
  }

  public void setDashboard(DashboardMonthDto dashboard) {
    this.dashboard = dashboard;
  }

  public List<RiskDto> getRisks() {
    return risks;
  }

  public void setRisks(List<RiskDto> risks) {
    this.risks = risks;
  }

  public MilestoneDto getMilestone() {
    return milestone;
  }

  public void setMilestone(MilestoneDto milestone) {
    this.milestone = milestone;
  }

  public List<MilestoneDto> getMilestones() {
    return milestones;
  }

  public void setMilestones(List<MilestoneDto> milestones) {
    this.milestones = milestones;
  }

  public String getWorkpackType() {
    return workpackType;
  }

  public void setWorkpackType(String workpackType) {
    this.workpackType = workpackType;
  }

  public ScheduleMeasureUnit getUnitMeasure() {
    return unitMeasure;
  }

  public void setUnitMeasure(ScheduleMeasureUnit unitMeasure) {
    this.unitMeasure = unitMeasure;
  }
}
