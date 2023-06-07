package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RiskRepository extends Neo4jRepository<Risk, Long>, CustomRepository {

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
         "return risk, [ " +
         "   [ (risk)-[isReportedFor:IS_FORSEEN_ON]->(workpack:Workpack{deleted:false,canceled:false}) | [isReportedFor, " +
         "workpack] ], " +
         "   [ (risk)<-[mitigates:MITIGATES]-(response:RiskResponse) | [mitigates, response] ], " +
         "   [ (response)<-[responsibleFor:IS_RESPONSIBLE_FOR]-(responsible:Person) | [responsibleFor, responsible] ] " +
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
    Long workpackId,
    String importance
  );

  @Query("match (w:Workpack{deleted:false,canceled:false}) " +
         "where id(w)=$workpackId " +
         "optional match (w)<-[:IS_FORSEEN_ON]-(r1:Risk{status:'OPEN'}) " +
         "with w,r1 " +
         "optional match (w)<-[:IS_IN*]-(v:Workpack{deleted:false,canceled:false})<-[:IS_FORSEEN_ON]-(r2:Risk{status:'OPEN'}) " +
         "with collect(r1) + collect(r2) as riskList " +
         "unwind riskList as risks " +
         "return count(distinct risks)")
  Long countAllOpenedRisksOfWorkpack(Long workpackId);

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
  Long countClosedRisksOfWorkpack(Long workpackId);

}
