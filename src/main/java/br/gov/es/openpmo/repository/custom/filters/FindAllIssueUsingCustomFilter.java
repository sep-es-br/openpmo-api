package br.gov.es.openpmo.repository.custom.filters;

import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.repository.IssueRepository;
import br.gov.es.openpmo.repository.custom.FindAllUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindAllIssueUsingCustomFilter extends FindAllUsingCustomFilterBuilder {

  private final IssueRepository issueRepository;

  @Autowired
  public FindAllIssueUsingCustomFilter(final IssueRepository issueRepository) {
    this.issueRepository = issueRepository;
  }

  @Override
  protected Session getSession() {
    return this.issueRepository.getSession();
  }

  @Override
  protected void buildMatchClause(final CustomFilter filter, final StringBuilder query) {
    query.append("MATCH (issue:").append(this.nodeName)
      .append(")-[reported:IS_REPORTED_FOR]->(workpack:Workpack{deleted:false})").append(" ");
  }

  @Override
  protected void buildWhereClause(final CustomFilter filter, final StringBuilder query) {
    query.append("WHERE id(workpack)=$idWorkpack").append(" ");
  }

  @Override
  protected void buildReturnClause(final StringBuilder query) {
    query.append("RETURN issue");
  }

  @Override
  protected String[] getDefinedExternalParams() {
    return new String[]{
      "idWorkpack"
    };
  }
}
