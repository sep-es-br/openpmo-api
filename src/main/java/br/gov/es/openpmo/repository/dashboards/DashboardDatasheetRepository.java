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

  @Query("MATCH p=(current:Workpack{deleted:false,canceled:false})<-[:IS_IN*]-(child:Workpack{deleted:false,canceled:false})\n"
      +
      "WHERE id(current)=$workpackId\n" +
      "MATCH (current)-[:IS_INSTANCE_BY|IS_LINKED_TO]->(n:WorkpackModel)\n" +
      "WHERE id(n)=$workpackModelId\n" +
      "MATCH (child)-[:IS_INSTANCE_BY|IS_LINKED_TO]->(m:WorkpackModel)-[:IS_IN*]->(n)\n" +
      "MATCH (child)-[:BELONGS_TO]->(pl:Plan)\n" +
      "OPTIONAL MATCH (child)<-[:IS_SNAPSHOT_OF]-(b_mc:Milestone{deleted:false , canceled:false})-[:COMPOSES]->(bl:Baseline {active: TRUE})\n"
      +
      "WITH id(m) AS idWorkpackModel,\n" +
      "     child,\n" +
      "     m.modelName AS singularName,\n" +
      "     m.modelNameInPlural AS pluralName,\n" +
      "     m.fontIcon AS icon,\n" +
      "     p,\n" +
      "     pl,\n" +
      "     b_mc IS NOT NULL AS baselined,\n" +
      "     left(b_mc.date, 10) AS baselineDate,\n" +
      "     left(child.date, 10) AS actualDate\n" +
      "WHERE CASE \n" +
      "        WHEN ('Milestone' IN labels(child)) THEN (\n" +
      "          (baselined AND baselineDate >= pl.start AND baselineDate <= pl.finish) \n" +
      "        )\n" +
      "        WHEN ('Deliverable' IN labels(child)) THEN (\n" +
      "          EXISTS((child)<-[:IS_SNAPSHOT_OF]-()-[]-(:Baseline{active: true}))\n" +
      "        ) ELSE true END\n" +
      "RETURN idWorkpackModel,\n" +
      "        count(DISTINCT child) AS quantity,\n" +
      "       singularName,\n" +
      "       pluralName,\n" +
      "       icon,\n" +
      "       length(p) AS level")
  List<WorkpackByModelQueryResult> workpackByModel(Long workpackId, Long workpackModelId);

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
