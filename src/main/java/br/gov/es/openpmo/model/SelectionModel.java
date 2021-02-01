package br.gov.es.openpmo.model;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class SelectionModel extends PropertyModel {

	private String defaultValue;
	private String possibleValues;
	private boolean multipleSelection;

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getPossibleValues() {
		return possibleValues;
	}

	public void setPossibleValues(String possibleValues) {
		this.possibleValues = possibleValues;
	}

	public boolean isMultipleSelection() {
		return multipleSelection;
	}

	public void setMultipleSelection(boolean multipleSelection) {
		this.multipleSelection = multipleSelection;
	}

}
