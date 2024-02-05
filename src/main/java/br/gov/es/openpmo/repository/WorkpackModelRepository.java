package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.properties.models.PropertyModel;
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
    + " RETURN w, wp, pm, ["
    + "  [(w)-[is:IS_SORTED_BY]->(ps:PropertyModel) | [is, ps] ] "
    + "]")
  List<WorkpackModel> findAllByIdPlanModel(@Param("id") Long id);

  @Query("MATCH (w:WorkpackModel)-[wp:BELONGS_TO]->(pm:PlanModel) "
    + "WHERE id(w) = $id "
    + "RETURN w, wp, pm, [ "
    + "  [(w)-[i:IS_IN]->(w2:WorkpackModel) | [i, w2]], "
    + "  [(w)<-[i2:IS_IN]-(w3:WorkpackModel) | [i2,w3] ],"
    + "  [(w)<-[f:FEATURES]-(p:PropertyModel) | [f, p] ], "
    + "  [(w)-[is:IS_SORTED_BY]->(ps:PropertyModel) | [is, ps] ], "
    + "  [(w)<-[featureGroup:FEATURES]-(group:GroupModel)-[groups:GROUPS]->(groupedProperty:PropertyModel) | [featureGroup, group, groups, groupedProperty] ], "
    + "  [(w)<-[featureGroupl:FEATURES]-(groupl:GroupModel)-[groupsl:GROUPS]->(groupedPropertyl:PropertyModel)-[dlg:DEFAULTS_TO]->(gl:Locality) | [featureGroupl, groupl, groupsl, groupedPropertyl, dlg, gl] ], "
    + "  [(w)<-[featureGroupd:FEATURES]-(groupd:GroupModel)-[groupsd:GROUPS]->(groupedPropertyd:PropertyModel)-[irg:IS_LIMITED_BY]->(gdm:Domain) | [featureGroupd, groupd, groupsd, groupedPropertyd, irg, gdm] ], "
    + "  [(w)<-[featureGroupu:FEATURES]-(groupu:GroupModel)-[groupsu:GROUPS]->(groupedPropertyu:PropertyModel)-[dug:DEFAULTS_TO]->(gu:UnitMeasure) | [featureGroupu, groupu, groupsu, groupedPropertyu, dug, gu] ], "
    + "  [(w)<-[featureGroupo:FEATURES]-(groupo:GroupModel)-[groupso:GROUPS]->(groupedPropertyo:PropertyModel)-[dg:DEFAULTS_TO]->(oo:Organization) | [featureGroupo, groupo, groupso, groupedPropertyo, dg, oo] ], "
    + "  [(w)<-[f1:FEATURES]-(p1:PropertyModel)-[dl:DEFAULTS_TO]->(l:Locality) | [dl, l] ], "
    + "  [(w)<-[f2:FEATURES]-(p2:PropertyModel)-[ir:IS_LIMITED_BY]->(dm:Domain) | [ir, dm] ], "
    + "  [(w)<-[f3:FEATURES]-(p3:PropertyModel)-[du:DEFAULTS_TO]->(u:UnitMeasure) | [du, u] ], "
    + "  [(w)<-[f4:FEATURES]-(p4:PropertyModel)-[d:DEFAULTS_TO]->(o:Organization) | [d, o] ]"
    + "] ")
  Optional<WorkpackModel> findAllByIdWorkpackModel(@Param("id") Long id);

  @Query("MATCH (wm:WorkpackModel)<-[f:FEATURES]-(pm:PropertyModel) "
          + "WHERE id(wm) = $id "
          + "RETURN wm, f, pm, "
          + "  [(wm)<-[featureGroup:FEATURES]-(group:GroupModel)-[groups:GROUPS]->(groupedProperty:PropertyModel) | [groups, groupedProperty] ] "
          )
  Set<PropertyModel> findAllPropertyModels(@Param("id") Long id);

  @Query("MATCH (w:Workpack)-[wp:IS_INSTANCE_BY]->(wm:WorkpackModel) "
    + "WHERE id(w) = $idWorkpack "
    + " RETURN wm , ["
    + "  [(wm)-[is:IS_SORTED_BY]->(ps:PropertyModel) | [is, ps] ], "
    + "  [(wm)<-[i:IS_IN]-(wm2:WorkpackModel) |[i,wm2] ],"
    + "  [(wm)<-[f:FEATURES]-(p:PropertyModel) |[f, p] ]"
    + "] ")
  Optional<WorkpackModel> findByIdWorkpack(@Param("idWorkpack") Long idWorkpack);

  @Query("MATCH (w:WorkpackModel)-[rf:BELONGS_TO]->(p:PlanModel) "
    + " WHERE id(w) = $id "
    + " RETURN  w, rf, p, [ "
    + "  [(w)-[wi:IS_IN*]->(w2:WorkpackModel) | [wi, w2] ]"
    + " ]")
  Optional<WorkpackModel> findByIdWithParents(@Param("id") Long id);

  @Query("MATCH (wm:WorkpackModel)-[wp:BELONGS_TO]->(pm:PlanModel) "
          + " WHERE id(pm)=$id AND NOT (wm)-[:IS_IN]->(:WorkpackModel) "
          + " RETURN wm, wp, pm , [ "
          + " [(wm)<-[features:FEATURES]-(propertyModel:PropertyModel) | [features, propertyModel] ], "
          + " [(wm)-[isSortedBy1:IS_SORTED_BY]->(sorter1:PropertyModel) | [isSortedBy1, sorter1] ], "
          + " [(wm)<-[features2:FEATURES]-(propertyModel2:PropertyModel)-[groups:GROUPS]->(groupedProperty:PropertyModel) | [features2, propertyModel2, groups, groupedProperty] ], "
          + " [(wm)<-[ii1:IS_IN*]-(wm1:WorkpackModel) | [wm , ii1, wm1] ], "
          + " [(wm)<-[i:IS_IN*]-(wm2:WorkpackModel)<-[feature3:FEATURES]-(propertyModel3:PropertyModel) | [wm2, feature3, propertyModel3] ], "
          + " [(wm)<-[i:IS_IN*]-(wm3:WorkpackModel)-[isSortedBy2:IS_SORTED_BY]->(sorter2:PropertyModel) | [wm3, isSortedBy2, sorter2] ], "
          + " [(wm)<-[i:IS_IN*]-(wm4:WorkpackModel)<-[features4:FEATURES]-(propertyModel4:PropertyModel)-[groups2:GROUPS]->(groupedProperty2:PropertyModel) | [wm4, features4, propertyModel4, groups2, groupedProperty2] ] "
          + " ] ")
  Set<WorkpackModel> findAllByIdPlanModelWithChildren(@Param("id") Long id);

  @Query("MATCH (wm:WorkpackModel)-[wp:BELONGS_TO]->(pm:PlanModel) "
          + "WHERE id(pm)=$id AND NOT (wm)-[:IS_IN]->(:WorkpackModel) "
          + " RETURN wm, wp, pm , ["
          + "  [(wm)<-[i:IS_IN*]-(wm2:WorkpackModel)-[:BELONGS_TO]->(:PlanModel)  | [i,wm2] ] "
          + "] ")
  Set<WorkpackModel> findAllByIdPlanModelWithChildrenThin(@Param("id") Long id);

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
    "RETURN wm, [ " +
    "    [ (wm)<-[f:FEATURES]-(pm:PropertyModel) | [f,pm] ], " +
    "    [ (wm)<-[i:IS_IN*]-(wmc:WorkpackModel) | [i,wmc] ], " +
    "    [ (wm)<-[i:IS_IN*]-(:WorkpackModel)<-[fc:FEATURES]-(pmc:PropertyModel) | [fc,pmc] ] " +
    "]")
  Optional<WorkpackModel> findByIdWorkpackWithChildren(Long idWorkpackModel);

  @Query("MATCH (w:Workpack)-[i:IS_INSTANCE_BY]->(:WorkpackModel) " +
    "WHERE id(w)=$workpackId " +
    "DETACH DELETE i")
  void deleteRelationshipByWorkpackId(Long workpackId);

  @Query("MATCH (w:Workpack), (m:WorkpackModel) " +
    "WHERE id(w)=$workpackId AND id(m)=$workpackModelId " +
    "CREATE (w)-[:IS_INSTANCE_BY]->(m)")
  void createRelationshipByWorkpackIdAndModelId(
    Long workpackId,
    Long workpackModelId
  );

  @Query("MATCH (w:WorkpackModel), (p:PropertyModel) " +
    "WHERE id(w)=$workpackModelId AND id(p)=$propertyModelId " +
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
    "RETURN wm, [ " +
    "    [ (wm)<-[i:IS_IN*]-(wmc:WorkpackModel) | [i,wmc] ] " +
    "]")
  Optional<WorkpackModel> findByIdWithChildren(Long idWorkpackModel);

  @Query("MATCH (parent:WorkpackModel)<-[:IS_IN]-(model:WorkpackModel) " +
    "WHERE id(parent)=$idParentModel and id(model)=$idWorkpackModel " +
    "RETURN model, [ " +
    " [(model)<-[i:IS_IN*]->(family:WorkpackModel) | [i,family]] " +
    "]")
  Optional<WorkpackModel> findAllByIdWithParents(Long idWorkpackModel, Long idParentModel);
}
