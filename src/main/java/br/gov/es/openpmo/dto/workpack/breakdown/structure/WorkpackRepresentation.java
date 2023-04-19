package br.gov.es.openpmo.dto.workpack.breakdown.structure;

import br.gov.es.openpmo.dto.dashboards.v2.SimpleDashboard;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class WorkpackRepresentation {

  private Long idWorkpack;

  private String workpackName;

  private String workpackType;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private SimpleDashboard dashboard;

  @JsonUnwrapped
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private MilestoneRepresentation milestone;

  @JsonUnwrapped
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private ScheduleRepresentation schedule;

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

  public SimpleDashboard getDashboard() {
    return dashboard;
  }

  public void setDashboard(SimpleDashboard dashboard) {
    this.dashboard = dashboard;
  }

  public MilestoneRepresentation getMilestone() {
    return milestone;
  }

  public void setMilestone(MilestoneRepresentation milestone) {
    this.milestone = milestone;
  }

  public ScheduleRepresentation getSchedule() {
    return schedule;
  }

  public void setSchedule(ScheduleRepresentation schedule) {
    this.schedule = schedule;
  }

  public String getWorkpackType() {
    return workpackType;
  }

  public void setWorkpackType(String workpackType) {
    this.workpackType = workpackType;
  }

}
