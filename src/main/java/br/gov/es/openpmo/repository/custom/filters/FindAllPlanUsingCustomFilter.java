package br.gov.es.openpmo.repository.custom.filters;


import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.repository.PlanRepository;
import br.gov.es.openpmo.repository.custom.FindAllUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindAllPlanUsingCustomFilter extends FindAllUsingCustomFilterBuilder {

  private final PlanRepository repository;

  @Autowired
  public FindAllPlanUsingCustomFilter(final PlanRepository repository) {
    this.repository = repository;
  }

  @Override
  public Session getSession() {
    return this.repository.getSession();
  }

  @Override
  public void buildMatchClause(final CustomFilter filter, final StringBuilder query) {
    query.append("MATCH (").append(this.nodeName)
      .append(":Plan)-[r:IS_ADOPTED_BY]->(o:Office),")
      .append("(").append(this.nodeName).append(")-[sb:IS_STRUCTURED_BY]->(pm:PlanModel)")
      .append(" ");
  }

  @Override
  public void buildWhereClause(final CustomFilter filter, final StringBuilder query) {
    query.append("WHERE ID(o)=$idOffice").append(" ");
  }

  @Override
  public void buildReturnClause(final StringBuilder query) {
    query.append("RETURN ").append(this.nodeName).append(", r, o, sb, pm");
  }

  @Override
  public String[] getDefinedExternalParams() {
    return new String[]{"idOffice"};
  }
}
