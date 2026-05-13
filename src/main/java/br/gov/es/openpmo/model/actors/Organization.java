package br.gov.es.openpmo.model.actors;


import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.relations.OrganizationOfficeRelationship;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Organization extends Actor {

  private String website;
  private OrganizationEnum sector;
  private String address;

  @Relationship(type = "IS_REGISTERED_IN")
  private OrganizationOfficeRelationship organizationOffice;

  public String getWebsite() {
    return this.website;
  }

  public void setWebsite(final String website) {
    this.website = website;
  }

  public OrganizationOfficeRelationship getOrganizationOffice() {
    return this.organizationOffice;
  }

  public void setOrganizationOffice(final OrganizationOfficeRelationship organizationOffice) {
    this.organizationOffice = organizationOffice;
  }

  public OrganizationEnum getSector() {
    return this.sector;
  }

  public void setSector(final OrganizationEnum sector) {
    this.sector = sector;
  }

  public String getAddress() {
    return this.address;
  }

  public void setAddress(final String address) {
    this.address = address;
  }

}
