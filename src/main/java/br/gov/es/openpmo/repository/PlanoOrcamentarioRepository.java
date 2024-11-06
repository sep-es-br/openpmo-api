package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.budget.PlanoOrcamentario;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlanoOrcamentarioRepository extends Neo4jRepository<PlanoOrcamentario, Long> {

    @Query("MATCH (c:CostAccount)<-[ctrl:ASSIGNED]-(po:PlanoOrcamentario) " +
            "where id(c) = $id " +
            "return po")
    Optional<PlanoOrcamentario> findByIdCostAccout(@Param("id") Long id);
}
