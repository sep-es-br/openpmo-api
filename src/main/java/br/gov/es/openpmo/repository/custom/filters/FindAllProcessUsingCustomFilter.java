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
  protected void buildMatchClause(
    final CustomFilter filter,
    final StringBuilder query
  ) {
    query.append("MATCH (").append(this.nodeName)
      .append(":Process)-[isReportedFor:IS_BELONGS_TO]->(workpack:Workpack{deleted:false})\n");
  }

  @Override
  protected void buildWhereClause(
          final CustomFilter filter,
          final StringBuilder query
  ) {
    query.append("WITH *, apoc.text.levenshteinSimilarity(apoc.text.clean(")
            .append(this.nodeName)
            .append(".name), apoc.text.clean($term)) as nameScore, ")
            .append("apoc.text.levenshteinSimilarity(apoc.text.clean(")
            .append(this.nodeName)
            .append(".subject), apoc.text.clean($term)) as subjectScore ")
            .append("WITH *, CASE WHEN nameScore > subjectScore THEN nameScore ELSE subjectScore END AS score ")
            .append("WHERE id(workpack)=$idWorkpack AND ($term is null OR $term = '' OR score > $searchCutOffScore) ");
  }

  @Override
  protected void buildReturnClause(final StringBuilder query) {
    query.append("\n").append("RETURN ").append(this.nodeName).append(", isReportedFor, workpack ").append("\n");
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
  protected String[] getDefinedExternalParams() {
    return new String[]{
      "idWorkpack",
      "term",
      "searchCutOffScore"
    };
  }

}
