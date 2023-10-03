package br.gov.es.openpmo.repository.custom.filters;


import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.repository.DomainRepository;
import br.gov.es.openpmo.repository.custom.FindAllUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FindAllDomainUsingCustomFilter extends FindAllUsingCustomFilterBuilder {

  private final DomainRepository repository;

  @Autowired
  public FindAllDomainUsingCustomFilter(final DomainRepository repository) {
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
      .append(":Domain)-[apl:APPLIES_TO]->(o:Office) ");
  }

  @Override
  public void buildWhereClause(
    final CustomFilter filter,
    final StringBuilder query
  ) {
    query.append("WHERE ( ID(o) = $idOffice OR $idOffice IS NULL ) ");
    query.append("OPTIONAL MATCH (").append(this.nodeName).append(")<-[bt:BELONGS_TO]-(l:Locality) ");
    String complementWITH = ", bt, l ";
    if (!filter.isSimilarityFilter()) {
      query.append("WITH * ");
    }
    buildFilterBySimilarity(filter, query, complementWITH);
    if (!filter.isSimilarityFilter() && filter.getRules() != null && !filter.getRules().isEmpty()) {
    	if (hasAppendedBooleanBlock()) query.append(" WHERE TRUE ");
    	else  query.append(" WHERE ");
    }
  }

  @Override
  public void buildReturnClause(final StringBuilder query) {
    query.append("RETURN ").append(this.nodeName).append(", bt, l, [")
      .append("[ (").append(this.nodeName).append(")-[apl:APPLIES_TO]->(o:Office) | [apl, o] ], ")
      .append("[ (").append(this.nodeName).append(
        ")-[isRootOf:IS_ROOT_OF]->(root:Locality) | [isRootOf, root] ], ")
      .append("[ (l)<-[btl:IS_IN]-(lc:Locality) | [btl, lc] ], ")
      .append("[ (l)-[btl:IS_IN]->(lc:Locality) | [btl, lc] ] ")
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
  public void buildOrderingAndDirectionClause(
    final CustomFilter filter,
    Map<String, Object> params,
    final StringBuilder query
  ) {
	if (filter.getSortBy() != null || filter.isSimilarityFilter()) query.append(" ").append("ORDER BY ");
	buildOrderingBySimilarity(filter, query);
	if (filter.getSortBy() != null && filter.isSimilarityFilter()) query.append(", ");
    this.appendStringIfTrue(
      filter.getSortBy() != null,
      builder -> builder.append(this.nodeName).append(".")
        .append(filter.getSortBy())
        .append(" ").append(filter.getDirection()),
      query
    );
  }

  @Override
  public String[] getDefinedExternalParams() {
    return new String[]{"idOffice"};
  }

}
