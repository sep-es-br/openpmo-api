package br.gov.es.openpmo.repository.custom.filters;

import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.repository.ProcessRepository;
import br.gov.es.openpmo.repository.custom.FindAllUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindAllProcessUsingCustomFilter extends FindAllUsingCustomFilterBuilder {

  private final ProcessRepository repository;

  @Autowired
  public FindAllProcessUsingCustomFilter(final ProcessRepository repository) {
    this.repository = repository;
  }

  @Override
  protected Session getSession() {
    return this.repository.getSession();
  }

  @Override
  protected void buildMatchClause(final CustomFilter filter, final StringBuilder query) {
    query.append("MATCH (" + this.nodeName + ":Process)-[isReportedFor:IS_BELONG_TO]->(workpack:Workpack)\n");
  }

  @Override
  protected void buildWhereClause(final CustomFilter filter, final StringBuilder query) {
    query.append("WHERE id(workpack)=$idWorkpack\n");
  }

  @Override
  protected void buildReturnClause(final StringBuilder query) {
    query.append("RETURN " + this.nodeName).append("\n");
  }

  @Override
  protected String[] getDefinedExternalParams() {
    return new String[]{
      "idWorkpack"
    };
  }
}