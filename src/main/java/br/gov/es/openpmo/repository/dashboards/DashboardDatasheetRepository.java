package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetStakeholderQueryResult;
import br.gov.es.openpmo.dto.dashboards.datasheet.WorkpackByModelQueryResult;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DashboardDatasheetRepository extends Neo4jRepository<Workpack, Long> {

  @Query("MATCH (w:Workpack{deleted:false})<-[:IS_IN*]-(v:Workpack{deleted:false,canceled:false})-[:IS_INSTANCE_BY]->" +
         "(m:WorkpackModel) " +
         "WHERE id(w)=$workpackId AND NOT (v)-[:IS_IN*..0]->(:Workpack{canceled:false})-[:IS_IN*..0]->(w) " +
         "WITH v, collect(DISTINCT m) AS modelList " +
         "WITH v, collect([n IN modelList WHERE id(n)=v.idWorkpackModel][0]) AS modelGroup " +
         "UNWIND modelGroup AS models " +
         "RETURN id(models), " +
         "    count(v) AS quantity, " +
         "    models.modelName AS singularName, " +
         "    models.modelNameInPlural AS pluralName, " +
         "    models.fontIcon AS icon")
  List<WorkpackByModelQueryResult> workpackByModel(Long workpackId);

  @Query("MATCH (a:Actor)-[s:IS_STAKEHOLDER_IN]->(w:Workpack{deleted:false,canceled:false}) " +
         "OPTIONAL MATCH (w)-[:IS_IN*]->(v:Workpack{deleted:false,canceled:false}) " +
         "OPTIONAL MATCH (wm:WorkpackModel)<-[:IS_INSTANCE_BY]-(w) " +
         "OPTIONAL MATCH (a)<-[:IS_A_PORTRAIT_OF]-(file:File) " +
         "WITH * " +
         "ORDER BY (s.role IN wm.organizationRoles), s.role, a.name " +
         "WHERE " +
         "    ( " +
         "        (a)-[s]->(w) AND id(w)=$workpackId " +
         "    ) " +
         "RETURN " +
         "    DISTINCT id(a) AS id, " +
         "    a.name AS name, " +
         "    a.fullName AS fullName, " +
         "    s.role AS role, " +
         "    file, " +
         "    'Organization' IN labels(a) AS organization")
  List<DatasheetStakeholderQueryResult> stakeholders(Long workpackId);

}
