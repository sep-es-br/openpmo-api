package br.gov.es.openpmo.model;

import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class LocalitySelectionModel extends PropertyModel {

	private boolean multipleSelection;

	@Relationship(value = "DEFAULTS_TO")
	private Set<Locality> defaultValue;

	@Relationship(value = "IS_ROOT_OF")
	private Domain domain;

	public boolean isMultipleSelection() {
		return multipleSelection;
	}

	public void setMultipleSelection(boolean multipleSelection) {
		this.multipleSelection = multipleSelection;
	}

	public Set<Locality> getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Set<Locality> defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Domain getDomain() {
		return this.domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}
}
