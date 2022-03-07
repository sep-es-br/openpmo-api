package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.dto.costaccount.CostAccountDto;
import br.gov.es.openpmo.dto.dashboards.v2.SimpleDashboard;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.plan.PlanDto;
import br.gov.es.openpmo.dto.workpackLink.WorkpackModelLinkedDto;
import br.gov.es.openpmo.dto.workpackmodel.details.WorkpackModelDetailDto;
import br.gov.es.openpmo.dto.workpackshared.WorkpackSharedDto;
import br.gov.es.openpmo.model.relations.IsLinkedTo;
import br.gov.es.openpmo.model.workpacks.Workpack;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.swagger.annotations.ApiModel;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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

    private PlanDto plan;

    private WorkpackModelDetailDto model;

    private Set<WorkpackDetailDto> children;

    private List<? extends PropertyDto> properties;

    private Set<CostAccountDto> costs;

    private List<PermissionDto> permissions;

    private WorkpackModelLinkedDto modelLinked;

    private List<WorkpackSharedDto> sharedWith;

    private Boolean linked;

    private Long linkedModel;

    @JsonProperty("cancelable")
    private boolean isCancelable;

    @JsonProperty("canceled")
    private boolean isCanceled;

    private boolean hasActiveBaseline;

    private boolean pendingBaseline;

    private boolean cancelPropose;

    private String activeBaselineName;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate endManagementDate;

    private String reason;

    private Boolean completed;

    private SimpleDashboard dashboard;

    public boolean isHasActiveBaseline() {
        return this.hasActiveBaseline;
    }

    public void setHasActiveBaseline(final boolean hasActiveBaseline) {
        this.hasActiveBaseline = hasActiveBaseline;
    }

    public List<WorkpackSharedDto> getSharedWith() {
        return this.sharedWith;
    }

    public void setSharedWith(final List<WorkpackSharedDto> sharedWith) {
        this.sharedWith = sharedWith;
    }

    public Boolean getLinked() {
        return this.linked;
    }

    public void setLinked(final Boolean linked) {
        this.linked = linked;
    }

    public void applyLinkedStatus(final Workpack workpack, final Long idWorkpackModel) {
        this.linkedModel = Optional.ofNullable(workpack.getLinkedTo())
                .map(linkeds -> linkeds.stream()
                        .map(IsLinkedTo::getWorkpackModelId)
                        .filter(id -> Objects.equals(id, idWorkpackModel))
                        .findFirst()
                        .orElse(null)
                ).orElse(null);
        this.linked = this.linkedModel != null;
    }

    public Long getLinkedModel() {
        return this.linkedModel;
    }

    public void setLinkedModel(final Long linkedModel) {
        this.linkedModel = linkedModel;
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

    public void setModelLinked(final WorkpackModelLinkedDto modelLinked) {
        this.modelLinked = modelLinked;
    }

    public PlanDto getPlan() {
        return this.plan;
    }

    public void setPlan(final PlanDto plan) {
        this.plan = plan;
    }

    public WorkpackModelDetailDto getModel() {
        return this.model;
    }

    public void setModel(final WorkpackModelDetailDto model) {
        this.model = model;
    }

    public Set<WorkpackDetailDto> getChildren() {
        return this.children;
    }

    public void setChildren(final Set<WorkpackDetailDto> children) {
        this.children = children;
    }

    public List<? extends PropertyDto> getProperties() {
        return this.properties;
    }

    public void setProperties(final List<? extends PropertyDto> properties) {
        this.properties = properties;
    }

    public Set<CostAccountDto> getCosts() {
        return this.costs;
    }

    public void setCosts(final Set<CostAccountDto> costs) {
        this.costs = costs;
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
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public SimpleDashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(SimpleDashboard dashboard) {
        this.dashboard = dashboard;
    }

    public String getActiveBaselineName() {
        return activeBaselineName;
    }

    public void setActiveBaselineName(String activeBaselineName) {
        this.activeBaselineName = activeBaselineName;
    }
}
