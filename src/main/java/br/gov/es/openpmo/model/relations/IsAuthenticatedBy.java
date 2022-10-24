package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.actors.AuthService;
import br.gov.es.openpmo.model.actors.Person;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.TargetNode;
import org.springframework.data.annotation.Transient;

@RelationshipProperties
public class IsAuthenticatedBy {

  @RelationshipId
  private Long id;

  private String key;

  private String name;

  private String email;

  private String guid;

  private Person person;

  @TargetNode
  private AuthService authService;

  public IsAuthenticatedBy() {
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

  @Transient
  public Long getIdPerson() {
    return this.person.getId();
  }

  public String getKey() {
    return this.key;
  }

  public void setKey(final String key) {
    this.key = key;
  }

}
