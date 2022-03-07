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

    @Query("match (w:Workpack) " +
            "where id(w)=$workpackId " +
            "optional match (w)<-[:IS_FORSEEN_ON]-(r1:Risk) " +
            "with w,r1 " +
            "optional match (w)<-[:IS_IN*]-(v:Workpack)<-[:IS_FORSEEN_ON]-(r2:Risk) " +
            "with collect(r1) + collect(r2) as riskList " +
            "unwind riskList as risks " +
            "return risks")
    Set<Risk> findAll(Long workpackId);

    @Query("match (risk:Risk)<-[triggeredBy:IS_TRIGGERED_BY]-(:Issue) " +
            "where id(risk)=$riskId " +
            "return risk")
    Optional<Risk> findRiskIfHasIssueRelationship(Long riskId);

    @Query("match (risk:Risk) " +
            "where id(risk)=$riskId " +
            "return risk, [ " +
            "   [ (risk)-[isReportedFor:IS_FORSEEN_ON]->(workpack:Workpack) | [isReportedFor, workpack] ], " +
            "   [ (risk)<-[mitigates:MITIGATES]-(response:RiskResponse) | [mitigates, response] ], " +
            "   [ (response)<-[responsibleFor:IS_RESPONSIBLE_FOR]-(responsible:Person) | [responsibleFor, responsible] ] " +
            "]")
    Optional<Risk> findRiskDetailById(Long riskId);

    @Query("match (risk:Risk)-[isReportedFor:IS_FORSEEN_ON]->(workpack:Workpack) " +
            "where id(risk)=$riskId " +
            "return id(workpack) ")
    Optional<Long> findWorkpackIdByRiskId(Long riskId);

    @Query("match (w:Workpack) " +
            "where id(w)=$workpackId " +
            "optional match (w)<-[:IS_FORSEEN_ON]-(r1:Risk{status:'OPEN'}) " +
            "where r1.importance=$importance " +
            "with w,r1 " +
            "optional match (w)<-[:IS_IN*]-(v:Workpack)<-[:IS_FORSEEN_ON]-(r2:Risk{status:'OPEN'}) " +
            "where r2.importance=$importance " +
            "with collect(r1) + collect(r2) as riskList " +
            "unwind riskList as risks " +
            "return count(distinct risks)")
    Long countOpenedRiskOfWorkpackByImportance(Long workpackId, String importance);

    @Query("match (w:Workpack) " +
            "where id(w)=$workpackId " +
            "optional match (w)<-[:IS_FORSEEN_ON]-(r1:Risk) " +
            "with w,r1 " +
            "optional match (w)<-[:IS_IN*]-(v:Workpack)<-[:IS_FORSEEN_ON]-(r2:Risk) " +
            "with collect(r1) + collect(r2) as riskList " +
            "unwind riskList as risks " +
            "return count(distinct risks)")
    Long countAllRisksOfWorkpack(Long workpackId);

    @Query("match (w:Workpack) " +
            "where id(w)=$workpackId " +
            "optional match (w)<-[:IS_FORSEEN_ON]-(r1:Risk) " +
            "where r1.status <> 'OPEN' " +
            "with w,r1 " +
            "optional match (w)<-[:IS_IN*]-(v:Workpack)<-[:IS_FORSEEN_ON]-(r2:Risk) " +
            "where r2.status <> 'OPEN' " +
            "with collect(r1) + collect(r2) as riskList " +
            "unwind riskList as risks " +
            "return count(distinct risks)")
    Long countClosedRisksOfWorkpack(Long workpackId);

}
