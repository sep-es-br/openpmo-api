package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.budget.UnidadeOrcamentaria;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UnidadeOrcamentariaRepository extends Neo4jRepository<UnidadeOrcamentaria, Long> {

    @Query("MATCH (c:CostAccount)<-[ctrl:CONTROLS]-(uo:UnidadeOrcamentaria) " +
            "where id(c) = $id " +
            "return uo")
    Optional<UnidadeOrcamentaria> findByIdCostAccount(@Param("id") Long id);
}
