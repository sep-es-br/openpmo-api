package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetStakeholderQueryResult;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardDatasheetRepository extends Neo4jRepository<Workpack, Long> {

  @Query("match (a:Actor)-[s:IS_STAKEHOLDER_IN]->(w:Workpack) " +
      "optional match (w)-[:IS_IN*]->(v:Workpack) " +
      "optional match (a)<-[:IS_A_PORTRAIT_OF]-(file:File) " +
      "with a,s,w,v,file " +
      "where " +
      "    ( " +
      "        (a)-[s]->(w) and id(w)=$workpackId " +
      "    ) " +
      "    or " +
      "    ( " +
      "        (a)-[s]->(w)-[:IS_IN*]->(v:Workpack) and id(v)=$workpackId " +
      "    ) " +
      "return id(a) as id, a.name as name, a.fullName as fullName, s.role as role, file")
  List<DatasheetStakeholderQueryResult> stakeholders(Long workpackId);

  @Query("match (p:Project)-[:IS_IN*]->(w:Workpack) " +
      "where id(w)=$workpackId " +
      "return count(distinct p)")
  Long quantityOfProjects(Long workpackId);

  @Query("match (d:Deliverable)-[:IS_IN*]->(w:Workpack) " +
      "where id(w)=$workpackId " +
      "return count(distinct d)")
  Long quantityOfDeliverables(Long workpackId);

  @Query("match (m:Milestone)-[:IS_IN*]->(w:Workpack) " +
      "where id(w)=$workpackId " +
      "return count(distinct m)")
  Long quantityOfMilestones(Long workpackId);

}
