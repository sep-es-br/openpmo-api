package br.gov.es.openpmo.dto.workpackmodel.details;

import br.gov.es.openpmo.dto.planmodel.PlanModelDto;
import br.gov.es.openpmo.dto.workpackmodel.MilestoneModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.DashboardConfiguration;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.annotations.ApiModel;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({@Type(value = PortfolioModelDetailDto.class, name = "PortfolioModel"),
  @Type(value = ProgramModelDetailDto.class, name = "ProgramModel"),
  @Type(value = OrganizerModelDetailDto.class, name = "OrganizerModel"),
  @Type(value = DeliverableModelDetailDto.class, name = "DeliverableModel"),
  @Type(value = ProjectModelDetailDto.class, name = "ProjectModel"),
  @Type(value = MilestoneModelDetailDto.class, name = "MilestoneModel")})
@ApiModel(subTypes = {PortfolioModelDetailDto.class, ProgramModelDetailDto.class, OrganizerModelDetailDto.class,
  DeliverableModelDetailDto.class, ProjectModelDetailDto.class,
  MilestoneModelDto.class}, discriminator = "type", description = "Supertype of all WorkpackModel.")
public abstract class WorkpackModelDetailDto {

  private Long id;

  private String fontIcon;

  private String modelName;

  private String modelNameInPlural;

  private Boolean costSessionActive;

  private Boolean scheduleSessionActive;

  private Boolean stakeholderSessionActive;

  private Boolean childWorkpackModelSessionActive;

  private Boolean riskAndIssueManagementSessionActive;

  private Boolean processesManagementSessionActive;

  @JsonUnwrapped
  private DashboardConfiguration dashboardConfiguration;

  private List<String> personRoles;

  private List<String> organizationRoles;

  private List<? extends PropertyModelDto> properties;

  private PlanModelDto planModel;

  private Set<WorkpackModelDetailDto> children;

  private Collection<WorkpackModelDto> parent;

  private PropertyModelDto sortBy;

  private Boolean journalManagementSessionActive;

  public WorkpackModelDetailDto() {
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getFontIcon() {
    return this.fontIcon;
  }

  public void setFontIcon(final String fontIcon) {
    this.fontIcon = fontIcon;
  }

  public String getModelName() {
    return this.modelName;
  }

  public void setModelName(final String modelName) {
    this.modelName = modelName;
  }

  public String getModelNameInPlural() {
    return this.modelNameInPlural;
  }

  public void setModelNameInPlural(final String modelNameInPlural) {
    this.modelNameInPlural = modelNameInPlural;
  }

  public List<String> getPersonRoles() {
    return this.personRoles;
  }

  public void setPersonRoles(final List<String> personRoles) {
    this.personRoles = personRoles;
  }

  public List<String> getOrganizationRoles() {
    return this.organizationRoles;
  }

  public void setOrganizationRoles(final List<String> organizationRoles) {
    this.organizationRoles = organizationRoles;
  }

  public List<? extends PropertyModelDto> getProperties() {
    return this.properties;
  }

  public void setProperties(final List<? extends PropertyModelDto> properties) {
    this.properties = properties;
  }

  public PlanModelDto getPlanModel() {
    return this.planModel;
  }

  public void setPlanModel(final PlanModelDto planModel) {
    this.planModel = planModel;
  }

  public Set<WorkpackModelDetailDto> getChildren() {
    return this.children;
  }

  public void setChildren(final Set<WorkpackModelDetailDto> children) {
    this.children = children;
  }

  public Collection<WorkpackModelDto> getParent() {
    return this.parent;
  }

  public void setParent(final Collection<WorkpackModelDto> parent) {
    this.parent = parent;
  }

  public PropertyModelDto getSortBy() {
    return this.sortBy;
  }

  public void setSortBy(final PropertyModelDto sortBy) {
    this.sortBy = sortBy;
  }

  public Boolean getCostSessionActive() {
    return this.costSessionActive;
  }

  public void setCostSessionActive(final boolean costSessionActive) {
    this.costSessionActive = costSessionActive;
  }

  public void setCostSessionActive(final Boolean costSessionActive) {
    this.costSessionActive = costSessionActive;
  }

  public Boolean getScheduleSessionActive() {
    return this.scheduleSessionActive;
  }

  public void setScheduleSessionActive(final boolean scheduleSessionActive) {
    this.scheduleSessionActive = scheduleSessionActive;
  }

  public void setScheduleSessionActive(final Boolean scheduleSessionActive) {
    this.scheduleSessionActive = scheduleSessionActive;
  }

  public Boolean getStakeholderSessionActive() {
    return this.stakeholderSessionActive;
  }

  public void setStakeholderSessionActive(final boolean stakeholderSessionActive) {
    this.stakeholderSessionActive = stakeholderSessionActive;
  }

  public void setStakeholderSessionActive(final Boolean stakeholderSessionActive) {
    this.stakeholderSessionActive = stakeholderSessionActive;
  }

  public Boolean getChildWorkpackModelSessionActive() {
    return this.childWorkpackModelSessionActive;
  }

  public void setChildWorkpackModelSessionActive(final boolean childWorkpackModelSessionActive) {
    this.childWorkpackModelSessionActive = childWorkpackModelSessionActive;
  }

  public void setChildWorkpackModelSessionActive(final Boolean childWorkpackModelSessionActive) {
    this.childWorkpackModelSessionActive = childWorkpackModelSessionActive;
  }

  public Boolean getRiskAndIssueManagementSessionActive() {
    return this.riskAndIssueManagementSessionActive;
  }

  public void setRiskAndIssueManagementSessionActive(final Boolean riskAndIssueManagementSessionActive) {
    this.riskAndIssueManagementSessionActive = riskAndIssueManagementSessionActive;
  }

  public Boolean getProcessesManagementSessionActive() {
    return this.processesManagementSessionActive;
  }

  public void setProcessesManagementSessionActive(final Boolean processesManagementSessionActive) {
    this.processesManagementSessionActive = processesManagementSessionActive;
  }

  public boolean hasProperties() {
    return this.properties != null && !this.properties.isEmpty();
  }

  public DashboardConfiguration getDashboardConfiguration() {
    return this.dashboardConfiguration;
  }

  public void setDashboardConfiguration(final DashboardConfiguration dashboardConfiguration) {
    this.dashboardConfiguration = dashboardConfiguration;
  }

  public void dashboardConfiguration(final WorkpackModel workpackModel) {
    if(Objects.isNull(workpackModel)) return;
    this.dashboardConfiguration = new DashboardConfiguration(
      workpackModel.getDashboardShowRisks(),
      workpackModel.getDashboardShowEva(),
      workpackModel.getDashboardShowMilestones(),
      Optional.of(workpackModel)
        .map(WorkpackModel::getDashboardShowStakeholders)
        .orElse(new HashSet<>())
    );
  }

  public Boolean getJournalManagementSessionActive() {
    return this.journalManagementSessionActive;
  }

  public void setJournalManagementSessionActive(final Boolean journalManagementSessionActive) {
    this.journalManagementSessionActive = journalManagementSessionActive;
  }

}
