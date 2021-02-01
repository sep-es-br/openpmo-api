package br.gov.es.openpmo.model;

import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class OrganizationSelectionModel extends PropertyModel {
	
	private boolean multipleSelection;

	@Relationship(value = "DEFAULTS_TO")
	private Set<Organization> defaultValue;

	public boolean isMultipleSelection() {
		return multipleSelection;
	}

	public void setMultipleSelection(boolean multipleSelection) {
		this.multipleSelection = multipleSelection;
	}

	public Set<Organization> getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Set<Organization> defaultValue) {
		this.defaultValue = defaultValue;
	}
}
