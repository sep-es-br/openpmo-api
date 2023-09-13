package br.gov.es.openpmo.model.workpacks.models;

import br.gov.es.openpmo.dto.workpackmodel.params.DashboardConfiguration;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.relations.IsLinkedTo;
import br.gov.es.openpmo.model.workpacks.Workpack;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.swagger.annotations.ApiModel;
import org.apache.commons.collections.CollectionUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({
  @Type(value = PortfolioModel.class, name = "PortfolioModel"),
  @Type(value = ProgramModel.class, name = "ProgramModel"),
  @Type(value = OrganizerModel.class, name = "OrganizerModel"),
  @Type(value = DeliverableModel.class, name = "DeliverableModel"),
  @Type(value = ProjectModel.class, name = "ProjectModel"),
  @Type(value = MilestoneModel.class, name = "MilestoneModel")
})
@ApiModel(subTypes = {PortfolioModel.class, ProgramModel.class, OrganizerModel.class, DeliverableModel.class,
  ProjectModel.class, MilestoneModel.class}, discriminator = "type", description = "Supertype of all WorkpackModel."
)
@NodeEntity
public abstract class WorkpackModel extends Entity {

  private String fontIcon;

  private String modelName;

  private String modelNameInPlural;

  private Boolean costSessionActive;

  private Boolean stakeholderSessionActive;

  private Boolean childWorkpackModelSessionActive;

  private Boolean scheduleSessionActive;

  private Boolean riskAndIssueManagementSessionActive;

  private Boolean processesManagementSessionActive;

  private Boolean dashboardShowRisks;

  private Boolean dashboardShowEva;

  private Boolean dashboardShowMilestones;

  private Set<String> dashboardShowStakeholders;

  private Set<String> personRoles;

  private Set<String> organizationRoles;

  private Boolean journalManagementSessionActive;

  private Boolean dashboardSessionActive;

  private Long position;

  @Relationship(type = "IS_SORTED_BY")
  private PropertyModel sortBy;

  @Relationship(type = "IS_IN")
  private Set<WorkpackModel> parent;

  @Relationship(type = "BELONGS_TO")
  private PlanModel planModel;

  @Relationship(type = "FEATURES", direction = Relationship.INCOMING)
  private Set<PropertyModel> properties;

  @Relationship(type = "IS_IN", direction = Relationship.INCOMING)
  private Set<WorkpackModel> children;

  @Relationship(type = "IS_LINKED_TO", direction = Relationship.INCOMING)
  private Set<IsLinkedTo> linkedToRelationship;

  @Transient
  private Long idParent;

  @Transient
  private Long idPlanModel;

  public String getFontIcon() {
    return this.fontIcon;
  }

  public void setFontIcon(final String fontIcon) {
    this.fontIcon = fontIcon;
  }

  public String getModelNameInPlural() {
    return this.modelNameInPlural;
  }

  public void setModelNameInPlural(final String modelNameInPlural) {
    this.modelNameInPlural = modelNameInPlural;
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

  public Boolean getScheduleSessionActive() {
    return this.scheduleSessionActive;
  }

  public void setScheduleSessionActive(final boolean scheduleSessionActive) {
    this.scheduleSessionActive = scheduleSessionActive;
  }

  public void setScheduleSessionActive(final Boolean scheduleSessionActive) {
    this.scheduleSessionActive = scheduleSessionActive;
  }

  public Set<String> getPersonRoles() {
    return this.personRoles;
  }

  public void setPersonRoles(final Set<String> personRoles) {
    this.personRoles = personRoles;
  }

  public Set<String> getOrganizationRoles() {
    return this.organizationRoles;
  }

  public void setOrganizationRoles(final Set<String> organizationRoles) {
    this.organizationRoles = organizationRoles;
  }

  public Set<PropertyModel> getProperties() {
    if (properties == null) {
      properties = new HashSet<>();
    }
    return this.properties;
  }

  public void setProperties(final Set<PropertyModel> properties) {
    this.properties = properties;
  }

  public PlanModel getPlanModel() {
    return this.planModel;
  }

  public void setPlanModel(final PlanModel planModel) {
    this.planModel = planModel;
  }

  public Set<WorkpackModel> getChildren() {
    return this.children;
  }

  public void setChildren(final Set<WorkpackModel> children) {
    this.children = children;
  }

  public Long getIdPlanModel() {
    return this.idPlanModel;
  }

  public void setIdPlanModel(final Long idPlanModel) {
    this.idPlanModel = idPlanModel;
  }

  public PropertyModel getSortBy() {
    return this.sortBy;
  }

  public void setSortBy(final PropertyModel sortBy) {
    this.sortBy = sortBy;
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

  @Transient
  public boolean hasParent() {
    return this.parent != null && !this.parent.isEmpty();
  }

  public Set<WorkpackModel> getParent() {
    return this.parent;
  }

  public void setParent(final Set<WorkpackModel> parent) {
    this.parent = parent;
  }

  @Transient
  public boolean isTypeOf(final String type) {
    final String typeName = this.getClass().getTypeName();
    return typeName.equals(type);
  }

  @Transient
  public void addParent(final Collection<? extends WorkpackModel> parent) {
    for (final WorkpackModel model : parent) {
      this.addParent(model);
    }
  }

  public boolean containsChild(final WorkpackModel child) {
    if (this.children == null) return false;
    return this.children.contains(child);
  }

  public boolean containsParent(final WorkpackModel parent) {
    if (this.parent == null) return false;
    return this.parent.contains(parent);
  }

  @Transient
  public void addParent(final WorkpackModel parent) {
    if (this.parent == null) this.parent = new HashSet<>();
    this.parent.add(parent);
    if (!parent.containsChild(this)) {
      parent.addChildren(this);
    }
  }

  @Transient
  public void addChildren(final WorkpackModel child) {
    if (this.children == null) this.children = new HashSet<>();
    this.children.add(child);
    if (!child.containsParent(this)) {
      child.addParent(this);
    }
  }

  public void addChildren(final WorkpackModel... children) {
    Arrays.asList(children).forEach(this::addChildren);
  }

  public boolean hasMoreThanOneParent() {
    return this.parent != null && this.parent.size() > 1;
  }

  @Transient
  public boolean hasSameType(final WorkpackModel model) {
    if (model == null) return false;
    return this.getClass().getTypeName().equals(model.getClass().getTypeName());
  }

  @Transient
  public boolean hasSameName(final WorkpackModel model) {
    if (model == null) return false;
    return this.modelName.equals(model.modelName);
  }

  public String getModelName() {
    return this.modelName;
  }

  public void setModelName(final String modelName) {
    this.modelName = modelName;
  }

  public String getModelNameWithOffice() {
    final Office office = this.planModel.getOffice();
    return this.modelName + " (" + office.getName() + ")";
  }

  public boolean hasChildren() {
    return this.children != null && !this.children.isEmpty();
  }

  public Boolean getDashboardShowRisks() {
    return this.dashboardShowRisks;
  }

  public void setDashboardShowRisks(final Boolean dashboardShowRisks) {
    this.dashboardShowRisks = dashboardShowRisks;
  }

  public Boolean getDashboardShowEva() {
    return this.dashboardShowEva;
  }

  public void setDashboardShowEva(final Boolean dashboardShowEva) {
    this.dashboardShowEva = dashboardShowEva;
  }

  public Boolean getDashboardShowMilestones() {
    return this.dashboardShowMilestones;
  }

  public void setDashboardShowMilestones(final Boolean dashboardShowMilestones) {
    this.dashboardShowMilestones = dashboardShowMilestones;
  }

  public Set<String> getDashboardShowStakeholders() {
    return this.dashboardShowStakeholders;
  }

  public void setDashboardShowStakeholders(final Set<String> dashboardShowStakeholders) {
    this.dashboardShowStakeholders = dashboardShowStakeholders;
  }

  public void dashboardConfiguration(final DashboardConfiguration dashboardConfiguration) {
    this.dashboardShowEva = dashboardConfiguration.getDashboardShowEva();
    this.dashboardShowMilestones = dashboardConfiguration.getDashboardShowMilestones();
    this.dashboardShowRisks = dashboardConfiguration.getDashboardShowRisks();
    this.dashboardShowStakeholders = dashboardConfiguration.getDashboardShowStakeholders();
  }

  public Boolean getJournalManagementSessionActive() {
    return this.journalManagementSessionActive;
  }

  public void setJournalManagementSessionActive(final Boolean journalManagementSessionActive) {
    this.journalManagementSessionActive = journalManagementSessionActive;
  }

  public void updateFields(final WorkpackModel workpackModel) {
    this.modelName = workpackModel.modelName;
    this.personRoles = workpackModel.personRoles;
    this.fontIcon = workpackModel.fontIcon;
    this.modelNameInPlural = workpackModel.modelNameInPlural;
    this.organizationRoles = workpackModel.organizationRoles;
    this.sortBy = workpackModel.sortBy;
    this.costSessionActive = workpackModel.costSessionActive;
    this.childWorkpackModelSessionActive = workpackModel.childWorkpackModelSessionActive;
    this.stakeholderSessionActive = workpackModel.stakeholderSessionActive;
    this.scheduleSessionActive = workpackModel.scheduleSessionActive;
    this.riskAndIssueManagementSessionActive = workpackModel.riskAndIssueManagementSessionActive;
    this.processesManagementSessionActive = workpackModel.processesManagementSessionActive;
    this.journalManagementSessionActive = workpackModel.journalManagementSessionActive;
    this.dashboardShowEva = workpackModel.dashboardShowEva;
    this.dashboardShowRisks = workpackModel.dashboardShowRisks;
    this.dashboardShowMilestones = workpackModel.dashboardShowMilestones;
    this.dashboardShowStakeholders = workpackModel.dashboardShowStakeholders;
    this.dashboardSessionActive = workpackModel.dashboardSessionActive;
    this.position = workpackModel.position;
  }

  public Boolean getDashboardSessionActive() {
    return this.dashboardSessionActive;
  }

  public void setDashboardSessionActive(final Boolean dashboardSessionActive) {
    this.dashboardSessionActive = dashboardSessionActive;
  }

  public Long getPosition() {
    return this.position;
  }

  public void setPosition(final Long position) {
    this.position = position;
  }

  @Transient
  public boolean hasProperties() {
    return !ObjectUtils.isEmpty(this.properties);
  }

  public abstract Set<? extends Workpack> getInstances();

  public String getType() {
    throw new UnsupportedOperationException();
  }

  public boolean sortByWasChanged(final PropertyModel sortBy) {
    Objects.requireNonNull(sortBy);
    if (Objects.isNull(this.sortBy)) return true;
    return !this.sortBy.getId().equals(sortBy.getId());
  }

  @Transient
  public boolean isOrganizationRole(final String role) {
    return this.organizationRoles.contains(role);
  }

  @Transient
  public Long getPositionOrElseZero() {
    return Optional.ofNullable(this.position)
      .orElse(0L);
  }

  public Long getIdParent() {
    return this.idParent;
  }

  public Set<IsLinkedTo> getLinkedToRelationship() {
    return this.linkedToRelationship;
  }

  public void setLinkedToRelationship(final Set<IsLinkedTo> linkedToRelationship) {
    this.linkedToRelationship = linkedToRelationship;
  }

  public void setIdParent(final Long idParent) {
    this.idParent = idParent;
  }

  @Transient
  public boolean hasLinkedWorkpack() {
    return CollectionUtils.isNotEmpty(this.linkedToRelationship);
  }

  @Transient
  public boolean isCompatibleWith(WorkpackModel other) {
    return Objects.equals(this.getModelName(), other.getModelName())
      && Objects.equals(this.getType(), other.getType());
  }

}
