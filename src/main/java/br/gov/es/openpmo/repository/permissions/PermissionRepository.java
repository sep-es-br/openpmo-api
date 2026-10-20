package br.gov.es.openpmo.repository.permissions;

import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import java.util.List;
import java.util.Map;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends Neo4jRepository<Workpack, Long>, CustomRepository {

    
    @Query(
        "MATCH (n) WHERE id(n) IN $ids " +
        "OPTIONAL MATCH (n)-[:IS_IN|IS_ADOPTED_BY|BELONGS_TO|IS_STRUCTURED_BY|IS_FORSEEN_ON|APPLIES_TO|FEATURES|MITIGATES|IS_TRIGGER_BY|ADDRESSES|IS_REPORTED_FOR|IS_BELONGS_TO|SCOPE_TO|IS_LINKED_TO|COMPOSES|IS_BASELINED_BY*0..]->(m) " +
        "   <-[rel:CAN_ACCESS_WORKPACK|CAN_ACCESS_PLAN|CAN_ACCESS_OFFICE]-(p:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +

        "OPTIONAL MATCH (n)-[:IS_IN*0..]->(proj:Project) " +
        "OPTIONAL MATCH (proj)<-[:FEATURES]-(prop:Property)-[:IS_DRIVEN_BY]->(pm:PropertyModel) " +
        "WHERE pm.name IN ['Situação','Status'] " +
        "WITH collect(distinct rel.permissionLevel) AS permissions, " +
        "     collect(distinct prop.value) AS statusList " +
        "RETURN DISTINCT { permissions: permissions, statusList: statusList } AS result LIMIT 1"

    )
    Map<String, Object> fetchAccessInfo(
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
    "MATCH (person:Person)-[stakeholder:IS_STAKEHOLDER_IN {active:true}]->(rootProgram:Program) " +
      "-[:MEMBER_OF]->(view:WorkpackModel {classification:'TRANSVERSAL'}) " +
      "MATCH (person)-[:IS_AUTHENTICATED_BY {key:$sub}]->() " +
      "MATCH (target:Workpack) WHERE id(target) = $id " +
      "AND (stakeholder.from IS NULL OR date(stakeholder.from) <= date()) " +
      "AND (stakeholder.to IS NULL OR date(stakeholder.to) >= date()) " +
      "AND (target = rootProgram " +
      "OR EXISTS((target:Program)-[:IS_IN*1..]->(rootProgram)) " +
      "OR EXISTS { " +
      "  MATCH (program:Program)-[:MEMBER_OF]->(view), " +
      "        (program)-[:BELONGS_TO]->(plan:Plan), " +
      "        (selected:Workpack)-[:BELONGS_TO]->(plan), " +
      "        (selected)-[:IS_INSTANCE_BY]->(selectedModel:WorkpackModel), " +
      "        (selectedModel)<-[:FEATURES]-(selectionModel:TransversalViewSelectionModel), " +
      "        (selected)<-[:FEATURES]-(selection:TransversalViewSelection)-[:IS_DRIVEN_BY]->(selectionModel), " +
      "        (selectedModel)-[:IS_IN*0..]->(rootModel:WorkpackModel)<-[:FEATURES]-(:TransversalViewSelectionModel), " +
      "        (target)-[:BELONGS_TO]->(plan), " +
      "        (target)-[:IS_INSTANCE_BY]->(targetModel:WorkpackModel) " +
      "  WHERE (program = rootProgram OR EXISTS((program)-[:IS_IN*1..]->(rootProgram))) " +
      "  AND toString(id(program)) IN [token IN split(coalesce(selection.value, ''), ',') | trim(token)] " +
      "  AND NOT EXISTS((rootModel)-[:IS_IN*1..]->(:WorkpackModel)<-[:FEATURES]-(:TransversalViewSelectionModel)) " +
      "  AND EXISTS((targetModel)-[:IS_IN*0..]->(rootModel)) " +
      "  AND (target = selected OR EXISTS((target)-[:IS_IN*1..]->(selected)) " +
      "    OR EXISTS((selected)-[:IS_IN*1..]->(target))) " +
      "} " +
      "OR EXISTS { " +
      "  MATCH (program:Program)-[:MEMBER_OF]->(view), " +
      "        (program)-[:INCLUDES]->(included:Workpack) " +
      "  WHERE (program = rootProgram OR EXISTS((program)-[:IS_IN*1..]->(rootProgram))) " +
      "  AND (target = included OR EXISTS((target)-[:IS_IN*1..]->(included))) " +
      "} " +
      ") RETURN count(DISTINCT rootProgram) > 0"
  )
  boolean hasTransversalStakeholderReadAccess(
    @Param("id") Long idWorkpack,
    @Param("sub") String sub
  );

  @Query(
    "MATCH (person:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
    "MATCH (view:WorkpackModel {classification:'TRANSVERSAL'})<-[:MEMBER_OF]-(rootProgram:Program) " +
    "OPTIONAL MATCH (person)-[stakeholder:IS_STAKEHOLDER_IN {active:true}]->(rootProgram) " +
    "OPTIONAL MATCH (person)-[directAccess:CAN_ACCESS_WORKPACK]->(rootProgram) " +
    "WHERE id(view) = $id " +
    "AND ((stakeholder IS NOT NULL AND (stakeholder.from IS NULL OR date(stakeholder.from) <= date()) " +
    "AND (stakeholder.to IS NULL OR date(stakeholder.to) >= date())) OR directAccess IS NOT NULL) " +
    "RETURN count(DISTINCT rootProgram) > 0"
  )
  boolean hasTransversalStakeholderViewReadAccess(
    @Param("id") Long idTransversalView,
    @Param("sub") String sub
  );

  @Query(
    "MATCH (person:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
    "MATCH (view:WorkpackModel {classification:'TRANSVERSAL'})<-[:MEMBER_OF]-(rootProgram:Program) " +
    "OPTIONAL MATCH (person)-[stakeholder:IS_STAKEHOLDER_IN {active:true}]->(rootProgram) " +
    "OPTIONAL MATCH (person)-[directAccess:CAN_ACCESS_WORKPACK]->(rootProgram) " +
    "MATCH (target:WorkpackModel) " +
    "MATCH (program:Program)-[:MEMBER_OF]->(view) " +
    "WHERE id(target) = $id " +
    "AND (program = rootProgram OR EXISTS((program)-[:IS_IN*1..]->(rootProgram))) " +
    "AND (target = view OR EXISTS((view)-[:USES]->(target)) OR EXISTS((program)-[:IS_INSTANCE_BY]->(target))) " +
    "AND ((stakeholder IS NOT NULL AND (stakeholder.from IS NULL OR date(stakeholder.from) <= date()) " +
    "AND (stakeholder.to IS NULL OR date(stakeholder.to) >= date())) OR directAccess IS NOT NULL) " +
    "RETURN count(DISTINCT rootProgram) > 0"
  )
  boolean hasTransversalStakeholderModelReadAccess(
    @Param("id") Long idWorkpackModel,
    @Param("sub") String sub
  );

  @Query(
    "MATCH " +
    "   (m)<-[r:CAN_ACCESS_WORKPACK|CAN_ACCESS_PLAN]-(p:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
    "   WHERE r.permissionLevel IN ['READ', 'UPDATE', 'EDIT'] " +
    "MATCH path=shortestPath((n)<-[:IS_IN|IS_ADOPTED_BY|BELONGS_TO|IS_STRUCTURED_BY|IS_FORSEEN_ON|APPLIES_TO|FEATURES|MITIGATES|IS_TRIGGER_BY|ADDRESSES|IS_REPORTED_FOR|IS_BELONGS_TO|SCOPE_TO|IS_LINKED_TO|COMPOSES|IS_BASELINED_BY*0..]-(m)) " +
    "    WHERE id(n) IN $ids " +
    "RETURN count(path)>0")
  boolean hasBasicReadPermission(
    @Param("ids") List<Long> ids,
    @Param("sub") String sub
  );

  @Query(
    "MATCH " +
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
      "AND c1.permissionLevel in ['EDIT', 'READ', 'UPDATE']" +
      "RETURN COUNT(DISTINCT ID(c1)) AS totalPermissions " +
      "UNION ALL " +
      "MATCH (w2:Workpack)-[:BELONGS_TO]->(plan2:Plan)-[:IS_ADOPTED_BY]->(o:Office)<-[c2:CAN_ACCESS_OFFICE]-(p2:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
      "WHERE ID (w2) = $id " +
      "AND c2.permissionLevel in ['EDIT', 'READ', 'UPDATE']" +
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
      "AND c1.permissionLevel in ['EDIT', 'READ', 'UPDATE'] " +
      "RETURN COUNT(DISTINCT ID(c1)) AS totalPermissions " +
      "UNION ALL " +
      "MATCH (plan2:Plan)-[:IS_ADOPTED_BY]->(o:Office)<-[c2:CAN_ACCESS_OFFICE]-(p2:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
      "WHERE ID (plan2) = $id " +
      "AND c2.permissionLevel in ['EDIT', 'READ', 'UPDATE'] " +
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
      "AND c1.permissionLevel in ['EDIT', 'READ', 'UPDATE'] " +
      "RETURN COUNT(DISTINCT ID(c1)) AS totalPermissions " +
      "UNION ALL " +
      "MATCH (w:Workpack)<-[c3:CAN_ACCESS_WORKPACK]-(p3:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
      "WHERE ID(w) = $id " +
      "AND c3.permissionLevel in ['EDIT', 'READ', 'UPDATE'] " +
      "RETURN COUNT(DISTINCT ID(c3)) AS totalPermissions " +
    "} " +
    "RETURN SUM(totalPermissions) > 0 "
  )
  boolean hasPermissionWorkpackSelfOrParents(
    @Param("id") Long id,
    @Param("sub") String sub
  );

  @Query(
    "MATCH (w:Workpack)-[i:IS_IN*]->(p:Workpack), " +
    "(w)<-[c3:CAN_ACCESS_WORKPACK]-(p3:Person)-[:IS_AUTHENTICATED_BY {key:$sub}]-() " +
    "WHERE id(p) IN $idsWorkpacks " +
    "AND c3.permissionLevel in ['EDIT', 'READ', 'UPDATE'] " +
    "RETURN id(w)"
  )
  List<Long> idsWorkpacksChildrenWithPermission(
    List<Long> idsWorkpacks,
    @Param("sub") String sub
  );
}
