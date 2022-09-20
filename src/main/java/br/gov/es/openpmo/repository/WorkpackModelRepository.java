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
         + " RETURN w, wp, pm, ["
         + "  [(w)-[is:IS_SORTED_BY]->(ps:PropertyModel) | [is, ps] ] "
         + "]")
  List<WorkpackModel> findAllByIdPlanModel(@Param("id") Long id);

  @Query("MATCH (w:WorkpackModel)-[wp:BELONGS_TO]->(pm:PlanModel) "
         + "WHERE id(w) = $id "
         + "RETURN w, wp, pm, [ "
         + "  [(w)-[i:IS_IN*]->(w2:WorkpackModel) | [i, w2]], "
         + "  [(w)<-[i2:IS_IN*]-(w3:WorkpackModel) | [i2,w3] ],"
         + "  [(w)<-[ib:IS_INSTANCE_BY]-(w4:Workpack) | [ib, w4]],"
         + "  [(w)<-[f:FEATURES]-(p:PropertyModel) | [f, p] ], "
         + "  [(w)-[is:IS_SORTED_BY]->(ps:PropertyModel) | [is, ps] ], "
         + "  [(w)-[featureGroup:FEATURES]->(group:GroupModel) | [featureGroup, group] ], "
         + "  [(group)-[groups:GROUPS]->(groupedProperty:PropertyModel) | [groups, groupedProperty] ], "
         + "  [(p)-[dl:DEFAULTS_TO]->(l:Locality) | [dl, l] ], "
         + "  [(p)-[ir:IS_LIMITED_BY]->(dm:Domain) | [ir, dm] ], "
         + "  [(p)-[du:DEFAULTS_TO]->(u:UnitMeasure) | [du, u] ], "
         + "  [(p)-[d:DEFAULTS_TO]->(o:Organization) | [d, o] ]"
         + "] ")
  Optional<WorkpackModel> findAllByIdWorkpackModel(@Param("id") Long id);

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
         + "WHERE id(pm)=$id AND NOT (wm)-[:IS_IN]->(:WorkpackModel) "
         + " RETURN wm, wp, pm , ["
         + "  [(wm)<-[i:IS_IN*]-(wm2:WorkpackModel)-[:BELONGS_TO]->(:PlanModel) | [i,wm2] ], "
         + "  [(wm)<-[features:FEATURES]-(propertyModel:PropertyModel) | [features, propertyModel] ], "
         + "  [(wm2)<-[features2:FEATURES]-(propertyModel2:PropertyModel) | [features2, propertyModel2] ], "
         + "  [(propertyModel)-[groups:GROUPS]->(groupedProperty:PropertyModel) | [groups, groupedProperty] ], "
         + "  [(propertyModel)-[groups2:GROUPS]->(groupedProperty2:PropertyModel) | [groups2, groupedProperty2] ] "
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

  @Query("match (w:Workpack)-[i:IS_INSTANCE_BY]->(m:WorkpackModel) " +
         "where id(w)=$idWorkpack and id(m)=$idWorkpackModel " +
         "return count(i)>0")
  boolean isWorkpackInstanceByModel(
    Long idWorkpack,
    Long idWorkpackModel
  );

  @Query("match (wm:WorkpackModel) " +
         "where id(wm)=$idWorkpackModel " +
         "return wm, [ " +
         "    [ (wm)<-[f:FEATURES]-(pm:PropertyModel) | [f,pm] ], " +
         "    [ (wm)<-[i:IS_IN*]-(wmc:WorkpackModel) | [i,wmc] ], " +
         "    [ (wmc)<-[fc:FEATURES]-(pmc:PropertyModel) | [fc,pmc] ] " +
         "]")
  Optional<WorkpackModel> findByIdWorkpackWithChildren(Long idWorkpackModel);

  @Query("match (w:Workpack)-[i:IS_INSTANCE_BY]->(:WorkpackModel) " +
         "where id(w)=$workpackId " +
         "detach delete i")
  void deleteRelationshipByWorkpackId(Long workpackId);

  @Query("match (w:Workpack), (m:WorkpackModel) " +
         "where id(w)=$workpackId and id(m)=$workpackModelId " +
         "create (w)-[:IS_INSTANCE_BY]->(m)")
  void createRelationshipByWorkpackIdAndModelId(
    Long workpackId,
    Long workpackModelId
  );

  @Query("match (w:WorkpackModel), (p:PropertyModel) " +
         "where id(w)=$workpackModelId and id(p)=$propertyModelId " +
         "create (w)<-[:FEATURES]-(p)")
  void createFeaturesRelationship(
    Long workpackModelId,
    Long propertyModelId
  );

}
