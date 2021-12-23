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
  public void buildMatchClause(final CustomFilter filter, final StringBuilder query) {
    query.append("MATCH (d:Domain)<-[:BELONGS_TO]-(").append(this.nodeName).append(":Locality)").append(" ");
  }

  @Override
  public void buildWhereClause(final CustomFilter filter, final StringBuilder query) {
    query.append("WHERE id(d) = $idDomain AND NOT (").append(this.nodeName).append(")-[:IS_IN]->(:Locality)").append(" ");
  }

  @Override
  public void buildReturnClause(final StringBuilder query) {
    query.append("RETURN ").append(this.nodeName).append(", [")
      .append("[(").append(this.nodeName).append(")<-[btl:IS_IN]-(lc:Locality)-[:BELONGS_TO]->(d) | [btl, lc] ] ")
      .append("]");
  }

  @Override
  public String[] getDefinedExternalParams() {
    return new String[]{"idDomain"};
  }
}
