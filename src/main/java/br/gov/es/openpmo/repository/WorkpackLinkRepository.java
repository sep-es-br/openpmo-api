package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.relations.IsLinkedTo;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
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

  @Query("MATCH (workpack:Workpack) " +
         "MATCH (plan:Plan)-[:IS_STRUCTURED_BY]->(:PlanModel)<-[:BELONGS_TO]-(workpackModel:WorkpackModel) " +
         "MATCH (workpack)-[:IS_LINKED_TO]->(workpackModel) " +
         "WHERE id(workpack)=$idWorkpack AND id(plan)=$idPlan " +
         "RETURN workpackModel"
  )
  Optional<WorkpackModel> findWorkpackModelLinkedByWorkpackAndPlan(
    Long idWorkpack,
    Long idPlan
  );

  @Query("match (w:Workpack), (wm:WorkpackModel) " +
    "where id(w)=$idWorkpack and id(wm)=$idWorkpackModel " +
    "create (w)-[:IS_LINKED_TO]->(wm)")
  void linkWorkpackAndWorkpackModel(
    Long idWorkpack,
    Long idWorkpackModel
  );

  @Query("MATCH (plan:Plan)  " +
         "WHERE id(plan)=$idPlan " +
         "MATCH (workpack:Workpack)-[belongsTo:BELONGS_TO]->(plan) " +
         "MATCH (workpack)-[linkedTo:IS_LINKED_TO]->(model:WorkpackModel) " +
         "OPTIONAL MATCH (workpack)-[isIn:IS_IN]->(parent:Workpack)-[:BELONGS_TO]->(plan) " +
         "WITH plan, workpack, belongsTo, isIn, parent, linkedTo, model " +
         "WHERE id(workpack)=$idWorkpack AND id(model)=$idWorkpackModel " +
         "DETACH DELETE isIn"
  )
  void unlinkParentRelation(
    Long idPlan,
    Long idWorkpackModel,
    Long idWorkpack
  );

  @Query("MATCH (plan:Plan)  " +
         "WHERE id(plan)=$idPlan " +
         "MATCH (workpack:Workpack)-[belongsTo:BELONGS_TO]->(plan) " +
         "MATCH (workpack)-[linkedTo:IS_LINKED_TO]->(model:WorkpackModel) " +
         "MATCH (workpack)<-[isIn:IS_IN*]-(children:Workpack) " +
         "OPTIONAL MATCH (p:Person)-[linkedWorkpackPermission:CAN_ACCESS_WORKPACK{idPlan:id(plan)}]->(workpack) " +
         "OPTIONAL MATCH (p2:Person)-[childrenWorkpackPermission:CAN_ACCESS_WORKPACK{idPlan:id(plan)}]->(children) " +
         "WITH plan, workpack, belongsTo, linkedTo, model, p, linkedWorkpackPermission, p2, childrenWorkpackPermission, " +
         "children, isIn " +
         "WHERE id(workpack)=$idWorkpack AND id(model)=$idWorkpackModel " +
         "DETACH DELETE linkedWorkpackPermission, childrenWorkpackPermission "
  )
  void unlinkPermissions(
    Long idPlan,
    Long idWorkpackModel,
    Long idWorkpack
  );

  @Query("MATCH (plan:Plan)   " +
         "WHERE id(plan)=$idPlan  " +
         "MATCH (workpack:Workpack)-[belongsTo:BELONGS_TO]->(plan)  " +
         "OPTIONAL MATCH (workpack)-[isIn:IS_IN]->(parent:Workpack)-[:BELONGS_TO]->(plan)  " +
         "WITH plan, workpack, belongsTo, isIn, parent " +
         "WHERE id(workpack)=$idWorkpack " +
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

  @Query(
    "MATCH (w:Workpack) " +
    "MATCH (wm:WorkpackModel)-[bt:BELONGS_TO]->(plm:PlanModel)<-[st:IS_STRUCTURED_BY]-(pl:Plan) " +
    "MATCH (w)-[ii:IS_IN*]->(wp:Workpack)-[lt:IS_LINKED_TO]->(wm) " +
    "WHERE id(w)=$idWorkpack AND id(pl)=$idPlan " +
    "RETURN wp, lt, wm, [" +
    "   [ (wp)-[ii2:IS_IN*]->(wpp:Workpack)-[bt2:BELONGS_TO]->(pl2:Plan) | [ii2, wpp, bt2, pl2] ] " +
    "]"
  )
  Optional<IsLinkedTo> findWorkpackParentLinked(
    Long idWorkpack,
    Long idPlan
  );

}
