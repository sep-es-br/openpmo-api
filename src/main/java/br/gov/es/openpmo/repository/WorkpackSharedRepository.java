package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
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
    "MATCH (office:Office)<-[:IS_ADOPTED_BY]-(planModel:PlanModel)<-[:BELONGS_TO]-(model:WorkpackModel), (planLink:Plan), "
    + "(office)<-[sharedWith:IS_SHARED_WITH]-(workpack:Workpack)-[:BELONGS_TO]->(plan:Plan) "
    + "OPTIONAL MATCH (workpack)-[instanceBy:IS_INSTANCE_BY]-(instance:WorkpackModel) "
    + "OPTIONAL MATCH (instance)<-[isInInstance:IS_IN*]-(instanceChildren:WorkpackModel) "
    + "WITH office, planModel, model, sharedWith, workpack, instanceBy, instance, isInInstance, instanceChildren "
    + "WHERE id(model) = $idWorkpackModel AND id(plan) <> $idPlan AND id(planLink) = $idPlan AND NOT (workpack)-[:BELONGS_TO{linked:true}]->(planLink) "
    + "RETURN office, planModel, sharedWith, workpack, instanceBy, instance, model, isInInstance, instanceChildren, [ "
    + "    [(workpack)-[bt:BELONGS_TO]->(originalPlan:Plan) WHERE NOT EXISTS(bt.linked) OR bt.linked = false | [bt, originalPlan]], "
    + "    [(workpack)-[bt2:BELONGS_TO]->(:Plan)-[iab:IS_ADOPTED_BY]->(originalOffice:Office) WHERE NOT EXISTS(bt2.linked) OR bt2.linked = false | [iab, originalOffice]] " +
    "]"
  )
  List<IsSharedWith> listAllWorkpacksShared(Long idWorkpackModel, Long idPlan);

  @Query(
    "MATCH (workpack:Workpack)-[:BELONGS_TO]->(plan:Plan), (planLink:Plan) "
    + "OPTIONAL MATCH (workpack)-[instanceBy:IS_INSTANCE_BY]-(instance:WorkpackModel) "
    + "OPTIONAL MATCH (office:Office)<-[adoptedBy:IS_ADOPTED_BY]-(planModel:PlanModel)<-[belongsTo:BELONGS_TO]-(instance) "
    + "OPTIONAL MATCH (instance)<-[isInInstance:IS_IN*]-(instanceChildren:WorkpackModel) "
    + "WITH office, planModel, workpack, adoptedBy, belongsTo, instanceBy, instance, isInInstance, instanceChildren "
    + "WHERE workpack.public=true AND id(plan) <> $idPlan AND id(planLink) = $idPlan AND NOT (workpack)-[:BELONGS_TO{linked:true}]->(planLink) "
    + "RETURN office, planModel, workpack, adoptedBy, belongsTo, instanceBy, instance, isInInstance, instanceChildren, [ "
    + "    [(workpack)-[bt:BELONGS_TO]->(originalPlan:Plan) WHERE NOT EXISTS(bt.linked) OR bt.linked = false | [bt, originalPlan]], "
    + "    [(originalPlan:Plan)-[iab:IS_ADOPTED_BY]->(originalOffice:Office) | [iab, originalOffice]] " +
    "]"
  )
  List<Workpack> listAllWorkpacksPublic(Long idPlan);

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


  @Query("MATCH (:Workpack)-[r:IS_SHARED_WITH]->(:Office) WHERE id(r) = $relationId " +
          "SET r.permissionLevel = $permissionLevel")
  void updatePermissionLevel(Long relationId, PermissionLevelEnum permissionLevel);

  @Query("MATCH (w:Workpack), (o:Office) " +
          "WHERE id(w) = $workpackId AND id(o) = $officeId " +
          "CREATE (w)-[r:IS_SHARED_WITH {permissionLevel: $permissionLevel}]->(o) " +
          "RETURN r")
  IsSharedWith createIsSharedWith(Long workpackId, Long officeId, PermissionLevelEnum permissionLevel);

  @Query("MATCH (w:Workpack)-[bb:BELONGS_TO {linked: true}]->(p:Plan)-[:IS_ADOPTED_BY]->(o:Office) where id(w) = $idWorkpack and id(o) = $idOffice return count(bb)> 0")
  boolean findAnyLink(Long idWorkpack, Long idOffice);

  @Query("MATCH (w:Workpack)-[share:IS_SHARED_WITH]->(o:Office) " +
          "WHERE id(share) = $idSharedWith " +
          "DETACH DELETE share")
  void deleteSharedWith(Long idSharedWith);
}
