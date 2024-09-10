package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.relations.IsLinkedTo;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkpackLinkRepository extends Neo4jRepository<IsLinkedTo, Long>, CustomRepository {

  @Query("MATCH (n:Workpack)-[is:IS_LINKED_TO]->(m:WorkpackModel) WHERE id(n) = $idWorkpack AND id(m) = $idWorkpackModel RETURN" +
         " n,m,is")
  Optional<IsLinkedTo> findByIdWorkpackAndIdWorkpackModel(
    @Param("idWorkpack") Long idWorkpack,
    @Param("idWorkpackModel") Long idWorkpackModel
  );

  @Query("match (w:Workpack), (wm:WorkpackModel) " +
    "where id(w)=$idWorkpack and id(wm)=$idWorkpackModel " +
    "create (w)-[:IS_LINKED_TO]->(wm)")
  void linkWorkpackAndWorkpackModel(
    Long idWorkpack,
    Long idWorkpackModel
  );

  @Query("match (w:Workpack), (p:Plan) " +
          "where id(w)=$idWorkpack and id(p)=$idPlan " +
          "create (w)-[:BELONGS_TO{linked:true}]->(p)")
  void linkWorkpackAndPlan(
          Long idWorkpack,
          Long idPlan
  );

  @Query("match (w:Workpack), (p:Workpack) " +
          "where id(w)=$idWorkpack and id(p)=$idParent " +
          "create (w)-[:IS_IN]->(p)")
  void linkWorkpackAndParent(
          Long idWorkpack,
          Long idParent
  );

  @Query("MATCH (plan)<-[:BELONGS_TO {linked: true}]-(workpack:Workpack)-[:IS_LINKED_TO]->(model:WorkpackModel) " +
          "WHERE id(plan) = $idPlan AND id(workpack) = $idWorkpack AND id(model) = $idWorkpackModel " +
          "MATCH (workpack)-[isIn:IS_IN]->(parent:Workpack)-[:BELONGS_TO]->(plan) " +
         "DETACH DELETE isIn"
  )
  void unlinkParentRelation(
    Long idPlan,
    Long idWorkpackModel,
    Long idWorkpack
  );

  @Query("MATCH (workpack:Workpack)-[belongsTo:BELONGS_TO{linked:true}]->(plan:Plan)  " +
         "WHERE id(workpack)=$idWorkpack AND id(plan)=$idPlan " +
         "DETACH DELETE belongsTo")
  void unlinkPlan(
    Long idPlan,
    Long idWorkpack
  );

  @Query("call { " +
    "    match (w:Workpack)-[r:IS_LINKED_TO]->(wm:WorkpackModel) " +
    "    where id(w)=$idWorkpack and id(wm)=$idWorkpackModel " +
    "    return r " +
    "    limit 1 " +
    "} " +
    "delete r")
  void unlinkWorkpackModel(
    Long idWorkpack,
    Long idWorkpackModel
  );

  @Query("MATCH (plan)<-[:BELONGS_TO {linked: true}]-(workpack:Workpack)-[:IS_LINKED_TO]->(model:WorkpackModel) " +
          "WHERE id(plan) = $idPlan AND id(workpack) = $idWorkpack AND id(model) = $idWorkpackModel " +
          "MATCH (p:Person)-[linkedWorkpackPermission:CAN_ACCESS_WORKPACK{idPlan:id(plan)}]->(workpack) " +
          "DETACH DELETE linkedWorkpackPermission "
  )
  void unlinkPermissions(
          Long idPlan,
          Long idWorkpackModel,
          Long idWorkpack
  );

  @Query("MATCH (plan)<-[:BELONGS_TO {linked: true}]-(workpack:Workpack)-[:IS_LINKED_TO]->(model:WorkpackModel) " +
          "WHERE id(plan) = $idPlan AND id(workpack) = $idWorkpack AND id(model) = $idWorkpackModel " +
          "MATCH (workpack)<-[:IS_IN*]-(children:Workpack) " +
          "MATCH (p2:Person)-[childrenWorkpackPermission:CAN_ACCESS_WORKPACK {idPlan: id(plan)}]->(children) " +
          "DETACH DELETE  childrenWorkpackPermission")
  void unlinkChildrenPermissions(Long idPlan, Long idWorkpackModel, Long idWorkpack);
}
