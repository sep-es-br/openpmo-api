package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardBaseline;
import br.gov.es.openpmo.dto.dashboards.DashboardWorkpackDetailDto;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStepDto;
import br.gov.es.openpmo.model.dashboards.Dashboard;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DashboardRepository extends Neo4jRepository<Dashboard, Long> {

  @Query("MATCH (dashboard:Dashboard)-[blt:BELONGS_TO]->(workpack:Workpack{deleted:false,canceled:false}) " +
      ",(dashboard)<-[isPartOf:IS_PART_OF]-(month:DashboardMonth) " +
      "WHERE id(workpack) IN $ids AND month.date <  $date " +
      "RETURN dashboard, blt , workpack, isPartOf, month, [" +
      "  [ (dashboard)<-[:IS_PART_OF]-(:DashboardMonth)<-[isAt:IS_AT]-(entities) | [ isAt, entities] ] " +
      "] "
  )
  List<Dashboard> findAllByWorkpackId(List<Long> ids, LocalDate date);

  @Query("MATCH (dashboard:Dashboard)-[:BELONGS_TO]->(workpack:Workpack{deleted:false,canceled:false}) " +
         "WHERE id(workpack)=$workpackId " +
         "RETURN dashboard, [" +
         "  [ (dashboard)<-[isPartOf:IS_PART_OF]-(month:DashboardMonth) | [isPartOf, month] ], " +
         "  [ (dashboard)<-[:IS_PART_OF]-(:DashboardMonth)<-[isAt:IS_AT]-(entities) | [ isAt, entities] ] " +
         "] "
  )
  Optional<Dashboard> findByWorkpackId(Long workpackId);

  @Query("MATCH (dashboard:Dashboard)-[:BELONGS_TO]->(workpack:Workpack{deleted:false,canceled:false}) " +
         "WHERE id(workpack)=$workpackId " +
         "RETURN dashboard, [" +
         "  [ (dashboard)<-[isPartOf:IS_PART_OF]-(month:DashboardMonth) | [isPartOf, month] ] " +
         "]")
  Optional<Dashboard> findByWorkpackIdForInterval(Long workpackId);

  @Query("MATCH (d:Dashboard)<-[:IS_PART_OF]-(dm:DashboardMonth)<-[:IS_AT]-(n) " +
         "DETACH DELETE d, dm, n")
  void purgeAllDashboards();


  @Query(
      "MATCH (w:Deliverable{deleted:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step) " +
      "WHERE (w.category <> 'SNAPSHOT' OR w.category IS NULL) " +
      "AND ($workpackIds IS NULL OR ID(w) IN $workpackIds) " +
      "RETURN ID(w) AS idWorkpack, s. start AS start, s.end AS end, toString(SUM(toFloat(st.plannedWork))) AS foreseenWork "
  )
  List<DashboardWorkpackDetailDto> findAllScheduleAndStep(List<Long> workpackIds);

  @Query(
      "MATCH (master:Deliverable)<-[:IS_SNAPSHOT_OF]-(w:Deliverable{deleted:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step) " +
      ",(st)-[:COMPOSES]->(b:Baseline) " +
      "WHERE ID(b) IN $ids " +
      "AND ($workpackIds IS NULL OR ID(master) IN $workpackIds) " +
      "RETURN ID(master) AS idWorkpack, s.start AS baselineStart, s.end AS baselineEnd,  toString(SUM(toFloat(st.plannedWork))) AS plannedWork "
  )
  List<DashboardWorkpackDetailDto> findAllScheduleAndStepBaseline(List<Long> ids, List<Long> workpackIds);

  @Query(
      "MATCH (w:Deliverable{deleted:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step)-[co:CONSUMES]->(ca:CostAccount) " +
      "WHERE (w.category <> 'SNAPSHOT' OR w.category IS NULL) " +
      "AND ($workpackIds IS NULL OR ID(w) IN $workpackIds) " +
      "RETURN ID(w) AS idWorkpack, toString(SUM(toFloat(co.plannedCost))) AS foreseenCost "
  )
  List<DashboardWorkpackDetailDto> findAllCost(List<Long> workpackIds);

  @Query(
      "MATCH (master:Deliverable)<-[:IS_SNAPSHOT_OF]-(w:Deliverable{deleted:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step)-[co:CONSUMES]->(ca:CostAccount) " +
          ",(s)-[:COMPOSES]->(b:Baseline) " +
          "WHERE ID(b) IN $ids " +
          "AND ($workpackIds IS NULL OR ID(master) IN $workpackIds) " +
          "RETURN ID(master) AS idWorkpack, toString(SUM(toFloat(co.plannedCost))) AS plannedCost "
  )
  List<DashboardWorkpackDetailDto> findAllCostBaseline(List<Long> ids, List<Long> workpackIds);


  @Query(
      "MATCH (w:Workpack{deleted:false})-[:IS_BASELINED_BY]->(b:Baseline) WHERE b.status IN ['APPROVED', 'PROPOSED'] " +
      "RETURN ID(w) AS idWorkpack, ID(b) AS idBaseline, b.active AS active, b.proposalDate AS proposalDate, b.status AS status"
  )
  List<DashboardBaseline> findAllBaseline();


  @Query(
      "MATCH (m:Milestone{deleted:false}) " +
          "WHERE (m.category <> 'SNAPSHOT' OR m.category IS NULL) AND m.date IS NOT NULL " +
          "RETURN ID(m) AS idWorkpack, left(m.date, 10) AS start, left(m.date, 10) AS end "
  )
  List<DashboardWorkpackDetailDto> findAllMilestoneMaster();


  @Query(
      "MATCH (master:Milestone)<-[:IS_SNAPSHOT_OF]-(m:Milestone{deleted:false})-[:COMPOSES]->(b:Baseline) " +
      "WHERE ID(b) IN $ids AND m.date IS NOT NULL " +
      "RETURN ID(master) AS idWorkpack,  left(m.date, 10) AS start, left(m.date, 10) AS end "
  )
  List<DashboardWorkpackDetailDto> findAllMilestoneBaseline(List<Long> ids);

  @Query(
      "MATCH (w:Deliverable{deleted:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step) " +
      "WHERE (w.category <> 'SNAPSHOT' OR w.category IS NULL) AND st.periodFromStart IS NOT NULL " +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart}))  <= date.truncate('month', date()) " +
      "AND ($workpackIds IS NULL OR ID(w) IN $workpackIds) " +
      "AND ($yearMonth IS NULL OR (st.periodFromStart IS NOT NULL AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date($yearMonth))) " +
      "RETURN ID(w) AS idWorkpack, toString(SUM(toFloat(st.actualWork))) AS actualWork, toString(SUM(toFloat(st.plannedWork))) AS foreseenWorkRefMonth "
  )
  List<DashboardWorkpackDetailDto> findAllActualWork(List<Long> workpackIds, LocalDate yearMonth);


  @Query(
      "MATCH (w:Deliverable{deleted:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step)-[co:CONSUMES]-(c:CostAccount) " +
      "WHERE (w.category <> 'SNAPSHOT' OR w.category IS NULL) AND st.periodFromStart IS NOT NULL " +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart}))  <= date.truncate('month', date()) " +
      "AND ($workpackIds IS NULL OR ID(w) IN $workpackIds ) " +
      "AND ($yearMonth IS NULL OR (st.periodFromStart IS NOT NULL AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date($yearMonth))) " +
      "RETURN ID(w) AS idWorkpack, toString(SUM(toFloat(co.actualCost))) AS actualCost "
  )
  List<DashboardWorkpackDetailDto> findAllActualCost(List<Long> workpackIds, LocalDate yearMonth);


  @Query (
      "MATCH (w:Deliverable{deleted:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step) " +
          "WHERE (w.category <> 'SNAPSHOT' OR w.category IS NULL) AND st.periodFromStart IS NOT NULL  " +
          "AND ($workpackIds IS NULL OR ID(w) IN $workpackIds) " +
          "RETURN DISTINCT toString(date.truncate('month', date(s.start) + Duration({months: st.periodFromStart}))) AS date " +
          "ORDER BY date "
  )
  List<EarnedValueByStepDto> findAllEarnedValues(List<Long> workpackIds);

  @Query (
      "MATCH (w:Deliverable{deleted:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step)-[co:CONSUMES]->(ca:CostAccount) " +
      "WHERE (w.category <> 'SNAPSHOT' OR w.category IS NULL) AND st.periodFromStart IS NOT NULL  " +
      "AND ($workpackIds IS NULL OR ID(w) IN $workpackIds) " +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart}))  <= date.truncate('month', date()) " +
      "AND ($yearMonth IS NULL OR (st.periodFromStart IS NOT NULL AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date($yearMonth))) " +
      "RETURN toString(date.truncate('month', date(s.start) + Duration({months: st.periodFromStart}))) AS date " +
      ", toString(SUM(toFloat(co.actualCost))) AS actualCost " +
      "ORDER BY date "
  )
  List<EarnedValueByStepDto> findAllEarnedValueActualCost(List<Long> workpackIds, LocalDate yearMonth);

  @Query (
      "MATCH (w:Deliverable{deleted:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step) " +
      "WHERE (w.category <> 'SNAPSHOT' OR w.category IS NULL) AND st.periodFromStart IS NOT NULL  " +
      "AND ($workpackIds IS NULL OR ID(w) IN $workpackIds) " +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart}))  <= date.truncate('month', date()) " +
      "AND ($yearMonth IS NULL OR (st.periodFromStart IS NOT NULL AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date($yearMonth))) " +
      "RETURN toString(date.truncate('month', date(s.start) + Duration({months: st.periodFromStart}))) AS date " +
      ", toString(SUM(toFloat(st.actualWork))) AS actualWork " +
      "ORDER BY date "
  )
  List<EarnedValueByStepDto> findAllEarnedValueActualWork(List<Long> workpackIds, LocalDate yearMonth);

  @Query (
      "MATCH (w:Deliverable{deleted:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step)-[co:CONSUMES]->(ca:CostAccount) " +
      "WHERE (w.category <> 'SNAPSHOT' OR w.category IS NULL) AND st.periodFromStart IS NOT NULL  " +
      "AND ($workpackIds IS NULL OR ID(w) IN $workpackIds) " +
      "RETURN toString(date.truncate('month', date(s.start) + Duration({months: st.periodFromStart}))) AS date " +
      ", toString(SUM(toFloat(co.plannedCost))) AS estimatedCost " +
      "ORDER BY date "
  )
  List<EarnedValueByStepDto> findAllEarnedValueEstimatedCost(List<Long> workpackIds);

  @Query (
      "MATCH (master:Deliverable)<-[:IS_SNAPSHOT_OF]-(w:Deliverable{deleted:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step)-[co:CONSUMES]->(ca:CostAccount) " +
      ",(s)-[:COMPOSES]->(b:Baseline) " +
      "WHERE ID(b) IN $baselineIds " +
      "AND st.periodFromStart IS NOT NULL  " +
      "AND ($workpackIds IS NULL OR ID(master) IN $workpackIds) " +
      "RETURN toString(date.truncate('month', date(s.start) + Duration({months: st.periodFromStart}))) AS date " +
      ", toString(SUM(toFloat(co.plannedCost))) AS plannedCost " +
      "ORDER BY date "
  )
  List<EarnedValueByStepDto> findAllEarnedValuePlannedCost(List<Long> baselineIds, List<Long> workpackIds);

  @Query (
      "MATCH (master:Deliverable)<-[:IS_SNAPSHOT_OF]-(w:Deliverable{deleted:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step) " +
      ",(s)-[:COMPOSES]->(b:Baseline) " +
      "WHERE ID(b) IN $baselineIds " +
      "AND st.periodFromStart IS NOT NULL " +
      "AND ($workpackIds IS NULL OR ID(master) IN $workpackIds) " +
      "RETURN toString(date.truncate('month', date(s.start) + Duration({months: st.periodFromStart}))) AS date " +
      ", toString(SUM(toFloat(st.plannedWork))) AS plannedWork " +
      "ORDER BY date "
  )
  List<EarnedValueByStepDto> findAllEarnedValuePlannedWork(List<Long> baselineIds, List<Long> workpackIds);


}
