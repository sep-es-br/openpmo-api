package br.gov.es.openpmo.model;


import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import br.gov.es.openpmo.enumerator.OrganizationEnum;

@NodeEntity
public class Organization extends Actor {

	private String website;
	private OrganizationEnum sector;

	@Relationship(type = "IS_REGISTERED_IN")
	private Office office;

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public Office getOffice() {
		return this.office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public OrganizationEnum getSector() {
		return this.sector;
	}

	public void setSector(OrganizationEnum sector) {
		this.sector = sector;
	}

}
