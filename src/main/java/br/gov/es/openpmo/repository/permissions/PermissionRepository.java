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
      " CALL {  " +
              "MATCH (children1:Workpack)-[:IS_IN*]->(parent1:Workpack)<-[c1:CAN_ACCESS_WORKPACK]-(p1:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
              "WHERE ID(children1) = $id " +
              "RETURN COUNT(DISTINCT ID(c1)) AS totalPermissions " +
              "UNION ALL " +
              "MATCH (parent2:Workpack)<-[:IS_IN*]-(children2:Workpack)<-[c2:CAN_ACCESS_WORKPACK]-(p2:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
              "WHERE ID(parent2) = $id " +
              "RETURN COUNT(DISTINCT ID(c2)) AS totalPermissions " +
              "UNION ALL " +
              "MATCH (w:Workpack)<-[c3:CAN_ACCESS_WORKPACK]-(p3:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
              "WHERE ID(w) = $id " +
              "RETURN COUNT(DISTINCT ID(c3)) AS totalPermissions " +
      "} " +
      "RETURN SUM(totalPermissions) > 0 "
      )
  boolean hasPermissionWorkpack(
      @Param("id") Long id,
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


  @Query(
      "CALL {  " +
                "MATCH (w1:Workpack)-[:BELONGS_TO]->(plan1:Plan)<-[c1:CAN_ACCESS_PLAN]-(p1:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
                "WHERE ID (w1) = $id " +
                "AND c1.permissionLevel in ['EDIT', 'READ']" +
                "RETURN COUNT(DISTINCT ID(c1)) AS totalPermissions " +
                "UNION ALL " +
                "MATCH (w2:Workpack)-[:BELONGS_TO]->(plan2:Plan)-[:IS_ADOPTED_BY]->(o:Office)<-[c2:CAN_ACCESS_OFFICE]-(p2:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
                "WHERE ID (w2) = $id " +
                "AND c2.permissionLevel in ['EDIT', 'READ']" +
                "RETURN COUNT(DISTINCT ID(c2)) AS totalPermissions " +
      "} " +
      "RETURN SUM(totalPermissions) > 0 "
  )
  boolean hasPermisionOfficeOrPlan(
      @Param("id") Long id,
      @Param("sub") String sub
  );

  @Query(
          "CALL {  " +
                  "MATCH (plan1:Plan)<-[c1:CAN_ACCESS_PLAN]-(p1:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
                  "WHERE ID (plan1) = $id " +
                  "AND c1.permissionLevel in ['EDIT', 'READ'] " +
                  "RETURN COUNT(DISTINCT ID(c1)) AS totalPermissions " +
                  "UNION ALL " +
                  "MATCH (plan2:Plan)-[:IS_ADOPTED_BY]->(o:Office)<-[c2:CAN_ACCESS_OFFICE]-(p2:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
                  "WHERE ID (plan2) = $id " +
                  "AND c2.permissionLevel in ['EDIT', 'READ'] " +
                  "RETURN COUNT(DISTINCT ID(c2)) AS totalPermissions " +
                  "} " +
                  "RETURN SUM(totalPermissions) > 0 "
  )
  boolean hasPermissionOfficeOrPlanByIdPlan(
          @Param("id") Long id,
          @Param("sub") String sub
  );

  @Query(
          " CALL {  " +
                  "MATCH (children1:Workpack)-[:IS_IN*]->(parent1:Workpack)<-[c1:CAN_ACCESS_WORKPACK]-(p1:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
                  "WHERE ID(children1) = $id " +
                  "AND c1.permissionLevel in ['EDIT', 'READ'] " +
                  "RETURN COUNT(DISTINCT ID(c1)) AS totalPermissions " +
                  "UNION ALL " +
                  "MATCH (w:Workpack)<-[c3:CAN_ACCESS_WORKPACK]-(p3:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
                  "WHERE ID(w) = $id " +
                  "AND c3.permissionLevel in ['EDIT', 'READ'] " +
                  "RETURN COUNT(DISTINCT ID(c3)) AS totalPermissions " +
                  "} " +
                  "RETURN SUM(totalPermissions) > 0 "
  )
  boolean hasPermissionWorkpackSelfOrParents(
          @Param("id") Long id,
          @Param("sub") String sub
  );

  @Query("MATCH (w:Workpack)-[i:IS_IN*]->(p:Workpack), " +
          "(w)<-[c3:CAN_ACCESS_WORKPACK]-(p3:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
          "WHERE id(p) IN $idsWorkpacks " +
          "AND c3.permissionLevel in ['EDIT', 'READ'] " +
          "RETURN id(w)")
  List<Long> idsWorkpacksChildrenWithPermission(
          List<Long> idsWorkpacks,
          @Param("sub") String sub
          );


}
