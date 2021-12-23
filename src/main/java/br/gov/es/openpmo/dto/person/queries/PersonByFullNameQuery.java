package br.gov.es.openpmo.dto.person.queries;

import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@QueryResult
public class PersonByFullNameQuery {

  private final List<Person> persons;
  private final Set<IsInContactBookOf> contacts;

  public PersonByFullNameQuery(
    final List<Person> persons,
    final Set<IsInContactBookOf> contacts
  ) {
    this.persons = Collections.unmodifiableList(persons);
    this.contacts = Collections.unmodifiableSet(contacts);
  }

  public List<Person> getPersons() {
    return this.persons;
  }

  public Set<IsInContactBookOf> getContacts() {
    return this.contacts;
  }
}
