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

@Repository
public interface DashboardRepository extends Neo4jRepository<Dashboard, Long> {

  @Query(
      "MATCH (plan:Plan)<-[:BELONGS_TO]-(w:Deliverable{deleted:false,canceled:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step) " +
      "WHERE (w.category <> 'SNAPSHOT' OR w.category IS NULL) " +
      "AND ($planId IS NULL OR ID(plan) = $planId) " +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date.truncate('month', date(plan.finish))" +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) >= date.truncate('month', date(plan.start))" +
      "AND ($workpackIds IS NULL OR ID(w) IN $workpackIds) " +
      "RETURN ID(w) AS idWorkpack, ID(plan) as idPlan, s.start AS start, s.end AS end, plan.start AS startPlan, plan.finish AS endPlan, toString(SUM(toFloat(st.plannedWork))) AS foreseenWork "
  )
  List<DashboardWorkpackDetailDto> findAllScheduleAndStep(List<Long> workpackIds, Long planId);

  @Query(
      "MATCH (plan:Plan)<-[:BELONGS_TO]-(master:Deliverable{deleted:false,canceled:false})<-[:IS_SNAPSHOT_OF]-(w:Deliverable{deleted:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step) " +
      ",(st)-[:COMPOSES]->(b:Baseline) " +
      "WHERE ID(b) IN $ids " +
      "AND ($planId IS NULL OR ID(plan) = $planId) " +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date.truncate('month', date(plan.finish))" +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) >= date.truncate('month', date(plan.start))" +
      "AND ($workpackIds IS NULL OR ID(master) IN $workpackIds) " +
      "RETURN ID(master) AS idWorkpack, ID(plan) as idPlan, s.start AS baselineStart, s.end AS baselineEnd, plan.start AS startPlan, plan.finish AS endPlan,  toString(SUM(toFloat(st.plannedWork))) AS plannedWork "
  )
  List<DashboardWorkpackDetailDto> findAllScheduleAndStepBaseline(List<Long> ids, List<Long> workpackIds, Long planId);

  @Query(
      "MATCH (plan:Plan)<-[:BELONGS_TO]-(w:Deliverable{deleted:false,canceled:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step)-[co:CONSUMES]->(ca:CostAccount) " +
      "WHERE (w.category <> 'SNAPSHOT' OR w.category IS NULL) " +
      "AND ($planId IS NULL OR ID(plan) = $planId) " +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date.truncate('month', date(plan.finish))" +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) >= date.truncate('month', date(plan.start))" +
      "AND ($workpackIds IS NULL OR ID(w) IN $workpackIds) " +
      "RETURN ID(w) AS idWorkpack, ID(plan) as idPlan, toString(SUM(toFloat(co.plannedCost))) AS foreseenCost "
  )
  List<DashboardWorkpackDetailDto> findAllCost(List<Long> workpackIds, Long planId);

  @Query(
      "MATCH (plan:Plan)<-[:BELONGS_TO]-(master:Deliverable{deleted:false,canceled:false})<-[:IS_SNAPSHOT_OF]-(w:Deliverable{deleted:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step)-[co:CONSUMES]->(ca:CostAccount) " +
          ",(s)-[:COMPOSES]->(b:Baseline) " +
          "WHERE ID(b) IN $ids " +
          "AND ($planId IS NULL OR ID(plan) = $planId) " +
          "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date.truncate('month', date(plan.finish)) " +
          "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) >= date.truncate('month', date(plan.start)) " +
          "AND ($workpackIds IS NULL OR ID(master) IN $workpackIds) " +
          "RETURN ID(master) AS idWorkpack, ID(plan) as idPlan, toString(SUM(toFloat(co.plannedCost))) AS plannedCost "
  )
  List<DashboardWorkpackDetailDto> findAllCostBaseline(List<Long> ids, List<Long> workpackIds, Long planId);

  @Query(
      "MATCH (plan:Plan)<-[:BELONGS_TO]-(master:Deliverable{deleted:false,canceled:false})<-[:IS_SNAPSHOT_OF]-(w:Deliverable{deleted:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step)-[co:CONSUMES]->(ca:CostAccount) " +
          ",(s)-[:COMPOSES]->(b:Baseline) " +
          "WHERE ID(b) IN $ids " +
          "AND ($planId IS NULL OR ID(plan) = $planId) " +
          "AND ($workpackIds IS NULL OR ID(master) IN $workpackIds) " +
          "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date.truncate('month', date(plan.finish)) " +
          "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) >= date.truncate('month', date(plan.start)) " +
          "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) < date.truncate('month', date()) " +
          "AND ($yearMonth IS NULL OR (st.periodFromStart IS NOT NULL AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date($yearMonth))) " +
          "RETURN ID(master) AS idWorkpack, ID(plan) as idPlan, toString(SUM(toFloat(co.plannedCost))) AS plannedCostRefMonth "
  )
  List<DashboardWorkpackDetailDto> findAllCostBaseline(List<Long> ids, List<Long> workpackIds, LocalDate yearMonth, Long planId);

  @Query(
          "MATCH "
                  + "(plan:Plan)<-[:BELONGS_TO]-(master:Deliverable {deleted: false, canceled: false})<-[:IS_SNAPSHOT_OF]-(w:Deliverable {deleted: false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step)-[co:CONSUMES]->(ca:CostAccount), "
                  + "(st)-[:IS_SNAPSHOT_OF]->(stm:Step), "
                  + "(s)-[:COMPOSES]->(b:Baseline) "
                  + "WHERE ID(b) IN $ids "
                  + "AND ($workpackIds IS NULL OR ID(master) IN $workpackIds) "
                  + "AND ($planId IS NULL OR ID(plan) = $planId) "
                  + "AND toFloat(st.plannedWork) > 0 "
                  + "WITH "
                  + "master, "
                  + "plan, "
                  + "sum(toFloat(co.plannedCost)) as totalPlannedCost, "
                  + "sum(toFloat(st.plannedWork)) as totalPlannedWork, "
                  + "sum(toFloat(stm.actualWork)) as totalActualWork "
                  + "RETURN "
                  + "ID(master) AS idWorkpack, "
                  + "ID(plan) as idPlan, "
                  + "toString((totalPlannedCost / totalPlannedWork) * totalActualWork) AS earnedValue ")
  List<DashboardWorkpackDetailDto> findAllEarnedValueBaseline(List<Long> ids, List<Long> workpackIds, LocalDate yearMonth, Long planId);


  @Query(
      "MATCH (w:Workpack{deleted:false,canceled:false})-[:IS_BASELINED_BY]->(b:Baseline) WHERE b.status IN ['APPROVED', 'PROPOSED'] " +
      "RETURN ID(w) AS idWorkpack, ID(b) AS idBaseline, b.active AS active, b.proposalDate AS proposalDate, b.status AS status"
  )
  List<DashboardBaseline> findAllBaseline();


  @Query(
      "MATCH (plan:Plan)<-[:BELONGS_TO]-(m:Milestone{deleted:false,canceled:false}) " +
          "WHERE (m.category <> 'SNAPSHOT' OR m.category IS NULL) AND m.date IS NOT NULL " +
          "AND ($planId IS NULL OR ID(plan) = $planId) " +
          "AND date.truncate('month', date(left(m.date, 10))) <= date.truncate('month', date(plan.finish)) " +
          "AND date.truncate('month', date(left(m.date, 10))) >= date.truncate('month', date(plan.start)) " +
          "AND ($workpackIds IS NULL OR ID(m) IN $workpackIds) " +
          "RETURN ID(m) AS idWorkpack, ID(plan) as idPlan, left(m.date, 10) AS start, left(m.date, 10) AS end, plan.start AS startPlan, plan.finish AS endPlan "
  )
  List<DashboardWorkpackDetailDto> findAllMilestoneMaster(List<Long> workpackIds, Long planId);


  @Query(
      "MATCH (plan:Plan)<-[:BELONGS_TO]-(master:Milestone{deleted:false,canceled:false})<-[:IS_SNAPSHOT_OF]-(m:Milestone{deleted:false})-[:COMPOSES]->(b:Baseline) " +
      "WHERE ID(b) IN $ids AND m.date IS NOT NULL " +
      "AND ($planId IS NULL OR ID(plan) = $planId) " +
      "AND ($workpackIds IS NULL OR ID(master) IN $workpackIds) " +
      "AND date.truncate('month', date(left(m.date, 10))) <= date.truncate('month', date(plan.finish)) " +
      "AND date.truncate('month', date(left(m.date, 10))) >= date.truncate('month', date(plan.start)) " +
      "RETURN ID(master) AS idWorkpack, ID(plan) as idPlan, left(m.date, 10) AS start, left(m.date, 10) AS end, plan.start AS startPlan, plan.finish AS endPlan "
  )
  List<DashboardWorkpackDetailDto> findAllMilestoneBaseline(List<Long> ids, List<Long> workpackIds, Long planId);

  @Query(
      "MATCH (plan:Plan)<-[:BELONGS_TO]-(w:Deliverable{deleted:false,canceled:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step) " +
      "WHERE (w.category <> 'SNAPSHOT' OR w.category IS NULL) AND st.periodFromStart IS NOT NULL " +
      "AND ($planId IS NULL OR ID(plan) = $planId) " +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date.truncate('month', date(plan.finish))" +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) >= date.truncate('month', date(plan.start))" +
      "AND ($yearMonth IS NOT NULL OR date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) < date.truncate('month', date())) " +
      "AND ($workpackIds IS NULL OR ID(w) IN $workpackIds) " +
      "AND ($yearMonth IS NULL OR (st.periodFromStart IS NOT NULL AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date($yearMonth))) " +
      "RETURN ID(w) AS idWorkpack, ID(plan) as idPlan, toString(SUM(toFloat(st.actualWork))) AS actualWork, toString(SUM(toFloat(st.plannedWork))) AS foreseenWorkRefMonth "
  )
  List<DashboardWorkpackDetailDto> findAllActualWork(List<Long> workpackIds, LocalDate yearMonth, Long planId);


  @Query(
      "MATCH (plan:Plan)<-[:BELONGS_TO]-(w:Deliverable{deleted:false,canceled:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step)-[co:CONSUMES]-(c:CostAccount) " +
      "WHERE (w.category <> 'SNAPSHOT' OR w.category IS NULL) AND st.periodFromStart IS NOT NULL " +
      "AND ($planId IS NULL OR ID(plan) = $planId) " +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date.truncate('month', date(plan.finish))" +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) >= date.truncate('month', date(plan.start))" +
      "AND ($yearMonth IS NOT NULL OR date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) < date.truncate('month', date())) " +
      "AND ($workpackIds IS NULL OR ID(w) IN $workpackIds ) " +
      "AND ($yearMonth IS NULL OR (st.periodFromStart IS NOT NULL AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date($yearMonth))) " +
      "RETURN ID(w) AS idWorkpack, ID(plan) as idPlan, toString(SUM(toFloat(co.actualCost))) AS actualCost "
  )
  List<DashboardWorkpackDetailDto> findAllActualCost(List<Long> workpackIds, LocalDate yearMonth, Long planId);


  @Query (
      "MATCH (plan:Plan)<-[:BELONGS_TO]-(w:Deliverable{deleted:false,canceled:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step) " +
          "WHERE (w.category <> 'SNAPSHOT' OR w.category IS NULL) AND st.periodFromStart IS NOT NULL  " +
          "AND ($workpackIds IS NULL OR ID(w) IN $workpackIds) " +
          "AND ($planId IS NULL OR ID(plan) = $planId) " +
          "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date.truncate('month', date(plan.finish)) " +
          "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) >= date.truncate('month', date(plan.start)) " +
          "RETURN DISTINCT  toString(date.truncate('month', date(s.start) + Duration({months: st.periodFromStart}))) AS date " +
          "ORDER BY date "
  )
  List<EarnedValueByStepDto> findAllEarnedValuesStep(List<Long> workpackIds, Long planId);

  @Query (
      "MATCH (plan:Plan)<-[:BELONGS_TO]-(w:Deliverable{deleted:false,canceled:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step)-[co:CONSUMES]->(ca:CostAccount) " +
      "WHERE (w.category <> 'SNAPSHOT' OR w.category IS NULL) AND st.periodFromStart IS NOT NULL  " +
      "AND ($workpackIds IS NULL OR ID(w) IN $workpackIds) " +
      "AND ($planId IS NULL OR ID(plan) = $planId) " +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date.truncate('month', date(plan.finish)) " +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) >= date.truncate('month', date(plan.start)) " +
      "AND ($yearMonth IS NOT NULL OR date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) < date.truncate('month', date())) " +
      "AND ($yearMonth IS NULL OR (st.periodFromStart IS NOT NULL AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date($yearMonth))) " +
      "RETURN toString(date.truncate('month', date(s.start) + Duration({months: st.periodFromStart}))) AS date " +
      ", toString(SUM(toFloat(co.actualCost))) AS actualCost " +
      "ORDER BY date "
  )
  List<EarnedValueByStepDto> findAllEarnedValueActualCost(List<Long> workpackIds, LocalDate yearMonth, Long planId);

  @Query (
      "MATCH (plan:Plan)<-[:BELONGS_TO]-(w:Deliverable{deleted:false,canceled:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step) " +
      "WHERE (w.category <> 'SNAPSHOT' OR w.category IS NULL) AND st.periodFromStart IS NOT NULL  " +
      "AND ($workpackIds IS NULL OR ID(w) IN $workpackIds) " +
      "AND ($planId IS NULL OR ID(plan) = $planId) " +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date.truncate('month', date(plan.finish)) " +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) >= date.truncate('month', date(plan.start)) " +
      "AND ($yearMonth IS NOT NULL OR date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) < date.truncate('month', date())) " +
      "AND ($yearMonth IS NULL OR (st.periodFromStart IS NOT NULL AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date($yearMonth))) " +
      "RETURN toString(date.truncate('month', date(s.start) + Duration({months: st.periodFromStart}))) AS date " +
      ", toString(SUM(toFloat(st.actualWork))) AS actualWork " +
      "ORDER BY date "
  )
  List<EarnedValueByStepDto> findAllEarnedValueActualWork(List<Long> workpackIds, LocalDate yearMonth, Long planId);

  @Query (
      "MATCH (plan:Plan)<-[:BELONGS_TO]-(w:Deliverable{deleted:false,canceled:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step)-[co:CONSUMES]->(ca:CostAccount) " +
      "WHERE (w.category <> 'SNAPSHOT' OR w.category IS NULL) AND st.periodFromStart IS NOT NULL  " +
      "AND ($workpackIds IS NULL OR ID(w) IN $workpackIds) " +
      "AND ($planId IS NULL OR ID(plan) = $planId) " +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date.truncate('month', date(plan.finish)) " +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) >= date.truncate('month', date(plan.start)) " +
      "RETURN toString(date.truncate('month', date(s.start) + Duration({months: st.periodFromStart}))) AS date " +
      ", toString(SUM(toFloat(co.plannedCost))) AS estimatedCost " +
      "ORDER BY date "
  )
  List<EarnedValueByStepDto> findAllEarnedValueEstimatedCost(List<Long> workpackIds, Long planId);


  @Query (
          "CALL { "
                  + "MATCH (plan:Plan)<-[:BELONGS_TO]-(master:Deliverable {deleted: false, canceled: false})<-[:IS_SNAPSHOT_OF]-(w:Deliverable {deleted: false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step)-[co:CONSUMES]->(ca:CostAccount), "
                  + "(st)-[:IS_SNAPSHOT_OF]->(stm:Step), "
                  + "(s)-[:COMPOSES]->(b:Baseline) "
                  + "WHERE ID(b) IN $ids "
                  + "AND ($planId IS NULL OR ID(plan) = $planId) "
                  + "AND ($workpackIds IS NULL OR ID(master) IN $workpackIds) "
                  + "AND toFloat(st.plannedWork) > 0 "
                  + "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date.truncate('month', date(plan.finish)) "
                  + "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) >= date.truncate('month', date(plan.start)) "
                  + "RETURN "
                  + "sum(toFloat(st.plannedWork)) AS totalPlannedWorkSum, "
                  + "sum(toFloat(co.plannedCost)) AS totalPlannedCostSum "
                  + "} "
                  + "WITH totalPlannedWorkSum, totalPlannedCostSum "
                  + "MATCH (plan:Plan)<-[:BELONGS_TO]-(master:Deliverable {deleted: false, canceled: false})<-[:IS_SNAPSHOT_OF]-(w:Deliverable {deleted: false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step)-[co:CONSUMES]->(ca:CostAccount), "
                  + "(st)-[:IS_SNAPSHOT_OF]->(stm:Step), "
                  + "(s)-[:COMPOSES]->(b:Baseline) "
                  + "WHERE ID(b) IN $ids "
                  + "AND ($planId IS NULL OR ID(plan) = $planId) "
                  + "AND ($workpackIds IS NULL OR ID(master) IN $workpackIds) "
                  + "AND toFloat(st.plannedWork) > 0 "
                  + "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date.truncate('month', date(plan.finish)) "
                  + "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) >= date.truncate('month', date(plan.start)) "
                  + "WITH date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) AS date, "
                  + "ID(plan) as idPlan, "
                  + "sum(toFloat(st.plannedWork)) AS totalPlannedWork, "
                  + "sum(toFloat(co.plannedCost)) AS totalPlannedCost, "
                  + "sum(toFloat(stm.actualWork)) AS totalActualWork, "
                  + "totalPlannedWorkSum, "
                  + "totalPlannedCostSum "
                  + "RETURN toString(date) AS date, "
                  + "idPlan, "
                  + "toString((totalPlannedCostSum / totalPlannedWorkSum) * totalActualWork) AS earnedValue "
                  + "ORDER BY date"
  )
  List<EarnedValueByStepDto> findAllEarnedValue(List<Long> ids, List<Long> workpackIds, Long planId);

  @Query (
      "MATCH (plan:Plan)<-[:BELONGS_TO]-(master:Deliverable{deleted:false,canceled:false})<-[:IS_SNAPSHOT_OF]-(w:Deliverable{deleted:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step)-[co:CONSUMES]->(ca:CostAccount) " +
      ",(s)-[:COMPOSES]->(b:Baseline) " +
      "WHERE ID(b) IN $baselineIds " +
      "AND ($planId IS NULL OR ID(plan) = $planId) " +
      "AND st.periodFromStart IS NOT NULL  " +
      "AND ($workpackIds IS NULL OR ID(master) IN $workpackIds) " +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date.truncate('month', date(plan.finish)) " +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) >= date.truncate('month', date(plan.start)) " +
      "RETURN toString(date.truncate('month', date(s.start) + Duration({months: st.periodFromStart}))) AS date " +
      ", toString(SUM(toFloat(co.plannedCost))) AS plannedCost " +
      "ORDER BY date "
  )
  List<EarnedValueByStepDto> findAllEarnedValuePlannedCost(List<Long> baselineIds, List<Long> workpackIds, Long planId);

  @Query (
      "MATCH (plan:Plan)<-[:BELONGS_TO]-(master:Deliverable{deleted:false,canceled:false})<-[:IS_SNAPSHOT_OF]-(w:Deliverable{deleted:false})<-[:FEATURES]-(s:Schedule)<-[:COMPOSES]-(st:Step) " +
      ",(s)-[:COMPOSES]->(b:Baseline) " +
      "WHERE ID(b) IN $baselineIds " +
      "AND ($planId IS NULL OR ID(plan) = $planId) " +
      "AND st.periodFromStart IS NOT NULL " +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) <= date.truncate('month', date(plan.finish)) " +
      "AND date.truncate('month', date(s.start) + Duration({months: st.periodFromStart})) >= date.truncate('month', date(plan.start)) " +
      "AND ($workpackIds IS NULL OR ID(master) IN $workpackIds) " +
      "RETURN toString(date.truncate('month', date(s.start) + Duration({months: st.periodFromStart}))) AS date " +
      ", toString(SUM(toFloat(st.plannedWork))) AS plannedWork " +
      "ORDER BY date "
  )
  List<EarnedValueByStepDto> findAllEarnedValuePlannedWork(List<Long> baselineIds, List<Long> workpackIds, Long planId);


}
