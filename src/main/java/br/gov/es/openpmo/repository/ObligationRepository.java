package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.obligations.Obligation;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObligationRepository extends Neo4jRepository<Obligation, Long> {

    @Query(
        "MATCH (obligation:Obligation)-[r:RELATED_TO]->(workpack:Workpack) " +
        "WHERE id(workpack) = $idWorkpack " +
        "RETURN obligation, r, workpack " +
        "ORDER BY obligation.obligationNumber"
    )
    List<Obligation> findAllByIdWorkpack(
        @Param("idWorkpack")
        Long idWorkpack
    );
   
}