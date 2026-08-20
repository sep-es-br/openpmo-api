package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetStakeholderQueryResult;
import br.gov.es.openpmo.dto.dashboards.datasheet.WorkpackByModelQueryResult;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardDatasheetRepository extends Neo4jRepository<Workpack, Long> {

    @Query("MATCH p=(current:Workpack{deleted:false,canceled:false})<-[:IS_IN*]-(child:Workpack{deleted:false,canceled:false})\n" +
       "WHERE id(current)=$workpackId\n" +
       "MATCH (current)-[:IS_INSTANCE_BY|IS_LINKED_TO]->(n:WorkpackModel)\n" +
       "WHERE id(n)=$workpackModelId\n" +
       "MATCH (child)-[:IS_INSTANCE_BY|IS_LINKED_TO]->(m:WorkpackModel)-[:IS_IN*]->(n)\n" +
       "MATCH (child)-[:BELONGS_TO]->(pl:Plan)\n" +

       "// baseline direta do próprio child\n" +
       "OPTIONAL MATCH (child)<-[:IS_SNAPSHOT_OF]-(b_mc:Milestone{deleted:false,canceled:false})-[:COMPOSES]->(bl:Baseline{active:true})\n" +

       "// procura descendentes com baseline válida\n" +
       "OPTIONAL MATCH (child)<-[:IS_IN*]-(desc:Workpack{deleted:false,canceled:false})\n" +
       "OPTIONAL MATCH (desc)<-[:IS_SNAPSHOT_OF]-(s)-[:COMPOSES]->(d_bl:Baseline{active:true})\n" +
       "WHERE (s:Milestone OR s:Deliverable)\n" +
       "  AND s.deleted=false\n" +
       "  AND s.canceled=false\n" +

       "WITH id(m) AS idWorkpackModel,\n" +
       "     child,\n" +
       "     m.modelName AS singularName,\n" +
       "     m.modelNameInPlural AS pluralName,\n" +
       "     m.fontIcon AS icon,\n" +
       "     p,\n" +
       "     pl,\n" +
       "     b_mc IS NOT NULL AS baselined,\n" +
       "     left(b_mc.date,10) AS baselineDate,\n" +
       "     COUNT(DISTINCT d_bl) > 0 AS hasDescendantBaseline\n" +

       "WHERE CASE\n" +
       "        WHEN ('Milestone' IN labels(child)) THEN (\n" +
       "          baselined AND baselineDate >= pl.start AND baselineDate <= pl.finish\n" +
       "        )\n" +
       "        WHEN ('Deliverable' IN labels(child)) THEN (\n" +
       "          EXISTS((child)<-[:IS_SNAPSHOT_OF]-()-[]-(:Baseline{active:true}))\n" +
       "        )\n" +
       "        ELSE hasDescendantBaseline\n" +
       "      END\n" +

       "RETURN idWorkpackModel,\n" +
       "       count(DISTINCT child) AS quantity,\n" +
       "       singularName,\n" +
       "       pluralName,\n" +
       "       icon,\n" +
       "       length(p) AS level")
List<WorkpackByModelQueryResult> workpackByModel(Long workpackId, Long workpackModelId);

  @Query("MATCH (plan:Plan)<-[:BELONGS_TO]-(workpack:Workpack{deleted:false,canceled:false}) " +
      "WHERE id(plan)=$planId AND (workpack.category <> 'SNAPSHOT' OR workpack.category IS NULL) " +
      "MATCH (workpack)-[:IS_INSTANCE_BY|IS_LINKED_TO]->(model:WorkpackModel) " +
      "OPTIONAL MATCH hierarchy=(workpack)-[:IS_IN*]->(:Workpack{deleted:false,canceled:false}) " +
      "WITH plan, workpack, model, coalesce(max(length(hierarchy)),0) AS hierarchyLevel " +
      "OPTIONAL MATCH (workpack)<-[:IS_SNAPSHOT_OF]-(milestoneSnapshot:Milestone{deleted:false,canceled:false})" +
      "-[:COMPOSES]->(directBaseline:Baseline{active:true}) " +
      "OPTIONAL MATCH (workpack)<-[:IS_IN*]-(descendant:Workpack{deleted:false,canceled:false}) " +
      "OPTIONAL MATCH (descendant)<-[:IS_SNAPSHOT_OF]-(snapshot)-[:COMPOSES]->(descendantBaseline:Baseline{active:true}) " +
      "WHERE (snapshot:Milestone OR snapshot:Deliverable) AND snapshot.deleted=false AND snapshot.canceled=false " +
      "WITH plan, workpack, model, hierarchyLevel, milestoneSnapshot, directBaseline, " +
      "count(DISTINCT descendantBaseline) > 0 AS hasDescendantBaseline " +
      "WHERE CASE " +
      "WHEN 'Milestone' IN labels(workpack) THEN directBaseline IS NOT NULL " +
      "AND left(milestoneSnapshot.date,10) >= left(plan.start,10) " +
      "AND left(milestoneSnapshot.date,10) <= left(plan.finish,10) " +
      "WHEN 'Deliverable' IN labels(workpack) THEN " +
      "EXISTS((workpack)<-[:IS_SNAPSHOT_OF]-()-[:COMPOSES]->(:Baseline{active:true})) " +
      "ELSE hasDescendantBaseline END " +
      "WITH plan, model.modelName AS singularName, model.modelNameInPlural AS pluralName, " +
      "model.fontIcon AS icon, collect(DISTINCT workpack) AS workpacks, collect(DISTINCT id(model)) AS modelIds, " +
      "min(hierarchyLevel) AS level, min(coalesce(model.position,0)) AS position " +
      "RETURN head(modelIds) AS idWorkpackModel, id(plan) AS idPlan, size(workpacks) AS quantity, " +
      "singularName, pluralName, icon, level, position " +
      "ORDER BY level ASC, position ASC, pluralName ASC")
  List<WorkpackByModelQueryResult> workpackByModelForPlan(Long planId);

  @Query("MATCH (a:Actor)-[s:IS_STAKEHOLDER_IN{active: true }]->(w:Workpack{deleted: false , canceled: false })\n" +
      "WHERE id(w)=$workpackId AND (a)-[s]->(w)\n" +
      "OPTIONAL MATCH (w)-[:IS_IN*]->(v:Workpack{deleted: false , canceled: false })\n" +
      "OPTIONAL MATCH (wm:WorkpackModel)<-[:IS_INSTANCE_BY]-(w)\n" +
      "OPTIONAL MATCH (a)<-[:IS_A_PORTRAIT_OF]-(file:File)\n" +
      "WITH * ORDER BY (s.role IN wm.organizationRoles), CASE WHEN wm.dashboardShowStakeholders IS NOT null THEN [i IN range(0, size(wm.dashboardShowStakeholders)-1)\n"
      +
      "WHERE toLower(wm.dashboardShowStakeholders[i]) = toLower(s.role)][0] ELSE 0 END, a.name\n" +
      "WHERE ( any(role IN wm.dashboardShowStakeholders\n" +
      "WHERE toLower(role) = toLower(s.role)) AND (s.from IS null OR date(s.from) <= date()) AND (s.to IS null OR date(s.to) >= date()) )\n"
      +
      "RETURN DISTINCT id(a) AS id, a.name AS name, a.fullName AS fullName, s.role AS role, file, 'Organization' IN labels(a) AS organization")
  List<DatasheetStakeholderQueryResult> stakeholders(Long workpackId);

}
