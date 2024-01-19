package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.dashboards.RiskDataChartDto;
import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RiskRepository extends Neo4jRepository<Risk, Long>, CustomRepository {

  @Query("match (w:Workpack)<-[:IS_IN*0..]-(:Workpack)<-[:IS_FORSEEN_ON]-(r:Risk) " +
    "where id(w)=$workpackId " +
    "return distinct r")
  List<Risk> findByWorkpackId(Long workpackId);

  @Query("match (w:Workpack{deleted:false,canceled:false}) " +
         "where id(w)=$workpackId " +
         "optional match (w)<-[:IS_FORSEEN_ON]-(r:Risk) " +
         "with *, apoc.text.levenshteinSimilarity(apoc.text.clean(r.name), apoc.text.clean($term)) AS score " +
         "where ($term is null OR $term = '' OR score > $searchCutOffScore) " +
         "return r " +
         "order by score desc"
  )
  Set<Risk> findAll(
    Long workpackId,
    String term,
    Double searchCutOffScore
  );

  @Query("match (risk:Risk)<-[triggeredBy:IS_TRIGGERED_BY]-(:Issue) " +
         "where id(risk)=$riskId " +
         "return risk")
  Optional<Risk> findRiskIfHasIssueRelationship(Long riskId);

  @Query("match (risk:Risk) " +
         "where id(risk)=$riskId " +
         " OPTIONAL MATCH (risk)-[isReportedFor:IS_FORSEEN_ON]->(workpack:Workpack{deleted:false,canceled:false}) " +
         " OPTIONAL MATCH (risk)<-[mitigates:MITIGATES]-(response:RiskResponse) " +
         " OPTIONAL MATCH (response)<-[responsibleFor:IS_RESPONSIBLE_FOR]-(responsible:Person) " +
         "return risk, [ " +
         "   [ [isReportedFor, workpack] ], " +
         "   [ [mitigates, response] ], " +
         "   [ [responsibleFor, responsible] ] " +
         "]")
  Optional<Risk> findRiskDetailById(Long riskId);

  @Query("match (risk:Risk)-[isReportedFor:IS_FORSEEN_ON]->(workpack:Workpack{deleted:false,canceled:false}) " +
         "where id(risk)=$riskId " +
         "return id(workpack) ")
  Optional<Long> findWorkpackIdByRiskId(Long riskId);

  @Query("match (w:Workpack{deleted:false,canceled:false}) " +
         "where id(w)=$workpackId " +
         "optional match (w)<-[:IS_FORSEEN_ON]-(r1:Risk{status:'OPEN'}) " +
         "where r1.importance=$importance " +
         "with w,r1 " +
         "optional match (w)<-[:IS_IN*]-(v:Workpack{deleted:false,canceled:false})<-[:IS_FORSEEN_ON]-(r2:Risk{status:'OPEN'}) " +
         "where r2.importance=$importance " +
         "with collect(r1) + collect(r2) as riskList " +
         "unwind riskList as risks " +
         "return count(distinct risks)")
  Long countOpenedRiskOfWorkpackByImportance(
    @Param("workpackId") Long workpackId,
    @Param("importance") String importance
  );

  @Query("match (w:Workpack{deleted:false,canceled:false}) " +
         "where id(w)=$workpackId " +
         "optional match (w)<-[:IS_FORSEEN_ON]-(r1:Risk{status:'OPEN'}) " +
         "with w,r1 " +
         "optional match (w)<-[:IS_IN*]-(v:Workpack{deleted:false,canceled:false})<-[:IS_FORSEEN_ON]-(r2:Risk{status:'OPEN'}) " +
         "with collect(r1) + collect(r2) as riskList " +
         "unwind riskList as risks " +
         "return count(distinct risks)")
  Long countAllOpenedRisksOfWorkpack(@Param("workpackId") Long workpackId);

  @Query("match (w:Workpack{deleted:false,canceled:false}) " +
         "where id(w)=$workpackId " +
         "optional match (w)<-[:IS_FORSEEN_ON]-(r1:Risk) " +
         "where r1.status <> 'OPEN' " +
         "with w,r1 " +
         "optional match (w)<-[:IS_IN*]-(v:Workpack{deleted:false,canceled:false})<-[:IS_FORSEEN_ON]-(r2:Risk) " +
         "where r2.status <> 'OPEN' " +
         "with collect(r1) + collect(r2) as riskList " +
         "unwind riskList as risks " +
         "return count(distinct risks)")
  Long countClosedRisksOfWorkpack(@Param("workpackId") Long workpackId);

  @Query("MATCH (w:Workpack{deleted: false , canceled: false }) " +
          "WHERE id(w)=$workpackId " +
          "OPTIONAL MATCH (w)<-[:IS_FORSEEN_ON]-(r1:Risk) " +
          "WITH w, r1 " +
          "OPTIONAL MATCH (w)<-[:IS_IN*]-(v:Workpack{deleted: false , canceled: false })<-[:IS_FORSEEN_ON]-(r2:Risk) " +
          "WITH collect(r1) + collect(r2) AS riskList UNWIND riskList AS risks " +
          "RETURN count( DISTINCT risks) AS count, risks.status AS status, risks.importance AS importance")
  List<RiskDataChartDto> countRisksOfWorkpack(@Param("workpackId") Long workpackId);
}
