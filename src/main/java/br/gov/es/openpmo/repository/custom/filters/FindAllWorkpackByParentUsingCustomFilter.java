package br.gov.es.openpmo.repository.custom.filters;


import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.Rules;
import br.gov.es.openpmo.repository.PropertyModelRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.custom.FindAllUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindAllWorkpackByParentUsingCustomFilter extends FindAllUsingCustomFilterBuilder implements ApplyFilterUsingPropertyModel {

  private final WorkpackRepository repository;
  private final PropertyModelRepository propertyModelRepository;

  @Autowired
  public FindAllWorkpackByParentUsingCustomFilter(
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
  public void buildMatchClause(final CustomFilter filter, final StringBuilder query) {
    query.append("MATCH (node:Workpack)-[rf:BELONGS_TO]->(p:Plan), ")
      .append("(p)-[is:IS_STRUCTURED_BY]->(pm:PlanModel), ")
      .append("(node)-[ii:IS_INSTANCE_BY]->(wm:WorkpackModel), ")
      .append("(node)-[wc:IS_IN]->(wp:Workpack)")
      .append("(wm)<-[:FEATURES]-(propertyModel:PropertyModel), ")
      .append("(propertyModel)-[:GROUPS]->(groupedProperty:PropertyModel)")
      .append(" ");
  }

  @Override
  public void buildWhereClause(final CustomFilter filter, final StringBuilder query) {
    query.append("WHERE id(p)=$idPlan AND\n")
      .append("(id(pm)=$idPlanModel OR $idPlanModel IS NULL) AND\n")
      .append("(id(wm)=$idWorkPackModel OR $idWorkPackModel IS NULL) AND\n")
      .append("id(wp)=$idWorkPackParent ");
  }

  @Override
  public void buildReturnClause(final StringBuilder query) {
    query.append("RETURN node, rf, p, ii, pm, wm, [\n")
      .append("  [(node)<-[f:FEATURES]-(p:Property)-[d:IS_DRIVEN_BY]->(pm:PropertyModel) | [f, p, d, pm] ],\n")
      .append("  [(node)<-[wi:IS_IN]-(w2:Workpack) | [wi, w2] ],\n")
      .append("  [(node)-[wi2:IS_IN]->(w3:Workpack) | [wi2, w3] ],\n")
      .append("  [(node)<-[wa:APPLIES_TO]-(ca:CostAccount) | [wa, ca] ],\n")
      .append("  [(ca)<-[f1:FEATURES]-(p2:Property)-[d1:IS_DRIVEN_BY]->(pmc:PropertyModel) | [ca, f1, p2, d1, pmc ] ],\n")
      .append("  [(wm)<-[wmi:IS_IN]-(wm2:WorkpackModel) | [wmi,wm2] ],\n")
      .append("  [(wm)-[wmi2:IS_IN]->(wm3:WorkpackModel) | [wmi2,wm3] ],\n")
      .append("  [(wm)<-[f2:FEATURES]-(pm2:PropertyModel) | [f2, pm2] ],\n")
      .append("  [(node)-[sharedWith:IS_SHARED_WITH]->(office:Office) | [sharedWith, office]],\n")
      .append("  [(node)-[isLinkedTo:IS_LINKED_TO]->(workpackModel:WorkpackModel) | [isLinkedTo, workpackModel] ],\n")
      .append("  [(wp)-[parentSharedWith]->(officeParent:Office) | [parentSharedWith, officeParent]]\n")
      .append("]\n");
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
    return new String[]{
      "idPlan",
      "idPlanModel",
      "idWorkPackModel",
      "idWorkPackParent"
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
