package br.gov.es.openpmo.repository.custom.filters;


import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.Rules;
import br.gov.es.openpmo.repository.PropertyModelRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.custom.FindAllUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class FindAllWorkpackUsingCustomFilter extends FindAllUsingCustomFilterBuilder implements ApplyFilterUsingPropertyModel {

  private final WorkpackRepository repository;
  private final PropertyModelRepository propertyModelRepository;

  @Autowired
  public FindAllWorkpackUsingCustomFilter(
    final WorkpackRepository repository,
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
    query.append("MATCH (wm:WorkpackModel)<-[:IS_INSTANCE_BY | IS_LINKED_TO]-(node:Workpack{deleted:false})-[rf:BELONGS_TO]->(p:Plan), " +
            "(p)-[is:IS_STRUCTURED_BY]->(pm:PlanModel), (wm)<-[:FEATURES]-(propertyModel:PropertyModel) " +
            "OPTIONAL MATCH (node)<-[:FEATURES]-(property:Property)-[:IS_DRIVEN_BY]->(propertyModel) " +
            "OPTIONAL MATCH (propertyModel)-[:GROUPS]->(groupedProperty:PropertyModel) " +
            "OPTIONAL MATCH (node)<-[:FEATURES]-(:Property)-[:VALUES]->(values) " +
            "WITH *, " +
            "apoc.text.levenshteinSimilarity(apoc.text.clean(node.name), apoc.text.clean($term)) as nameScore, " +
            "apoc.text.levenshteinSimilarity(apoc.text.clean(node.fullName), apoc.text.clean($term)) as fullNameScore " +
            "WITH *, CASE WHEN nameScore > fullNameScore THEN nameScore ELSE fullNameScore END AS score, " +
            "collect( property ) as properties, " +
            "collect( id(values) ) as selectedValues ");
  }

  @Override
  public void buildWhereClause(
    final CustomFilter filter,
    final StringBuilder query
  ) {
    query.append("WHERE (" +
                 "  id(p)=$idPlan " +
                 "  AND (id(pm)=$idPlanModel OR $idPlanModel IS NULL) " +
                 "  AND (id(wm)=$idWorkPackModel OR $idWorkPackModel IS NULL) " +
                 "  AND ($term IS NULL OR $term = '' OR score > $searchCutOffScore ) " +
                 ") ");
  }

  @Override
  public void buildReturnClause(final StringBuilder query) {
    query.append("RETURN node, rf, p, wm, [ ")
        .append(" [ (node)-[sharedWith:IS_SHARED_WITH]->(office:Office) | [sharedWith, office]], ")
        .append(" [ (node)-[instanceBy:IS_INSTANCE_BY]->(wm) | [instanceBy, wm] ], ")
        .append(" [ (node)-[isLinkedTo:IS_LINKED_TO]->(wm) | [isLinkedTo, wm] ], ")
        .append(" [ (node)<-[b:BELONGS_TO]-(d:Dashboard)<-[ipo:IS_PART_OF]-(dm:DashboardMonth)<-[ia:IS_AT]-(nodes) | [b,d,ipo,dm,ia,nodes] ] ")
      .append("] ");
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
    final Map<String, Object> params,
    final StringBuilder query
  ) {
    final String term = (String) params.get("term");
    if (StringUtils.hasText(term)) {
      query.append(" ")
        .append("ORDER BY score DESC");
      return;
    }
    this.buildOrderingAndDirectionClauseForWorkpack(filter, query);
  }

  @Override
  public String[] getDefinedExternalParams() {
    return new String[]{
      "idPlan",
      "idPlanModel",
      "idWorkPackModel",
      "searchCutOffScore",
      "term"
    };
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
