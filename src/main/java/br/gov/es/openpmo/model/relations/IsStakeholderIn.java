package br.gov.es.openpmo.model.relations;

import java.time.LocalDate;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.model.Actor;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.Workpack;

@RelationshipEntity(type = "IS_STAKEHOLDER_IN")
public class IsStakeholderIn extends Entity {

	private String role;
	private LocalDate to;
	private LocalDate from;
	private boolean active;
	private String permitedRole;
	private PermissionLevelEnum permissionLevel;

	public String getPermitedRole() {
		return this.permitedRole;
	}

	public void setPermitedRole(String permitedRole) {
		this.permitedRole = permitedRole;
	}

	public PermissionLevelEnum getPermissionLevel() {
		return this.permissionLevel;
	}

	public void setPermissionLevel(PermissionLevelEnum permissionLevel) {
		this.permissionLevel = permissionLevel;
	}

	@StartNode
	private Actor actor;

	@EndNode
	private Workpack workpack;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public LocalDate getTo() {
		return to;
	}

	public void setTo(LocalDate to) {
		this.to = to;
	}

	public LocalDate getFrom() {
		return from;
	}

	public void setFrom(LocalDate from) {
		this.from = from;
	}

	public Actor getActor() {
		return actor;
	}

	public void setActor(Actor actor) {
		this.actor = actor;
	}

	public Workpack getWorkpack() {
		return workpack;
	}

	public void setWorkpack(Workpack workpack) {
		this.workpack = workpack;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
