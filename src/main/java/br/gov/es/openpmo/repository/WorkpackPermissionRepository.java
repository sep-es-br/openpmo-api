package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.relations.CanAccessWorkpack;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface WorkpackPermissionRepository extends Neo4jRepository<CanAccessWorkpack, Long> {

  @Query("MATCH (person:Person)-[canAccess:CAN_ACCESS_WORKPACK]->(workpack:Workpack) "
         + " WHERE id(workpack) = $idWorkpack "
         + " AND (id(person) = $idPerson OR $idPerson IS NULL) "
         + " RETURN person, canAccess, workpack ")
  List<CanAccessWorkpack> findByIdWorkpackAndIdPerson(
    @Param("idWorkpack") Long idWorkpack,
    @Param("idPerson") Long idPerson
  );

  @Query("MATCH (p:Person)-[ca:CAN_ACCESS_WORKPACK]->(o:Workpack) WHERE id(o) = $idWorkpack RETURN o,p,ca")
  List<CanAccessWorkpack> findByIdWorkpack(@Param("idWorkpack") Long idWorkpack);

  @Query("MATCH (person:Person)-[canAccessWorkpack:CAN_ACCESS_WORKPACK]->(workpack:Workpack)  " +
         "OPTIONAL MATCH (workpack)-[isIn:IS_IN]->(parent:Workpack) " +
         "OPTIONAL MATCH (parent)-[instanceBy:IS_INSTANCE_BY]->(parentModel:WorkpackModel)  " +
         "OPTIONAL MATCH (person)-[canAccessParent:CAN_ACCESS_WORKPACK]->(parent) " +
         "WITH person, canAccessWorkpack, workpack, isIn, parent, instanceBy, parentModel, canAccessParent " +
         "WHERE id(workpack)=$workpackId  " +
         "AND id(person)=$personId " +
         "RETURN person, isIn, parent, instanceBy, parentModel, canAccessParent")
  Set<CanAccessWorkpack> findInheritedPermission(
    Long workpackId,
    Long personId
  );

  @Query("MATCH (person:Person)-[permission:CAN_ACCESS_WORKPACK]->(workpack:Workpack) " +
         "WHERE id(person)=$idPerson " +
         "RETURN person, permission, workpack")
  Set<CanAccessWorkpack> findAllPermissionsOfPerson(Long idPerson);


}
