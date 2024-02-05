package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.MilestoneDateDto;
import br.gov.es.openpmo.model.workpacks.Milestone;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardMilestoneRepository extends Neo4jRepository<Milestone, Long> {

  @Query("match (w:Workpack)<-[i:IS_IN*]-(m:Milestone)<-[f:FEATURES]-(d:Date) " +
    "optional match (w)-[ii:IS_IN*0..]-(:Project{deleted:false,canceled:false})-[:IS_BASELINED_BY]->(b:Baseline) " +
    "with * " +
    "where id(w)=$workpackId and ($baselineId is null or id(b)=$baselineId) " +
    "and w.deleted=false and w.canceled=false " +
    "and m.deleted=false and m.canceled=false " +
    "and b.active=true " +
    "return m, f, d, [ " +
    "    [(m)<-[iso:IS_SNAPSHOT_OF]-(ms:Milestone)-[c:COMPOSES]->(b) | [iso,ms,c]], " +
    "    [(d)<-[iso2:IS_SNAPSHOT_OF]-(d3:Date)-[f3:FEATURES]->(ms2:Milestone)-[c2:COMPOSES]->(b) | [iso2,d3,f3,ms2,c2]] " +
    "]")
  List<Milestone> findByParentId(Long workpackId, Long baselineId);

  @Query("MATCH (w:Workpack{deleted:false, canceled:false})<-[:IS_IN*]-(m:Milestone{deleted:false , canceled:false}) " +
          "WHERE id(w)=$workpackId " +
          "OPTIONAL MATCH (m)<-[:IS_SNAPSHOT_OF]-(s:Milestone{deleted:false , canceled:false }) " +
          "OPTIONAL MATCH (s)-[:COMPOSES]->(b:Baseline{active: true }) " +
          "RETURN m.completed AS completed, m.date AS milestoneDate, s.date AS snapshotDate")
  List<MilestoneDateDto> findByParentId(Long workpackId);


  @Query("MATCH (w:Workpack{deleted:false, canceled:false})<-[:IS_IN*]-(m:Milestone{deleted:false , canceled:false}) " +
      "WHERE id(w) IN $workpackId " +
      "OPTIONAL MATCH (m)<-[:IS_SNAPSHOT_OF]-(s:Milestone{deleted:false , canceled:false }) " +
      "OPTIONAL MATCH (s)-[:COMPOSES]->(b:Baseline{active: true }) " +
      "RETURN id(w) AS idWorkpack, m.completed AS completed, m.date AS milestoneDate, s.date AS snapshotDate")
  List<MilestoneDateDto> findByParentIds(List<Long> workpackId);

  @Query("MATCH (w:Workpack{deleted:false, canceled:false}) " +
      "WHERE id(w) IN $workpackId " +
      "OPTIONAL MATCH (w)<-[:IS_SNAPSHOT_OF]-(s:Milestone{deleted:false , canceled:false }) " +
      "OPTIONAL MATCH (s)-[:COMPOSES]->(b:Baseline{active: true }) " +
      "RETURN id(w) AS idWorkpack, w.completed AS completed, w.date AS milestoneDate, s.date AS snapshotDate")
  List<MilestoneDateDto> findByIds(List<Long> workpackId);

  @Query("MATCH (w:Workpack{deleted:false, canceled:false})<-[:IS_IN*]-(m:Milestone {deleted:false , canceled:false }) " +
          "WHERE id(w)=$workpackId " +
          "MATCH (b:Baseline) " +
          "WHERE id(b)=$baselineId " +
          "MATCH (m)<-[:IS_SNAPSHOT_OF]-(s:Milestone{deleted:false ,canceled:false }) " +
          "RETURN m.completed AS completed, m.date AS milestoneDate, s.date AS snapshotDate")
  List<MilestoneDateDto> findByParentAndBaselineId(Long workpackId, Long baselineId);

  @Query("match (m:Milestone{deleted:false,canceled:false})-[:IS_IN]->(w:Workpack{deleted:false,canceled:false}) " +
         "where id(m)=$milestoneId " +
         "return id(w)")
  Long findParentIdByMilestoneId(Long milestoneId);

  @Query("match (m:Milestone{deleted:false,canceled:false})-[:IS_IN*]->(w:Workpack{deleted:false,canceled:false})" +
         "-[:IS_BASELINED_BY]->(b:Baseline{active: true}) " +
         "where id(m)=$milestoneId " +
         "return id(w) " +
         "limit 1")
  Long findWorkpackIdByMilestoneId(Long milestoneId);

  @Query("match (m:Milestone{deleted:false,canceled:false})-[:IS_IN*]->(w:Workpack{deleted:false,canceled:false})" +
         "-[:IS_BASELINED_BY]->(b:Baseline{active: true}) " +
         "where id(m)=$milestoneId " +
         "return id(b) " +
         "limit 1")
  Long findBaselineIdByMilestoneId(Long milestoneId);


}
