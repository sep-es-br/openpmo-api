package br.gov.es.openpmo.model;

import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import br.gov.es.openpmo.enumerator.LocalityTypesEnum;

@NodeEntity
public class Locality extends Entity {

	private String name;
	private String fullName;
	private String latitude;
	private String longitude;

	private LocalityTypesEnum type;

	@Relationship(type = "IS_IN")
	private Locality parent;

	@Relationship(type = "BELONGS_TO")
	private Domain domain;

	@Relationship(type = "IS_IN", direction = Relationship.INCOMING)
	private Set<Locality> children;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public LocalityTypesEnum getType() {
		return type;
	}

	public void setType(LocalityTypesEnum type) {
		this.type = type;
	}

	public Locality getParent() {
		return parent;
	}

	public void setParent(Locality parent) {
		this.parent = parent;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public Set<Locality> getChildren() {
		return children;
	}

	public void setChildren(Set<Locality> children) {
		this.children = children;
	}
}
