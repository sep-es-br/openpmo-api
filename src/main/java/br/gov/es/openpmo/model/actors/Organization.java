package br.gov.es.openpmo.model.actors;

import br.gov.es.openpmo.model.relations.OrganizationOfficeRelationship;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@NodeEntity
public class Organization extends Actor {

  private OrganizationEnum sector;

  private String integration;

  private String suffix;

  private String guid;

  @Relationship(type = "IS_REGISTERED_IN")
  private List<OrganizationOfficeRelationship> organizationOffices = new ArrayList<>();

  @Relationship(type = "IS")
  private Set<WorkPlace> workPlaces = new HashSet<>();

  public List<OrganizationOfficeRelationship> getOrganizationOffices() {
    return organizationOffices;
  }

  public void setOrganizationOffices(List<OrganizationOfficeRelationship> organizationOffices) {
    this.organizationOffices = organizationOffices;
  }

  public Set<WorkPlace> getWorkPlaces() {
    return this.workPlaces;
  }

  public void setWorkPlaces(final Set<WorkPlace> workPlaces) {
    this.workPlaces = workPlaces;
  }

  public OrganizationEnum getSector() {
    return this.sector;
  }

  public void setSector(final OrganizationEnum sector) {
    this.sector = sector;
  }

  public String getIntegration() {
    return this.integration;
  }

  public void setIntegration(final String integration) {
    this.integration = integration;
  }

  public String getSuffix() {
    return this.suffix;
  }

  public void setSuffix(final String suffix) {
    this.suffix = suffix;
  }

  public String getGuid() {
    return this.guid;
  }

  public void setGuid(final String guid){ this.guid = guid; }

}
