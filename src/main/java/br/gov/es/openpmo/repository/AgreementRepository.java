package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.agreements.Agreement;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgreementRepository extends Neo4jRepository<Agreement, Long>, CustomRepository {

    @Query(
        "MATCH (agreement:Agreement)-[:SIGNED_FOR]->(workpack:Workpack) " +
        "WHERE id(workpack) = $idWorkpack " +
        "AND agreement.processId = $processId " +
        "AND (($agreementType = 'CONTRACT' AND agreement:Contract) " +
        "OR ($agreementType = 'COOPERATION' AND agreement:Cooperation)) " +
        "RETURN count(agreement) > 0"
    )
    boolean existsByWorkpackAndProcessIdAndType(
        @Param("idWorkpack") Long idWorkpack,
        @Param("processId") Long processId,
        @Param("agreementType") String agreementType
    );

    @Query(
        "MATCH (agreement:Agreement)-[r:SIGNED_FOR]->(workpack:Workpack) " +
        "WHERE id(workpack) = $idWorkpack " +
        "RETURN agreement, r, workpack " +
        "ORDER BY agreement.processId"
    )
    List<Agreement> findAllByIdWorkpack(@Param("idWorkpack") Long idWorkpack);
}
