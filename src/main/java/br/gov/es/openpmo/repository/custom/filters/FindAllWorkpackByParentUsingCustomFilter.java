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
public class FindAllWorkpackByParentUsingCustomFilter extends FindAllUsingCustomFilterBuilder
  implements ApplyFilterUsingPropertyModel {

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
  public void buildMatchClause(
    final CustomFilter filter,
    final StringBuilder query
  ) {
    query.append(
      "MATCH (pl:Plan), (wm:WorkpackModel), (p:Workpack) " +
      "WHERE id(pl)=$idPlan AND id(wm)=$idWorkpackModel AND id(p)=$idWorkpackParent " +
      "OPTIONAL MATCH (w:Workpack{deleted:false})-[:IS_IN]->(p) " +
      "OPTIONAL MATCH (w)-[:IS_INSTANCE_BY]->(wm) " +
      "OPTIONAL MATCH (w)-[bt1:BELONGS_TO]->(pl) " +
      "OPTIONAL MATCH (w)<-[:FEATURES]-(property1:Property) " +
      "OPTIONAL MATCH (w)<-[:FEATURES]-(name1:Property)-[:IS_DRIVEN_BY]->(:PropertyModel{name: 'name'}) " +
      "OPTIONAL MATCH (w)<-[:FEATURES]-(fullName1:Property)-[:IS_DRIVEN_BY]->(:PropertyModel{name: 'fullName'}) " +
      "OPTIONAL MATCH (w)<-[:FEATURES]-(:Property)-[:VALUES]->(values1) " +
      "WITH *, " +
      "  apoc.text.levenshteinSimilarity(apoc.text.clean(name1.value), apoc.text.clean($term)) AS nameScore1, " +
      "  apoc.text.levenshteinSimilarity(apoc.text.clean(fullName1.value), apoc.text.clean($term)) AS fullNameScore1 " +
      "WITH *, CASE WHEN nameScore1 > fullNameScore1 THEN nameScore1 ELSE fullNameScore1 END AS score1, " +
      "  collect( property1 ) + collect( name1 ) + collect( fullName1 ) AS properties1, " +
      "  collect( id(values1) ) AS selectedValues1 " +
      "OPTIONAL MATCH (p)<-[:IS_IN]-(v:Workpack{deleted:false})-[:IS_LINKED_TO]->(wm) " +
      "OPTIONAL MATCH (v)-[bt2:BELONGS_TO]->(pl) " +
      "OPTIONAL MATCH (w)<-[:FEATURES]-(property2:Property) " +
      "OPTIONAL MATCH (v)<-[:FEATURES]-(name2:Property)-[:IS_DRIVEN_BY]->(:PropertyModel{name: 'name'}) " +
      "OPTIONAL MATCH (v)<-[:FEATURES]-(fullName2:Property)-[:IS_DRIVEN_BY]->(:PropertyModel{name: 'fullName'}) " +
      "OPTIONAL MATCH (v)<-[:FEATURES]-(:Property)-[:VALUES]->(values2) " +
      "WITH *," +
      "    apoc.text.levenshteinSimilarity(apoc.text.clean(name2.value), apoc.text.clean($term)) AS nameScore2, " +
      "    apoc.text.levenshteinSimilarity(apoc.text.clean(fullName2.value), apoc.text.clean($term)) AS fullNameScore2 " +
      "WITH *, CASE WHEN nameScore2 > fullNameScore2 THEN nameScore2 ELSE fullNameScore2 END AS score2, " +
      "  collect( property2 ) + collect( name2 ) + collect( fullName2 ) AS properties2, " +
      "  collect( id(values2) ) AS selectedValues2 " +
      "WITH *, " +
      "  collect(properties1) + collect(properties2) AS allProperties, " +
      "  collect(selectedValues1) + collect(selectedValues2) AS allSelectedValues " +
      "UNWIND allProperties AS properties " +
      "UNWIND allSelectedValues AS selectedValues " +
      "WITH * "
    );
  }

  @Override
  public void buildWhereClause(
    final CustomFilter filter,
    final StringBuilder query
  ) {
    query.append(
      "WHERE ( (bt1.linked=null OR bt1.linked=false) AND ($term IS NULL OR $term = '' OR score1 > $searchCutOffScore) ) OR " +
      "      ( bt2.linked=true AND ($term IS NULL OR $term = '' OR score2 > $searchCutOffScore) ) "
    );
  }

  @Override
  public void buildReturnClause(final StringBuilder query) {
    query.append(
      "WITH *, CASE WHEN coalesce(score1, 0) > coalesce(score2, 0) THEN score1 ELSE score2 END AS score " +
      "WITH score, collect(w)+collect(v) AS workpackList " +
      "UNWIND workpackList AS workpacks " +
      "RETURN workpacks, [ " +
      "    [ (workpacks)<-[f:FEATURES]-(p:Property)-[d:IS_DRIVEN_BY]->(pm:PropertyModel) | [f, p, d, pm] ], " +
      "    [ (workpacks)-[iib:IS_INSTANCE_BY]->(m1:WorkpackModel) | [iib, m1] ], " +
      "    [ (m1)<-[f2:FEATURES]-(pm2:PropertyModel) | [f2, pm2] ], " +
      "    [ (workpacks)-[ilt:IS_LINKED_TO]->(m2:WorkpackModel) | [ilt, m2] ], " +
      "    [ (m2)<-[f3:FEATURES]-(pm3:PropertyModel) | [f3, pm3] ], " +
      "    [ (workpacks)-[bt:BELONGS_TO]->(pn:Plan) | [bt,pn] ], " +
      "    [ (workpacks)<-[ii:IS_IN]->(z:Workpack) | [ii, z] ], " +
      "    [ (workpacks)-[isw:IS_SHARED_WITH]->(o:Office) | [isw, o] ] " +
      "] "
    );
  }

  @Override
  protected String buildCustomFilterRule(
    final Rules rule,
    final String label
  ) {
    return this.buildFilterRuleForWorkpack(
      rule,
      label
    );
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
    if (StringUtils.isEmpty(term)) {
      query.append("\n")
        .append("ORDER BY score DESC");
      return;
    }
    this.buildOrderingAndDirectionClauseForWorkpack(
      filter,
      query
    );
  }

  @Override
  public String[] getDefinedExternalParams() {
    return new String[]{
      "idPlan",
      "idWorkpackModel",
      "idWorkpackParent",
      "term",
      "searchCutOffScore"
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
