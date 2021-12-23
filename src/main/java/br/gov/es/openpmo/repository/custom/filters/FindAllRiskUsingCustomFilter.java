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
    query.append("MATCH (" + this.nodeName + ":Risk)-[isReportedFor:IS_FORSEEN_ON]->(workpack:Workpack)\n");
  }

  @Override
  protected void buildWhereClause(final CustomFilter filter, final StringBuilder query) {
    query.append("WHERE id(workpack)=$idWorkpack\n");
  }

  @Override
  protected void buildReturnClause(final StringBuilder query) {
    query.append("RETURN " + this.nodeName + ", isReportedFor, workpack\n");
  }

  @Override
  protected String[] getDefinedExternalParams() {
    return new String[]{
      "idWorkpack"
    };
  }
}
