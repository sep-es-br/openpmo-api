package br.gov.es.openpmo.model.actors;

import br.gov.es.openpmo.model.office.Office;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * Structural node that connects a person's office contact to an organization.
 * It intentionally has no business properties.
 */
@NodeEntity
public class WorkPlace {

  @Id
  @GeneratedValue
  private Long id;

  @Relationship(type = "FOR")
  private Office office;

  @Relationship(type = "IS")
  private Organization organization;

  public Long getId() {
    return this.id;
  }

  public Office getOffice() {
    return this.office;
  }

  public void setOffice(final Office office) {
    this.office = office;
  }

  public Organization getOrganization() {
    return this.organization;
  }

  public void setOrganization(final Organization organization) {
    this.organization = organization;
  }
}
