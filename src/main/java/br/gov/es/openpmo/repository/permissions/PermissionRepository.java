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
    "    path=(n)-[:IS_IN|IS_ADOPTED_BY|BELONGS_TO|IS_STRUCTURED_BY|IS_FORSEEN_ON|APPLIES_TO|FEATURES|MITIGATES|IS_TRIGGER_BY|ADDRESSES|IS_REPORTED_FOR|IS_BELONGS_TO|SCOPE_TO|IS_LINKED_TO|COMPOSES|IS_BASELINED_BY*0..]->(m) " +
    "WHERE id(n) IN $ids " +
    "AND (" +
    "  (m)<-[:CAN_ACCESS_WORKPACK {permissionLevel:'EDIT'}]-(p) OR " +
    "  (m)<-[:CAN_ACCESS_OFFICE {permissionLevel:'EDIT'}]-(p) OR " +
    "  (m)<-[:CAN_ACCESS_PLAN {permissionLevel:'EDIT'}]-(p) " +
    ")" +
    "RETURN count(path)>0"
  )
  boolean hasEditPermission(
    @Param("ids") List<Long> ids,
    @Param("sub") String sub
  );

  @Query(
    "MATCH " +
    "   (m:Workpack)<-[r:CAN_ACCESS_WORKPACK]-(p:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
    "   WHERE r.permissionLevel IN ['READ', 'EDIT'] " +
    "MATCH path=shortestPath((n)<-[:IS_IN|IS_ADOPTED_BY|BELONGS_TO|IS_STRUCTURED_BY|IS_FORSEEN_ON|APPLIES_TO|FEATURES|MITIGATES|IS_TRIGGER_BY|ADDRESSES|IS_REPORTED_FOR|IS_BELONGS_TO|SCOPE_TO|IS_LINKED_TO|COMPOSES|IS_BASELINED_BY*0..]-(m)) " +
    "    WHERE id(n) IN $ids " +
    "RETURN count(path)>0")
  boolean hasBasicReadPermission(
    @Param("ids") List<Long> ids,
    @Param("sub") String sub
  );

  @Query(
    "MATCH  " +
    "    (p:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-(a:AuthService), " +
    "    path=(n)-[:IS_IN|IS_ADOPTED_BY|BELONGS_TO|IS_STRUCTURED_BY|IS_FORSEEN_ON|APPLIES_TO|FEATURES|MITIGATES|IS_TRIGGER_BY|ADDRESSES|IS_REPORTED_FOR|IS_BELONGS_TO|SCOPE_TO|IS_LINKED_TO|COMPOSES|IS_BASELINED_BY*0..]->(m) " +
    "    WHERE id(n) IN $ids " +
    "    AND ( " +
    "    (m)<-[:CAN_ACCESS_WORKPACK {permissionLevel:'EDIT'} ]-(p) OR " +
    "    (m)<-[:CAN_ACCESS_WORKPACK {permissionLevel:'READ'} ]-(p) OR " +
    "    (m)<-[:CAN_ACCESS_OFFICE {permissionLevel:'EDIT'}]-(p) OR " +
    "    (m)<-[:CAN_ACCESS_OFFICE {permissionLevel:'READ'}]-(p) OR " +
    "    (m)<-[:CAN_ACCESS_PLAN {permissionLevel:'EDIT'}]-(p) OR " +
    "    (m)<-[:CAN_ACCESS_PLAN {permissionLevel:'READ'}]-(p) " +
    ") " +
    "RETURN count(path)>0"
  )
  boolean hasReadPermission(
    @Param("ids") List<Long> ids,
    @Param("sub") String sub
  );

  @Query("MATCH " +
         "    (m:Office)<-[:CAN_ACCESS_OFFICE {permissionLevel:'EDIT'}]-(p:Person) " +
         "    WHERE (p)-[:IS_AUTHENTICATED_BY {key:$sub}]->() " +
         "WITH p, m " +
         "MATCH " +
         "    path=((n)-[*0..]->(m)) " +
         "    WHERE id(n) IN $ids " +
         "RETURN count(path)>0 "
  )
  boolean hasEditManagementPermission(
    @Param("ids") List<Long> ids,
    @Param("sub") String sub
  );


}
