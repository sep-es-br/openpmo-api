package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.scheduler.updateroles.HasRole;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.TargetNode;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.annotation.Transient;

@RelationshipProperties
public class CanAccessWorkpack implements HasRole {

  @RelationshipId
  private Long id;
  private String organization;
  private Long idPlan;
  @Property("permitedRole")
  private String role;
  private PermissionLevelEnum permissionLevel;

  private Person person;

  @TargetNode
  private Workpack workpack;

  public CanAccessWorkpack() {
  }

  public CanAccessWorkpack(
    final Long id,
    final String organization,
    final String role,
    final PermissionLevelEnum permissionLevel,
    final Person person,
    final Workpack workpack,
    final Long idPlan
  ) {
    this.id = id;
    this.organization = organization;
    this.role = role;
    this.permissionLevel = permissionLevel;
    this.person = person;
    this.idPlan = idPlan;
    this.workpack = workpack;
  }

  @Transient
  public Long getIdWorkpack() {
    if(this.workpack == null) {
      return null;
    }
    return this.workpack.getId();
  }

  @Transient
  public String getWorkpackName() {
    if(this.workpack == null) {
      return null;
    }
    return this.workpack.getWorkpackModelInstance().getModelName();
  }

  public Long getIdPlan() {
    return this.idPlan;
  }

  public void setIdPlan(final Long idPlan) {
    this.idPlan = idPlan;
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

  public Person getPerson() {
    return this.person;
  }

  public void setPerson(final Person person) {
    this.person = person;
  }

  public Workpack getWorkpack() {
    return this.workpack;
  }

  public void setWorkpack(final Workpack workpack) {
    this.workpack = workpack;
  }

  @Transient
  public boolean hasSameUser(final Long idUser) {
    return this.person.getId().equals(idUser);
  }

}
