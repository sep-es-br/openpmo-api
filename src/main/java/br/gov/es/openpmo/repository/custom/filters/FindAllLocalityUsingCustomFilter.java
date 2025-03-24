package br.gov.es.openpmo.repository.custom.filters;

import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.repository.LocalityRepository;
import br.gov.es.openpmo.repository.custom.FindAllUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindAllLocalityUsingCustomFilter extends FindAllUsingCustomFilterBuilder {

  private final LocalityRepository repository;

  @Autowired
  public FindAllLocalityUsingCustomFilter(final LocalityRepository repository) {
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
    query.append("MATCH (domain:Domain)<-[:IS_ROOT_OF]-(root:Locality) ")
            .append("MATCH (root)<-[:IS_IN]-(").append(this.nodeName).append(":Locality)-[:BELONGS_TO]->(domain) ")
            .append("WITH *, apoc.text.levenshteinSimilarity(apoc.text.clean(").append(this.nodeName).append(".name), apoc.text.clean($term)) AS nameScore, ")
            .append("apoc.text.levenshteinSimilarity(apoc.text.clean(").append(this.nodeName).append(".fullName), apoc.text.clean($term)) AS fullNameScore ")
            .append("WITH *, CASE WHEN nameScore > fullNameScore THEN nameScore ELSE fullNameScore END AS score ");
  }

  @Override
  public void buildWhereClause(
    final CustomFilter filter,
    final StringBuilder query
  ) {
    query.append("WHERE id(domain)=$idDomain ")
      .append("AND ($term is null OR $term = '' OR score > $searchCutOffScore) ");
  }

  @Override
  public void buildReturnClause(final StringBuilder query) {
    query.append("RETURN ")
      .append(this.nodeName)
      .append(", [")
      .append("[(")
      .append(this.nodeName)
      .append(")<-[isIn:IS_IN]-(children:Locality)-[:BELONGS_TO]->(domain) | [isIn, children] ] ")
      .append("]");
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
      "idDomain",
      "term",
      "searchCutOffScore"
    };
  }

}
