package br.gov.es.openpmo.repository.custom.filters;

import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.repository.OfficeRepository;
import br.gov.es.openpmo.repository.custom.FindAllUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FindAllOfficeUsingCustomFilter extends FindAllUsingCustomFilterBuilder {


  private final OfficeRepository repository;

  @Autowired
  public FindAllOfficeUsingCustomFilter(final OfficeRepository repository) {
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
    query.append("MATCH (").append(this.nodeName).append(":").append(filter.getType().getNodeName()).append(") ");
  }

  @Override
  public void buildWhereClause(
    final CustomFilter filter,
    final StringBuilder query
  ) {
	buildFilterBySimilarity(filter, query);
    if(filter.getRules() == null || filter.getRules().isEmpty() || filter.isSimilarityFilter()) return;
    query.append("WHERE").append(" ");
  }

  @Override
  public void buildReturnClause(final StringBuilder query) {
    query.append("RETURN").append(" ").append(this.nodeName);
  }

  @Override
  protected boolean hasAppendedBooleanBlock() {
    return false;
  }

  @Override
  protected boolean hasToCloseAppendedBooleanBlock() {
    return false;
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
      builder -> builder.append(this.nodeName).append(".").append(filter.getSortBy()),
      query
    );
    this.appendStringIfTrue(
      filter.getDirection() != null,
      builder -> builder.append(" ").append(filter.getDirection()),
      query
    );
  }

  @Override
  public String[] getDefinedExternalParams() {
    String[] a = {"term", "searchCutOffScore"};
    return a;
  }


}
