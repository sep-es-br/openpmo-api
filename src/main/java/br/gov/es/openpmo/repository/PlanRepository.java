package br.gov.es.openpmo.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.openpmo.model.Plan;

public interface PlanRepository extends Neo4jRepository<Plan, Long> {

    @Query("match (p: Plan)-[r:IS_ADOPTED_BY]->(o:Office) "
               + ", (p)-[sb:IS_STRUCTURED_BY]->(pm:PlanModel) "
               + "where id(o)= $id return p,r,o,sb,pm")
    List<Plan> findAllInOffice(@Param("id") Long id);
}
