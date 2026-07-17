package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.procurements.Procurement;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcurementRepository extends Neo4jRepository<Procurement, Long> {

    @Query(
        "MATCH (procurement:Procurement)-[r:RELATED_TO]->(workpack:Workpack) " +
        "WHERE id(workpack) = $idWorkpack " +
        "RETURN procurement, r, workpack " +
        "ORDER BY procurement.processNumber"
    )
    List<Procurement> findAllByIdWorkpack(@Param("idWorkpack") Long idWorkpack);
}
