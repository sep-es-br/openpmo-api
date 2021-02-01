package br.gov.es.openpmo.model.relations;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import br.gov.es.openpmo.model.AuthService;
import br.gov.es.openpmo.model.Person;

@RelationshipEntity
public class IsAtuthenticatedBy {

	@Id
	@GeneratedValue
	private Long id;

	private String name;
	private String email;

	@StartNode
	private Person person;

	@EndNode
	private AuthService authService;

	public IsAtuthenticatedBy(Long id, String name, String email, Person person, AuthService authService) {

		this.id = id;
		this.name = name;
		this.email = email;
		this.person = person;
		this.authService = authService;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public AuthService getAuthService() {
		return authService;
	}

	public void setAuthService(AuthService authService) {
		this.authService = authService;
	}
}
