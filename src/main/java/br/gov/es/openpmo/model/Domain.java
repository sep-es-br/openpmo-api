package br.gov.es.openpmo.model;

import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Domain extends Entity {

	private String name;
	private String fullName;

	@Relationship(type = "APPLIES_TO")
	private Office office;

	@Relationship(type = "BELONGS_TO", direction = Relationship.INCOMING)
	private Set<Locality> localities;

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

	public Set<Locality> getLocalities() {
		return localities;
	}

	public void setLocalities(Set<Locality> localities) {
		this.localities = localities;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
}
