package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.person.detail.permissions.CanAccessPlanResultDto;
import br.gov.es.openpmo.dto.person.detail.permissions.PlanResultDto;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.relations.BelongsTo;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlanRepository extends Neo4jRepository<Plan, Long>, CustomRepository {



  @Query("MATCH (p:Plan)-[r:IS_ADOPTED_BY]->(o:Office) "
      + "WHERE id(o) = $idOffice "
      + "RETURN id(p) AS id, p.name AS name, ID(o) AS idOffice ")
  List<PlanResultDto> findAllPlanResultByIdOffice(Long idOffice);

  @Query("MATCH (person:Person)-[permission:CAN_ACCESS_PLAN]->(plan:Plan)-[r:IS_ADOPTED_BY]->(o:Office) " +
      "WHERE ID(person) = $idPerson AND ID(o) = $idOffice AND permission.permissionLevel <> 'NONE' " +
      "RETURN ID(plan) AS idPlan, ID(person) AS idPerson, permission.permissionLevel AS permissionLevel " +
      ", permission.role AS role, ID(o) AS idOffice ")
  List<CanAccessPlanResultDto> findAllCanAccessPlanResultDtoByIdPerson(Long idPerson, Long idOffice);

  @Query("MATCH (plan:Plan) " +
      "WHERE id(plan) = $id " +
      "RETURN plan, [ " +
      "[ (n)-[r_i1:`IS_STRUCTURED_BY`]->(p1:`PlanModel`) | [ r_i1, p1 ] ]," +
      "[ (n)-[r_i1:`IS_ADOPTED_BY`]->(o1:`Office`) | [ r_i1, o1 ] ] " +
      "]"
  )
  Optional<Plan> findById(Long id);

  @Query("MATCH (p: Plan)-[r:IS_ADOPTED_BY]->(o:Office) WHERE id(o)= $id RETURN id(p) ORDER BY p.start DESC")
  List<Long> findAllIdsInOfficeOrderByStartDesc(@Param("id") Long id);

  @Query("MATCH (p: Plan) RETURN id(p) ")
  List<Long> findAllIds();

  @Query("MATCH (person:Person)-[permission:CAN_ACCESS_PLAN]->(plan:Plan)-[r:IS_ADOPTED_BY]->(o:Office) " +
      "WHERE id(person) = $idPerson AND id(o) = $idOffice AND id(plan) = $idPlan " +
      "RETURN id(plan)")
  List<Long> findAllWithPermissionByUserAndOffice(
      @Param("idOffice") Long idOffice,
      @Param("idPerson") Long idPerson,
      @Param("idPlan") Long idPlan
  );

  @Query("MATCH (p: Plan)-[r:IS_ADOPTED_BY]->(o:Office) "
         + ", (p)-[sb:IS_STRUCTURED_BY]->(pm:PlanModel) "
         + "WHERE id(o)= $id RETURN p,r,o,sb,pm")
  List<Plan> findAllInOffice(@Param("id") Long id);

  @Query("MATCH (workpack:Workpack)-[belongsTo:BELONGS_TO]->(plan:Plan) " +
         "MATCH (plan)-[adoptedBy:IS_ADOPTED_BY]->(office:Office) " +
         "WHERE id(workpack)=$id AND (belongsTo.linked is null or belongsTo.linked=false) " +
         "RETURN plan, workpack, belongsTo, adoptedBy, office"
  )
  Optional<Plan> findPlanWithNotLinkedBelongsToRelationship(Long id);

  @Query("MATCH (plan:Plan)  " +
         "WHERE id(plan)=$idPlan " +
         "MATCH (workpack:Workpack)-[isIn:IS_IN]->(parent:Workpack) " +
         "OPTIONAL MATCH (workpack)-[belongsTo:BELONGS_TO]->(plan)  " +
         "OPTIONAL MATCH (parent)-[parentBelongsTo:BELONGS_TO]->(plan)  " +
         "WITH plan, workpack, isIn, parent, belongsTo, parentBelongsTo " +
         "WHERE id(workpack)=$idWorkpack  " +
         "RETURN workpack, parent, belongsTo, parentBelongsTo, plan "
  )
  List<BelongsTo> hasLinkWithWorkpack(
    Long idWorkpack,
    Long idPlan
  );

  @Query("MATCH (p: Plan)-[r:IS_ADOPTED_BY]->(o:Office) " +
          ", (p)-[sb:IS_STRUCTURED_BY]->(pm:PlanModel) " +
          "WHERE id(o)= $id " +
          "WITH p,r,o,sb,pm, " +
          "apoc.text.levenshteinSimilarity(apoc.text.clean(p.name), apoc.text.clean($term)) AS nameScore, " +
          "apoc.text.levenshteinSimilarity(apoc.text.clean(p.fullName), apoc.text.clean($term)) AS fullNameScore " +
          "WITH *, CASE WHEN nameScore > fullNameScore THEN nameScore ELSE fullNameScore END AS score " +
          "WHERE score > $searchCutOffScore " +
          "RETURN p,r,o,sb,pm " +
          "ORDER BY score DESC")
  List<Plan> findAllInOfficeByTerm(
          @Param("id") Long id,
          @Param("term") String term,
          @Param("searchCutOffScore") double searchCutOffScore
  );

  @Query(
          "match (p:Plan)<-[bt:BELONGS_TO]-(w:Workpack{deleted:false}) " +
                  "where id(p)=$idPlan " +
                  "and (NOT EXISTS(bt.linked) or bt.linked = false) " +
                  "return id(w)"
  )
  List<Long> findWorkpacksBelongsToPlan(@Param("idPlan") Long idPlan);

  @Query(
          "match (p:Plan)<-[bt:BELONGS_TO]-(w:Workpack{deleted:false})<-[c:CAN_ACCESS_WORKPACK]-(person:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
                  "where id(p)=$idPlan " +
                  "and (NOT EXISTS(bt.linked) OR bt.linked = false) " +
                  "AND c.permissionLevel in ['EDIT', 'READ'] " +
                  "return id(w)"
  )
  List<Long> findWorkpacksBelongsToPlanWithPermission(
          @Param("idPlan") Long idPlan,
          @Param("sub") String sub
  );

}
