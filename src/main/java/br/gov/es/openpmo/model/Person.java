package br.gov.es.openpmo.model;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Person extends Actor {

	private boolean administrator;

	public boolean isAdministrator() {
		return administrator;
	}

	public void setAdministrator(boolean administrator) {
		this.administrator = administrator;
	}
	
}
