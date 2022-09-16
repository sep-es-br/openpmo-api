package br.gov.es.openpmo.repository.permissions;

import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends Neo4jRepository<Workpack, Long>, CustomRepository {

  @Query(
    "MATCH" +
    "(p:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-(a:AuthService)," +
    "path=shortestPath((n)-[*0..]->(m)) " +
    "WHERE id(n) IN $ids " +
    "AND (" +
    "  (m)<-[:CAN_ACCESS_WORKPACK {permissionLevel:'EDIT'}]-(p) OR " +
    "  (m)<-[:CAN_ACCESS_OFFICE {permissionLevel:'EDIT'}]-(p) OR " +
    "  (m)<-[:CAN_ACCESS_PLAN {permissionLevel:'EDIT'}]-(p) " +
    ")" +
    "RETURN count(path)"
  )
  Long hasEditPermission(
    @Param("ids") List<Long> ids,
    @Param("sub") String sub
  );

  @Query(
    "MATCH " +
    "    (p:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-(a:AuthService), " +
    "    path=shortestPath((n)<-[*0..]-(m)) " +
    "    WHERE id(n) IN $ids " +
    "    AND (" +
    "    (m)<-[:CAN_ACCESS_WORKPACK {permissionLevel:'EDIT'} ]-(p) OR" +
    "    (m)<-[:CAN_ACCESS_WORKPACK {permissionLevel:'READ'} ]-(p) " +
    ")" +
    "RETURN count(path)")
  Long hasBasicReadPermission(
    @Param("ids") List<Long> ids,
    @Param("sub") String sub
  );

  @Query(
    "MATCH  " +
    "    (p:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-(a:AuthService), " +
    "    path=shortestPath((n)-[*0..]->(m)) " +
    "    WHERE id(n) IN $ids " +
    "    AND ( " +
    "    (m)<-[:CAN_ACCESS_WORKPACK {permissionLevel:'EDIT'} ]-(p) OR " +
    "    (m)<-[:CAN_ACCESS_WORKPACK {permissionLevel:'READ'} ]-(p) OR " +
    "    (m)<-[:CAN_ACCESS_OFFICE {permissionLevel:'EDIT'}]-(p) OR " +
    "    (m)<-[:CAN_ACCESS_OFFICE {permissionLevel:'READ'}]-(p) OR " +
    "    (m)<-[:CAN_ACCESS_PLAN {permissionLevel:'EDIT'}]-(p) OR " +
    "    (m)<-[:CAN_ACCESS_PLAN {permissionLevel:'READ'}]-(p) " +
    ") " +
    "RETURN count(path)"
  )
  Long hasReadPermission(
    @Param("ids") List<Long> ids,
    @Param("sub") String sub
  );

  @Query("MATCH  " +
         "    (p:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-(a:AuthService), " +
         "    path=shortestPath((n)-[*0..]->(m)) " +
         "    WHERE id(n) IN $ids AND " +
         "    (m)<-[:CAN_ACCESS_OFFICE {permissionLevel:'EDIT'}]-(p) " +
         "RETURN count(path) "
  )
  Long hasEditManagementPermission(
    @Param("ids") List<Long> ids,
    @Param("sub") String sub
  );


}
