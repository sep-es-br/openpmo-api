package br.gov.es.openpmo.repository.custom.filters;


import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.repository.PlanRepository;
import br.gov.es.openpmo.repository.custom.FindStakeholderAndPermissionUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindStakeholderAndPermissionUsingCustomFilter extends FindStakeholderAndPermissionUsingCustomFilterBuilder {

  private final PlanRepository repository;

  @Autowired
  public FindStakeholderAndPermissionUsingCustomFilter(final PlanRepository repository) {
    this.repository = repository;
  }

  @Override
  public Session getSession() {
    return this.repository.getSession();
  }

  @Override
  public void buildMatchClause(
    final CustomFilter filter,
    final StringBuilder query
  ) {
    query.append("MATCH (actor:Actor)-->(workpack:Workpack{deleted:false}) ")
      .append("OPTIONAL MATCH (actor)-[isStakeholderIn:IS_STAKEHOLDER_IN]->(workpack) ")
      .append("OPTIONAL MATCH (actor)-[canAccessWorkpack:CAN_ACCESS_WORKPACK]->(workpack) ")
      .append("OPTIONAL MATCH (workpack)-[belongsTo:BELONGS_TO]->(plan:Plan) ")
      .append("OPTIONAL MATCH (person)-[contact:IS_IN_CONTACT_BOOK_OF]->(office:Office) ")
      .append("OPTIONAL MATCH (office)<-[adoptedBy:IS_ADOPTED_BY]-(plan)")
      .append(" ");
  }

  @Override
  public void buildWhereClause(
    final CustomFilter filter,
    final StringBuilder query
  ) {
    query
      .append("WITH actor, isStakeholderIn, workpack, belongsTo, plan, person, canAccessWorkpack, contact, office, adoptedBy ")
      .append("WHERE ID(workpack)=$idWorkpack").append(" ");
  }

  @Override
  public void buildReturnClause(final StringBuilder query) {
    query
      .append("RETURN collect(DISTINCT actor) as actors, collect(DISTINCT isStakeholderIn) as stakeholders, [x in collect(DISTINCT actor) where 'Person' in labels(x)] as persons, ")
      .append("collect(DISTINCT workpack) as workpacks, collect(DISTINCT canAccessWorkpack) as permissions ")
      .append(" ");
  }

  @Override
  protected boolean hasAppendedBooleanBlock() {
    return true;
  }

  @Override
  protected boolean hasToCloseAppendedBooleanBlock() {
    return true;
  }

  @Override
  public String[] getDefinedExternalParams() {
    return new String[]{"idWorkpack"};
  }

}
