package br.gov.es.openpmo.model;

import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@NodeEntity
public class Office extends Entity {

	private String name;
	private String fullName;

	@JsonIgnoreProperties("office")
	@Relationship(type = "IS_ADOPTED_BY", direction = Relationship.INCOMING)
	private Set<Plan> plans;

	@JsonIgnoreProperties("office")
	@Relationship(type = "IS_ADOPTED_BY", direction = Relationship.INCOMING)
	private Set<PlanModel> plansModel;

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

	public Set<Plan> getPlans() {
		return plans;
	}

	public void setPlans(Set<Plan> plans) {
		this.plans = plans;
	}

	public Set<PlanModel> getPlansModel() {
		return plansModel;
	}

	public void setPlansModel(Set<PlanModel> plansModel) {
		this.plansModel = plansModel;
	}
}
