package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.actors.AuthService;
import br.gov.es.openpmo.model.actors.Person;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "IS_AUTHENTICATED_BY")
public class IsAuthenticatedBy {

  @Id
  @GeneratedValue
  private Long id;

  private String name;

  private String email;

  private String guid;

  @StartNode
  private Person person;

  @EndNode
  private AuthService authService;

  public IsAuthenticatedBy() {
  }

  public IsAuthenticatedBy(final Long id, final String name, final String email, final Person person, final AuthService authService) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.person = person;
    this.authService = authService;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public Person getPerson() {
    return this.person;
  }

  public void setPerson(final Person person) {
    this.person = person;
  }

  public AuthService getAuthService() {
    return this.authService;
  }

  public void setAuthService(final AuthService authService) {
    this.authService = authService;
  }

  public String getGuid() {
    return this.guid;
  }

  public void setGuid(final String guid) {
    this.guid = guid;
  }

  public Long getIdPerson() {
    return this.person.getId();
  }
}
