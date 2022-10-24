package br.gov.es.openpmo.dto.person.queries;


import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.relations.CanAccessPlan;
import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import org.springframework.data.neo4j.repository.query.QueryResult;

import java.util.Set;

@QueryResult
public class PersonDetailQuery {

  private final Person person;
  private final Office office;
  private final IsAuthenticatedBy authentication;
  private final IsInContactBookOf contact;
  private final File avatar;
  private final Set<CanAccessPlan> canAccessPlans;

  public PersonDetailQuery(
    final Person person,
    final Office office,
    final IsAuthenticatedBy authentication,
    final IsInContactBookOf contact,
    final Set<CanAccessPlan> canAccessPlans,
    final File avatar
  ) {
    this.person = person;
    this.office = office;
    this.authentication = authentication;
    this.contact = contact;
    this.canAccessPlans = canAccessPlans;
    this.avatar = avatar;
  }

  public Person getPerson() {
    return this.person;
  }

  public Office getOffice() {
    return this.office;
  }

  public Set<CanAccessPlan> getCanAccessPlans() {
    return this.canAccessPlans;
  }

  public IsAuthenticatedBy getAuthentication() {
    return this.authentication;
  }

  public IsInContactBookOf getContact() {
    return this.contact;
  }

  public Long getIdPerson() {
    return this.person.getId();
  }

  public File getAvatar() {
    return this.avatar;
  }

}
