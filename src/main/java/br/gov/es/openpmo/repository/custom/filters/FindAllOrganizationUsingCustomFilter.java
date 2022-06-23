package br.gov.es.openpmo.repository.custom.filters;


import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.repository.OrganizationRepository;
import br.gov.es.openpmo.repository.custom.FindAllUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindAllOrganizationUsingCustomFilter extends FindAllUsingCustomFilterBuilder {

  private final OrganizationRepository repository;

  @Autowired
  public FindAllOrganizationUsingCustomFilter(final OrganizationRepository repository) {
    this.repository = repository;
  }

  @Override
  public Session getSession() {
    return this.repository.getSession();
  }

  @Override
  public void buildMatchClause(final CustomFilter filter, final StringBuilder query) {
    query.append("MATCH (").append(this.nodeName)
      .append(":Organization)-[is:IS_REGISTERED_IN]->(office:Office) ");
  }

  @Override
  public void buildWhereClause(final CustomFilter filter, final StringBuilder query) {
    query.append("WHERE ID(office) = $idOffice ");
  }

  @Override
  public void buildReturnClause(final StringBuilder query) {
    query.append("RETURN ").append(this.nodeName).append(", office");
  }

  @Override protected boolean hasAppendedBooleanBlock() {
    return true;
  }

  @Override protected boolean hasToCloseAppendedBooleanBlock() {
    return true;
  }

  @Override
  public String[] getDefinedExternalParams() {
    return new String[]{"idOffice"};
  }
}
