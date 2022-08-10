package br.gov.es.openpmo.dto.person.queries;

import br.gov.es.openpmo.model.actors.Person;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class PersonQuery {

  private final Person person;
  private final String key;
  private final String email;

  public PersonQuery(final Person person, String key, final String email) {
    this.person = person;
    this.key = key;
    this.email = email;
  }

  public Person getPerson() {
    return this.person;
  }

  public String getEmail() {
    return this.email;
  }

  public String getKey() {
    return key;
  }
}
