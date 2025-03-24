package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.MilestoneDateDto;
import br.gov.es.openpmo.model.workpacks.Milestone;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardMilestoneRepository extends Neo4jRepository<Milestone, Long> {

  @Query("MATCH (w:Workpack{deleted:false, canceled:false})<-[:IS_IN*]-(m:Milestone{deleted:false , canceled:false}) " +
          "WHERE id(w)=$workpackId " +
          "OPTIONAL MATCH (m)<-[:IS_SNAPSHOT_OF]-(s:Milestone{deleted:false , canceled:false })-[:COMPOSES]->(b:Baseline{active: true }) " +
          "RETURN m.completed AS completed, m.date AS milestoneDate, s.date AS snapshotDate")
  List<MilestoneDateDto> findByParentId(Long workpackId);


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

  @Query("MATCH (w:Workpack{deleted:false, canceled:false})<-[:IS_IN*]-(m:Milestone {deleted:false , canceled:false }) " +
          "WHERE id(w)=$workpackId " +
          "MATCH (b:Baseline) " +
          "WHERE id(b)=$baselineId " +
          "MATCH (m)<-[:IS_SNAPSHOT_OF]-(s:Milestone{deleted:false ,canceled:false }) " +
          "RETURN m.completed AS completed, m.date AS milestoneDate, s.date AS snapshotDate")
  List<MilestoneDateDto> findByParentAndBaselineId(Long workpackId, Long baselineId);

  @Query("match (m:Milestone{deleted:false,canceled:false})-[:IS_IN*]->(w:Workpack{deleted:false,canceled:false})" +
         "-[:IS_BASELINED_BY]->(b:Baseline{active: true}) " +
         "where id(m)=$milestoneId " +
         "return id(b) " +
         "limit 1")
  Long findBaselineIdByMilestoneId(Long milestoneId);


}
