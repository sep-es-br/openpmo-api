package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStepQueryResult;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EarnedValueAnalysisRepository extends Neo4jRepository<Workpack, Long> {

  @Query("MATCH (b:Baseline)<-[:IS_BASELINED_BY]-(:Workpack{deleted:false,canceled:false})<-[:IS_SNAPSHOT_OF]-" +
         "(:Workpack{deleted:false,canceled:false}) " +
         "WHERE id(b) IN $baselineId " +
         "MATCH (b)<-[:COMPOSES]-(:Step)-[:IS_SNAPSHOT_OF]->(m:Step)-[:COMPOSES]->(sch:Schedule)-[:FEATURES]->" +
         "(w:Workpack{deleted:false,canceled:false})-[:IS_IN*]->(v:Workpack{deleted:false,canceled:false}) " +
         "WHERE $workpackId IN [ id(w), id(v) ] AND date($startOfMonth) <= date(sch.start) + duration({months: m" +
         ".periodFromStart}) <= date($endOfMonth) " +
         "MATCH (m)<-[:IS_SNAPSHOT_OF]-(s:Step)-[:COMPOSES]->(b) " +
         "MATCH (s)-[cs:CONSUMES]->(sca:CostAccount) " +
         "MATCH (m)-[cm:CONSUMES]->(mca:CostAccount) " +
         "WITH " +
         "    collect(DISTINCT cs) AS snapshotConsumesList, " +
         "    collect(DISTINCT s) AS snapshotStepList, " +
         "    collect(DISTINCT cm) AS masterConsumesList, " +
         "    collect(DISTINCT m) AS masterStepList, " +
         "    collect(DISTINCT mca) AS masterCostAccountList, " +
         "    collect(DISTINCT sca) AS snapshotCostAccountList " +
         "RETURN DISTINCT *, " +
         "    snapshotConsumesList, " +
         "    snapshotStepList, " +
         "    masterConsumesList, " +
         "    masterStepList, " +
         "    $endOfMonth AS date "
  )
  EarnedValueByStepQueryResult getEarnedValueByStep(
    @Param("workpackId") Long workpackId,
    @Param("baselineId") List<Long> baselineId,
    @Param("startOfMonth") LocalDate startOfMonth,
    @Param("endOfMonth") LocalDate endOfMonth
  );

}
