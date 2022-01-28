package br.gov.es.openpmo.repository.custom.filters;


import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.Rules;
import br.gov.es.openpmo.repository.CostAccountRepository;
import br.gov.es.openpmo.repository.PropertyModelRepository;
import br.gov.es.openpmo.repository.custom.FindAllUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindAllCostAccountUsingCustomFilter extends FindAllUsingCustomFilterBuilder implements ApplyFilterUsingPropertyModel {

  private final CostAccountRepository repository;
  private final PropertyModelRepository propertyModelRepository;

  @Autowired
  public FindAllCostAccountUsingCustomFilter(final CostAccountRepository repository, final PropertyModelRepository propertyModelRepository) {
    this.repository = repository;
    this.propertyModelRepository = propertyModelRepository;
  }

  @Override
  public Session getSession() {
    return this.repository.getSession();
  }

  @Override
  public void buildMatchClause(final CustomFilter filter, final StringBuilder query) {
    query.append("MATCH (workpack:Workpack)<-[i:APPLIES_TO]-(node:CostAccount), ")
      .append("(workpack)-[belongsTo:BELONGS_TO]->(plan:Plan) ")
      .append("(workpack)-[:IS_INSTANCE_BY]->(wm:WorkpackModel)<-[:FEATURES]-(propertyModel:PropertyModel),")
      .append("(propertyModel)-[:GROUPS]->(groupedProperty:PropertyModel)")
      .append(" ");
  }

  @Override
  public void buildWhereClause(final CustomFilter filter, final StringBuilder query) {
    query.append("WHERE ID(workpack)=$idWorkpack ")
      .append("AND belongsTo.linked=false " )
      .append(" ");
  }


  @Override
  public void buildReturnClause(final StringBuilder query) {
    query.append("RETURN workpack, [\n")
      .append("  [(workpack)<-[appliesTo:APPLIES_TO]-(node) | [appliesTo, node] ],\n")
      .append("  [(node)<-[f1:FEATURES]-(p1:Property)-[d1:IS_DRIVEN_BY]->(pm1:PropertyModel) | [f1, p1, d1, pm1] ],\n")
      .append("  [(p2)-[v1:VALUES]->(o:Organization) | [v1, o] ],\n")
      .append("  [(p2)-[v2:VALUES]-(l:Locality) | [v2, l] ],\n")
      .append("  [(p2)-[v3:VALUES]-(u:UnitMeasure) | [v3, u] ],\n")
      .append("  [(workpack)-[isIn:IS_IN*]->(w2:Workpack)-[:BELONGS_TO]->(plan) | [isIn, w2] ],\n")
      .append("  [(w2)<-[i2:APPLIES_TO]-(c2:CostAccount) | [i2, c2] ],\n")
      .append("  [(c2)<-[f2:FEATURES]-(p2:Property)-[d2:IS_DRIVEN_BY]->(pm2:PropertyModel) | [f2, p2, d2, pm2] ]\n")
      .append("]");
  }

  @Override
  protected String buildCustomFilterRule(final Rules rule, final String label) {
    return this.buildFilterRuleForWorkpack(rule, label);
  }

  @Override
  protected void buildOrderingAndDirectionClause(final CustomFilter filter, final StringBuilder query) {
    this.buildOrderingAndDirectionClauseForWorkpack(filter, query);
  }

  @Override
  public String[] getDefinedExternalParams() {
    return new String[]{"idWorkpack"};
  }

  @Override
  public String getNodeName() {
    return this.nodeName;
  }

  @Override
  public void setNodeName(final String nodeName) {
    this.nodeName = nodeName;
  }

  @Override
  public PropertyModelRepository getPropertyModelRepository() {
    return this.propertyModelRepository;
  }
}
