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

  @Query("MATCH (n)-[:IS_IN|IS_ADOPTED_BY|BELONGS_TO|IS_STRUCTURED_BY|IS_FORSEEN_ON|APPLIES_TO|FEATURES|MITIGATES|IS_TRIGGER_BY|ADDRESSES|IS_REPORTED_FOR|IS_BELONGS_TO|SCOPE_TO|IS_LINKED_TO|COMPOSES|IS_BASELINED_BY*0..]->(m)<-[:CAN_ACCESS_WORKPACK|CAN_ACCESS_PLAN|CAN_ACCESS_OFFICE {permissionLevel:'EDIT'}]-(p:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
          "WHERE id(n) IN $ids " +
          "with n limit 1 " +
          "with count(n)>0 as hasEditPermission " +
          "RETURN hasEditPermission")
  boolean hasEditPermission(
    @Param("ids") List<Long> ids,
    @Param("sub") String sub
  );

  @Query(
    "MATCH " +
    "   (m)<-[r:CAN_ACCESS_WORKPACK|CAN_ACCESS_PLAN]-(p:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
    "   WHERE r.permissionLevel IN ['READ', 'EDIT'] " +
    "MATCH path=shortestPath((n)<-[:IS_IN|IS_ADOPTED_BY|BELONGS_TO|IS_STRUCTURED_BY|IS_FORSEEN_ON|APPLIES_TO|FEATURES|MITIGATES|IS_TRIGGER_BY|ADDRESSES|IS_REPORTED_FOR|IS_BELONGS_TO|SCOPE_TO|IS_LINKED_TO|COMPOSES|IS_BASELINED_BY*0..]-(m)) " +
    "    WHERE id(n) IN $ids " +
    "RETURN count(path)>0")
  boolean hasBasicReadPermission(
    @Param("ids") List<Long> ids,
    @Param("sub") String sub
  );

  @Query("MATCH (n)-[:IS_IN|IS_ADOPTED_BY|BELONGS_TO|IS_STRUCTURED_BY|IS_FORSEEN_ON|APPLIES_TO|FEATURES|MITIGATES|IS_TRIGGER_BY|ADDRESSES|IS_REPORTED_FOR|IS_BELONGS_TO|SCOPE_TO|IS_LINKED_TO|COMPOSES|IS_BASELINED_BY*0..]->(m)<-[r:CAN_ACCESS_WORKPACK|CAN_ACCESS_PLAN|CAN_ACCESS_OFFICE]-(p:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
          "WHERE id(n) IN $ids " +
          "AND r.permissionLevel IN ['READ', 'EDIT'] " +
          "with n limit 1 " +
          "with count(n)>0 as hasEditPermission " +
          "RETURN hasEditPermission")
  boolean hasReadPermission(
    @Param("ids") List<Long> ids,
    @Param("sub") String sub
  );

  @Query("MATCH " +
         "    (m:Office)<-[:CAN_ACCESS_OFFICE {permissionLevel:'EDIT'}]-(p:Person) " +
         "    WHERE (p)-[:IS_AUTHENTICATED_BY {key:$sub}]->() " +
         "WITH p, m " +
         "MATCH " +
         "    path=shortestPath((n)-[*0..]->(m)) " +
         "    WHERE id(n) IN $ids " +
         "RETURN count(path)>0 "
  )
  boolean hasEditManagementPermission(
    @Param("ids") List<Long> ids,
    @Param("sub") String sub
  );


}
