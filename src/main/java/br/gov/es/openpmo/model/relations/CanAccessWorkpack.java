package br.gov.es.openpmo.model.relations;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.model.Person;
import br.gov.es.openpmo.model.Workpack;

@RelationshipEntity(type = "CAN_ACCESS_WORKPACK")
public class CanAccessWorkpack {

	@Id
	@GeneratedValue
	private Long id;
	private String organization;
	private String permitedRole;
	private PermissionLevelEnum permissionLevel;

	@StartNode
	private Person person;

	@EndNode
	private Workpack workpack;

	public CanAccessWorkpack(Long id, String organization, String permitedRole, PermissionLevelEnum permissionLevel,
			Person person, Workpack workpack) {
		this.id = id;
		this.organization = organization;
		this.permitedRole = permitedRole;
		this.permissionLevel = permissionLevel;
		this.person = person;
		this.workpack = workpack;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrganization() {
		return this.organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

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

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Workpack getWorkpack() {
		return workpack;
	}

	public void setWorkpack(Workpack workpack) {
		this.workpack = workpack;
	}
}
