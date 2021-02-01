package br.gov.es.openpmo.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.openpmo.model.PlanModel;

public interface PlanModelRepository extends Neo4jRepository<PlanModel, Long> {

    @Query("match (p: PlanModel)-[r:IS_ADOPTED_BY]->(o:Office) where id(o)= $id return p,r,o")
    List<PlanModel> findAllInOffice(@Param("id") Long id);

}
