package br.gov.es.openpmo.model;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class ToggleModel extends PropertyModel {

	private boolean defaultValue;

	public boolean isDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

}
