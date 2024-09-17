package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.dto.dashboards.v2.SimpleDashboard;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.plan.PlanDto;
import br.gov.es.openpmo.dto.workpackLink.WorkpackModelLinkedDto;
import br.gov.es.openpmo.dto.workpackmodel.details.WorkpackModelDetailDto;
import br.gov.es.openpmo.model.workpacks.Workpack;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.swagger.annotations.ApiModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({@Type(value = PortfolioDetailDto.class, name = "Portfolio"),
  @Type(value = ProgramDetailDto.class, name = "Program"),
  @Type(value = OrganizerDetailDto.class, name = "Organizer"),
  @Type(value = DeliverableDetailDto.class, name = "Deliverable"),
  @Type(value = ProjectDetailDto.class, name = "Project"),
  @Type(value = MilestoneDetailDto.class, name = "Milestone")})
@ApiModel(subTypes = {PortfolioDetailDto.class, ProgramDetailDto.class, OrganizerDetailDto.class,
  DeliverableDetailDto.class, ProjectDetailDto.class,
  MilestoneDto.class}, discriminator = "type", description = "Supertype of all Workpack.")
public abstract class WorkpackDetailDto {

  private Long id;

  private Long idParent;

  private Long idWorkpackModel;

  private PlanDto plan;

  private PlanDto planOrigin;

  private WorkpackModelDetailDto model;

  private Boolean hasChildren;

  private List<? extends PropertyDto> properties;

  private List<PermissionDto> permissions;

  private WorkpackModelLinkedDto modelLinked;

  private Boolean sharedWith;

  @JsonProperty("canceled")
  private boolean isCanceled;

  private boolean isFavoritedBy;

  private Boolean hasScheduleSectionActive;

  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate endManagementDate;

  private String reason;

  private Boolean completed;

  private SimpleDashboard dashboard;

  private String name;

  private String fullName;

  private LocalDateTime date;

  public static <TYPE extends WorkpackDetailDto> WorkpackDetailDto of(
    final Workpack workpack,
    final Supplier<TYPE> instanceSupplier
  ) {
    final TYPE instance = instanceSupplier.get();
    instance.setId(workpack.getId());
    instance.setCanceled(workpack.isCanceled());
    instance.setEndManagementDate(workpack.getEndManagementDate());
    instance.setReason(workpack.getReason());
    instance.setCompleted(workpack.getCompleted());
    instance.setIdParent(workpack.getIdParent());
    return instance;
  }

  public Boolean getSharedWith() {
    return this.sharedWith;
  }

  public void setSharedWith(final Boolean sharedWith) {
    this.sharedWith = sharedWith;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public WorkpackModelLinkedDto getModelLinked() {
    return this.modelLinked;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public void setModelLinked(final WorkpackModelLinkedDto modelLinked) {
    this.modelLinked = modelLinked;
  }

  public PlanDto getPlan() {
    return this.plan;
  }

  public void setPlan(final PlanDto plan) {
    this.plan = plan;
  }

  public PlanDto getPlanOrigin() {
    return planOrigin;
  }

  public void setPlanOrigin(PlanDto planOrigin) {
    this.planOrigin = planOrigin;
  }

  public WorkpackModelDetailDto getModel() {
    return this.model;
  }

  public void setModel(final WorkpackModelDetailDto model) {
    this.model = model;
  }

  public Boolean getHasChildren() {
    return hasChildren;
  }

  public void setHasChildren(Boolean hasChildren) {
    this.hasChildren = hasChildren;
  }

  public List<? extends PropertyDto> getProperties() {
    return this.properties;
  }

  public void setProperties(final List<? extends PropertyDto> properties) {
    this.properties = properties;
  }

  public List<PermissionDto> getPermissions() {
    return this.permissions;
  }

  public void setPermissions(final List<PermissionDto> permissions) {
    this.permissions = permissions;
  }

  public boolean hasStakeholderSessionActive() {
    return this.model.getStakeholderSessionActive();
  }

  public boolean samePlanId(final Long planId) {
    return this.plan.getId().equals(planId);
  }

  @JsonIgnore
  public Long getIdPlan() {
    return this.plan.getId();
  }

  public boolean isCanceled() {
    return this.isCanceled;
  }

  public void setCanceled(final boolean canceled) {
    this.isCanceled = canceled;
  }

  public boolean isFavoritedBy() {
    return this.isFavoritedBy;
  }

  public void setFavoritedBy(final boolean favoritedBy) {
    this.isFavoritedBy = favoritedBy;
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

  public SimpleDashboard getDashboard() {
    return this.dashboard;
  }

  public void setDashboard(final SimpleDashboard dashboard) {
    this.dashboard = dashboard;
  }

  public Boolean getHasScheduleSectionActive() {
    return this.hasScheduleSectionActive;
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

  public void setHasScheduleSectionActive(final Boolean hasScheduleSectionActive) {
    this.hasScheduleSectionActive = hasScheduleSectionActive;
  }

  public Long getIdParent() {
    return idParent;
  }

  public void setIdParent(Long idParent) {
    this.idParent = idParent;
  }

  public Long getIdWorkpackModel() {
    return idWorkpackModel;
  }

  public void setIdWorkpackModel(Long idWorkpackModel) {
    this.idWorkpackModel = idWorkpackModel;
  }
}
