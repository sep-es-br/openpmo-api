package br.gov.es.openpmo.repository.custom.filters;

import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.repository.RiskRepository;
import br.gov.es.openpmo.repository.custom.FindAllUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindAllRiskUsingCustomFilter extends FindAllUsingCustomFilterBuilder {

  private final RiskRepository repository;

  @Autowired
  public FindAllRiskUsingCustomFilter(final RiskRepository repository) {
    this.repository = repository;
  }

  @Override
  protected Session getSession() {
    return this.repository.getSession();
  }

  @Override
  protected void buildMatchClause(final CustomFilter filter, final StringBuilder query) {
    query.append("MATCH (")
      .append(this.nodeName)
      .append(":Risk)-[isReportedFor:IS_FORSEEN_ON]->(workpack:Workpack{deleted:false})")
      .append("\n");
  }

  @Override
  protected void buildWhereClause(final CustomFilter filter, final StringBuilder query) {
    query.append("WHERE ").append("id(workpack)=$idWorkpack").append("\n");
  }

  @Override
  protected void buildReturnClause(final StringBuilder query) {
    query.append("RETURN ").append(this.nodeName).append(", isReportedFor, workpack").append("\n");
  }

  @Override protected boolean hasAppendedBooleanBlock() {
    return true;
  }

  @Override protected boolean hasToCloseAppendedBooleanBlock() {
    return true;
  }

  @Override
  protected String[] getDefinedExternalParams() {
    return new String[]{"idWorkpack"};
  }
}
