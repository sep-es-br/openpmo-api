package br.gov.es.openpmo.model;

import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import br.gov.es.openpmo.model.relations.CanAccessWorkpack;
import io.swagger.annotations.ApiModel;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = Portfolio.class, name = "Portfolio"),
		@JsonSubTypes.Type(value = Program.class, name = "Program"),
		@JsonSubTypes.Type(value = Organizer.class, name = "Organizer"),
		@JsonSubTypes.Type(value = Deliverable.class, name = "Deliverable"),
		@JsonSubTypes.Type(value = Project.class, name = "Project"),
		@JsonSubTypes.Type(value = Milestone.class, name = "Milestone") })
@ApiModel(subTypes = { Portfolio.class, Program.class, Organizer.class, Deliverable.class, Project.class,
		Milestone.class }, discriminator = "type", description = "Supertype of all Workpack.")
@NodeEntity
public class Workpack extends Entity {

	@Relationship(type = "BELONGS_TO")
	private Plan plan;

	@Relationship(type = "IS_IN")
	private Workpack parent;

	@Relationship(type = "IS_IN", direction  = Relationship.INCOMING)
	private Set<Workpack> children;

	@Relationship(type = "FEATURES", direction  = Relationship.INCOMING)
	private Set<Property> properties;

	@Relationship(type = "APPLIES_TO", direction  = Relationship.INCOMING)
	private Set<CostAccount> costs;

	@Relationship(type = "CAN_ACCESS_WORKPACK", direction  = Relationship.INCOMING)
	private Set<CanAccessWorkpack> canAccess;

	@Transient
	@JsonIgnore
	private Long idParent;

	@Transient
	@JsonIgnore
	private Long idWorkpackModel;

	@Transient
	@JsonIgnore
	private Long idPlan;


	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	public Workpack getParent() {
		return parent;
	}

	public void setParent(Workpack parent) {
		this.parent = parent;
	}

	public Set<Workpack> getChildren() {
		return children;
	}

	public void setChildren(Set<Workpack> children) {
		this.children = children;
	}

	public Set<Property> getProperties() {
		return properties;
	}

	public void setProperties(Set<Property> properties) {
		this.properties = properties;
	}

	public Set<CostAccount> getCosts() {
		return costs;
	}

	public void setCosts(Set<CostAccount> costs) {
		this.costs = costs;
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

	public Long getIdPlan() {
		return idPlan;
	}

	public void setIdPlan(Long idPlan) {
		this.idPlan = idPlan;
	}

	public Set<CanAccessWorkpack> getCanAccess() {
		return canAccess;
	}

	public void setCanAccess(Set<CanAccessWorkpack> canAccess) {
		this.canAccess = canAccess;
	}
}
