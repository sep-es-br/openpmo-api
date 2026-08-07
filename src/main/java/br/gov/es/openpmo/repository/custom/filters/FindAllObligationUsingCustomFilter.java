package br.gov.es.openpmo.repository.custom.filters;

import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.repository.ObligationRepository;
import br.gov.es.openpmo.repository.custom.FindAllUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Component;

@Component
public class FindAllObligationUsingCustomFilter extends FindAllUsingCustomFilterBuilder {

  private final ObligationRepository repository;

  public FindAllObligationUsingCustomFilter(final ObligationRepository repository) {
    this.repository = repository;
  }

  @Override protected Session getSession() { return this.repository.getSession(); }

  @Override protected void buildMatchClause(final CustomFilter filter, final StringBuilder query) {
    query.append("MATCH (").append(this.nodeName)
      .append(":Obligation)-[relationship:ISSUED_FOR]->(workpack:Workpack) ");
  }

  @Override protected void buildWhereClause(final CustomFilter filter, final StringBuilder query) {
    query.append("WHERE id(workpack) = $idWorkpack ");
  }

  @Override protected void buildReturnClause(final StringBuilder query) {
    query.append(" RETURN ").append(this.nodeName).append(", relationship, workpack ");
  }

  @Override protected boolean hasAppendedBooleanBlock() { return true; }
  @Override protected boolean hasToCloseAppendedBooleanBlock() { return true; }
  @Override protected String[] getDefinedExternalParams() { return new String[]{"idWorkpack", "term"}; }
}
