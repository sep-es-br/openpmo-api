package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface WorkpackModelRepository extends Neo4jRepository<WorkpackModel, Long> {

  @Query("MATCH (w:WorkpackModel)-[wp:BELONGS_TO]->(pm:PlanModel) "
    + "WHERE id(pm) = $id AND NOT (w)-[:IS_IN]->(:WorkpackModel) "
    + " OPTIONAL MATCH (w)-[is:IS_SORTED_BY]->(ps:PropertyModel) "
    + " RETURN w, wp, pm, ["
    + "  [ [is, ps] ] "
    + "]")
  List<WorkpackModel> findAllByIdPlanModel(@Param("id") Long id);

  @Query(" MATCH (wm:WorkpackModel)-[wp:BELONGS_TO]->(pm:PlanModel) " +
  " WHERE id(wm) = $id " + 
  "  OPTIONAL MATCH (wm)-[i:IS_IN]->(wm2:WorkpackModel) " + 
  "  OPTIONAL MATCH (wm)<-[i2:IS_IN]-(wm3:WorkpackModel) " + 
//  "  OPTIONAL MATCH (wm)<-[ib:IS_INSTANCE_BY]-(w4:Workpack) " + 
//  "  OPTIONAL MATCH (wm2)<-[ib2:IS_INSTANCE_BY]-(w5:Workpack) " + 
//  "  OPTIONAL MATCH (wm3)<-[ib3:IS_INSTANCE_BY]-(w6:Workpack) " + 
  "  OPTIONAL MATCH (wm)<-[f:FEATURES]-(p:PropertyModel) " + 
  "  OPTIONAL MATCH (wm)-[is:IS_SORTED_BY]->(ps:PropertyModel) " + 
  "  OPTIONAL MATCH (wm)-[featureGroup:FEATURES]->(group:GroupModel) " + 
  "  OPTIONAL MATCH (group)-[groups:GROUPS]->(groupedProperty:PropertyModel) " + 
  "  OPTIONAL MATCH (p)-[dl:DEFAULTS_TO]->(l:Locality) " + 
  "  OPTIONAL MATCH (p)-[ir:IS_LIMITED_BY]->(dm:Domain) " + 
  "  OPTIONAL MATCH (p)-[du:DEFAULTS_TO]->(u:UnitMeasure) " + 
  "  OPTIONAL MATCH (p)-[d:DEFAULTS_TO]->(o:Organization) " + 
  " RETURN wm, wp, pm, [ " + 
  "   [ [i, wm2]], " + 
  "   [ [i2,wm3] ], " + 
//  "   [ [ib, w4]], " + 
//  "   [ [ib2, w5]], " + 
//  "   [ [ib3, w6]], " + 
  "   [ [f, p] ], " + 
  "   [ [is, ps] ], " + 
  "   [ [featureGroup, group] ], " + 
  "   [ [groups, groupedProperty] ], " + 
  "   [ [dl, l] ], " + 
  "   [ [ir, dm] ], " + 
  "   [ [du, u] ], " + 
  "   [ [d, o] ] " + 
  " ] ")
  Optional<WorkpackModel> findAllByIdWorkpackModel(@Param("id") Long id);

  @Query("MATCH (w:Workpack)-[wp:IS_INSTANCE_BY]->(wm:WorkpackModel) "
    + "WHERE id(w) = $idWorkpack "
    + " OPTIONAL MATCH (wm)-[is:IS_SORTED_BY]->(ps:PropertyModel) "
    + " OPTIONAL MATCH (wm)<-[i:IS_IN]-(wm2:WorkpackModel) "
    + " OPTIONAL MATCH (wm)<-[f:FEATURES]-(p:PropertyModel) "
    + " RETURN wm , ["
    + "  [ [is, ps] ], "
    + "  [ [i,wm2] ],"
    + "  [ [f, p] ]"
    + "] ")
  Optional<WorkpackModel> findByIdWorkpack(@Param("idWorkpack") Long idWorkpack);

  @Query("MATCH (w:WorkpackModel)-[rf:BELONGS_TO]->(p:PlanModel) "
    + " WHERE id(w) = $id "
    + " OPTIONAL MATCH (w)-[wi:IS_IN*]->(w2:WorkpackModel) "
    + " RETURN  w, rf, p, [ "
    + "  [ [wi, w2] ]"
    + " ]")
  Optional<WorkpackModel> findByIdWithParents(@Param("id") Long id);

  @Query("MATCH (wm:WorkpackModel)-[wp:BELONGS_TO]->(pm:PlanModel) "
    + "WHERE id(pm)=$id AND NOT (wm)-[:IS_IN]->(:WorkpackModel) "
    + " OPTIONAL MATCH (wm)<-[i:IS_IN*]-(wm2:WorkpackModel)-[:BELONGS_TO]->(:PlanModel) "
    + " OPTIONAL MATCH (wm)<-[features:FEATURES]-(propertyModel:PropertyModel) "
    + " OPTIONAL MATCH (wm2)<-[features2:FEATURES]-(propertyModel2:PropertyModel) "
    + " OPTIONAL MATCH (wm)-[isSortedBy1:IS_SORTED_BY]->(sorter1:PropertyModel) "
    + " OPTIONAL MATCH (wm2)-[isSortedBy2:IS_SORTED_BY]->(sorter2:PropertyModel) "
    + " OPTIONAL MATCH (propertyModel)-[groups:GROUPS]->(groupedProperty:PropertyModel) "
    + " OPTIONAL MATCH (propertyModel2)-[groups2:GROUPS]->(groupedProperty2:PropertyModel) "
    + " RETURN wm, wp, pm , ["
    + "  [ [i,wm2] ], "
    + "  [ [features, propertyModel] ], "
    + "  [ [features2, propertyModel2] ], "
    + "  [ [isSortedBy1, sorter1] ], "
    + "  [ [isSortedBy2, sorter2] ], "
    + "  [ [groups, groupedProperty] ], "
    + "  [ [groups2, groupedProperty2] ] "
    + "] ")
  Set<WorkpackModel> findAllByIdPlanModelWithChildren(@Param("id") Long id);

  @Query("MATCH (children:WorkpackModel)-[isIn:IS_IN]->(parent:WorkpackModel) " +
    "WHERE id(children)=$childrenId AND id(parent)=$parentId " +
    "DETACH DELETE isIn")
  void deleteRelationshipBetween(
    Long childrenId,
    Long parentId
  );

  @Query("MATCH (target:WorkpackModel) " +
    "OPTIONAL MATCH (target)<-[:FEATURES]-(targetProperty:PropertyModel) " +
    "OPTIONAL MATCH (target)<-[:IS_IN*]-(children:WorkpackModel) " +
    "OPTIONAL MATCH (children)<-[:FEATURES]-(childrenProperty:PropertyModel) " +
    "OPTIONAL MATCH (childrenProperty)-[childrenGroup:GROUPS]->(childrenPropertyGroup:PropertyModel) " +
    "OPTIONAL MATCH (targetProperty)-[targetGroup:GROUPS]->(targetPropertyGroup:PropertyModel) " +
    "WITH target, children, targetProperty, childrenProperty, childrenGroup, childrenPropertyGroup, targetPropertyGroup, " +
    "targetGroup " +
    "WHERE id(target)=$id " +
    "DETACH DELETE target, children, targetProperty, childrenProperty, childrenGroup, childrenPropertyGroup, " +
    "targetPropertyGroup, targetGroup ")
  void deleteCascadeAllNodesRelated(Long id);

  @Query("MATCH (w:Workpack)-[i:IS_INSTANCE_BY]->(m:WorkpackModel) " +
    "WHERE id(w)=$idWorkpack AND id(m)=$idWorkpackModel " +
    "RETURN count(i)>0")
  boolean isWorkpackInstanceByModel(
    Long idWorkpack,
    Long idWorkpackModel
  );

  @Query("MATCH (wm:WorkpackModel) " +
    "WHERE id(wm)=$idWorkpackModel " +
    " OPTIONAL MATCH (wm)<-[f:FEATURES]-(pm:PropertyModel) " +
    " OPTIONAL MATCH (wm)<-[i:IS_IN*]-(wmc:WorkpackModel) " +
    " OPTIONAL MATCH (wm)<-[i:IS_IN*]-(:WorkpackModel)<-[fc:FEATURES]-(pmc:PropertyModel) " +
    "RETURN wm, [ " +
    "    [  [f,pm] ], " +
    "    [  [i,wmc] ], " +
    "    [  [fc,pmc] ] " +
    "]")
  Optional<WorkpackModel> findByIdWorkpackWithChildren(Long idWorkpackModel);

  @Query("MATCH (w:Workpack)-[i:IS_INSTANCE_BY]->(:WorkpackModel) " +
    "WHERE id(w)=$workpackId " +
    "DETACH DELETE i")
  void deleteRelationshipByWorkpackId(Long workpackId);

  @Query("MATCH (w:Workpack) WHERE id(w)=$workpackId " +
    "MATCH (m:WorkpackModel) WHERE id(m)=$workpackModelId " +
    "CREATE (w)-[:IS_INSTANCE_BY]->(m)")
  void createRelationshipByWorkpackIdAndModelId(
    Long workpackId,
    Long workpackModelId
  );

  @Query("MATCH (w:WorkpackModel) WHERE id(w)=$workpackModelId " +
    "MATCH (p:PropertyModel) WHERE id(p)=$propertyModelId " +
    "CREATE (w)<-[:FEATURES]-(p)")
  void createFeaturesRelationship(
    Long workpackModelId,
    Long propertyModelId
  );

  @Query(
    "MATCH (model:WorkpackModel)-[:BELONGS_TO]->(planModel:PlanModel) " +
      "WHERE id(model)=$idWorkpackModel " +
      "OPTIONAL MATCH (model)-[:IS_IN*1..]->(parent:WorkpackModel)-[:BELONGS_TO]->(parentPlanModel:PlanModel) " +
      "WITH *, collect(id(parent)) + collect(id(planModel)) + collect(id(parentPlanModel)) as hierarchyGroup " +
      "UNWIND hierarchyGroup as hierarchy " +
      "RETURN hierarchy"
  )
  Set<Long> findWorkpackModelParentsHierarchy(@Param("idWorkpackModel") Long idWorkpackModel);

  @Query(
    "MATCH (wm1:WorkpackModel)<-[:IS_IN]-(wm2:WorkpackModel) " +
      "WHERE id(wm1)=$idWorkpackModel " +
      "RETURN max(wm2.position)"
  )
  Long findActualPosition(@Param("idWorkpackModel") Long idWorkpackModel);

  @Query("MATCH (wm:WorkpackModel) " +
    "WHERE id(wm)=$idWorkpackModel " +
    " OPTIONAL MATCH (wm)<-[i:IS_IN*]-(wmc:WorkpackModel) " +
    "RETURN wm, [ " +
    "    [ [i,wmc] ] " +
    "]")
  Optional<WorkpackModel> findByIdWithChildren(Long idWorkpackModel);

  @Query("MATCH (parent:WorkpackModel)<-[:IS_IN]-(model:WorkpackModel) " +
    "WHERE id(parent)=$idParentModel and id(model)=$idWorkpackModel " +
    "RETURN model, [ " +
    " [(model)<-[i:IS_IN*]->(family:WorkpackModel) | [i,family]] " +
    "]")
  Optional<WorkpackModel> findAllByIdWithParents(Long idWorkpackModel, Long idParentModel);
}
