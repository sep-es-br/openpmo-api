package br.gov.es.openpmo.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.gov.es.openpmo.model.relations.CanAccessPlan;

@Repository
public interface PlanPermissionRepository extends Neo4jRepository<CanAccessPlan, Long> {

    @Query("MATCH (p:Person)-[is:CAN_ACCESS_PLAN]->(o:Plan) WHERE ID(o) = $idPlan AND ID(p) = $idPerson  RETURN o,p,is")
    List<CanAccessPlan> findByIdPlanAndIdPerson(@Param("idPlan") Long idPlan, @Param("idPerson") Long idPerson);

    @Query("MATCH (p:Person)-[is:CAN_ACCESS_PLAN]->(o:Plan) WHERE ID(o) = $idPlan RETURN o,p,is")
    List<CanAccessPlan> findByIdPlan(@Param("idPlan") Long idPlan);

}