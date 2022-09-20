package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.relations.IsSharedWith;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkpackSharedRepository extends Neo4jRepository<IsSharedWith, Long>, CustomRepository {

  @Query("MATCH (n:Workpack)-[is:IS_SHARED_WITH]->(o:Office) WHERE id(n) = $idWorkpack RETURN n,o,is")
  List<IsSharedWith> findSharedWithDataByWorkpackId(@Param("idWorkpack") Long idWorkpack);

  @Query("MATCH (n:Workpack)-[is:IS_SHARED_WITH]->(o:Office) WHERE id(n) = $idWorkpack AND id(o) = $idOffice RETURN n,o,is")
  Optional<IsSharedWith> findByIdWorkpackAndIdOffice(
    @Param("idWorkpack") Long idWorkpack,
    @Param("idOffice") Long idOffice
  );

  @Query(
    "MATCH (office:Office)<-[:IS_ADOPTED_BY]-(planModel:PlanModel)<-[:BELONGS_TO]-(model:WorkpackModel), "
    + "(office)<-[sharedWith:IS_SHARED_WITH]-(workpack:Workpack) "
    + "OPTIONAL MATCH (workpack)-[instanceBy:IS_INSTANCE_BY]-(instance:WorkpackModel) "
    + "OPTIONAL MATCH (instance)<-[isInInstance:IS_IN*]-(instanceChildren:WorkpackModel) "
    + "WITH office, planModel, model, sharedWith, workpack, instanceBy, instance, isInInstance, instanceChildren "
    + "WHERE id(model) = $idWorkpackModel "
    + "RETURN office, planModel, sharedWith, workpack, instanceBy, instance, model, isInInstance, instanceChildren, [ "
    + "    [(workpack)-[bt:BELONGS_TO{linked:false}]->(originalPlan:Plan) | [bt, originalPlan]], "
    + "    [(originalPlan:Plan)-[iab:IS_ADOPTED_BY]->(originalOffice:Office) | [iab, originalOffice]] " +
    "]"
  )
  List<IsSharedWith> listAllWorkpacksShared(Long idWorkpackModel);

  @Query(
    "MATCH (workpack:Workpack) "
    + "OPTIONAL MATCH (workpack)-[instanceBy:IS_INSTANCE_BY]-(instance:WorkpackModel) "
    + "OPTIONAL MATCH (office:Office)<-[adoptedBy:IS_ADOPTED_BY]-(planModel:PlanModel)<-[belongsTo:BELONGS_TO]-(instance) "
    + "OPTIONAL MATCH (instance)<-[isInInstance:IS_IN*]-(instanceChildren:WorkpackModel) "
    + "WITH office, planModel, workpack, adoptedBy, belongsTo, instanceBy, instance, isInInstance, instanceChildren "
    + "WHERE workpack.public=true "
    + "RETURN office, planModel, workpack, adoptedBy, belongsTo, instanceBy, instance, isInInstance, instanceChildren, [ "
    + "    [(workpack)-[bt:BELONGS_TO{linked:false}]->(originalPlan:Plan) | [bt, originalPlan]], "
    + "    [(originalPlan:Plan)-[iab:IS_ADOPTED_BY]->(originalOffice:Office) | [iab, originalOffice]] " +
    "]"
  )
  List<Workpack> listAllWorkpacksPublic();

  @Query(
    "MATCH (workpack:Workpack) " +
    "OPTIONAL MATCH (workpack)-[isSharedWith:IS_SHARED_WITH]->(office:Office)  " +
    "WITH workpack, isSharedWith, office " +
    "WHERE id(workpack)=$idWorkpack AND ( " +
    "   (NOT (workpack)-[:IS_SHARED_WITH]->(:Office) AND workpack.public=true) OR" +
    "   (workpack)-[:IS_SHARED_WITH]->(:Office) " +
    ") " +
    "RETURN workpack, isSharedWith, office"
  )
  Optional<Workpack> findWorkpackById(Long idWorkpack);

  @Query(
    "MATCH (plan:Plan)-[isAdoptedBy:IS_ADOPTED_BY]->(office:Office)   " +
    "MATCH (workpack:Workpack)-[isIn:IS_IN]->(parent:Workpack)   " +
    "OPTIONAL MATCH (workpack)-[belongsTo:BELONGS_TO]->(plan)  " +
    "OPTIONAL MATCH (parent)-[parentBelongsTo:BELONGS_TO]->(plan)   " +
    "OPTIONAL MATCH (workpack)-[isSharedWith:IS_SHARED_WITH]->(office) " +
    "OPTIONAL MATCH (parent)-[parentIsSharedWith:IS_SHARED_WITH]->(office) " +
    "WITH plan, workpack, isIn, parent, isSharedWith, office, parentIsSharedWith, isAdoptedBy, belongsTo, parentBelongsTo  " +
    "WHERE id(plan)=$idPlan AND id(workpack)=$idWorkpack  " +
    "AND (  " +
    "        (   " +
    "            (NOT (workpack)-[:IS_SHARED_WITH]->(:Office) AND workpack.public=true) OR  " +
    "            (workpack)-[:IS_SHARED_WITH]->(:Office)   " +
    "        ) OR  " +
    "        (  " +
    "            (NOT (parent)-[:IS_SHARED_WITH]->(:Office) AND parent.public=true) OR  " +
    "            (parent)-[:IS_SHARED_WITH]->(:Office)  " +
    "        )  " +
    ")  " +
    "RETURN office, isAdoptedBy, plan, parentIsSharedWith, isSharedWith, " +
    "CASE   " +
    "    WHEN belongsTo.linked = true THEN workpack " +
    "    WHEN parentBelongsTo.linked = true THEN parent " +
    "END"
  )
  Optional<Workpack> findSharedWorkpackByIdPlan(
    Long idWorkpack,
    Long idPlan
  );


  @Query(
    "MATCH (workpack:Workpack)-[isLinkedTo:IS_LINKED_TO]->(workpackModel:WorkpackModel) " +
    "MATCH (workpackModel)-[belongsTo:BELONGS_TO]->(planModel:PlanModel) " +
    "MATCH (planModel:PlanModel)-[isAdoptedBy:IS_ADOPTED_BY]->(office:Office)<-[isSharedWith:IS_SHARED_WITH]-(workpack) " +
    "WHERE id(workpack)=$idWorkpack AND id(workpackModel)=$idWorkpackModelLinked " +
    "RETURN workpack, isSharedWith, office"
  )
  Optional<IsSharedWith> commonSharedWithBetweenLinkedWorkpackModelAndWorkpack(
    Long idWorkpack,
    Long idWorkpackModelLinked
  );

  @Query(
    "MATCH (workpack:Workpack)   " +
    "MATCH (workpack)-[shared:IS_SHARED_WITH]->(office:Office) " +
    "MATCH (office)<-[planAdoptedBy:IS_ADOPTED_BY]-(planModel:PlanModel) " +
    "OPTIONAL MATCH (workpack)-[isLinkedTo:IS_LINKED_TO]->(linkedModel:WorkpackModel), " +
    "(linkedModel)-[modelBelongsTo:BELONGS_TO]->(planModel) " +
    "WITH workpack, shared, office, planAdoptedBy, planModel, isLinkedTo, linkedModel, modelBelongsTo   " +
    "WHERE id(workpack)=$idWorkpack AND (id(office)=$idOffice OR $idOffice IS NULL)  " +
    "DETACH DELETE isLinkedTo"
  )
  void deleteExternalLinkedRelationship(
    Long idWorkpack,
    Long idOffice
  );

  @Query(
    "MATCH (workpack:Workpack)   " +
    "OPTIONAL MATCH (workpack)-[shared:IS_SHARED_WITH]->(office:Office)  " +
    "OPTIONAL MATCH (plan:Plan)-[isAdoptedBy:IS_ADOPTED_BY]->(office)  " +
    "OPTIONAL MATCH (workpack)-[belongsTo:BELONGS_TO{linked:true}]->(plan)   " +
    "WITH workpack, shared, office, plan, belongsTo, isAdoptedBy   " +
    "WHERE id(workpack)=$idWorkpack AND (id(office)=$idOffice OR $idOffice IS NULL)  " +
    "DETACH DELETE shared, belongsTo "
  )
  void deleteSharedRelationship(
    Long idWorkpack,
    Long idOffice
  );

  @Query(
    "MATCH (workpack:Workpack)  " +
    "MATCH (:Workpack)-[:BELONGS_TO{linked:true}]->(plan:Plan)  " +
    "OPTIONAL MATCH (children:Workpack)-[isIn:IS_IN]->(workpack)  " +
    "OPTIONAL MATCH (workpack)-[belongsTo:BELONGS_TO]->(plan)  " +
    "OPTIONAL MATCH (children)-[childrenBelongsTo:BELONGS_TO]->(plan)  " +
    "OPTIONAL MATCH (office:Office)<-[adoptedBy:IS_ADOPTED_BY]-(plan)  " +
    "OPTIONAL MATCH (person:Person)-[permission:CAN_ACCESS_WORKPACK{idPlan:id(plan)}]->(workpack)  " +
    "OPTIONAL MATCH (person2:Person)-[childrenPermission:CAN_ACCESS_WORKPACK{idPlan:id(plan)}]->(children)  " +
    "WITH workpack, children, isIn, plan, belongsTo, childrenBelongsTo,  " +
    "office, adoptedBy, person, permission, childrenPermission, person2  " +
    "WHERE id(workpack)=$idWorkpack AND (id(office)=$idOffice OR $idOffice  IS NULL) " +
    "DETACH DELETE permission, childrenPermission"
  )
  void deleteExternalPermission(
    Long idWorkpack,
    Long idOffice
  );

  @Query(
    "MATCH (workpack:Workpack) " +
    "MATCH (workpack)-[belongsTo:BELONGS_TO{linked:true}]->(plan:Plan) " +
    "OPTIONAL MATCH (office:Office)<-[adoptedBy:IS_ADOPTED_BY]-(plan) " +
    "WITH workpack, belongsTo, plan, office, adoptedBy " +
    "WHERE id(workpack)=$idWorkpack AND (id(office)=$idOffice OR $idOffice IS NULL) " +
    "OPTIONAL MATCH (workpack)-[isIn:IS_IN]->(parent:Workpack) " +
    "WITH workpack, belongsTo, plan, isIn, parent, office, adoptedBy " +
    "WHERE (parent)-[:BELONGS_TO]-(plan) " +
    "DETACH DELETE isIn"
  )
  void deleteExternalParent(
    Long idWorkpack,
    Long idOffice
  );

}
