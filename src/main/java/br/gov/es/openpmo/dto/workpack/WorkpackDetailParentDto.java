package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.dto.dashboards.DashboardMonthDto;
import br.gov.es.openpmo.dto.dashboards.MilestoneDto;
import br.gov.es.openpmo.dto.dashboards.RiskDto;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.plan.PlanDto;
import br.gov.es.openpmo.model.relations.IsLinkedTo;
import br.gov.es.openpmo.model.workpacks.Workpack;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.swagger.annotations.ApiModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({@Type(value = PortfolioDetailParentDto.class, name = "Portfolio"),
  @Type(value = ProgramDetailParentDto.class, name = "Program"),
  @Type(value = OrganizerDetailParentDto.class, name = "Organizer"),
  @Type(value = DeliverableDetailParentDto.class, name = "Deliverable"),
  @Type(value = ProjectDetailParentDto.class, name = "Project"),
  @Type(value = MilestoneDetailParentDto.class, name = "Milestone")})
@ApiModel(subTypes = {PortfolioDetailParentDto.class, ProgramDetailParentDto.class, OrganizerDetailParentDto.class,
  DeliverableDetailParentDto.class, ProjectDetailParentDto.class,
  MilestoneDetailParentDto.class}, discriminator = "type", description = "Supertype of all Workpack.")
public abstract class WorkpackDetailParentDto {

  private Long id;

  private Long idWorkpackModel;

  private PlanDto plan;

  private String name;

  private String fullName;

  private LocalDateTime date;

  private String fontIcon;

  private List<PermissionDto> permissions;

  private Boolean sharedWith;

  private Long linkedModel;

  private Boolean linked;

  @JsonProperty("cancelable")
  private boolean isCancelable;

  @JsonProperty("canceled")
  private boolean isCanceled;

  @JsonProperty("canDeleted")
  private boolean canBeDeleted;

  private boolean hasActiveBaseline;

  private boolean pendingBaseline;

  private boolean cancelPropose;

  private String activeBaselineName;

  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate endManagementDate;

  private String reason;

  private Boolean completed;

  private DashboardMonthDto dashboard;

  private List<MilestoneDto> milestones;

  private List<RiskDto> risks;

  public static <TYPE extends WorkpackDetailParentDto> WorkpackDetailParentDto of(
    final Workpack workpack,
    final Supplier<TYPE> instanceSupplier
  ) {
    final TYPE instance = instanceSupplier.get();
    instance.setId(workpack.getId());
    instance.setCancelable(workpack.isCancelable());
    instance.setCanceled(workpack.isCanceled());
    instance.setCanBeDeleted(workpack.isDeleted());
    instance.setEndManagementDate(workpack.getEndManagementDate());
    instance.setReason(workpack.getReason());
    instance.setCompleted(workpack.getCompleted());
    return instance;
  }

  public void applyLinkedStatus(
    final Workpack workpack,
    final Long idWorkpackModel
  ) {
    this.linkedModel = Optional.ofNullable(workpack.getLinkedTo())
      .flatMap(linkeds -> linkeds.stream()
        .map(IsLinkedTo::getWorkpackModelId)
        .filter(id -> Objects.equals(id, idWorkpackModel))
        .findFirst())
      .orElse(null);
    this.linked = this.linkedModel != null;
  }

  public Long getIdWorkpackModel() {
    return idWorkpackModel;
  }

  public void setIdWorkpackModel(Long idWorkpackModel) {
    this.idWorkpackModel = idWorkpackModel;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getFontIcon() {
    return fontIcon;
  }

  public void setFontIcon(String fontIcon) {
    this.fontIcon = fontIcon;
  }

  public void setCanBeDeleted(final boolean canBeDeleted) {
    this.canBeDeleted = canBeDeleted;
  }

  public boolean isHasActiveBaseline() {
    return this.hasActiveBaseline;
  }

  public void setHasActiveBaseline(final boolean hasActiveBaseline) {
    this.hasActiveBaseline = hasActiveBaseline;
  }

  public Boolean getSharedWith() {
    return this.sharedWith;
  }

  public void setSharedWith(final Boolean sharedWith) {
    this.sharedWith = sharedWith;
  }

  public Long getLinkedModel() {
    return linkedModel;
  }

  public void setLinkedModel(Long linkedModel) {
    this.linkedModel = linkedModel;
  }

  public Boolean getLinked() {
    return this.linked;
  }

  public void setLinked(final Boolean linked) {
    this.linked = linked;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public PlanDto getPlan() {
    return this.plan;
  }

  public void setPlan(final PlanDto plan) {
    this.plan = plan;
  }

  public List<PermissionDto> getPermissions() {
    return this.permissions;
  }

  public void setPermissions(final List<PermissionDto> permissions) {
    this.permissions = permissions;
  }

  public boolean samePlanId(final Long planId) {
    return this.plan.getId().equals(planId);
  }

  @JsonIgnore
  public Long getIdPlan() {
    return this.plan.getId();
  }

  public boolean isCancelable() {
    return this.isCancelable;
  }

  public void setCancelable(final boolean cancelable) {
    this.isCancelable = cancelable;
  }

  public boolean isCanceled() {
    return this.isCanceled;
  }

  public void setCanceled(final boolean canceled) {
    this.isCanceled = canceled;
  }

  public boolean canBeDeleted() {
    return this.canBeDeleted;
  }

  public boolean getPendingBaseline() {
    return this.pendingBaseline;
  }

  public void setPendingBaseline(final boolean pendingBaseline) {
    this.pendingBaseline = pendingBaseline;
  }

  public boolean getCancelPropose() {
    return this.cancelPropose;
  }

  public void setCancelPropose(final boolean cancelPropose) {
    this.cancelPropose = cancelPropose;
  }

  public LocalDate getEndManagementDate() {
    return this.endManagementDate;
  }

  public void setEndManagementDate(final LocalDate endManagementDate) {
    this.endManagementDate = endManagementDate;
  }

  public Boolean getCompleted() {
    return this.completed;
  }

  public void setCompleted(final Boolean completed) {
    this.completed = completed;
  }

  public String getReason() {
    return this.reason;
  }

  public void setReason(final String reason) {
    this.reason = reason;
  }

  public DashboardMonthDto getDashboard() {
    return dashboard;
  }

  public void setDashboard(DashboardMonthDto dashboard) {
    this.dashboard = dashboard;
  }

  public List<MilestoneDto> getMilestones() {
    return milestones;
  }

  public void setMilestones(List<MilestoneDto> milestones) {
    this.milestones = milestones;
  }

  public List<RiskDto> getRisks() {
    return risks;
  }

  public void setRisks(List<RiskDto> risks) {
    this.risks = risks;
  }

  public String getActiveBaselineName() {
    return this.activeBaselineName;
  }

  public void setActiveBaselineName(final String activeBaselineName) {
    this.activeBaselineName = activeBaselineName;
  }

}
