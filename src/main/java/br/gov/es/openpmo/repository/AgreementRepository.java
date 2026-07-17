package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.agreements.Agreement;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgreementRepository extends Neo4jRepository<Agreement, Long> {

    @Query(
        "MATCH (agreement:Agreement)-[r:RELATED_TO]->(workpack:Workpack) " +
        "WHERE id(workpack) = $idWorkpack " +
        "RETURN agreement, r, workpack " +
        "ORDER BY agreement.processNumber"
    )
    List<Agreement> findAllByIdWorkpack(@Param("idWorkpack") Long idWorkpack);
}
