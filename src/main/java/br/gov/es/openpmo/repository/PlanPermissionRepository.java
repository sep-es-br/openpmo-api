package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.model.relations.CanAccessOffice;
import br.gov.es.openpmo.model.relations.CanAccessPlan;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PlanPermissionRepository extends Neo4jRepository<CanAccessPlan, Long> {

  @Query("MATCH (p:Person)-[is:CAN_ACCESS_PLAN]->(o:Plan) WHERE id(o) = $idPlan AND id(p) = $idPerson  RETURN o,p,is")
  List<CanAccessPlan> findByIdPlanAndIdPerson(
    @Param("idPlan") Long idPlan,
    @Param("idPerson") Long idPerson
  );

  @Query("MATCH (p:Person)-[is:CAN_ACCESS_PLAN]->(o:Plan) WHERE id(o) = $idPlan RETURN o,p,is")
  List<CanAccessPlan> findByIdPlan(@Param("idPlan") Long idPlan);

  @Query("MATCH (plan:Plan)<-[belongsTo:BELONGS_TO]-(workpack:Workpack)<-[permission:CAN_ACCESS_WORKPACK]-(person:Person) " +
    " WHERE id(plan) = $idPlan " +
    " AND id(person) = $idPerson " +
    " AND permission.idPlan = id(plan) " +
    " RETURN count(permission) > 0")
  boolean hasWorkpackPermission(
    @Param("idPlan") Long idPlan,
    @Param("idPerson") Long idPerson
  );

  @Query("MATCH (person:Person)-[permission:CAN_ACCESS_PLAN]->(plan:Plan) " +
    "WHERE id(plan) = $idPlan " +
    "AND id(person) = $idPerson " +
    "RETURN count(permission) > 0")
  boolean hasPermissionPlan(
    @Param("idPlan") Long idPlan,
    @Param("idPerson") Long idPerson
  );

  @Query("MATCH (person:Person)-[canAccessWorkpack:CAN_ACCESS_WORKPACK]->(workpack:Workpack) " +
    "OPTIONAL MATCH (person:Person)-[canAccessPlan:CAN_ACCESS_PLAN]->(plan:Plan) " +
    "WITH person, canAccessPlan, canAccessWorkpack, workpack, plan " +
    "MATCH (workpack)-[belongsTo:BELONGS_TO]->(plan) " +
    "WHERE id(workpack)=$workpackId " +
    "AND id(person)=$personId " +
    "RETURN person, canAccessPlan, plan")
  Set<CanAccessPlan> findInheritedPermission(
    Long workpackId,
    Long personId
  );

  @Query("MATCH (person:Person)-[permission:CAN_ACCESS_PLAN]->(plan:Plan) " +
    "WHERE id(person)=$idPerson " +
    "RETURN person, permission, plan")
  Set<CanAccessPlan> findAllPermissionsOfPerson(@Param("idPerson") Long idPerson);

  @Query("MATCH (person:Person)-[canAccessPlan:CAN_ACCESS_PLAN]->(plan:Plan) " +
    "WHERE id(plan)=$idPlan AND id(person)=$idPerson " +
    "RETURN count(canAccessPlan)>0")
  boolean existsByIdWorkpackAndIdPerson(Long idPlan, Long idPerson);

  @Query("MATCH (p:Person) WHERE id(p) = $personId " +
          "MATCH (plan:Plan) WHERE id(plan) = $planId " +
          "CREATE (p)-[r:CAN_ACCESS_PLAN {organization: $organization, permitedRole: $role, permissionLevel: $permissionLevel}]->(plan) " +
          "RETURN r")
  CanAccessPlan createCanAccessPlan(@Param("personId") Long personId,
                                        @Param("planId") Long planId,
                                        @Param("organization") String organization,
                                        @Param("role") String role,
                                        @Param("permissionLevel") PermissionLevelEnum permissionLevel);

  @Query("MATCH (p:Person)-[r:CAN_ACCESS_PLAN]-(plan:Plan) WHERE id(p) = $personId AND id(plan) = $planId AND id(r) = $relationId " +
          "SET r.organization = $organization, " +
          "r.permitedRole = $role, " +
          "r.permissionLevel = $permissionLevel " +
          "RETURN r")
  CanAccessOffice updateCanAccessPlan(Long personId, Long planId, Long relationId, String organization, String role, PermissionLevelEnum permissionLevel);
}
