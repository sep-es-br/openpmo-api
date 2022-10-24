package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStepQueryResult;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.time.LocalDate;
import java.util.List;

public interface EarnedValueAnalysisRepository extends Neo4jRepository<Workpack, Long> {

  @Query("match (b:Baseline)<-[:IS_BASELINED_BY]-(w:Workpack{deleted:false,canceled:false})<-[:IS_SNAPSHOT_OF]-" +
         "(:Project{deleted:false,canceled:false}) " +
         "where id(w)=$workpackId and id(b)=$baselineId " +
         "match (w)<-[:IS_IN*]-(:Workpack{deleted:false,canceled:false})<-[:FEATURES]-(:Schedule)<-[:COMPOSES]-(m:Step) " +
         "where date($startOfMonth) <= date(m.periodFromStart) <= date($endOfMonth) " +
         "match (m)<-[:IS_SNAPSHOT_OF]-(s:Step)-[:COMPOSES]->(b) " +
         "match (s)-[cs:CONSUMES]->(:CostAccount) " +
         "match (m)-[cm:CONSUMES]->(:CostAccount) " +
         "with distinct s, cs, cm, m, date(s.periodFromStart) <= date($referenceDate) as valid " +
         "with " +
         "    collect(toFloat(cs.plannedCost)) as snapshotPlannedCost, " +
         "    (case valid when true then collect(toFloat(s.plannedWork)) else [] end) as snapshotPlannedWork, " +
         "    (case valid when true then collect(toFloat(cm.actualCost)) else [] end) as masterActualCost, " +
         "    (case valid when true then collect(toFloat(m.actualWork)) else [] end) as masterActualWork " +
         "unwind (case snapshotPlannedCost when [] then [null] else snapshotPlannedCost end) as snapshotPlannedCostList " +
         "unwind (case snapshotPlannedWork when [] then [null] else snapshotPlannedWork end) as snapshotPlannedWorkList " +
         "unwind (case masterActualCost when [] then [null] else masterActualCost end) as masterActualCostList " +
         "unwind (case masterActualWork when [] then [null] else masterActualWork end)  as masterActualWorkList " +
         "with " +
         "    snapshotPlannedCostList, " +
         "    snapshotPlannedWorkList, " +
         "    masterActualCostList, " +
         "    masterActualWorkList " +
         "with " +
         "    toFloat(sum(snapshotPlannedCostList)) as plannedValue, " +
         "    toFloat(sum(masterActualCostList)) as actualCost, " +
         "    toFloat(sum(snapshotPlannedWorkList)) as plannedWork, " +
         "    toFloat(sum(masterActualWorkList)) as actualWork " +
         "return " +
         "    plannedValue, " +
         "    actualCost, " +
         "    plannedWork, " +
         "    actualWork, " +
         "    $endOfMonth as date")
  EarnedValueByStepQueryResult getEarnedValueByStep(
    Long workpackId,
    Long baselineId,
    LocalDate startOfMonth,
    LocalDate endOfMonth,
    LocalDate referenceDate
  );

  @Query("match (b:Baseline)<-[:IS_BASELINED_BY]-(:Workpack{deleted:false,canceled:false})<-[:IS_SNAPSHOT_OF]-" +
         "(:Workpack{deleted:false,canceled:false}) " +
         "where id(b) in $baselineId " +
         "match (b)<-[:COMPOSES]-(:Step)-[:IS_SNAPSHOT_OF]->(m:Step)-[:COMPOSES]->(sch:Schedule)-[:FEATURES]->" +
         "(w:Workpack{deleted:false,canceled:false})-[:IS_IN*]->(v:Workpack{deleted:false,canceled:false}) " +
         "where $workpackId in [ id(w), id(v) ] and date($startOfMonth) <= date(sch.start) + duration({months: m" +
         ".periodFromStart}) <= date($endOfMonth) " +
         "match (m)<-[:IS_SNAPSHOT_OF]-(s:Step)-[:COMPOSES]->(b) " +
         "match (s)-[cs:CONSUMES]->(:CostAccount) " +
         "match (m)-[cm:CONSUMES]->(:CostAccount) " +
         "with distinct s, cs, cm, m " +
         "with s, cs, cm, m, " +
         "    collect(toFloat(cs.plannedCost)) as snapshotPlannedCost, " +
         "    collect(toFloat(s.plannedWork)) as snapshotPlannedWork, " +
         "    collect(toFloat(cm.actualCost)) as masterActualCost, " +
         "    collect(toFloat(m.actualWork)) as masterActualWork " +
         "unwind (case snapshotPlannedCost when [] then [null] else snapshotPlannedCost end) as snapshotPlannedCostList " +
         "unwind (case snapshotPlannedWork when [] then [null] else snapshotPlannedWork end) as snapshotPlannedWorkList " +
         "unwind (case masterActualCost when [] then [null] else masterActualCost end) as masterActualCostList " +
         "unwind (case masterActualWork when [] then [null] else masterActualWork end)  as masterActualWorkList " +
         "with " +
         "    snapshotPlannedCostList, " +
         "    snapshotPlannedWorkList, " +
         "    masterActualCostList, " +
         "    masterActualWorkList " +
         "with " +
         "    toFloat(sum(snapshotPlannedCostList)) as plannedValue, " +
         "    toFloat(sum(masterActualCostList)) as actualCost, " +
         "    toFloat(sum(snapshotPlannedWorkList)) as plannedWork, " +
         "    toFloat(sum(masterActualWorkList)) as actualWork " +
         "return " +
         "    plannedValue, " +
         "    actualCost, " +
         "    plannedWork, " +
         "    actualWork, " +
         "    $endOfMonth as date")
  EarnedValueByStepQueryResult getEarnedValueByStep(
    Long workpackId,
    List<Long> baselineId,
    LocalDate startOfMonth,
    LocalDate endOfMonth
  );

}
