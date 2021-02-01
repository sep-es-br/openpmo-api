package br.gov.es.openpmo.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.gov.es.openpmo.model.relations.CanAccessWorkpack;

@Repository
public interface CanAccessWorkpackRepository extends Neo4jRepository<CanAccessWorkpack, Long> {
    @Query("MATCH (p:Person)-[ca:CAN_ACCESS_WORKPACK]->(o:Workpack) "
               + "WHERE ID(o) = $idWorkpack AND (ID(p) = $idPerson OR $idPerson IS NULL) "
               + "RETURN o,p,ca")
    List<CanAccessWorkpack> findByIdWorkpackAndIdPerson(@Param("idWorkpack") Long idWorkpack, @Param("idPerson") Long idPerson);

    @Query("MATCH (p:Person)-[ca:CAN_ACCESS_WORKPACK]->(o:Workpack) WHERE ID(o) = $idWorkpack RETURN o,p,ca")
    List<CanAccessWorkpack> findByIdWorkpack(@Param("idWorkpack") Long idWorkpack);

}