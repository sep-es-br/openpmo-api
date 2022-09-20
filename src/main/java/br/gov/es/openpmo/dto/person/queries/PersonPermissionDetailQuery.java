package br.gov.es.openpmo.dto.person.queries;

import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.relations.CanAccessOffice;
import br.gov.es.openpmo.model.relations.CanAccessPlan;
import br.gov.es.openpmo.model.relations.CanAccessWorkpack;
import br.gov.es.openpmo.model.relations.IsCCBMemberFor;
import br.gov.es.openpmo.model.relations.IsStakeholderIn;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class PersonPermissionDetailQuery {

  private final Person person;

  private final Office office;

  private final Plan plan;

  private final Workpack workpack;

  private final CanAccessOffice canAccessOffice;

  private final CanAccessPlan canAccessPlan;

  private final CanAccessWorkpack canAccessWorkpack;

  private final IsStakeholderIn isStakeholderIn;

  private final IsCCBMemberFor isCCBMemberFor;

  public PersonPermissionDetailQuery(
    final Person person,
    final Office office,
    final Plan plan,
    final Workpack workpack,
    final CanAccessOffice canAccessOffice,
    final CanAccessPlan canAccessPlan,
    final CanAccessWorkpack canAccessWorkpack,
    final IsStakeholderIn isStakeholderIn,
    final IsCCBMemberFor isCCBMemberFor
  ) {
    this.person = person;
    this.office = office;
    this.plan = plan;
    this.workpack = workpack;
    this.canAccessOffice = canAccessOffice;
    this.canAccessPlan = canAccessPlan;
    this.canAccessWorkpack = canAccessWorkpack;
    this.isStakeholderIn = isStakeholderIn;
    this.isCCBMemberFor = isCCBMemberFor;
  }

  public Person getPerson() {
    return this.person;
  }

  public Office getOffice() {
    return this.office;
  }

  public CanAccessOffice getCanAccessOffice() {
    return this.canAccessOffice;
  }

  public CanAccessPlan getCanAccessPlan() {
    return this.canAccessPlan;
  }

  public CanAccessWorkpack getCanAccessWorkpack() {
    return this.canAccessWorkpack;
  }

  public IsStakeholderIn getIsStakeholderIn() {
    return this.isStakeholderIn;
  }

  public IsCCBMemberFor getIsCCBMemberFor() {
    return this.isCCBMemberFor;
  }

  public Plan getPlan() {
    return this.plan;
  }

  public Workpack getWorkpack() {
    return this.workpack;
  }

}
