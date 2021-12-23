package br.gov.es.openpmo.repository.custom.filters;

import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.repository.OfficeRepository;
import br.gov.es.openpmo.repository.custom.FindAllUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
  public void buildMatchClause(final CustomFilter filter, final StringBuilder query) {
    query.append("MATCH (").append(this.nodeName).append(":").append(filter.getType().getNodeName()).append(") ");
  }

  @Override
  public void buildWhereClause(final CustomFilter filter, final StringBuilder query) {
    if(filter.getRules() == null || filter.getRules().isEmpty()) return;
    query.append("WHERE").append(" ");
  }

  @Override
  public void buildReturnClause(final StringBuilder query) {
    query.append("RETURN").append(" ").append(this.nodeName);
  }

  @Override
  public void buildOrderingAndDirectionClause(final CustomFilter filter, final StringBuilder query) {
    this.appendStringIfTrue(
      filter.getSortBy() != null,
      builder -> builder.append(" ").append("ORDER BY ").append(this.nodeName).append(".").append(filter.getSortBy()),
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
    return new String[0];
  }


}
