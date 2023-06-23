package br.gov.es.openpmo.repository.custom.filters;


import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.repository.UnitMeasureRepository;
import br.gov.es.openpmo.repository.custom.FindAllUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindAllUnitMeasureUsingCustomFilter extends FindAllUsingCustomFilterBuilder {

  private final UnitMeasureRepository repository;

  @Autowired
  public FindAllUnitMeasureUsingCustomFilter(final UnitMeasureRepository repository) {
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
    query.append("MATCH (").append(this.nodeName)
            .append(":UnitMeasure)-[a:AVAILABLE_IN]->(o:Office) ")
            .append("WITH *, apoc.text.levenshteinSimilarity(")
            .append("apoc.text.clean(")
            .append(this.nodeName)
            .append(".name), apoc.text.clean($term)) AS nameScore, ")
            .append("apoc.text.levenshteinSimilarity(")
            .append("apoc.text.clean(")
            .append(this.nodeName)
            .append(".fullName), apoc.text.clean($term)) AS fullNameScore ")
            .append("WITH *, CASE WHEN nameScore > fullNameScore THEN nameScore ELSE fullNameScore END AS score ");
  }

  @Override
  public void buildWhereClause(
    final CustomFilter filter,
    final StringBuilder query
  ) {
    query.append("WHERE ID(o) = $idOffice AND ( $term IS NULL OR $term = '' OR score > $searchCutOffScore ) ");
  }

  @Override
  public void buildReturnClause(final StringBuilder query) {
    query.append("RETURN ").append(this.nodeName).append(", a, o");
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
    return new String[]{
      "idOffice",
      "searchCutOffScore",
      "term"
    };
  }

}
