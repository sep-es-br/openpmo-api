package br.gov.es.openpmo.repository.custom.filters;

import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.repository.ProcurementRepository;
import br.gov.es.openpmo.repository.custom.FindAllUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Component;

@Component
public class FindAllProcurementUsingCustomFilter extends FindAllUsingCustomFilterBuilder {

  private final ProcurementRepository repository;

  public FindAllProcurementUsingCustomFilter(final ProcurementRepository repository) {
    this.repository = repository;
  }

  @Override protected Session getSession() { return this.repository.getSession(); }

  @Override protected void buildMatchClause(final CustomFilter filter, final StringBuilder query) {
    query.append("MATCH (").append(this.nodeName)
      .append(":Procurement)-[relationship:RELATED_TO]->(workpack:Workpack) ");
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
