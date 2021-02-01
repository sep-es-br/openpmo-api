package br.gov.es.openpmo.model;

import java.util.Objects;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import io.swagger.annotations.ApiModel;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = PortfolioModel.class, name = "PortfolioModel"),
		@JsonSubTypes.Type(value = ProgramModel.class, name = "ProgramModel"),
		@JsonSubTypes.Type(value = OrganizerModel.class, name = "OrganizerModel"),
		@JsonSubTypes.Type(value = DeliverableModel.class, name = "DeliverableModel"),
		@JsonSubTypes.Type(value = ProjectModel.class, name = "ProjectModel"),
		@JsonSubTypes.Type(value = MilestoneModel.class, name = "MilestoneModel") })
@ApiModel(subTypes = { PortfolioModel.class, ProgramModel.class, OrganizerModel.class, DeliverableModel.class,
		ProjectModel.class,
		MilestoneModel.class }, discriminator = "type", description = "Supertype of all WorkpackModel.")
@NodeEntity
public class WorkpackModel extends Entity {

	private String fontIcon;
	private String modelName;
	private String modelNameInPlural;

	private boolean costSessionActive;
	private boolean stakeholderSessionActive;
	private boolean childWorkpackModelSessionActive;
	private boolean scheduleSessionActive;

	private Set<String> personRoles;
	private Set<String> organizationRoles;

	@Relationship(type = "IS_SORTED_BY")
	private PropertyModel sortBy;

	@Relationship(type = "IS_IN")
	private WorkpackModel parent;

	@Relationship(type = "IS_ROOT_OF")
	private PlanModel planModel;

	@Relationship(type = "FEATURES", direction = Relationship.INCOMING)
	private Set<PropertyModel> properties;

	@Relationship(type = "IS_IN", direction = Relationship.INCOMING)
	private Set<WorkpackModel> children;

	@Transient
	private Long idParent;

	@Transient
	private Long idPlanModel;

	public String getFontIcon() {
		return fontIcon;
	}

	public void setFontIcon(String fontIcon) {
		this.fontIcon = fontIcon;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getModelNameInPlural() {
		return modelNameInPlural;
	}

	public void setModelNameInPlural(String modelNameInPlural) {
		this.modelNameInPlural = modelNameInPlural;
	}

	public boolean isCostSessionActive() {
		return costSessionActive;
	}

	public void setCostSessionActive(boolean costSessionActive) {
		this.costSessionActive = costSessionActive;
	}

	public boolean isStakeholderSessionActive() {
		return stakeholderSessionActive;
	}

	public void setStakeholderSessionActive(boolean stakeholderSessionActive) {
		this.stakeholderSessionActive = stakeholderSessionActive;
	}

	public boolean isChildWorkpackModelSessionActive() {
		return childWorkpackModelSessionActive;
	}

	public void setChildWorkpackModelSessionActive(boolean childWorkpackModelSessionActive) {
		this.childWorkpackModelSessionActive = childWorkpackModelSessionActive;
	}

	public boolean isScheduleSessionActive() {
		return this.scheduleSessionActive;
	}

	public void setScheduleSessionActive(boolean scheduleSessionActive) {
		this.scheduleSessionActive = scheduleSessionActive;
	}

	public Set<String> getPersonRoles() {
		return personRoles;
	}

	public void setPersonRoles(Set<String> personRoles) {
		this.personRoles = personRoles;
	}

	public Set<String> getOrganizationRoles() {
		return organizationRoles;
	}

	public void setOrganizationRoles(Set<String> organizationRoles) {
		this.organizationRoles = organizationRoles;
	}

	public Set<PropertyModel> getProperties() {
		return properties;
	}

	public void setProperties(Set<PropertyModel> properties) {
		this.properties = properties;
	}

	public WorkpackModel getParent() {
		return parent;
	}

	public void setParent(WorkpackModel parent) {
		this.parent = parent;
	}

	public PlanModel getPlanModel() {
		return planModel;
	}

	public void setPlanModel(PlanModel planModel) {
		this.planModel = planModel;
	}

	public Set<WorkpackModel> getChildren() {
		return children;
	}

	public void setChildren(Set<WorkpackModel> children) {
		this.children = children;
	}

	public Long getIdParent() {
		return idParent;
	}

	public void setIdParent(Long idParent) {
		this.idParent = idParent;
	}

	public Long getIdPlanModel() {
		return idPlanModel;
	}

	public void setIdPlanModel(Long idPlanModel) {
		this.idPlanModel = idPlanModel;
	}

	public PropertyModel getSortBy() {
		return sortBy;
	}

	public void setSortBy(PropertyModel sortBy) {
		this.sortBy = sortBy;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		WorkpackModel that = (WorkpackModel) o;
		return costSessionActive == that.costSessionActive && stakeholderSessionActive == that.stakeholderSessionActive
				&& childWorkpackModelSessionActive == that.childWorkpackModelSessionActive
				&& scheduleSessionActive == that.scheduleSessionActive && Objects.equals(fontIcon, that.fontIcon)
				&& Objects.equals(modelName, that.modelName)
				&& Objects.equals(modelNameInPlural, that.modelNameInPlural) && Objects.equals(parent, that.parent)
				&& Objects.equals(planModel, that.planModel);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), fontIcon, modelName, modelNameInPlural, costSessionActive,
				stakeholderSessionActive, childWorkpackModelSessionActive, scheduleSessionActive, parent, planModel);
	}
}
