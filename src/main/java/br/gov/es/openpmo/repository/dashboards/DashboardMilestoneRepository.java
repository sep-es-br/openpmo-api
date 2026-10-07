package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.MilestoneDateDto;
import br.gov.es.openpmo.model.workpacks.Milestone;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardMilestoneRepository extends Neo4jRepository<Milestone, Long> {

  @Query("OPTIONAL MATCH (plan:Plan)<-[:BELONGS_TO]-(milestoneByPlan:Milestone{deleted:false,canceled:false}) " +
      "WHERE id(plan)=$planId " +
      "OPTIONAL MATCH (root:Workpack{deleted:false,canceled:false})-[:BELONGS_TO]->(workpackPlan:Plan) " +
      "WHERE id(root)=$workpackId " +
      "OPTIONAL MATCH (root)<-[:IS_IN*]-(milestoneByWorkpack:Milestone{deleted:false,canceled:false}) " +
      "WITH coalesce(milestoneByPlan, milestoneByWorkpack) AS m, coalesce(plan, workpackPlan) AS scopePlan " +
      "WHERE m IS NOT NULL " +
      "MATCH (m)<-[:IS_SNAPSHOT_OF]-(s:Milestone{deleted:false,canceled:false})-[:COMPOSES]->(b:Baseline) " +
      "WHERE CASE WHEN $baselineId IS NULL THEN b.active ELSE id(b)=$baselineId END " +
      "AND ($baselineId IS NOT NULL OR (left(s.date,10) >= left(scopePlan.start,10) AND left(s.date,10) <= left(scopePlan.finish,10))) " +
      "RETURN m.completed AS completed, m.date AS milestoneDate, s.date AS snapshotDate")
  List<MilestoneDateDto> findForDashboard(Long planId, Long workpackId, Long baselineId);

  @Query("MATCH (root:Program)<-[:IS_IN*0..]-(program:Program)-[:INCLUDES]->(included:Workpack) " +
      "MATCH (included)<-[:IS_IN*0..]-(m:Milestone{deleted:false,canceled:false})-[:BELONGS_TO]->(plan:Plan) " +
      "WHERE id(root)=$workpackId " +
      "WITH DISTINCT m, plan " +
      "MATCH (m)<-[:IS_SNAPSHOT_OF]-(s:Milestone{deleted:false,canceled:false})-[:COMPOSES]->(b:Baseline{active:true}) " +
      "WHERE left(s.date,10) >= left(plan.start,10) AND left(s.date,10) <= left(plan.finish,10) " +
      "RETURN DISTINCT id(m) AS idWorkpack, m.completed AS completed, m.date AS milestoneDate, s.date AS snapshotDate")
  List<MilestoneDateDto> findForTransversalDashboard(Long workpackId);


  @Query("MATCH (w:Workpack{deleted:false, canceled:false})<-[:IS_IN*]-(m:Milestone{deleted:false , canceled:false})-[:BELONGS_TO]->(plan:Plan) " +
      "WHERE id(w) IN $workpackId " +
      "AND id(plan) = $idPlan " +
      "OPTIONAL MATCH (m)<-[:IS_SNAPSHOT_OF]-(s:Milestone{deleted:false , canceled:false }) " +
      "OPTIONAL MATCH (s)-[:COMPOSES]->(b:Baseline{active: true }) " +
      "RETURN id(w) AS idWorkpack, m.completed AS completed, m.date AS milestoneDate, s.date AS snapshotDate")
  List<MilestoneDateDto> findByParentIds(List<Long> workpackId, Long idPlan);

  @Query("MATCH (w:Workpack{deleted:false, canceled:false}) " +
      "WHERE id(w) IN $workpackId " +
      "OPTIONAL MATCH (w)<-[:IS_SNAPSHOT_OF]-(s:Milestone{deleted:false , canceled:false })-[:COMPOSES]->(b:Baseline{active: true }) " +
      "RETURN id(w) AS idWorkpack, w.completed AS completed, w.date AS milestoneDate, s.date AS snapshotDate")
  List<MilestoneDateDto> findByIds(List<Long> workpackId);

  @Query("match (m:Milestone{deleted:false,canceled:false})-[:IS_IN*]->(w:Workpack{deleted:false,canceled:false})" +
         "-[:IS_BASELINED_BY]->(b:Baseline{active: true}) " +
         "where id(m)=$milestoneId " +
         "return id(b) " +
         "limit 1")
  Long findBaselineIdByMilestoneId(Long milestoneId);


}
