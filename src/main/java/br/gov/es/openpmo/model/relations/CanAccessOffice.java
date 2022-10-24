package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.scheduler.updateroles.HasRole;

import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.TargetNode;
import org.springframework.data.neo4j.core.schema.Property;

@RelationshipProperties //(type = "CAN_ACCESS_OFFICE")
public class CanAccessOffice implements HasRole {

  @RelationshipId
  private Long id;
  private String organization;
  
  @Property("permitedRole")
  private String role;
  private PermissionLevelEnum permissionLevel;

  @TargetNode
  private Office office;

  public CanAccessOffice(
    final Long id,
    final String organization,
    final String role,
    final PermissionLevelEnum permissionLevel,
    final Person person,
    final Office office
  ) {
    this.id = id;
    this.organization = organization;
    this.role = role;
    this.permissionLevel = permissionLevel;
    this.office = office;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getOrganization() {
    return this.organization;
  }

  public void setOrganization(final String organization) {
    this.organization = organization;
  }

  public String getRole() {
    return this.role;
  }

  public void setRole(final String role) {
    this.role = role;
  }

  public PermissionLevelEnum getPermissionLevel() {
    return this.permissionLevel;
  }

  public void setPermissionLevel(final PermissionLevelEnum permissionLevel) {
    this.permissionLevel = permissionLevel;
  }

  public Office getOffice() {
    return this.office;
  }

  public void setOffice(final Office office) {
    this.office = office;
  }

  public String getOfficeName() {
    return this.office.getName();
  }

}
