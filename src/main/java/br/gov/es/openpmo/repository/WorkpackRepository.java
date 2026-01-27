package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.menu.PlanWorkpackDto;
import br.gov.es.openpmo.dto.menu.WorkpackResultDto;
import br.gov.es.openpmo.dto.universalSearch.UniversalSearchItemQueryResult;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.WorkpackBreakdownClassificationDto;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.workpacks.Program;
import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface WorkpackRepository extends Neo4jRepository<Workpack, Long>, CustomRepository {

  @Query("MATCH (w:Workpack{deleted:false})<-[f:FEATURES]-(p:UnitSelection)-[v:VALUES]-(u:UnitMeasure) " +
      "WHERE id(w) IN $ids " +
      "RETURN w, f, p, v, u ")
  List<Workpack> findAllDeliverable(List<Long> ids);

  @Query("MATCH (o:Office)<-[r:IS_ADOPTED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO]-(workpack:Workpack)<-[permission:CAN_ACCESS_WORKPACK]-(person:Person) "
      +
      " WHERE id(o) = $idOffice " +
      " AND id(person) = $idPerson " +
      " AND permission.idPlan = id(plan) " +
      " RETURN id(plan) as idPlan, collect(id(workpack)) as workpacks ")
  List<PlanWorkpackDto> findAllMappedByPlanWithPermission(
      @Param("idOffice") Long idOffice,
      @Param("idPerson") Long idPerson);

  @Query("MATCH (planModel:PlanModel)<-[isStructuredBy:IS_STRUCTURED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO]-(w:Workpack{deleted:false,canceled:false})-[instanceBy:IS_INSTANCE_BY]->(model:WorkpackModel) "
      +
      "WHERE id(plan) = $idPlan AND NOT (w)-[:IS_IN]->(:Workpack) " +
      "AND (NOT EXISTS(belongsTo.linked) OR belongsTo.linked = false) " +
      "RETURN DISTINCT id(w) as id, id(model) as idWorkpackModel, false as linked, id(plan) as idPlan, w.idParent as idParent "
      +
      ", w.name as name, w.fullName as fullName, model.fontIcon as fontIcon, model.modelName as modelName " +
      " , model.modelNameInPlural as modelNameInPlural,labels(w) as labels, model.position as position " +
      " , w.date as date, model.sortByField as sortByField  " +
      " UNION ALL " +
      "MATCH (planModel:PlanModel)<-[isStructuredBy:IS_STRUCTURED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO]-(w:Workpack{deleted:false,canceled:false})-[instanceBy:IS_INSTANCE_BY]->(model:WorkpackModel)  "
      +
      ",(w)<-[:IS_IN*]-(children:Workpack{deleted:false,canceled:false})-[:IS_INSTANCE_BY]->(modelChildren:WorkpackModel), (children)-[childBelongsTo:BELONGS_TO]-(plan)  "
      +
      "WHERE id(plan) = $idPlan AND (children)-[:IS_IN]->(w) " +
      "AND (NOT EXISTS(belongsTo.linked) OR belongsTo.linked = false) " +
      "AND (NOT EXISTS(childBelongsTo.linked) OR childBelongsTo.linked = false) " +
      "RETURN id(children) as id, id(modelChildren) as idWorkpackModel, false as linked, id(plan) as idPlan,id(w) as idParent "
      +
      ", children.name as name, children.fullName as fullName, modelChildren.fontIcon as fontIcon, modelChildren.modelName as modelName "
      +
      ", modelChildren.modelNameInPlural as modelNameInPlural, labels(children) as labels, modelChildren.position as position  "
      +
      ", children.date as date, modelChildren.sortByField as sortByField " +
      "UNION ALL " +
      "MATCH (planModel:PlanModel)<-[isStructuredBy:IS_STRUCTURED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO{linked:true}]-(w:Workpack{deleted:false,canceled:false})-[isLinkedTo:IS_LINKED_TO]->(model:WorkpackModel) "
      +
      ", (w)-[:IS_IN]->(parent:Workpack)-[:BELONGS_TO]-(plan) " +
      "WHERE id(plan) = $idPlan RETURN " +
      "DISTINCT id(w) as id, id(model) as idWorkpackModel, true as linked, id(plan) as idPlan, id(parent) as idParent "
      +
      ", w.name as name, w.fullName as fullName, model.fontIcon as fontIcon, model.modelName as modelName " +
      ", model.modelNameInPlural as modelNameInPlural,labels(w) as labels, model.position as position " +
      ", w.date as date, model.sortByField as sortByField " +
      "UNION ALL " +
      "MATCH (planModel:PlanModel)<-[isStructuredBy:IS_STRUCTURED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO{linked:true}]-(w:Workpack{deleted:false,canceled:false})-[isLinkedTo:IS_LINKED_TO]->(model:WorkpackModel) "
      +
      ", (w)-[:BELONGS_TO]->(plan) " +
      "WHERE id(plan) = $idPlan AND NOT (w)-[:IS_IN]->(:Workpack) RETURN " +
      "DISTINCT id(w) as id, id(model) as idWorkpackModel, true as linked, id(plan) as idPlan, w.idParent as idParent "
      +
      ", w.name as name, w.fullName as fullName, model.fontIcon as fontIcon, model.modelName as modelName " +
      ", model.modelNameInPlural as modelNameInPlural,labels(w) as labels, model.position as position " +
      ", w.date as date, model.sortByField as sortByField  ")
  List<WorkpackResultDto> findAllMenuCustomByIdPlan(final Long idPlan);

  @Query("MATCH (planModel:PlanModel)<-[isStructuredBy:IS_STRUCTURED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO]-(w:Workpack)-[instanceBy:IS_INSTANCE_BY]->(model:WorkpackModel) "
      +
      "WHERE id(plan) = $idPlan AND NOT (w)-[:IS_IN]->(:Workpack) " +
      "AND (NOT EXISTS(belongsTo.linked) OR belongsTo.linked = false) " +
      "RETURN DISTINCT id(w) as id, id(model) as idWorkpackModel, false as linked, id(plan) as idPlan, w.idParent as idParent "
      +
      ", w.name as name, w.fullName as fullName, model.fontIcon as fontIcon, model.modelName as modelName " +
      " , model.modelNameInPlural as modelNameInPlural,labels(w) as labels, model.position as position " +
      " , w.date as date, model.sortByField as sortByField  " +
      " UNION ALL " +
      "MATCH (planModel:PlanModel)<-[isStructuredBy:IS_STRUCTURED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO]-(w:Workpack)-[instanceBy:IS_INSTANCE_BY]->(model:WorkpackModel)  "
      +
      ",(w)<-[:IS_IN*]-(children:Workpack)-[:IS_INSTANCE_BY]->(modelChildren:WorkpackModel), (children)-[childBelongsTo:BELONGS_TO]-(plan)  "
      +
      "WHERE id(plan) = $idPlan AND (children)-[:IS_IN]->(w) " +
      "AND (NOT EXISTS(belongsTo.linked) OR belongsTo.linked = false) " +
      "AND (NOT EXISTS(childBelongsTo.linked) OR childBelongsTo.linked = false) " +
      "RETURN id(children) as id, id(modelChildren) as idWorkpackModel, false as linked, id(plan) as idPlan,id(w) as idParent "
      +
      ", children.name as name, children.fullName as fullName, modelChildren.fontIcon as fontIcon, modelChildren.modelName as modelName "
      +
      ", modelChildren.modelNameInPlural as modelNameInPlural, labels(children) as labels, modelChildren.position as position  "
      +
      ", children.date as date, modelChildren.sortByField as sortByField " +
      "UNION ALL " +
      "MATCH (planModel:PlanModel)<-[isStructuredBy:IS_STRUCTURED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO{linked:true}]-(w:Workpack)-[isLinkedTo:IS_LINKED_TO]->(model:WorkpackModel) "
      +
      ", (w)-[:IS_IN]->(parent:Workpack)-[:BELONGS_TO]-(plan) " +
      "WHERE id(plan) = $idPlan RETURN " +
      "DISTINCT id(w) as id, id(model) as idWorkpackModel, true as linked, id(plan) as idPlan, id(parent) as idParent "
      +
      ", w.name as name, w.fullName as fullName, model.fontIcon as fontIcon, model.modelName as modelName " +
      ", model.modelNameInPlural as modelNameInPlural,labels(w) as labels, model.position as position " +
      ", w.date as date, model.sortByField as sortByField " +
      "UNION ALL " +
      "MATCH (planModel:PlanModel)<-[isStructuredBy:IS_STRUCTURED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO{linked:true}]-(w:Workpack)-[isLinkedTo:IS_LINKED_TO]->(model:WorkpackModel) "
      +
      ", (w)-[:BELONGS_TO]->(plan) " +
      "WHERE id(plan) = $idPlan AND NOT (w)-[:IS_IN]->(:Workpack) RETURN " +
      "DISTINCT id(w) as id, id(model) as idWorkpackModel, true as linked, id(plan) as idPlan, w.idParent as idParent "
      +
      ", w.name as name, w.fullName as fullName, model.fontIcon as fontIcon, model.modelName as modelName " +
      ", model.modelNameInPlural as modelNameInPlural,labels(w) as labels, model.position as position " +
      ", w.date as date, model.sortByField as sortByField  ")
  List<WorkpackResultDto> findAllCompleteMenuCustomByIdPlan(final Long idPlan);

  @Query("MATCH (planModel:PlanModel)<-[isStructuredBy:IS_STRUCTURED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO]-(w:Workpack {deleted:false})-[instanceBy:IS_INSTANCE_BY]->(model:WorkpackModel) "
      +
      "WHERE id(plan) = $idPlan AND id(w) = $idWorkpackId AND NOT (w)-[:IS_IN]->(:Workpack) " +
      "AND (NOT EXISTS(belongsTo.linked) OR belongsTo.linked = false) " +
      "RETURN DISTINCT id(w) as id, id(model) as idWorkpackModel, false as linked, id(plan) as idPlan, w.idParent as idParent "
      +
      ", w.name as name, w.fullName as fullName, model.fontIcon as fontIcon, model.modelName as modelName " +
      ", model.modelNameInPlural as modelNameInPlural, labels(w) as labels, model.position as position " +
      ", w.date as date, model.sortByField as sortByField " +

      " UNION ALL " +

      "MATCH (planModel:PlanModel)<-[isStructuredBy:IS_STRUCTURED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO]-(w:Workpack {deleted:false})-[instanceBy:IS_INSTANCE_BY]->(model:WorkpackModel), "
      +
      "(w)<-[:IS_IN*]-(children:Workpack {deleted:false})-[:IS_INSTANCE_BY]->(modelChildren:WorkpackModel), " +
      "(children)-[childBelongsTo:BELONGS_TO]-(plan) " +
      "WHERE id(plan) = $idPlan AND id(children) = $idWorkpackId AND (children)-[:IS_IN]->(w) " +
      "AND (NOT EXISTS(belongsTo.linked) OR belongsTo.linked = false) " +
      "AND (NOT EXISTS(childBelongsTo.linked) OR childBelongsTo.linked = false) " +
      "RETURN id(children) as id, id(modelChildren) as idWorkpackModel, false as linked, id(plan) as idPlan, id(w) as idParent "
      +
      ", children.name as name, children.fullName as fullName, modelChildren.fontIcon as fontIcon, modelChildren.modelName as modelName "
      +
      ", modelChildren.modelNameInPlural as modelNameInPlural, labels(children) as labels, modelChildren.position as position "
      +
      ", children.date as date, modelChildren.sortByField as sortByField " +

      " UNION ALL " +

      "MATCH (planModel:PlanModel)<-[isStructuredBy:IS_STRUCTURED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO {linked:true}]-(w:Workpack {deleted:false})-[isLinkedTo:IS_LINKED_TO]->(model:WorkpackModel), "
      +
      "(w)-[:IS_IN]->(parent:Workpack)-[:BELONGS_TO]-(plan) " +
      "WHERE id(plan) = $idPlan AND id(w) = $idWorkpackId " +
      "RETURN DISTINCT id(w) as id, id(model) as idWorkpackModel, true as linked, id(plan) as idPlan, id(parent) as idParent "
      +
      ", w.name as name, w.fullName as fullName, model.fontIcon as fontIcon, model.modelName as modelName " +
      ", model.modelNameInPlural as modelNameInPlural, labels(w) as labels, model.position as position " +
      ", w.date as date, model.sortByField as sortByField " +

      " UNION ALL " +

      "MATCH (planModel:PlanModel)<-[isStructuredBy:IS_STRUCTURED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO {linked:true}]-(w:Workpack {deleted:false})-[isLinkedTo:IS_LINKED_TO]->(model:WorkpackModel) "
      +
      "WHERE id(plan) = $idPlan AND id(w) = $idWorkpackId AND NOT (w)-[:IS_IN]->(:Workpack) " +
      "RETURN DISTINCT id(w) as id, id(model) as idWorkpackModel, true as linked, id(plan) as idPlan, w.idParent as idParent "
      +
      ", w.name as name, w.fullName as fullName, model.fontIcon as fontIcon, model.modelName as modelName " +
      ", model.modelNameInPlural as modelNameInPlural, labels(w) as labels, model.position as position " +
      ", w.date as date, model.sortByField as sortByField ")
  List<WorkpackResultDto> findWorkpackMenuById(Long idPlan, Long idWorkpackId);

  @Query("MATCH (planModel:PlanModel)<-[isStructuredBy:IS_STRUCTURED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO]-(w:Workpack{deleted:false,canceled:false}) "
      +
      ", (w)-[instanceBy:IS_INSTANCE_BY]->(model:WorkpackModel)-[:IS_SORTED_BY]->(pm:PropertyModel)<-[:IS_DRIVEN_BY]-(prop:Property)-[:FEATURES]->(w) "
      +
      "WHERE id(plan) = $idPlan AND NOT (w)-[:IS_IN]->(:Workpack) " +
      "RETURN DISTINCT id(w) as id, prop.value as sort " +
      "UNION ALL " +
      "MATCH (planModel:PlanModel)<-[isStructuredBy:IS_STRUCTURED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO]-(w:Workpack{deleted:false,canceled:false}) "
      +
      ", (w)-[instanceBy:IS_INSTANCE_BY]->(model:WorkpackModel) " +
      ", (w)<-[:IS_IN*]-(children:Workpack{deleted:false,canceled:false})-[:IS_INSTANCE_BY]->(modelChildren:WorkpackModel) "
      +
      ", (modelChildren)-[:IS_SORTED_BY]->(pm:PropertyModel)<-[:IS_DRIVEN_BY]-(prop:Property)-[:FEATURES]->(children)  "
      +
      "WHERE id(plan) = $idPlan AND (children)-[:IS_IN]->(w) " +
      "RETURN id(children) as id, prop.value as sort ")
  List<WorkpackResultDto> findAllMenuCustomByIdPlanWithSort(final Long idPlan);

  @Query("MATCH (planModel:PlanModel)<-[isStructuredBy:IS_STRUCTURED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO]-(w:Workpack{deleted:false,canceled:false})-[instanceBy:IS_INSTANCE_BY]->(model:WorkpackModel) "
      +
      "WHERE id(plan) = $idPlan AND NOT (w)-[:IS_IN]->(:Workpack) " +
      "AND (NOT EXISTS(belongsTo.linked) OR belongsTo.linked = false) " +
      "WITH DISTINCT id(w) as id, id(model) as idWorkpackModel, false as linked, id(plan) as idPlan, w.idParent as idParent "
      +
      ", w.name as name, w.fullName as fullName, model.fontIcon as fontIcon, model.modelName as modelName " +
      " , model.modelNameInPlural as modelNameInPlural,labels(w) as labels, model.position as position " +
      " , w.date as date, model.sortByField as sortByField  " +
      "WITH collect([id, idWorkpackModel, linked, idPlan, idParent, name, fullName, fontIcon, modelName, modelNameInPlural, position, date, sortByField]) as list "
      +
      "RETURN apoc.util.sha1(list) AS output " +
      "UNION ALL " +
      "MATCH (planModel:PlanModel)<-[isStructuredBy:IS_STRUCTURED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO]-(w:Workpack{deleted:false,canceled:false})-[instanceBy:IS_INSTANCE_BY]->(model:WorkpackModel)  "
      +
      ",(w)<-[:IS_IN*]-(children:Workpack{deleted:false,canceled:false})-[:IS_INSTANCE_BY]->(modelChildren:WorkpackModel), (children)-[childBelongsTo:BELONGS_TO]-(plan)  "
      +
      "WHERE id(plan) = $idPlan AND (children)-[:IS_IN]->(w) " +
      "AND (NOT EXISTS(belongsTo.linked) OR belongsTo.linked = false) " +
      "AND (NOT EXISTS(childBelongsTo.linked) OR childBelongsTo.linked = false) " +
      "WITH id(children) as id, id(modelChildren) as idWorkpackModel, false as linked, id(plan) as idPlan,id(w) as idParent "
      +
      ", children.name as name, children.fullName as fullName, modelChildren.fontIcon as fontIcon, modelChildren.modelName as modelName "
      +
      ", modelChildren.modelNameInPlural as modelNameInPlural, labels(children) as labels, modelChildren.position as position  "
      +
      ", children.date as date, modelChildren.sortByField as sortByField " +
      "WITH collect([id, idWorkpackModel, linked, idPlan, idParent, name, fullName, fontIcon, modelName, modelNameInPlural, position, date, sortByField]) as list "
      +
      "RETURN apoc.util.sha1(list) AS output " +
      "UNION ALL " +
      "MATCH (planModel:PlanModel)<-[isStructuredBy:IS_STRUCTURED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO{linked:true}]-(w:Workpack{deleted:false,canceled:false})-[isLinkedTo:IS_LINKED_TO]->(model:WorkpackModel) "
      +
      ", (w)-[:IS_IN]->(parent:Workpack)-[:BELONGS_TO]-(plan) " +
      "WHERE id(plan) = $idPlan " +
      "WITH DISTINCT id(w) as id, id(model) as idWorkpackModel, true as linked, id(plan) as idPlan, id(parent) as idParent "
      +
      ", w.name as name, w.fullName as fullName, model.fontIcon as fontIcon, model.modelName as modelName " +
      ", model.modelNameInPlural as modelNameInPlural,labels(w) as labels, model.position as position " +
      ", w.date as date, model.sortByField as sortByField " +
      "WITH collect([id, idWorkpackModel, linked, idPlan, idParent, name, fullName, fontIcon, modelName, modelNameInPlural, position, date, sortByField]) as list "
      +
      "RETURN apoc.util.sha1(list) AS output " +
      "UNION ALL " +
      "MATCH (planModel:PlanModel)<-[isStructuredBy:IS_STRUCTURED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO{linked:true}]-(w:Workpack{deleted:false,canceled:false})-[isLinkedTo:IS_LINKED_TO]->(model:WorkpackModel) "
      +
      ", (w)-[:BELONGS_TO]->(plan) " +
      "WHERE id(plan) = $idPlan AND NOT (w)-[:IS_IN]->(:Workpack) " +
      "WITH DISTINCT id(w) as id, id(model) as idWorkpackModel, true as linked, id(plan) as idPlan, w.idParent as idParent "
      +
      ", w.name as name, w.fullName as fullName, model.fontIcon as fontIcon, model.modelName as modelName " +
      ", model.modelNameInPlural as modelNameInPlural,labels(w) as labels, model.position as position " +
      ", w.date as date, model.sortByField as sortByField  " +
      "WITH collect([id, idWorkpackModel, linked, idPlan, idParent, name, fullName, fontIcon, modelName, modelNameInPlural, position, date, sortByField]) as list "
      +
      "RETURN apoc.util.sha1(list) AS output ")
  List<String> getHashCodeMenuCustomByIdPlan(Long idPlan);

  @Query("MATCH (w:Workpack) " +
      "WHERE id(w)=$id " +
      "RETURN w, [ " +
      "   [(w)<-[ii:IS_IN*]-(v:Workpack) | [ii,v]] " +
      "]")
  Optional<Workpack> findByIdWithChildren(Long id);

  @Query("MATCH (wm:WorkpackModel)<-[:IS_INSTANCE_BY | IS_LINKED_TO]-(w:Workpack{deleted:false})-[rf:BELONGS_TO]->(p:Plan), "
      + "(p)-[is:IS_STRUCTURED_BY]->(pm:PlanModel) "
      + "WITH *, "
      + "apoc.text.levenshteinSimilarity(apoc.text.clean(w.name), apoc.text.clean($term)) AS nameScore, "
      + "apoc.text.levenshteinSimilarity(apoc.text.clean(w.fullName), apoc.text.clean($term)) AS fullNameScore "
      + "WITH *, CASE WHEN nameScore > fullNameScore THEN nameScore ELSE fullNameScore END AS score "
      + "WHERE id(p)=$idPlan "
      + "AND (id(pm)=$idPlanModel OR $idPlanModel IS NULL) "
      + "AND (id(wm)=$idWorkPackModel) "
      + "AND ($term IS NULL OR $term = '' OR score > $searchCutOffScore) "
      + "RETURN w, wm, rf, p, pm, [ "
      + " [ (w)<-[f1:FEATURES]-(p1:Property)-[d1:IS_DRIVEN_BY]->(pm1:PropertyModel) | [f1, p1, d1, pm1] ], "
      + " [ (w)<-[f2:FEATURES]-(l:LocalitySelection)-[v1:VALUES]->(l1:Locality) | [f2,l,v1,l1]], "
      + " [ (w)<-[f3:FEATURES]-(o:OrganizationSelection)-[v2:VALUES]->(o1:Organization) | [f3,o,v2,o1]], "
      + " [ (w)<-[f4:FEATURES]-(u:UnitSelection)-[v3:VALUES]->(u1:UnitMeasure) | [f4,u,v3,u1]], "
      + " [ (w)-[sharedWith:IS_SHARED_WITH]->(office:Office) | [sharedWith, office]], "
      + " [ (w)-[instanceBy:IS_INSTANCE_BY]->(wm) | [instanceBy, wm] ], "
      + " [ (w)-[isLinkedTo:IS_LINKED_TO]->(wm) | [isLinkedTo, wm] ] "
      + "] "
      + "ORDER BY score DESC")
  List<Workpack> findAll(
      @Param("idPlan") Long idPlan,
      @Param("idPlanModel") Long idPlanModel,
      @Param("idWorkPackModel") Long idWorkPackModel,
      @Param("term") String term,
      @Param("searchCutOffScore") Double searchCutOffScore);

  @Query("MATCH (wm:WorkpackModel)<-[:IS_INSTANCE_BY | IS_LINKED_TO]-(w:Workpack)-[rf:BELONGS_TO]->(p:Plan),\n" +
      "      (w)-[:IS_IN]->(pw:Workpack)\n" +
      "WHERE CASE WHEN NOT ('Milestone' IN labels(w)) THEN NOT w.deleted ELSE true END\n"
      + "WITH *, "
      + "apoc.text.levenshteinSimilarity(apoc.text.clean(w.name), apoc.text.clean($term)) AS nameScore, "
      + "apoc.text.levenshteinSimilarity(apoc.text.clean(w.fullName), apoc.text.clean($term)) AS fullNameScore "
      + "WITH *, CASE WHEN nameScore > fullNameScore THEN nameScore ELSE fullNameScore END AS score "
      + "WHERE id(p)=$idPlan "
      + "AND id(pw) = $idWorkpackParent "
      + "AND id(wm) = $idWorkpackModel "
      + "AND ($term IS NULL OR $term = '' OR score > $searchCutOffScore) "
      + "RETURN w, wm, rf, p, [ "
      + " [ (w)<-[f1:FEATURES]-(p1:Property)-[d1:IS_DRIVEN_BY]->(pm1:PropertyModel) | [f1, p1, d1, pm1] ], "
      + " [ (w)<-[f2:FEATURES]-(l:LocalitySelection)-[v1:VALUES]->(l1:Locality) | [f2,l,v1,l1]], "
      + " [ (w)<-[f3:FEATURES]-(o:OrganizationSelection)-[v2:VALUES]->(o1:Organization) | [f3,o,v2,o1]], "
      + " [ (w)<-[f4:FEATURES]-(u:UnitSelection)-[v3:VALUES]->(u1:UnitMeasure) | [f4,u,v3,u1]], "
      + " [ (w)-[sharedWith:IS_SHARED_WITH]->(office:Office) | [sharedWith, office]], "
      + " [ (p)<-[belongsTo:BELONGS_TO]-(w)-[instanceBy:IS_INSTANCE_BY]->(wm) WHERE NOT EXISTS(belongsTo.linked) OR belongsTo.linked = false | [instanceBy, wm] ], "
      + " [ (p)<-[:BELONGS_TO{linked:true}]-(w)-[isLinkedTo:IS_LINKED_TO]->(wm) | [isLinkedTo, wm] ] "
      + "] "
      + "ORDER BY score DESC")
  List<Workpack> findAllUsingParent(
      Long idWorkpackModel,
      Long idWorkpackParent,
      Long idPlan,
      String term,
      Double searchCutOffScore);

  @Query("MATCH (wm:WorkpackModel)<-[ilk:IS_LINKED_TO]-(w:Workpack{deleted:false}), "
      + "(w)-[:IS_IN]->(pw:Workpack), "
      + "(pw)-[:IS_IN*]->(asc:Workpack)-[rf:BELONGS_TO]->(p:Plan) "
      + "WITH *, "
      + "apoc.text.levenshteinSimilarity(apoc.text.clean(w.name), apoc.text.clean($term)) AS nameScore, "
      + "apoc.text.levenshteinSimilarity(apoc.text.clean(w.fullName), apoc.text.clean($term)) AS fullNameScore "
      + "WITH *, CASE WHEN nameScore > fullNameScore THEN nameScore ELSE fullNameScore END AS score "
      + "WHERE id(p)=$idPlan "
      + "AND id(pw) = $idWorkpackParent "
      + "AND id(wm) = $idWorkpackModel "
      + "AND ($term IS NULL OR $term = '' OR score > $searchCutOffScore) "
      + "RETURN w, wm,ilk, rf, p, [ "
      + " [ (w)<-[f1:FEATURES]-(p1:Property)-[d1:IS_DRIVEN_BY]->(pm1:PropertyModel) | [f1, p1, d1, pm1] ], "
      + " [ (w)<-[f2:FEATURES]-(l:LocalitySelection)-[v1:VALUES]->(l1:Locality) | [f2,l,v1,l1]], "
      + " [ (w)<-[f3:FEATURES]-(o:OrganizationSelection)-[v2:VALUES]->(o1:Organization) | [f3,o,v2,o1]], "
      + " [ (w)<-[f4:FEATURES]-(u:UnitSelection)-[v3:VALUES]->(u1:UnitMeasure) | [f4,u,v3,u1]], "
      + " [ (w)-[isLinkedTo:IS_LINKED_TO]->(wm) | [isLinkedTo, wm] ] "
      + "] "
      + "ORDER BY score DESC")
  List<Workpack> findAllUsingParentLinked(
      Long idWorkpackModel,
      Long idWorkpackParent,
      Long idPlan,
      String term,
      Double searchCutOffScore);

  @Query("OPTIONAL MATCH (w:Workpack{deleted:false})-[ro:BELONGS_TO]->(pl:Plan), (w)-[wp:IS_INSTANCE_BY]->(wm:WorkpackModel) "
      + "WITH w, ro, pl, wp, wm "
      + "WHERE id(w) = $id "
      + "RETURN w, ro, pl, wp, wm, [ "
      + " [(w)<-[f:FEATURES]-(p:Property)-[d:IS_DRIVEN_BY]->(pm:PropertyModel) | [f, p, d, pm] ], "
      + " [(w)<-[:FEATURES]-(pv1:Property)-[v1:VALUES]->(o:Organization) | [v1, o] ], "
      + " [(w)<-[:FEATURES]-(pv2:Property)-[v2:VALUES]-(l:Locality) | [v2, l] ], "
      + " [(w)<-[:FEATURES]-(pv3:Property)-[v3:VALUES]-(u:UnitMeasure) | [v3, u] ], "
      + " [(w)<-[wfg:FEATURES]-(wg:Group) | [wfg, wg] ], "
      + " [(w)<-[wfg1:FEATURES]-(wg2:Group)-[wgps:GROUPS]->(wgp:Property)-[gpd:IS_DRIVEN_BY]->(gpm:PropertyModel) | [wgps, wgp, gpd, gpm] ], "
      + " [(w)<-[wi:IS_IN*]-(w2:Workpack{deleted:false})-[wp2:IS_INSTANCE_BY]->(wm1:WorkpackModel) | [wi, w2, wp2] ], "
      + " [(w)-[wi2:IS_IN]->(w3:Workpack{deleted:false})-[wp3:IS_INSTANCE_BY]->(wm3:WorkpackModel) | [wi2, w3, wp3, wm3] ],"
      + " [(wm)<-[wmi:IS_IN*]-(wm2:WorkpackModel) | [wmi,wm2] ],"
      + " [(wm)-[wmi2:IS_IN]->(wm3:WorkpackModel) | [wmi2,wm3] ],"
      + " [(wm)<-[f2:FEATURES]-(pm2:PropertyModel) | [f2, pm2] ],"
      + " [(wm)-[featureGroup:FEATURES]->(group:GroupModel)-[groups:GROUPS]->(groupedProperty:PropertyModel) | [featureGroup, group, groups, groupedProperty] ] "
      + "]")
  Optional<Workpack> findByIdWorkpack(@Param("id") Long id);

  @Query("MATCH (w:Workpack{deleted:false})-[rf:BELONGS_TO]->(p:Plan)-[io:IS_ADOPTED_BY]->(o:Office), "
      + "  (p)-[is:IS_STRUCTURED_BY]->(pm:PlanModel), "
      + "  (w)-[ii:IS_INSTANCE_BY]->(wm:WorkpackModel) "
      + " WHERE id(p) = $idPlan AND NOT (w)-[:IS_IN]->(:Workpack) "
      + " RETURN w, rf, p, io, o, ii, pm, wm, [ "
      + " [(p)<-[cp:CAN_ACCESS_PLAN]-(p2:Person) | [cp, p2] ],"
      + " [(w)<-[ca:CAN_ACCESS_WORKPACK]-(p:Person) | [ca, p] ],"
      + " [(w)<-[wi:IS_IN*]-(w2:Workpack{deleted:false})-[ii_2:IS_INSTANCE_BY]->(wm_2:WorkpackModel) | [wi, w2, ii_2, wm_2] ],"
      + " [(w2)<-[ca2:CAN_ACCESS_WORKPACK]-(p2:Person) | [ca2, p2] ],"
      + " [(w2)-[rf_2:BELONGS_TO]->(p_2:Plan)-[io_2:IS_ADOPTED_BY]->(o_2:Office) | [rf_2, p_2, io_2, o_2] ],"
      + " [(w)-[wi2:IS_IN*]->(w3:Workpack{deleted:false})-[ii_3:IS_INSTANCE_BY]->(wm_3:WorkpackModel) | [wi2, w3, ii_3, wm_3] ],"
      + " [(w3)-[rf_3:BELONGS_TO]->(p_3:Plan)-[io_3:IS_ADOPTED_BY]->(o_3:Office) | [rf_3, p_3, io_3, o_3] ],"
      + " [(wm)<-[wmi:IS_IN*]-(wm2:WorkpackModel) | [wmi,wm2] ]"
      + " ]")
  Set<Workpack> findAllUsingPlan(@Param("idPlan") Long idPlan);

  @Query("MATCH (plan:Plan) " +
      "WHERE id(plan)=$idPlan " +
      "MATCH (plan)<-[belongsTo:BELONGS_TO]-(w:Workpack{deleted:false,canceled:false}) " +
      "MATCH (plan)-[isStructuredBy:IS_STRUCTURED_BY]->(planModel:PlanModel) " +
      "MATCH (w)-[instanceBy:IS_INSTANCE_BY]->(model:WorkpackModel) " +
      "WHERE NOT (w)-[:IS_IN]->(:Workpack{deleted:false,canceled:false}) " +
      "RETURN w, belongsTo, isStructuredBy, plan, instanceBy, planModel, model, [ " +
      " [(w)-[isLinkedTo:IS_LINKED_TO]-(modelLinked:WorkpackModel) | [isLinkedTo, modelLinked] ], " +
      " [(w)<-[f1:FEATURES]-(p1:Property)-[d1:IS_DRIVEN_BY]->(pm1:PropertyModel) | [f1, p1, d1, pm1] ], " +
      " [(w)<-[wi:IS_IN*]-(w2:Workpack{deleted:false,canceled:false})<-[f2:FEATURES]-(p2:Property)-[d2:IS_DRIVEN_BY]->(pm2:PropertyModel) | [wi,w2,f2, p2, d2, pm2] ], "
      +
      " [(w2)-[bt:BELONGS_TO]->(p:Plan) | [bt, p]], " +
      " [(w2)-[ib2:IS_INSTANCE_BY]->(wm2:WorkpackModel)<-[f5:FEATURES]-(pm5:PropertyModel) | [ib2, wm2, f5, pm5] ], " +
      " [(model)<-[f4:FEATURES]-(pm4:PropertyModel) | [f4, pm4] ], " +
      " [(model)<-[wmi:IS_IN*]-(wm2:WorkpackModel)<-[f6:FEATURES]-(pm6:PropertyModel) | [wmi,wm2, f6, pm6] ] " +
      " ]")
  Set<Workpack> findAllByPlanWithProperties(@Param("idPlan") Long idPlan);

  @Query("MATCH (w:Workpack{deleted:false})-[ii:IS_INSTANCE_BY]->(wm:WorkpackModel) "
      + " WHERE id(w) = $id "
      + " RETURN w, ii, wm, [ "
      + " [(w)<-[f1:FEATURES]-(p1:Property)-[d1:IS_DRIVEN_BY]->(pm1:PropertyModel) | [f1, p1, d1, pm1] ], "
      + " [(w)<-[f2:FEATURES]-(l:LocalitySelection)-[v1:VALUES]->(l1:Locality) | [f2,l,v1,l1]], "
      + " [(w)<-[f3:FEATURES]-(o:OrganizationSelection)-[v2:VALUES]->(o1:Organization) | [f3,o,v2,o1]], "
      + " [(w)<-[f4:FEATURES]-(u:UnitSelection)-[v3:VALUES]->(u1:UnitMeasure) | [f4,u,v3,u1]], "
      + " [(w)<-[wfg:FEATURES]-(wg:Group)-[wgps:GROUPS]->(wgp:Property)-[gd1:IS_DRIVEN_BY]->(gpm1:PropertyModel) | [wfg, wgps, wg, wgp, gd1, gpm1] ], "
      + " [(w)<-[wfg1:FEATURES]-(wg1:Group)-[wgps1:GROUPS]->(lg:LocalitySelection)-[vg1:VALUES]->(lg1:Locality) | [wfg1,wg1, wgps1, lg, vg1, lg1] ], "
      + " [(w)<-[wfg2:FEATURES]-(wg2:Group)-[wgps2:GROUPS]->(og:OrganizationSelection)-[vg2:VALUES]->(og2:Organization) | [wfg2, wg2, wgps2, og, vg2, og2] ], "
      + " [(w)<-[wfg3:FEATURES]-(wg3:Group)-[wgps3:GROUPS]->(ug:UnitSelection)-[vg3:VALUES]->(ug3:UnitMeasure) | [wfg3, wg3, wgps3, ug, vg3, ug3] ], "
      + " [(w)<-[wi2:IS_IN]-(w2:Workpack{deleted: false}) | [wi2,w2]] "
      + "]")
  Optional<Workpack> findByIdThin(@Param("id") Long id);

  @Query("MATCH (w:Workpack{deleted:false})-[ii:IS_INSTANCE_BY]->(wm:WorkpackModel) "
      + " WHERE id(w) = $id "
      + " RETURN w, ii, wm, "
      + " [(w)<-[wii:IS_IN*]-(children:Workpack) | [wii,children]] ")
  Optional<Workpack> findByIdWithAllChildren(@Param("id") Long id);

  @Query("MATCH (w:Workpack{deleted:false})<-[:IS_IN*]-(child:Workpack{deleted:false})<-[canAccess:CAN_ACCESS_WORKPACK]-(p:Person) "
      +
      " WHERE id(w) = $idWorkpack " +
      " AND id(p) = $idPerson " +
      " AND canAccess.idPlan = $idPlan" +
      " RETURN count(canAccess)")
  Long countCanAccessWorkpack(
      @Param("idWorkpack") Long idWorkpack,
      @Param("idPerson") Long idPerson,
      @Param("idPlan") Long idPlan);

  @Query("MATCH (person:Person) " +
      "WHERE id(person)=$idPerson " +
      "MATCH (plan:Plan)<-[belongsTo:BELONGS_TO]-(workpack:Workpack{deleted:false}) " +
      "OPTIONAL MATCH (workpack)<-[permission:CAN_ACCESS_WORKPACK]-(person) " +
      "WITH plan, belongsTo, workpack, permission, person " +
      "WHERE id(plan)=$idPlan AND ( " +
      " ( (workpack)<-[:CAN_ACCESS_WORKPACK]-(person) ) OR " +
      " ( person.administrator=true ) " +
      ") " +
      "RETURN id(workpack)")
  Set<Long> findAllWorkpacksWithPermissions(
      @Param("idPlan") Long idPlan,
      @Param("idPerson") Long idPerson);

  @Query("MATCH (workpack:Workpack{deleted:false}) " +
      "MATCH (workpack)-[belongsTo:BELONGS_TO]->(plan:Plan)-[structuredBy:IS_STRUCTURED_BY]->(planModel:PlanModel)" +
      " " +
      "MATCH (workpack)-[isLinkedTo:IS_LINKED_TO]->(model:WorkpackModel)-[modelBelongsTo:BELONGS_TO]->(planModel) " +
      "WHERE id(workpack)=$idWorkpack AND id(plan)=$idPlan " +
      "RETURN workpack, belongsTo, plan, structuredBy, planModel, isLinkedTo, model, modelBelongsTo, [" +
      " [ (model)<-[modelIsIn:IS_IN*]-(childrenModel:WorkpackModel) | [modelIsIn, childrenModel]] " +
      "]")
  Optional<WorkpackModel> findWorkpackModeLinkedByWorkpackAndPlan(
      Long idWorkpack,
      Long idPlan);

  @Query("MATCH (w:Workpack) " +
      "WHERE id(w)=$idWorkpack " +
      "RETURN w, [ " +
      " [(w)<-[f1:FEATURES]-(p:Property) | [f1,p]], " +
      " [(w)-[a:IS_INSTANCE_BY]->(m:WorkpackModel) | [a,m]], " +
      " [(w)<-[f2:FEATURES]-(l)-[v1:VALUES]->(l1) | [f2,l,v1,l1]], " +
      " [(w)<-[i:IS_IN*]-(v:Workpack{canceled:false}) | [i,v]], " +
      " [(w)<-[i:IS_IN*]-(:Workpack{canceled:false})<-[h:FEATURES]-(q:Property) | [h,q]], " +
      " [(w)<-[i:IS_IN*]-(:Workpack{canceled:false})-[ii:IS_INSTANCE_BY]->(n:WorkpackModel) | [ii,n]], " +
      " [(w)<-[i:IS_IN*]-(:Workpack{canceled:false})-[f5:FEATURES]-(l)-[v1:VALUES]->(l1) | [f5,l,v1,l1]] " +
      "]")
  Optional<Workpack> findWithPropertiesAndModelAndChildrenById(Long idWorkpack);

  @Query("MATCH (workpack:Workpack) " +
      "OPTIONAL MATCH (workpack)<-[snapshotOf:IS_SNAPSHOT_OF]-(:Workpack) " +
      "WITH workpack, snapshotOf " +
      "WHERE id(workpack)=$idWorkpack " +
      "RETURN count(snapshotOf)>0 ")
  boolean hasSnapshot(Long idWorkpack);

  @Query("MATCH (deliverable:Deliverable)<-[:FEATURES]-(schedule:Schedule)<-[:IS_SNAPSHOT_OF]-(scheduleSnapshot:Schedule)-[:COMPOSES]->(baseline:Baseline {active: true, cancelation: false}) " +
      "WHERE id(deliverable)=$idDeliverable " +
      "RETURN count(baseline)>0 ")
  boolean hasActiveBaseline(Long idDeliverable);

  @Query("MATCH (w:Workpack)-[:IS_IN*]->(:Project{deleted:false,canceled:false})-[:IS_BASELINED_BY]->" +
      "(b:Baseline{active: true})" +
      " " +
      "WHERE id(w)=$idWorkpack " +
      "RETURN count(b)>0 ")
  boolean hasChildrenWithActiveBaseline(Long idWorkpack);

  @Query("MATCH (w:Workpack)-[:IS_IN*]->(:Project{deleted:false,canceled:false})-[:IS_BASELINED_BY]->" +
      "(b:Baseline{active:true})," +
      " " +
      "   (w)<-[:IS_SNAPSHOT_OF]-(s)-[:COMPOSES]->(b) " +
      "WHERE id(w)=$idWorkpack " +
      "RETURN count(s)>0")
  boolean isPresentInBaseline(Long idWorkpack);

  @Query("MATCH (workpack:Workpack)-[:IS_BASELINED_BY]->(baseline:Baseline{active: true}) " +
      "WHERE id(workpack)=$idWorkpack " +
      "RETURN baseline ")
  Optional<Baseline> findActiveBaseline(Long idWorkpack);

  @Query("MATCH (w:Workpack)-[:IS_IN*]->(v:Project)-[:IS_BASELINED_BY]->(b:Baseline{active: true}), " +
      "(w)<-[:IS_SNAPSHOT_OF]-(s:Workpack)-[:COMPOSES]->(b) " +
      "WHERE id(w)=$idWorkpack " +
      "RETURN b ")
  List<Baseline> findActiveBaselineFromProjectChildren(Long idWorkpack);

  @Query("MATCH (w:Workpack)<-[:IS_IN*]-(v:Project)-[:IS_BASELINED_BY]->(b:Baseline{active: true}), " +
      "(w)<-[:IS_SNAPSHOT_OF]-(s:Workpack)-[:COMPOSES]->(b) " +
      "RETURN b ")
  List<Baseline> findActiveBaselineFromProjectParent(Long idWorkpack);

  @Query("MATCH (workpack:Workpack) " +
      "OPTIONAL MATCH (workpack)-[isBaselinedBy:IS_BASELINED_BY]->(baseline:Baseline{status:'PROPOSED'}) " +
      "WITH workpack, isBaselinedBy, baseline " +
      "WHERE id(workpack)=$idWorkpack " +
      "RETURN count(baseline)>0 ")
  boolean hasProposedBaseline(Long idWorkpack);

  @Query("MATCH (workpack:Workpack) " +
      "OPTIONAL MATCH (workpack)-[isBaselinedBy:IS_BASELINED_BY]->(baseline:Baseline{cancelation:true}) " +
      "WITH workpack, isBaselinedBy, baseline " +
      "WHERE id(workpack)=$idWorkpack " +
      "WITH collect(baseline) AS baselines " +
      "RETURN CASE baselines WHEN [] THEN false ELSE none(b IN baselines WHERE b.status IN ['DRAFT', 'REJECTED']) " +
      "END")
  boolean hasCancelPropose(Long idWorkpack);

  @Query("MATCH (w:Workpack)-[:IS_INSTANCE_BY]->(m:WorkpackModel) " +
      "WHERE id(w)=$idWorkpack " +
      "RETURN m")
  Optional<WorkpackModel> findWorkpackModelByWorkpackId(Long idWorkpack);

  @Query("MATCH (s:Workpack)-[:IS_SNAPSHOT_OF]->(:Workpack)-[:IS_INSTANCE_BY]->(m:WorkpackModel) " +
      "WHERE id(s)=$idWorkpack " +
      "RETURN m")
  Optional<WorkpackModel> findWorkpackModelBySnapshotId(Long idWorkpack);

  @Query("MATCH (w:Workpack)-[i:IS_IN]->(p:Workpack) " +
      "WHERE id(w)=$idWorkpack AND id(p)=$idParent " +
      "RETURN count(i)>0")
  boolean isWorkpackInParent(
      Long idWorkpack,
      Long idParent);

  @Query("MATCH (w:Workpack)-[i:IS_IN]->(p:Workpack) " +
      "WHERE id(w)=$workpackId AND id(p)=$parentId " +
      "DETACH DELETE i")
  void deleteIsInRelationshipByWorkpackIdAndParentId(
      Long workpackId,
      Long parentId);

  @Query("MATCH (w:Workpack)-[i:IS_IN*]->(p:Workpack) " +
      "WHERE id(p) IN $idsWorkpacks " +
      "RETURN id(w)")
  List<Long> idsWorkpacksChildren(
      List<Long> idsWorkpacks);

  @Query("MATCH (w:Workpack), (p:Workpack) " +
      "WHERE id(w)=$childId AND id(p)=$parentId " +
      "CREATE (w)-[:IS_IN]->(p)")
  void createIsInRelationship(
      @Param("childId") Long childId,
      @Param("parentId") Long parentId);

  @Query("MATCH (w:Workpack) " +
      "WHERE id(w)=$childId " +
      "SET w.idParent = $parentId ")
  void setNewIdParentPasted(
      @Param("childId") Long childId,
      @Param("parentId") Long parentId);

  @Query("MATCH (w:Workpack) " +
      "WHERE id(w)=$idWorkpack " +
      "SET w.public = $sharedPublicStatus, w.publicLevel = $publicLevel")
  void setSharedPublicStatus(
      @Param("idWorkpack") Long idWorkpack,
      @Param("sharedPublicStatus") Boolean $sharedPublicStatus,
      @Param("publicLevel") String $publicLevel);

  @Query("MATCH (w:Workpack) " +
      "WHERE id(w)=$idWorkpack " +
      "SET w.deleted = true")
  void setWorkpackDeleted(
      @Param("idWorkpack") Long idWorkpack);

  @Query("MATCH (w:Workpack) " +
      "WHERE id(w) IN $idsWorkpacks " +
      "SET w.canceled = $canceled")
  void setWorkpacksCanceled(
      @Param("idsWorkpacks") List<Long> idsWorkpacks,
      @Param("canceled") Boolean canceled);

  @Query("MATCH (w:Workpack) " +
      "WHERE id(w) IN $idsWorkpacks " +
      "SET w.deleted = $deleted")
  void setWorkpacksDeleted(
      @Param("idsWorkpacks") List<Long> idsWorkpacks,
      @Param("deleted") Boolean deleted);

  @Query("MATCH (w:Workpack), (wm:WorkpackModel) " +
      "WHERE id(w)=$workpackId AND id(wm)=$workpackModelId " +
      "CREATE (w)-[:IS_INSTANCE_BY]->(wm)")
  void createIsInstanceByRelationship(
      Long workpackId,
      Long workpackModelId);

  @Query("MATCH (w:Workpack), (p:Property) " +
      "WHERE id(w)=$workpackId AND id(p)=$propertyId " +
      "CREATE (w)<-[:FEATURES]-(p)")
  void createFeaturesRelationship(
      Long workpackId,
      Long propertyId);

  @Query("MATCH (workpack:Workpack)<-[:FEATURES]-(:Schedule) " +
      "WHERE id(workpack)=$idWorkpack " +
      "RETURN count(DISTINCT workpack) > 0")
  boolean hasScheduleRelated(Long idWorkpack);

  @Query("MATCH (d:Deliverable)-[:IS_IN*]->(p:Project) " +
      "WHERE id(d)=$idDeliverable " +
      "RETURN p")
  Optional<Project> findProject(Long idDeliverable);

  @Query("MATCH (d:Deliverable)-[:IS_IN*]->(p:Program) " +
      "WHERE id(d)=$idDeliverable " +
      "RETURN p")
  Optional<Program> findProgram(Long idDeliverable);

  @Query("MATCH (w:Workpack)-[:BELONGS_TO]->(p:Plan) " +
      "WHERE id(p)=$id " +
      "RETURN count(w)>0")
  boolean existsByPlanId(@Param("id") Long id);

  @Query("MATCH (w:Workpack)<-[:IS_IN*]-(v:Workpack{deleted:false}) " +
      "WHERE id(w)=$id " +
      "WITH collect(v) AS children " +
      "RETURN ALL(z IN children WHERE NOT (z)-[:IS_BASELINED_BY]->(:Baseline) OR z.canceled=true)")
  boolean canBeDeleted(Long id);

  @Query("MATCH (workpack:Workpack) " +
      "WHERE id(workpack)=$idWorkpack " +
      "OPTIONAL MATCH (children:Workpack)-[:IS_IN*]->(workpack) " +
      "OPTIONAL MATCH (:WorkpackModel{scheduleSessionActive:true})<-[:IS_INSTANCE_BY]-(children:Workpack) " +
      "WITH * " +
      "WHERE (children)<-[:FEATURES]-(:Schedule) " +
      "RETURN count(children) > 0")
  boolean hasAnyChildrenWithScheduleSessionActive(Long idWorkpack);

  @Query("MATCH (schedule:Schedule)-[:FEATURES]->(workpack:Workpack)-[:IS_INSTANCE_BY]->(model:WorkpackModel) " +
      "WHERE id(workpack)=$idWorkpack " +
      "RETURN model.scheduleSessionActive = true AND count(schedule) > 0 ")
  Boolean hasScheduleSessionActive(Long idWorkpack);

  @Query("MATCH (workpack:Workpack)-[belongsTo:BELONGS_TO]->(plan:Plan) " +
      "WHERE id(workpack)=$idWorkpack " +
      "AND id(plan)=$idPlan " +
      "RETURN workpack, [" +
      "  [ (workpack)-[ii:IS_INSTANCE_BY]->(wm:WorkpackModel) | [ii, wm] ], " +
      "  [ (workpack)<-[ca:CAN_ACCESS_WORKPACK]-(p:Person) | [ca, p] ], " +
      "  [ (workpack)-[i:IS_IN*]->(pw:Workpack) | [i, pw] ], " +
      "  [ (pw)-[ii2:IS_INSTANCE_BY]->(wm2:WorkpackModel) | [ii2, wm2] ], " +
      "  [ (pw)<-[ca2:CAN_ACCESS_WORKPACK]-(p2:Person) | [ca2, p2] ] " +
      "]")
  Optional<Workpack> findByIdWorkpackAndIdPlan(
      Long idWorkpack,
      Long idPlan);

  @Query("MATCH (workpack:Workpack) " +
      "WHERE id(workpack)=$idWorkpack " +
      "MATCH (workpack)-[:IS_IN*1..]->(parent:Workpack)-[:BELONGS_TO]->(plan:Plan) " +
      "WHERE id(plan)=$idPlan " +
      "RETURN parent")
  Set<Workpack> findWorkpackParentsHierarchy(
      @Param("idPlan") Long idPlan,
      @Param("idWorkpack") Long idWorkpack);

  @Query("MATCH (workpack:Workpack) " +
      "OPTIONAL MATCH (children:Workpack)-[:IS_IN]->(workpack) " +
      "WITH * " +
      "WHERE id(workpack)=$idWorkpack " +
      "RETURN count(children) > 0")
  boolean hasChildren(@Param("idWorkpack") Long idWorkpack);

  @Query("MATCH (w:Workpack), (p:Plan) " +
      "WHERE id(w)=$idWorkpack AND id(p)=$idPlan " +
      "CREATE (w)-[:BELONGS_TO{linked: false}]->(p)")
  void createBelongsToRelationship(@Param("idWorkpack") Long idWorkpack, @Param("idPlan") Long idPlan);

  @Override
  List<Workpack> findAll();

  @Query("MATCH (workpack:Workpack) " +
      "WHERE id(workpack) IN $ids " +
      "RETURN workpack ")
  List<Workpack> findAllWithDeletedByIdThin(List<Long> ids);

  @Query("MATCH (master:Workpack)<-[of:IS_SNAPSHOT_OF]-(workpack:Workpack)-[:COMPOSES]->(b:Baseline) " +
      "WHERE id(master) IN $ids AND ID(b) = $baseline " +
      "RETURN workpack, of, master ")
  List<Workpack> findAllSnapshotWithDeletedByIdThin(List<Long> ids, Long baseline);

  @Query(" MATCH (master:Workpack), (snapshot:Workpack) " +
      "WHERE ID(master) = $masterId AND ID(snapshot) = $snapshotId " +
      "SET master.category = 'MASTER' " +
      "CREATE (snapshot)-[:IS_SNAPSHOT_OF]->(master) ")
  void createSnapshotRelationshipWithMaster(
      Long masterId,
      Long snapshotId);

  @Query(" MATCH (baseline:Baseline), (snapshot:Workpack) " +
      "WHERE ID(baseline) = $baselineId AND ID(snapshot) = $snapshotId " +
      "CREATE (snapshot)-[:COMPOSES]->(baseline) ")
  void createSnapshotRelationshipWithBaseline(
      Long baselineId,
      Long snapshotId);

  @Query("MATCH (project:Workpack{deleted:false})<-[:IS_IN*]-(children:Workpack{deleted:false, canceled:false})  " +
      "WHERE id(project) = $id AND ((ANY(label IN labels(children) WHERE label IN ['Deliverable']) AND (children)<-[:FEATURES]-(:Schedule)) OR ANY(label IN labels(children) WHERE label IN ['Milestone'])) "
      +
      "RETURN ID(children) ")
  Set<Long> findAllDeliverableAndMilestoneByProject(Long id);

  @Query("MATCH (w:Workpack)<-[:IS_IN*]-(children:Workpack) " +
      "WHERE id(w)=$workpackId " +
      "RETURN ID(children) ")
  Set<Long> findAllChildren(@Param("workpackId") Long workpackId);

  @Query("MATCH (w:Workpack)<-[:FEATURES]-(p:Property)-[:IS_DRIVEN_BY]->(pm:PropertyModel) " +
      "WHERE id(w) = $id AND pm.name IN ['Situação', 'Status'] " +
      "SET p.value = $value")
  void updateSituationValue(Long id, String value);

  @Query("MATCH (w:Workpack)<-[:FEATURES]-(p:Property)-[:IS_DRIVEN_BY]->(pm:PropertyModel) " +
      "WHERE id(w) = $id AND pm.name IN ['Situação', 'Status'] " +
      "OPTIONAL MATCH (w)-[:IS_BASELINED_BY]->(b:Baseline {active: true}) " +
      "WITH w, p, pm, b " +
      "SET p.value = CASE " +
      "  WHEN b IS NOT NULL AND w:Project AND w.completed = true THEN 'Concluído' " + // não altera se concluído
      "  WHEN b IS NOT NULL AND w:Project THEN 'Execução' " +
      "  ELSE pm.defaultValue " +
      "END")
  void resetSituationOrStatusToDefault(Long id);

  @Query("MATCH (w:Workpack)<-[:FEATURES]-(p:Property)-[:IS_DRIVEN_BY]->(pm:PropertyModel {name:'Situação'}) " +
      "WHERE id(w) = $id " +
      "RETURN p.value = 'Concluída'")
  boolean isSituationConcluded(Long id);

  @Query("MATCH (w:Workpack) " +
      "WHERE id(w) = $id " +
      "WITH CASE " +
      "        WHEN w.category = 'SNAPSHOT' " +
      "        THEN head([(w)-[:IS_SNAPSHOT_OF]->(master:Workpack) | master]) " +
      "        ELSE w " +
      "     END AS masterW " +
      "MATCH (masterW)<-[:FEATURES]-(p:Property)-[:IS_DRIVEN_BY]->(pm:PropertyModel {name:'Situação'}) " +
      "RETURN p.value = 'A cancelar'")
  Boolean isSituationToCancel(Long id);

  @Query("MATCH (snapshot:Workpack)-[:IS_SNAPSHOT_OF]->(master:Workpack) " +
      "MATCH (master)<-[:FEATURES]-(p:Property)-[:IS_DRIVEN_BY]->(pm:PropertyModel {name:'Situação'}) " +
      "WHERE id(snapshot) = $id and snapshot.canceled = true " +
      "RETURN p.value = 'Cancelada'")
  Boolean isSituationCanceled(Long id);

  @Query("MATCH (snapshot:Workpack) " +
      "WHERE id(snapshot) = $id " +
      "RETURN snapshot.canceled")
  Boolean isCanceled(Long id);

  @Query("MATCH (w:Workpack)<-[:FEATURES]-(p:Property) " +
      "WHERE id(p) = $id " +
      "RETURN id(w)")
  Long findWorkpackIdByPropertyId(Long id);

  @Query("MATCH (w:Workpack) " +
      "WHERE id(w) = $id " +
      "RETURN 'Project' IN labels(w)")
  boolean isProject(Long id);

  @Query("MATCH (w:Workpack)<-[:FEATURES]-(p:Property)-[:IS_DRIVEN_BY]->(pm:PropertyModel) " +
      "WHERE id(w) = $id " +
      "AND pm.name IN ['Situação', 'Status'] " +
      "RETURN p.value = 'Concluído'")
  Boolean isSituationCompleted(Long id);

  @Query("MATCH (w:Workpack) " +
      "WHERE id(w) = $idWorkpack " +
      "MATCH (w)-[:IS_IN*0..]->(p:Project) " +
      "OPTIONAL MATCH (p)<-[:FEATURES]-(prop:Property)-[:IS_DRIVEN_BY]->(pm:PropertyModel) " +
      "WHERE pm.name IN ['Situação','Status'] " +
      "RETURN prop.value = 'Estruturação' AS isEstruturacao")
  Boolean isEstruturacao(@Param("idWorkpack") Long idWorkpack);

  // @Query(
  // "MATCH (organizer:Organizer)-[:IS_IN*]->(project:Project) " +
  // "WHERE id(organizer) = $idOrganizer " +
  // "RETURN ID(project)"
  // )
  // Optional<Long> findProjectIdByOrganizerId(Long idOrganizer);

  // @Query(
  // "MATCH (milestone:Milestone)-[:IS_IN*]->(project:Project) " +
  // "WHERE id(milestone) = $idMilestone " +
  // "RETURN ID(project)"
  // )
  // Long findProjectIdByMilestoneId(Long idMilestone);

  // @Query(
  // "MATCH (deliverable:Deliverable)-[:IS_IN*]->(project:Project) " +
  // "WHERE id(deliverable) = $idDeliverable " +
  // "RETURN ID(project)"
  // )
  // Long findProjectIdByDeliverableId(Long idDeliverable);

  // @Query(
  // "MATCH (children:Workpack{deleted:false})-[:IS_IN*]->(parent:Workpack) " +
  // "WHERE ID(parent) = $idParent AND (ANY(label IN labels(children) WHERE label
  // IN ['Deliverable']) " +
  // "AND NOT (children)<-[:FEATURES]-(:Schedule)) " +
  // "RETURN children"
  // )
  // List<Workpack> findDeliveriesWithoutScheduleByParentId(Long idParent);

  @Query("MATCH (w:Workpack) " +
      "WHERE ID(w) = $idWorkpack " +
      "OPTIONAL MATCH (w)<-[:IS_IN*0..]-(e:Workpack) " +
      "OPTIONAL MATCH (e)<-[:FEATURES]-(sc:Schedule) " +
      "OPTIONAL MATCH (sc)<-[:COMPOSES]-(st:Step) " +
      "WITH DISTINCT e " +
      ", CASE WHEN NOT (e.deleted OR e.canceled) AND sc IS NULL AND 'Deliverable' IN labels(e) THEN 1 ELSE 0 end AS noCron "
      +
      ", CASE WHEN NOT (e.deleted OR e.canceled) AND SUM(toFloat(st.plannedWork)) <= 0 AND 'Deliverable' IN labels(e) THEN 1 ELSE 0 end AS noScope "
      +
      ", CASE WHEN NOT e.deleted AND EXISTS ((e)<-[:FEATURES]-(:Property {value:'A cancelar'})-[:IS_DRIVEN_BY]->(:PropertyModel {name: 'Situação'}) ) AND 'Deliverable' IN labels(e) THEN 1 ELSE 0 end AS toCancel "
      +
      ", CASE WHEN e.deleted AND EXISTS ((e)<-[:IS_SNAPSHOT_OF]-(:Workpack)-[:COMPOSES]->(:Baseline {active:TRUE})) AND ('Deliverable' IN labels(e) OR 'Milestone' IN labels(e)) THEN 1 ELSE 0 end AS deleted "
      +
      ", CASE WHEN NOT (e.deleted or e.canceled) AND NOT EXISTS ((e)<-[:IS_SNAPSHOT_OF]-(:Workpack)-[:COMPOSES]->(:Baseline)) AND ('Deliverable' IN labels(e) OR 'Milestone' IN labels(e)) THEN 1 ELSE 0 end AS isNew "
      +
      " " +
      "RETURN " +
      "  CASE WHEN SUM(noCron) > 0 THEN true ELSE false end AS noSchedule " +
      ", CASE WHEN SUM(noScope) > 0 THEN true ELSE false end AS noScope " +
      ", CASE WHEN SUM(toCancel) > 0 THEN true ELSE false end AS toCancel " +
      ", CASE WHEN SUM(deleted) > 0 THEN true ELSE false end AS deletedWithBaseline " +
      ", CASE WHEN SUM(isNew) > 0 THEN true ELSE false end AS isNew ")
  WorkpackBreakdownClassificationDto getWorkpackClassifications(@Param("idWorkpack") Long idWorkpack);

  @Query("MATCH (o:Organizer) " +
      "WHERE ID(o) = $idOrganizer " +
      "RETURN EXISTS ((o)-[:IS_IN*]->(:Project)) ")
  Boolean getOrganizerIsInAProject(@Param("idOrganizer") Long idOrganizer);

  static final String doSearchInAll_queryBase = "//PARÂMETROS:\n" +
      "WITH \n" +
      "$workpackId \n" +
      "//null \n" +
      "as rootId,                      	// id do workpack escopo raiz da busca. Se NULL, procura no plano inteiro.\n" +
      "trim(coalesce($term, '')) as frase,     // Frase procurada\n" +
      "$userId as userId,                           		// id do usuário\n" +
      "$planId as planId                               // id do plano\n" +
      "\n" +
      "\n" +
      "WITH rootId, userId, planId, frase,\n" +
      "apoc.text.clean(frase) as token  // Obtem a frase completa em um só token\n" +
      "//[t IN split(frase, ' ') WHERE size(apoc.text.clean(t)) > 2] as tokens    // Obtém a coleção de palavras (>2 caracteres) da frase\n"
      +
      "\n" +
      "\n" +
      "// SELECIONA A (Person) DO USUÁRIO\n" +
      "MATCH (user:Person) \n" +
      "where id(user) = userId\n" +
      "\n" +
      "CALL {\n" +
      "	WITH token, planId, rootId\n" +
      "	// Filtra os workpacks pelo escopo a partir da raiz da busca\n" +
      "	MATCH (p:Plan)<-[:BELONGS_TO]-(r:Workpack)<-[:IS_IN*0..]-(w:Workpack { deleted:FALSE, canceled:FALSE })-[:IS_INSTANCE_BY]->(wm:WorkpackModel)\n"
      +
      "	WHERE size(token) >= 3\n" +
      "		and (rootId IS NOT NULL AND id(r)=rootId)\n" +
      "		and id(p) = planId\n" +
      "		and (apoc.text.clean(w.name) CONTAINS token              // Procura nos names pela frase completa\n" +
      "			OR apoc.text.clean(w.fullName) CONTAINS token)   // Procura nos fullNames pela frase completa\n" +
      "	RETURN w, wm\n" +
      "	\n" +
      "	UNION ALL\n" +
      "	\n" +
      "	// Filtra os workpacks pelo escopo a partir da raiz da busca\n" +
      "	WITH token, planId, rootId\n" +
      "	MATCH (p:Plan)<-[:BELONGS_TO]-(w:Workpack { deleted:FALSE, canceled:FALSE })-[:IS_INSTANCE_BY]->(wm:WorkpackModel)\n"
      +
      "	WHERE size(token) >= 3\n" +
      "    and rootId IS NULL\n" +
      "	and id(p) = planId\n" +
      "    and (apoc.text.clean(w.name) CONTAINS token          // Procura nos names pela frase completa\n" +
      "        OR apoc.text.clean(w.fullName) CONTAINS token)   // Procura nos fullNames pela frase completa\n" +
      "	RETURN w, wm\n" +
      "}\n" +
      "\n" +
      "  \n" +
      "WITH DISTINCT w, wm, token, user, planId, rootId, frase\n" +
      "where (w)-[:IS_IN|BELONGS_TO|IS_ADOPTED_BY*0..]->()<-[:CAN_ACCESS_OFFICE|CAN_ACCESS_PLAN|CAN_ACCESS_WORKPACK]-(user)\n"
      +
      "    OR (w)<-[:IS_IN*0..]-()<-[:CAN_ACCESS_WORKPACK]-(user)\n" +
      "    or user.administrator\n" +
      "\n" +
      "// Chama uma subquery para resgatar o breadcrumb de cada item encontrado\n" +
      "CALL {\n" +
      "    WITH w, rootId\n" +
      "    MATCH (w)-[:IS_IN*0..]->(parent:Workpack)-[:IS_IN*0..]->(last:Workpack),\n" +
      "    (parent)-[:IS_INSTANCE_BY]->(pwm:WorkpackModel)\n" +
      "    WHERE id(last) = rootId or (rootId IS NULL AND not exists ((last)-[:IS_IN]->(:Workpack)))\n" +
      "    MATCH path = ((w)-[:IS_IN*]->(parent))\n" +
      "    WITH distinct parent, pwm, length(path) AS hops\n" +
      "    ORDER BY hops desc\n" +
      "    WITH parent, pwm\n" +
      "    RETURN distinct \n" +
      "        //collect('(' + pwm.modelName + ') ' + parent.name) AS parents,\n" +
      "        collect({modelo: pwm.modelName, id:id(parent), nome: parent.name}) as breadcrumbs\n" +
      "}\n" +
      "\n" +
      "// Organiza as variáveis e calcula o nível de similaridade (com o token) para cada workpack encontrado\n" +
      "WITH distinct id(w) AS id, planId, token, frase, w, breadcrumbs, \n" +
      "     wm.modelName AS model,\n" +
      "     wm.fontIcon AS icon,\n" +
      "     trim(w.name) AS name,\n" +
      "     trim(w.fullName) AS fullName,\n" +
      "     case   \n" +
      "			when w.name = frase then 16\n" +
      "			when apoc.text.clean(w.name) = token then 14\n" +
      "			when w.fullName = frase then 12\n" +
      "			when apoc.text.clean(w.fullName) = token then 10\n" +
      "			when apoc.text.clean(w.name) contains token and apoc.text.clean(w.fullName) contains token then 8\n" +
      "            when apoc.text.clean(w.name) contains token then 6\n" +
      "            when apoc.text.clean(w.fullName) contains token then 4\n" +
      "            else 1\n" +
      "     end as fator\n" +
      "WITH  id, planId, token, frase, model, icon, name, fullName, breadcrumbs,\n" +
      "    // Calculando a pontuação no ranking de proximidade\n" +
      "    (apoc.text.jaroWinklerDistance(token, apoc.text.clean(w.name))\n" +

      "    + apoc.text.jaroWinklerDistance(token, apoc.text.clean(w.fullName)))/fator AS matchDistance\n" +
      "WHERE breadcrumbs IS NOT NULL AND size(breadcrumbs) > 0\n" +
      "  	";

  /**
   * 
   * @param workpackId
   * @param term
   * @param userId
   * @param planId
   * @param request
   * @return
   */
  @Query(value = doSearchInAll_queryBase +
      "RETURN planId, id, model, icon, name, fullName, matchDistance, breadcrumbs\n" +
      "ORDER BY matchDistance ASC", countQuery = doSearchInAll_queryBase +
          "RETURN count(id)")
  public Page<UniversalSearchItemQueryResult> doSearchInAll(Long workpackId, String term, Long userId, Long planId,
      PageRequest request);

  @Query("MATCH (w:Workpack)-[belongsTo:BELONGS_TO]->(plan:Plan) " +
      "WHERE ID(w) = $idWorkpack " +
      "AND (NOT EXISTS(belongsTo.linked) OR belongsTo.linked = false) " +
      "RETURN plan ")
  public Plan findPlanByWorkpackId(Long idWorkpack);

  @Query("MATCH (w:Workpack)<-[:IS_IN*]-(child:Workpack{deleted: false})\n" +
      "WHERE id(w) = $workpackId AND $type IN labels(child)\n" +
      "OPTIONAL MATCH (child)<-[:FEATURES]-(p:Property)-[:IS_DRIVEN_BY]->(pm:PropertyModel {name:'Situação'})\n" +
      "WHERE 'Deliverable' IN labels(child) AND NOT (p.value IN ['A cancelar', 'Cancelada'])\n" +
      "WITH *\n" +
      "WHERE NOT ('Deliverable' IN labels(child)) OR p IS NOT NULL\n" +
      "RETURN count(child)")
  public Long countTypeByWorkpack(Long workpackId, String type);

  @Query("MATCH (p:Project)<-[:FEATURES]-(pr:Property)-[:IS_DRIVEN_BY]->(:PropertyModel { name: 'Status' })\n" +
      "WHERE ID(p) = $idProject\n" +
      "RETURN pr.value")
  public String getProjectStatusByProjectId(Long idProject);

  @Query("MATCH (d:Deliverable)<-[:FEATURES]-(pr:Property)-[:IS_DRIVEN_BY]->(:PropertyModel { name: 'Situação' })\n" +
      "WHERE ID(d) = $idDeliverable\n" +
      "RETURN pr.value")
  public String getDeliverableStatusByDeliverableId(Long idDeliverable);

  @Query("MATCH p=(wp:Workpack)-[:IS_INSTANCE_BY]->(wpm:WorkpackModel)\n" +
      "WHERE id(wp) = $workpackId\n" +
      "RETURN exists((wpm)<-[:IS_IN*]-(:MilestoneModel))")
  public Boolean requireMilestone(Long workpackId);

  @Query("MATCH (w:Workpack)-[:IS_INSTANCE_BY]->(m:WorkpackModel)\n" +
      "WHERE ID(w) = $idWorkpack\n" +
      "RETURN ID(w) as id, w.idWorkpackModel as idWorkpackModel, w.idParent as idParent, " +
      "   w.name as name, w.fullName as fullName, labels(w) as labels, m.modelName as modelName, " +
      "   m.modelNameInPlural as modelNameInPlural, HEAD([label IN labels(w) WHERE label <> 'Workpack']) as type, " +
      "   m.fontIcon as fontIcon")
  public WorkpackResultDto getWorkpackResultDtoById(Long idWorkpack);
}
