package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiskRepository extends Neo4jRepository<Risk, Long>, CustomRepository {

  @Query("MATCH (risk:Risk)-[isReportedFor:IS_FORSEEN_ON]->(workpack:Workpack) " +
         "WHERE id(workpack)=$workpackId " +
         "RETURN risk, isReportedFor, workpack")
  List<Risk> findAll(Long workpackId);

  @Query("MATCH (risk:Risk)<-[triggeredBy:IS_TRIGGERED_BY]-(issue:Issue) " +
         "WHERE id(risk)=$idRisk " +
         "RETURN risk")
  Optional<Risk> findRiskIfHasIssueRelationship(Long idRisk);

  @Query("MATCH (risk:Risk) " +
         "WHERE id(risk)=$id " +
         "RETURN risk, [ " +
         "	[ (risk)-[isReportedFor:IS_FORSEEN_ON]->(workpack:Workpack) | [isReportedFor, workpack] ], " +
         "	[ (risk)<-[mitigates:MITIGATES]-(response:RiskResponse) | [mitigates, response] ], " +
         "	[ (response)<-[responsibleFor:IS_RESPONSIBLE_FOR]-(responsible:Person) | [responsibleFor, responsible] ] " +
         "]")
  Optional<Risk> findRiskDetailById(Long id);

  @Query("MATCH (risk:Risk)-[isReportedFor:IS_FORSEEN_ON]->(workpack:Workpack) " +
         "WHERE id(risk)=$riskId " +
         "RETURN id(workpack) ")
  Optional<Long> findWorkpackIdByRiskId(Long riskId);

  @Query(
    "MATCH (workpack:Workpack)<-[:IS_FORSEEN_ON]-(risk:Risk{status:'OPEN'}) " +
    "WHERE id(workpack)=$idWorkpack AND risk.importance=$importance " +
    "RETURN count(DISTINCT risk)"
  )
  Long countOpenedRiskOfWorkpackByImportance(Long idWorkpack, String importance);

  @Query(
    "MATCH (workpack:Workpack)<-[:IS_FORSEEN_ON]-(risk:Risk) " +
    "WHERE id(workpack)=$idWorkpack " +
    "RETURN count(DISTINCT risk)"
  )
  Long countAllRisksOfWorkpack(Long idWorkpack);

  @Query(
    "MATCH (workpack:Workpack)<-[:IS_FORSEEN_ON]-(risk:Risk) " +
    "WHERE id(workpack)=$idWorkpack AND risk.status <> 'OPEN' " +
    "RETURN count(DISTINCT risk)"
  )
  Long countClosedRisksOfWorkpack(Long idWorkpack);

}
