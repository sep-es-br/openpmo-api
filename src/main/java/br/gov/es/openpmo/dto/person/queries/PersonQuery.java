package br.gov.es.openpmo.dto.person.queries;

import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.service.actors.IGetPersonFromAuthorization;
import org.springframework.data.neo4j.repository.query.QueryResult;

@QueryResult
public class PersonQuery implements IGetPersonFromAuthorization.PersonDataResponse {

  private final Person person;
  private final String key;
  private final String email;

  public PersonQuery(
    final Person person,
    final String key,
    final String email
  ) {
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
    return this.key;
  }

}
