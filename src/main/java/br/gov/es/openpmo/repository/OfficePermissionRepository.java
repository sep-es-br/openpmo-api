package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.relations.CanAccessOffice;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface OfficePermissionRepository extends Neo4jRepository<CanAccessOffice, Long>, CustomRepository {

  @Query("MATCH (p:Person)-[is:CAN_ACCESS_OFFICE]->(o:Office) WHERE id(o) = $idOffice AND id(p) = $idPerson  RETURN o,p,is")
  List<CanAccessOffice> findByIdOfficeAndIdPerson(
    @Param("idOffice") Long idOffice,
    @Param("idPerson") Long idPerson
  );

  @Query("MATCH (p:Person)-[is:CAN_ACCESS_OFFICE]->(o:Office) WHERE id(o) = $idOffice RETURN o,p,is")
  List<CanAccessOffice> findByIdOffice(@Param("idOffice") Long idOffice);

  @Query("MATCH (person:Person)-[canAccessPlan:CAN_ACCESS_PLAN]->(:Plan)-[r:IS_ADOPTED_BY]->(office:Office) "
    + "WHERE id(office) = $idOffice AND id(person) = $idPerson "
    + "RETURN count(canAccessPlan) > 0"
  )
  boolean hasCanAccessPlan(
    @Param("idOffice") Long idOffice,
    @Param("idPerson") Long idPerson
  );

  @Query("MATCH (office:Office)<-[:IS_ADOPTED_BY]-(plan:Plan)<-[:BELONGS_TO]-(workpack:Workpack), "
    + " (workpack)<-[accessWorkpack:CAN_ACCESS_WORKPACK]-(person:Person) "
    + " WHERE id(office) = $idOffice "
    + " AND id(person) = $idPerson "
    + " AND accessWorkpack.idPlan=id(plan) "
    + " RETURN count(accessWorkpack) > 0")
  boolean hasCanAccessWorkpack(
    @Param("idOffice") Long idOffice,
    @Param("idPerson") Long idPerson
  );

  @Query("MATCH (person:Person)-[canAccessWorkpack:CAN_ACCESS_WORKPACK]->(workpack:Workpack) " +
    "OPTIONAL MATCH (person:Person)-[canAccessOffice:CAN_ACCESS_OFFICE]->(office:Office) " +
    "WITH person, canAccessWorkpack, workpack, canAccessOffice, office " +
    "MATCH (workpack:Workpack)-[belongsTo:BELONGS_TO]->(plan:Plan)-[isAdoptedBy:IS_ADOPTED_BY]->(office) " +
    "WHERE id(workpack)=$workpackId AND id(person)=$personId " +
    "RETURN office, person, canAccessOffice")
  Set<CanAccessOffice> findInheritedPermission(
    Long workpackId,
    Long personId
  );

  @Query("MATCH (person:Person)-[permission:CAN_ACCESS_OFFICE]->(office:Office) " +
    "WHERE id(person)=$idPerson " +
    "RETURN person, permission, office")
  Set<CanAccessOffice> findAllPermissionsOfPerson(@Param("idPerson") Long idPerson);

  @Query("MATCH (person:Person)-[canAccessOffice:CAN_ACCESS_OFFICE]->(office:Office) " +
    "WHERE id(office)=$idOffice AND id(person)=$idPerson " +
    "RETURN count(canAccessOffice)>0")
  boolean existsByIdWorkpackAndIdPerson(Long idOffice, Long idPerson);

}
