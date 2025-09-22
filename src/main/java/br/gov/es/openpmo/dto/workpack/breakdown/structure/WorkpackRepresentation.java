package br.gov.es.openpmo.dto.workpack.breakdown.structure;

import br.gov.es.openpmo.dto.MilestoneResultDto;
import br.gov.es.openpmo.dto.dashboards.DashboardMonthDto;
import br.gov.es.openpmo.dto.dashboards.MilestoneDto;
import br.gov.es.openpmo.dto.dashboards.RiskResultDto;
import br.gov.es.openpmo.enumerator.BaselineStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class WorkpackRepresentation {
  private Long idWorkpack;

  private String workpackName;

  private String workpackType;

  private Long idWorkpackModelLinked;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private DashboardMonthDto dashboard;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private RiskResultDto risks;

  @JsonUnwrapped
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private MilestoneDto milestone;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private MilestoneResultDto milestones;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private ScheduleMeasureUnit unitMeasure;

  private JournalInformationDto journalInformation;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Boolean projectHasActiveBaseline;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private BaselineStatus milestoneStatus;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private BaselineStatus deliverableStatus;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Boolean deliveryHasSchedule;

  /* Getters and Setters */

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

  public MilestoneDto getMilestone() {
    return milestone;
  }

  public void setMilestone(MilestoneDto milestone) {
    this.milestone = milestone;
  }

  public RiskResultDto getRisks() {
    return risks;
  }

  public void setRisks(RiskResultDto risks) {
    this.risks = risks;
  }

  public MilestoneResultDto getMilestones() {
    return milestones;
  }

  public void setMilestones(MilestoneResultDto milestones) {
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

  public JournalInformationDto getJournalInformation() {
    return journalInformation;
  }

  public void setJournalInformation(JournalInformationDto journalInformation) {
    this.journalInformation = journalInformation;
  }

  public Long getIdWorkpackModelLinked() {
    return idWorkpackModelLinked;
  }

  public void setIdWorkpackModelLinked(Long idWorkpackModelLinked) {
    this.idWorkpackModelLinked = idWorkpackModelLinked;
  }

  public Boolean getProjectHasActiveBaseline() {
    return projectHasActiveBaseline;
  }

  public void setProjectHasActiveBaseline(Boolean projectHasActiveBaseline) {
    this.projectHasActiveBaseline = projectHasActiveBaseline;
  }

  public BaselineStatus getMilestoneStatus() {
    return milestoneStatus;
  }

  public void setMilestoneStatus(BaselineStatus milestoneStatus) {
    this.milestoneStatus = milestoneStatus;
  }

  public BaselineStatus getDeliverableStatus() {
    return deliverableStatus;
  }

  public void setDeliverableStatus(BaselineStatus deliveryStatus) {
    this.deliverableStatus = deliveryStatus;
  }

  public Boolean getDeliveryHasSchedule() {
    return deliveryHasSchedule;
  }

  public void setDeliveryHasSchedule(Boolean deliveryHasSchedule) {
    this.deliveryHasSchedule = deliveryHasSchedule;
  }
}
