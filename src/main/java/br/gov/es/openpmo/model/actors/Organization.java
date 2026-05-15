package br.gov.es.openpmo.model.actors;


import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.relations.OrganizationOfficeRelationship;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Organization extends Actor {

  private OrganizationEnum sector;

  @Relationship(type = "IS_REGISTERED_IN")
  private OrganizationOfficeRelationship organizationOffice;

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

}
