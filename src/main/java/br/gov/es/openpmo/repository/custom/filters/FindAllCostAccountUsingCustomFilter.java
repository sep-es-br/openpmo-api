package br.gov.es.openpmo.repository.custom.filters;


import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.Rules;
import br.gov.es.openpmo.repository.CostAccountRepository;
import br.gov.es.openpmo.repository.PropertyModelRepository;
import br.gov.es.openpmo.repository.custom.FindAllUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FindAllCostAccountUsingCustomFilter extends FindAllUsingCustomFilterBuilder implements ApplyFilterUsingPropertyModel {

  private final CostAccountRepository repository;
  private final PropertyModelRepository propertyModelRepository;

  @Autowired
  public FindAllCostAccountUsingCustomFilter(
    final CostAccountRepository repository,
    final PropertyModelRepository propertyModelRepository
  ) {
    this.repository = repository;
    this.propertyModelRepository = propertyModelRepository;
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
    query.append(
                 "MATCH (plan:Plan)<-[belongsTo:BELONGS_TO]-(workpack:Workpack{deleted: false})<-[a:APPLIES_TO]-(costAccount:CostAccount)-[i:IS_INSTANCE_BY]->(costAccountModel:CostAccountModel), " +
                 "(costAccount)<-[f:FEATURES]-(property:Property)-[d:IS_DRIVEN_BY]->(propertyModel:PropertyModel)-[g:FEATURES]->(costAccountModel) " +
                 "OPTIONAL MATCH (property)-[v:VALUES]->(values) " +
                 "WITH *, " +
                 "collect( property ) as properties, " +
                 "collect( id(values) ) as selectedValues ");
  }

  @Override
  public void buildWhereClause(
    final CustomFilter filter,
    final StringBuilder query
  ) {
    query.append("WHERE (" +
                 "   id(workpack)=$idWorkpack " +
                 "   AND belongsTo.linked=false" +
                 ") ");
  }

  @Override
  public void buildReturnClause(final StringBuilder query) {
    query.append("RETURN costAccount, i, costAccountModel, a, workpack, belongsTo, plan, f, property, v, values, d, propertyModel, g, [")
      .append("[(costAccount)<-[a1:FEATURES]-(b1:Property)-[c1:IS_DRIVEN_BY]->(d1:PropertyModel)-[e1:FEATURES]->(costAccountModel)|[a1,b1,c1,d1,e1]]")
      .append("]");
  }

  @Override
  protected String buildCustomFilterRule(
    final Rules rule,
    final String label
  ) {
    return this.buildFilterRuleForWorkpack(rule, label);
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
  protected void buildOrderingAndDirectionClause(
    final CustomFilter filter,
    Map<String, Object> params,
    final StringBuilder query
  ) {
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
