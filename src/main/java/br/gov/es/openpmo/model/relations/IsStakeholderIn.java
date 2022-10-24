package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.model.actors.Actor;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.scheduler.updateroles.HasRole;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.TargetNode;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;

@RelationshipProperties
public class IsStakeholderIn implements HasRole {

  @RelationshipId
  private Long id;

  private String role;

  private LocalDate to;

  private LocalDate from;

  private boolean active;

  private String permitedRole;

  private PermissionLevelEnum permissionLevel;

  private Actor actor;

  @TargetNode
  private Workpack workpack;

  public IsStakeholderIn() {
  }

  @Transient
  public Long getIdWorkpack() {
    if(this.workpack == null) return null;
    return this.workpack.getId();
  }

  public String getPermitedRole() {
    return this.permitedRole;
  }

  public void setPermitedRole(final String permitedRole) {
    this.permitedRole = permitedRole;
  }

  public PermissionLevelEnum getPermissionLevel() {
    return this.permissionLevel;
  }

  public void setPermissionLevel(final PermissionLevelEnum permissionLevel) {
    this.permissionLevel = permissionLevel;
  }

  public String getRole() {
    return this.role;
  }

  public void setRole(final String role) {
    this.role = role;
  }

  public LocalDate getTo() {
    return this.to;
  }

  public void setTo(final LocalDate to) {
    this.to = to;
  }

  public LocalDate getFrom() {
    return this.from;
  }

  public void setFrom(final LocalDate from) {
    this.from = from;
  }

  public Actor getActor() {
    return this.actor;
  }

  public void setActor(final Actor actor) {
    this.actor = actor;
  }

  public Workpack getWorkpack() {
    return this.workpack;
  }

  public void setWorkpack(final Workpack workpack) {
    this.workpack = workpack;
  }

  public boolean isActive() {
    return this.active;
  }

  public void setActive(final boolean active) {
    this.active = active;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

}
