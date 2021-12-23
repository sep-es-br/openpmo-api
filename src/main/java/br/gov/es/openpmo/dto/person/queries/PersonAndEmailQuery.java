package br.gov.es.openpmo.dto.person.queries;

import br.gov.es.openpmo.model.actors.Person;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class PersonAndEmailQuery {

  private final Person person;
  private final String email;

  public PersonAndEmailQuery(final Person person, final String email) {
    this.person = person;
    this.email = email;
  }

  public Person getPerson() {
    return this.person;
  }

  public String getEmail() {
    return this.email;
  }
}
