package br.gov.es.openpmo.dto.workpackmodel.params;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.annotations.ApiModel;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({@Type(value = PortfolioModelParamDto.class, name = "PortfolioModel"),
    @Type(value = ProgramModelParamDto.class, name = "ProgramModel"),
    @Type(value = OrganizerModelParamDto.class, name = "OrganizerModel"),
    @Type(value = DeliverableModelParamDto.class, name = "DeliverableModel"),
    @Type(value = ProjectModelParamDto.class, name = "ProjectModel"),
    @Type(value = MilestoneModelParamDto.class, name = "MilestoneModel")})
@ApiModel(subTypes = {PortfolioModelParamDto.class, ProgramModelParamDto.class, OrganizerModelParamDto.class, DeliverableModelParamDto.class,
    ProjectModelParamDto.class,
    MilestoneModelParamDto.class}, discriminator = "type", description = "Supertype of all WorkpackModel.")
public abstract class WorkpackModelParamDto {

  private static final String PACKAGE_DTO = "br.gov.es.openpmo.dto.workpackmodel.params";

  private Long id;

  @NotBlank(message = "fontIcon.not.blank")
  private String fontIcon;

  @NotBlank(message = "modelName.not.blank")
  private String modelName;

  private String modelNameInPlural;

  private boolean costSessionActive;

  private boolean stakeholderSessionActive;

  private boolean childWorkpackModelSessionActive;

  private boolean scheduleSessionActive;

  private boolean riskAndIssueManagementSessionActive;

  private boolean processesManagementSessionActive;

  private Boolean journalManagementSessionActive;

  private List<String> personRoles;

  private List<String> organizationRoles;

  @Valid
  private List<? extends PropertyModelDto> properties;

  private Long idParent;

  private String sortBy;

  @JsonUnwrapped
  private DashboardConfiguration dashboardConfiguration;

  @NotNull
  private Long idPlanModel;

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

  public boolean isCostSessionActive() {
    return this.costSessionActive;
  }

  public void setCostSessionActive(final boolean costSessionActive) {
    this.costSessionActive = costSessionActive;
  }

  public boolean isStakeholderSessionActive() {
    return this.stakeholderSessionActive;
  }

  public void setStakeholderSessionActive(final boolean stakeholderSessionActive) {
    this.stakeholderSessionActive = stakeholderSessionActive;
  }

  public boolean isChildWorkpackModelSessionActive() {
    return this.childWorkpackModelSessionActive;
  }

  public void setChildWorkpackModelSessionActive(final boolean childWorkpackModelSessionActive) {
    this.childWorkpackModelSessionActive = childWorkpackModelSessionActive;
  }

  public boolean isScheduleSessionActive() {
    return this.scheduleSessionActive;
  }

  public void setScheduleSessionActive(final boolean scheduleSessionActive) {
    this.scheduleSessionActive = scheduleSessionActive;
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

  public Long getIdPlanModel() {
    return this.idPlanModel;
  }

  public void setIdPlanModel(final Long idPlanModel) {
    this.idPlanModel = idPlanModel;
  }

  public List<? extends PropertyModelDto> getProperties() {
    return Collections.unmodifiableList(this.properties);
  }

  public void setProperties(final List<? extends PropertyModelDto> properties) {
    this.properties = properties;
  }

  public String getSortBy() {
    return this.sortBy;
  }

  public void setSortBy(final String sortBy) {
    this.sortBy = sortBy;
  }

  public boolean isRiskAndIssueManagementSessionActive() {
    return this.riskAndIssueManagementSessionActive;
  }

  public void setRiskAndIssueManagementSessionActive(final boolean riskAndIssueManagementSessionActive) {
    this.riskAndIssueManagementSessionActive = riskAndIssueManagementSessionActive;
  }

  public boolean isProcessesManagementSessionActive() {
    return this.processesManagementSessionActive;
  }

  public void setProcessesManagementSessionActive(final boolean processesManagementSessionActive) {
    this.processesManagementSessionActive = processesManagementSessionActive;
  }

  public boolean isDeliverableDtoOrMilestoneDto() {
    return this.isDeliverableDto() || this.isMilestoneDto();
  }

  public boolean isDeliverableDto() {
    return this.getClass().getTypeName().equals(PACKAGE_DTO + ".DeliverableModelDto");
  }

  public boolean isMilestoneDto() {
    return this.getClass().getTypeName().equals(PACKAGE_DTO + ".MilestoneModelDto");
  }

  public boolean hasNoParent() {
    return this.getIdParent() == null;
  }

  public Long getIdParent() {
    return this.idParent;
  }

  public void setIdParent(final Long idParent) {
    this.idParent = idParent;
  }

  public Boolean isJournalManagementSessionActive() {
    return this.journalManagementSessionActive;
  }

  public void setJournalManagementSessionActive(final Boolean journalManagementSessionActive) {
    this.journalManagementSessionActive = journalManagementSessionActive;
  }

  public DashboardConfiguration getDashboardConfiguration() {
    return this.dashboardConfiguration;
  }

  public void setDashboardConfiguration(final DashboardConfiguration dashboardConfiguration) {
    this.dashboardConfiguration = dashboardConfiguration;
  }

}
