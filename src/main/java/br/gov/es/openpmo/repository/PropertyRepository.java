package br.gov.es.openpmo.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.openpmo.model.Property;

public interface PropertyRepository extends Neo4jRepository<Property, Long> {
    @Query("MATCH (p:Property)<-[:FEATURES]-(w:Workpack) WHERE ID(w) = $idWorkpack RETURN p ")
    List<Property> findAllByIdWorkpack(@Param("idWorkpack") Long idWorkpack);
}
