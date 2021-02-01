package br.gov.es.openpmo.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.openpmo.model.relations.Consumes;

public interface ConsumesRepository extends Neo4jRepository<Consumes, Long> {

    @Query("MATCH (c:CostAccount)<-[co:CONSUMES]-(s:Step)"
               + ", (c)-[:APPLIES_TO]->(w:Workpack) "
               + " WHERE ID(w) = $idWorkpack AND (ID(c) = $id OR $id IS NULL) RETURN c,co,s,w")
    List<Consumes> findAllByIdAndWorkpack(@Param("id") Long id, @Param("idWorkpack") Long idWorkpack);

    @Query("MATCH (c:CostAccount)<-[co:CONSUMES]-(s:Step)"
               + " WHERE ID(c) = $id RETURN c,co,s ")
    List<Consumes> findAllByIdCostAccount(@Param("id") Long id);
}
