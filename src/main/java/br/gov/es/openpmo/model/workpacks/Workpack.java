package br.gov.es.openpmo.model.workpacks;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.baselines.Snapshotable;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.relations.BelongsTo;
import br.gov.es.openpmo.model.relations.CanAccessWorkpack;
import br.gov.es.openpmo.model.relations.IsBaselinedBy;
import br.gov.es.openpmo.model.relations.IsLinkedTo;
import br.gov.es.openpmo.model.relations.IsSharedWith;
import br.gov.es.openpmo.model.relations.IsSnapshotOf;
import br.gov.es.openpmo.model.relations.IsWorkpackSnapshotOf;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.model.workpacks.models.ProjectModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.utils.ApplicationMessage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.swagger.annotations.ApiModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static br.gov.es.openpmo.utils.WorkpackInstanceType.TYPE_MODEL_NAME_DELIVERABLE;
import static br.gov.es.openpmo.utils.WorkpackInstanceType.TYPE_MODEL_NAME_MILESTONE;
import static br.gov.es.openpmo.utils.WorkpackInstanceType.TYPE_MODEL_NAME_ORGANIZER;
import static br.gov.es.openpmo.utils.WorkpackInstanceType.TYPE_MODEL_NAME_PORTFOLIO;
import static br.gov.es.openpmo.utils.WorkpackInstanceType.TYPE_MODEL_NAME_PROGRAM;
import static br.gov.es.openpmo.utils.WorkpackInstanceType.TYPE_MODEL_NAME_PROJECT;
import static org.neo4j.ogm.annotation.Relationship.INCOMING;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({
  @Type(value = Portfolio.class, name = "Portfolio"),
  @Type(value = Program.class, name = "Program"),
  @Type(value = Organizer.class, name = "Organizer"),
  @Type(value = Deliverable.class, name = "Deliverable"),
  @Type(value = Project.class, name = "Project"),
  @Type(value = Milestone.class, name = "Milestone")
})
@ApiModel(subTypes = {
  Portfolio.class,
  Program.class,
  Organizer.class,
  Deliverable.class,
  Project.class,
  Milestone.class
}, discriminator = "type", description = "Supertype of all Workpack.")
@NodeEntity
public class Workpack extends Entity implements Snapshotable<Workpack> {

  @org.neo4j.ogm.annotation.Property("public")
  private Boolean publicShared;

  private PermissionLevelEnum publicLevel;

  @Relationship(type = "COMPOSES")
  private Baseline baseline;

  @Relationship(type = "BELONGS_TO")
  private Set<BelongsTo> belongsTo;

  @Relationship(type = "IS_IN")
  private Set<Workpack> parent;

  @Relationship(type = "IS_IN", direction = INCOMING)
  private Set<Workpack> children;

  @Relationship(type = "FEATURES", direction = INCOMING)
  private Set<Property> properties;

  @Relationship(type = "APPLIES_TO", direction = INCOMING)
  private Set<CostAccount> costs;

  @Relationship(type = "CAN_ACCESS_WORKPACK", direction = INCOMING)
  private Set<CanAccessWorkpack> canAccess;

  @Relationship(type = "IS_SHARED_WITH")
  private Set<IsSharedWith> sharedWith;

  @Relationship(type = "IS_LINKED_TO")
  private Set<IsLinkedTo> linkedTo;

  @Relationship(type = "IS_BASELINED_BY")
  private Set<IsBaselinedBy> baselinedBy;

  @Relationship(type = "IS_SNAPSHOT_OF")
  private IsWorkpackSnapshotOf master;

  @Relationship(type = "IS_SNAPSHOT_OF", direction = INCOMING)
  private Set<IsWorkpackSnapshotOf> snapshots;

  @Relationship(type = "FEATURES", direction = INCOMING)
  private Schedule schedule;

  @org.neo4j.ogm.annotation.Property("cancelable")
  private boolean isCancelable;

  @org.neo4j.ogm.annotation.Property("canceled")
  private boolean isCanceled;

  private boolean deleted;

  private Boolean completed;

  private LocalDate endManagementDate;

  private String reason;

  private CategoryEnum category;

  @Transient
  @JsonIgnore
  private Long idParent;

  @Transient
  @JsonIgnore
  private Long idWorkpackModel;

  public Workpack() {
  }

  @Transient
  public boolean hasSameModelType(final WorkpackModel workpackModel) {
    final WorkpackModel instance = this.getWorkpackModelInstance();
    if(Objects.isNull(instance) || Objects.isNull(workpackModel)) {
      return false;
    }
    return instance.isTypeOf(this.getClassName(workpackModel));
  }

  @Transient
  public WorkpackModel getWorkpackModelInstance() {
    switch(this.getClass().getTypeName()) {
      case TYPE_MODEL_NAME_PORTFOLIO:
        return ((Portfolio) this).getInstance();
      case TYPE_MODEL_NAME_PROGRAM:
        return ((Program) this).getInstance();
      case TYPE_MODEL_NAME_ORGANIZER:
        return ((Organizer) this).getInstance();
      case TYPE_MODEL_NAME_DELIVERABLE:
        return ((Deliverable) this).getInstance();
      case TYPE_MODEL_NAME_PROJECT:
        return this.getProjectModel();
      case TYPE_MODEL_NAME_MILESTONE:
        return ((Milestone) this).getInstance();
      default:
        return null;
    }
  }

  @Transient
  private String getClassName(final WorkpackModel workpackModel) {
    return workpackModel.getClass().getName();
  }

  @Transient
  private ProjectModel getProjectModel() {
    return ((Project) this).getInstance();
  }

  @Transient
  public String getName() {
    final WorkpackModel workpackModel = this.getWorkpackModelInstance();
    return Optional.ofNullable(workpackModel)
      .map(WorkpackModel::getModelName)
      .orElse(null);
  }

  public boolean isDeleted() {
    return this.deleted;
  }

  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
  }

  public Set<IsLinkedTo> getLinkedTo() {
    return this.linkedTo;
  }

  public void setLinkedTo(final Set<IsLinkedTo> linkedTo) {
    this.linkedTo = linkedTo;
  }

  public Boolean getPublicShared() {
    return this.publicShared;
  }

  public void setPublicShared(final Boolean publicShared) {
    this.publicShared = publicShared;
  }

  public Set<IsSharedWith> getSharedWith() {
    return this.sharedWith;
  }

  public void setSharedWith(final Set<IsSharedWith> sharedWith) {
    this.sharedWith = sharedWith;
  }

  public Set<Workpack> getParent() {
    return this.parent;
  }

  public void setParent(final Set<Workpack> parent) {
    this.parent = parent;
  }

  public Set<Workpack> getChildren() {
    return this.children;
  }

  public void setChildren(final Set<Workpack> children) {
    this.children = children;
  }

  public Set<CostAccount> getCosts() {
    return this.costs;
  }

  public void setCosts(final Set<CostAccount> costs) {
    this.costs = costs;
  }

  public Long getIdParent() {
    return this.idParent;
  }

  public void setIdParent(final Long idParent) {
    this.idParent = idParent;
  }

  public Long getIdWorkpackModel() {
    return this.idWorkpackModel;
  }

  public void setIdWorkpackModel(final Long idWorkpackModel) {
    this.idWorkpackModel = idWorkpackModel;
  }

  public Set<CanAccessWorkpack> getCanAccess() {
    return this.canAccess;
  }

  public void setCanAccess(final Set<CanAccessWorkpack> canAccess) {
    this.canAccess = canAccess;
  }

  public PermissionLevelEnum getPublicLevel() {
    return this.publicLevel;
  }

  public void setPublicLevel(final PermissionLevelEnum publicLevel) {
    this.publicLevel = publicLevel;
  }

  public Schedule getSchedule() {
    return this.schedule;
  }

  public void setSchedule(final Schedule schedule) {
    this.schedule = schedule;
  }

  @Transient
  public void addParent(final Collection<? extends Workpack> parent) {
    parent.forEach(this::addParent);
  }

  @Transient
  public boolean containsChild(final Workpack child) {
    if(this.children == null) return false;
    return this.children.contains(child);
  }

  @Transient
  public boolean containsParent(final Workpack parent) {
    if(this.parent == null) return false;
    return this.parent.contains(parent);
  }

  @Transient
  public void addParent(final Workpack parent) {
    if(parent == null) return;
    if(this.parent == null) this.parent = new HashSet<>();
    this.parent.add(parent);
    if(!parent.containsChild(this)) {
      parent.addChildren(this);
    }
  }

  @Transient
  public void addChildren(final Workpack child) {
    if(this.children == null) this.children = new HashSet<>();
    this.children.add(child);
    if(!child.containsParent(this)) {
      child.addParent(this);
    }
  }

  @Transient
  public void addChildren(final WorkpackModel... children) {
    Arrays.asList(children).forEach(this::addChildren);
  }

  @Transient
  public boolean hasSharedWith() {
    return this.sharedWith != null && !this.sharedWith.isEmpty();
  }

  public Set<BelongsTo> getBelongsTo() {
    return this.belongsTo;
  }

  public void setBelongsTo(final Set<BelongsTo> belongsTo) {
    this.belongsTo = belongsTo;
  }

  @Transient
  public boolean isRestaurable() {
    return !this.isProject() && this.isCanceled;
  }

  @Transient
  public boolean isProject() {
    return TYPE_MODEL_NAME_PROJECT.equals(this.getClass().getTypeName());
  }

  @Transient
  public boolean isCanceled() {
    return this.isCanceled;
  }

  @Transient
  public void setCanceled(final boolean canceled) {
    this.isCanceled = canceled;
    this.isCancelable = this.canBeCanceled();
  }

  @Transient
  public boolean isMilestone() {
    return TYPE_MODEL_NAME_MILESTONE.equals(this.getClass().getTypeName());
  }

  @Transient
  public boolean isDeliverable() {
    return TYPE_MODEL_NAME_DELIVERABLE.equals(this.getClass().getTypeName());
  }

  @Transient
  public boolean isSnapshot() {
    return this.category == CategoryEnum.SNAPSHOT;
  }

  @Transient
  public boolean isCancelable() {
    this.isCancelable = this.canBeCanceled();
    return this.isCancelable;
  }

  @Transient
  public void setCancelable(final boolean cancelable) {
    this.isCancelable = cancelable;

    if(this.children != null) {
      this.children.forEach(child -> child.setCancelable(cancelable));
    }
  }

  @Transient
  private boolean canBeCanceled() {
    return !this.isProject() && !this.isCanceled;
  }

  public Set<IsBaselinedBy> getBaselinedBy() {
    return this.baselinedBy;
  }

  public void setBaselinedBy(final Set<IsBaselinedBy> baselinedBy) {
    this.baselinedBy = baselinedBy;
  }

  public Set<Property> getProperties() {
    return this.properties;
  }

  public void setProperties(final Set<Property> properties) {
    this.properties = properties;
  }

  @Override
  public Workpack snapshot() {
    return new Workpack();
  }

  @Override
  public Baseline getBaseline() {
    return this.baseline;
  }

  @Override
  public void setBaseline(final Baseline baseline) {
    this.baseline = baseline;
  }

  @Override
  public CategoryEnum getCategory() {
    return this.category;
  }

  @Override
  public void setCategory(final CategoryEnum category) {
    this.category = category;
  }

  @Override
  public boolean hasChanges(final Workpack other) {
    return false;
  }

  public Workpack ifIsNotProjectThrowsException() {
    if(this.isProject()) {
      return this;
    }
    throw new NegocioException(ApplicationMessage.WORKPACK_IS_NOT_PROJECT_INVALID_STATE_ERROR);
  }

  public boolean hasParent() {
    return this.parent != null && !this.parent.isEmpty();
  }

  public IsWorkpackSnapshotOf getMaster() {
    return this.master;
  }

  public void setMaster(final IsWorkpackSnapshotOf master) {
    this.master = master;
  }

  public Set<IsWorkpackSnapshotOf> getSnapshots() {
    return this.snapshots;
  }

  public void setSnapshots(final Set<IsWorkpackSnapshotOf> snapshots) {
    this.snapshots = snapshots;
  }

  @Transient
  public WorkpackModel getMasterModelInstance() {
    return Optional.ofNullable(this.master)
      .map(IsSnapshotOf::getMaster)
      .map(Workpack::getWorkpackModelInstance)
      .orElse(null);
  }

  @Transient
  public Long getWorkpackMasterId() {
    return Optional.ofNullable(this.getWorkpackMaster())
      .map(Workpack::getId)
      .orElse(null);
  }

  @Transient
  public Workpack getWorkpackMaster() {
    return Optional.ofNullable(this.master)
      .map(IsSnapshotOf::getMaster)
      .orElse(null);
  }

  public String getIcon() {
    return Optional.ofNullable(this.getWorkpackModelInstance())
      .map(WorkpackModel::getFontIcon)
      .orElse(null);
  }

  public Boolean getCompleted() {
    return this.completed;
  }

  public void setCompleted(final Boolean completed) {
    this.completed = completed;
  }

  public LocalDate getEndManagementDate() {
    return this.endManagementDate;
  }

  public void setEndManagementDate(final LocalDate endManagementDate) {
    this.endManagementDate = endManagementDate;
  }

  public String getReason() {
    return this.reason;
  }

  public void setReason(final String reason) {
    this.reason = reason;
  }

  @Transient
  public Optional<Plan> getOriginalPlan() {
    return Optional.ofNullable(this.belongsTo)
      .flatMap(relation -> relation.stream()
        .filter(a -> !a.getLinked())
        .findFirst()
      )
      .map(BelongsTo::getPlan);
  }

  @Transient
  public Optional<Office> getOriginalOffice() {
    return this.getOriginalPlan()
      .map(Plan::getOffice);
  }

  @Transient
  public boolean hasPropertyModel() {
    return Optional.ofNullable(this.getWorkpackModelInstance())
      .map(WorkpackModel::hasProperties)
      .orElse(false);
  }

  @Transient
  public boolean hasStakeholderSessionActive() {
    return this.getWorkpackModelInstance().getStakeholderSessionActive();
  }

  @Transient
  public boolean sameOriginalPlan(final Long planId) {
    return this.getOriginalPlan().map(plan -> planId.equals(plan.getId())).orElse(false);
  }

  @Transient
  public boolean hasEndManagementDate() {
    return !ObjectUtils.isEmpty(this.endManagementDate);
  }

  @Transient
  public boolean hasInstance() {
    return this.getWorkpackModelInstance() != null;
  }

}
