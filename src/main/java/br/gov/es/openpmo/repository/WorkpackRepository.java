package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.workpack.WorkpackName;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Program;
import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface WorkpackRepository extends Neo4jRepository<Workpack, Long>, CustomRepository {

  @Query("MATCH (w:Workpack) " +
         "WHERE id(w)=$id " +
         "RETURN w, [ " +
         "   [(w)<-[ii:IS_IN*]-(v:Workpack) | [ii,v]] " +
         "]")
  Optional<Workpack> findByIdWithChildren(Long id);

  @Query(
    "MATCH (w1:Workpack{deleted:false}) " +
    "WHERE id(w1)=$id " +
    "RETURN w1, [ " +
    " [(w1)-[iib1:IS_INSTANCE_BY]->(wm1:WorkpackModel)<-[ii1:IS_IN*]-(wm2:WorkpackModel)<-[iib2:IS_INSTANCE_BY]-(w2:Workpack{deleted:false})-[ii2:IS_IN*]->(w1) | [iib1, wm1, ii1, wm2, iib2, w2, ii2]], " +
    " [(w1)<-[f1:FEATURES]-(p1)-[idb2:IS_DRIVEN_BY]->(pm2:PropertyModel) | [f1, p1, idb2, pm2]], " +
    " [(w1)<-[:IS_IN*]-(w2)<-[f2:FEATURES]-(p2:Property)-[idb1:IS_DRIVEN_BY]->(pm1:PropertyModel)<-[isb1:IS_SORTED_BY]-(wm3:WorkpackModel)<-[:IS_INSTANCE_BY]-(w2) | [f2, p2, idb1, pm1, isb1, wm3]] " +
    "]"
  )
  Optional<Workpack> findWorkpackWithModelStructureById(Long id);

  @Query(
    "MATCH (w1:Workpack{deleted:false}) " +
    "WHERE id(w1)=$id " +
    "RETURN w1, [ " +
    " [(w1)-[iib1:IS_INSTANCE_BY]->(wm1:WorkpackModel)<-[ii1:IS_IN*..1]-(wm2:WorkpackModel)<-[iib2:IS_INSTANCE_BY]-(w2:Workpack{deleted:false})-[ii2:IS_IN*..1]->(w1) | [iib1, wm1, ii1, wm2, iib2, w2, ii2]], " +
    " [(w1)<-[f1:FEATURES]-(p1)-[idb2:IS_DRIVEN_BY]->(pm2:PropertyModel) | [f1, p1, idb2, pm2]], " +
    " [(w1)<-[:IS_IN*..1]-(w2)<-[f2:FEATURES]-(p2:Property)-[idb1:IS_DRIVEN_BY]->(pm1:PropertyModel)<-[isb1:IS_SORTED_BY]-(wm3:WorkpackModel)<-[:IS_INSTANCE_BY]-(w2) | [f2, p2, idb1, pm1, isb1, wm3]], " +
    " [(w1)<-[:IS_IN*..1]-(w2)<-[ii3:IS_IN*..1]-(w3:Workpack{deleted:false}) | [ii3, w3]] " +
    "]"
  )
  Optional<Workpack> findWorkpackWithModelStructureByIdFirstLevel(Long id);

  @Query("MATCH (w:Workpack)<-[f:FEATURES]-(p:Property) " +
         "MATCH (p)-[v:VALUES]->(u:UnitMeasure) " +
         "WHERE id(w)=$id  RETURN w, f, p, v, u")
  Optional<Workpack> findWorkpackUnitMeasure(Long id);

  @Query("MATCH (w:Workpack)-[ii:IS_IN*]->(v:Workpack) " +
         "WHERE id(w)=$id " +
         "RETURN v")
  Set<Workpack> findParentsById(Long id);

  @Query("MATCH (w:Workpack)-[rf:BELONGS_TO]->(p:Plan), "
          + "(p)-[is:IS_STRUCTURED_BY]->(pm:PlanModel) "
          + "MATCH (w)<-[:FEATURES]-(name:Property)-[:IS_DRIVEN_BY]->(:PropertyModel{name: 'name'}) "
          + "MATCH (w)<-[:FEATURES]-(fullName:Property)-[:IS_DRIVEN_BY]->(:PropertyModel{name: 'fullName'}) "
          + "OPTIONAL MATCH (w)-[ii:IS_INSTANCE_BY]->(wm:WorkpackModel) "
          + "OPTIONAL MATCH (w)-[lt:IS_LINKED_TO]->(wm2:WorkpackModel)-[bt:BELONGS_TO]->(pm) "
          + "WITH *, "
          + "apoc.text.levenshteinSimilarity(apoc.text.clean(name.value), apoc.text.clean($term)) AS nameScore, "
          + "apoc.text.levenshteinSimilarity(apoc.text.clean(fullName.value), apoc.text.clean($term)) AS fullNameScore "
          + "WITH *, CASE WHEN nameScore > fullNameScore THEN nameScore ELSE fullNameScore END AS score "
          + "WHERE id(p)=$idPlan "
          + "AND (id(pm)=$idPlanModel OR $idPlanModel IS NULL) "
          + "AND (id(wm)=$idWorkPackModel OR id(wm2)=$idWorkPackModel OR $idWorkPackModel IS NULL) "
          + "AND ($term IS NULL OR $term = '' OR score > $searchCutOffScore) "
          + "RETURN w, rf, p, ii, pm, wm, lt, wm2, bt, [ "
          + " [ (wm)<-[f2:FEATURES]-(pm1:PropertyModel) | [f2, pm1] ], "
          + " [ (wm)-[isSortedBy:IS_SORTED_BY]->(pm1) | [isSortedBy] ], "
          + " [ (w)<-[f1:FEATURES]-(p1:Property)-[idb:IS_DRIVEN_BY]->(pm1) | [f1, p1] ], "
          + " [ (w)-[sharedWith:IS_SHARED_WITH]->(office:Office) | [sharedWith, office]], "
          + " [ (w)-[isLinkedTo:IS_LINKED_TO]->(workpackModel:WorkpackModel) | [isLinkedTo, workpackModel] ] "
          + "] "
          + "ORDER BY score DESC"
  )
  List<Workpack> findAll(
          @Param("idPlan") Long idPlan,
          @Param("idPlanModel") Long idPlanModel,
          @Param("idWorkPackModel") Long idWorkPackModel,
          @Param("term") String term,
          @Param("searchCutOffScore") Double searchCutOffScore
  );

  @Query("MATCH (pl:Plan), (wm:WorkpackModel), (p:Workpack) " +
          "WHERE id(pl)=$idPlan AND id(wm)=$idWorkpackModel AND id(p)=$idWorkpackParent " +
          "OPTIONAL MATCH " +
          "    (w:Workpack{deleted:false})-[:IS_IN]->(p), (w)-[:IS_INSTANCE_BY]->(wm), (w)-[bt1:BELONGS_TO]->(pl), " +
          "    (w)<-[:FEATURES]-(name1:Property)-[:IS_DRIVEN_BY]->(:PropertyModel{name: 'name'}), " +
          "    (w)<-[:FEATURES]-(fullName1:Property)-[:IS_DRIVEN_BY]->(:PropertyModel{name: 'fullName'}) " +
          "WITH *, " +
          "    apoc.text.levenshteinSimilarity(apoc.text.clean(name1.value), apoc.text.clean($term)) AS nameScore1, " +
          "    apoc.text.levenshteinSimilarity(apoc.text.clean(fullName1.value), apoc.text.clean($term)) AS fullNameScore1 " +
          "WITH *, CASE WHEN nameScore1 > fullNameScore1 THEN nameScore1 ELSE fullNameScore1 END AS score1 " +
          "OPTIONAL MATCH " +
          "    (p)<-[:IS_IN]-(v:Workpack{deleted:false})-[:IS_LINKED_TO]->(wm), " +
          "    (v)-[bt2:BELONGS_TO]->(pl), " +
          "    (v)<-[:FEATURES]-(name2:Property)-[:IS_DRIVEN_BY]->(:PropertyModel{name: 'name'}), " +
          "    (v)<-[:FEATURES]-(fullName2:Property)-[:IS_DRIVEN_BY]->(:PropertyModel{name: 'fullName'}) " +
          "WITH *," +
          "    apoc.text.levenshteinSimilarity(apoc.text.clean(name2.value), apoc.text.clean($term)) AS nameScore2, " +
          "    apoc.text.levenshteinSimilarity(apoc.text.clean(fullName2.value), apoc.text.clean($term)) AS fullNameScore2 " +
          "WITH *, CASE WHEN nameScore2 > fullNameScore2 THEN nameScore2 ELSE fullNameScore2 END AS score2 " +
          "WHERE ( (bt1.linked=null OR bt1.linked=false) AND ($term IS NULL OR $term = '' OR score1 > $searchCutOffScore) ) OR " +
          "      ( bt2.linked=true AND ($term IS NULL OR $term = '' OR score2 > $searchCutOffScore) ) " +
          "WITH *, CASE WHEN coalesce(score1, 0) > coalesce(score2, 0) THEN score1 ELSE score2 END as score " +
          "WITH score, collect(w)+collect(v) AS workpackList " +
          "UNWIND workpackList AS workpacks " +
          "RETURN workpacks, [ " +
          "    [ (workpacks)<-[f:FEATURES]-(p:Property)-[d:IS_DRIVEN_BY]->(pm:PropertyModel) | [f, p, d, pm] ], " +
          "    [ (workpacks)-[iib:IS_INSTANCE_BY]->(m1:WorkpackModel) | [iib, m1] ], " +
          "    [ (m1)<-[f2:FEATURES]-(pm2:PropertyModel) | [f2, pm2] ], " +
          "    [ (workpacks)-[ilt:IS_LINKED_TO]->(m2:WorkpackModel) | [ilt, m2] ], " +
          "    [ (m2)<-[f3:FEATURES]-(pm3:PropertyModel) | [f3, pm3] ], " +
          "    [ (workpacks)-[bt:BELONGS_TO]->(pn:Plan) | [bt,pn] ], " +
          "    [ (workpacks)<-[ii:IS_IN]->(z:Workpack) | [ii, z] ], " +
          "    [ (workpacks)-[isw:IS_SHARED_WITH]->(o:Office) | [isw, o] ] " +
          "] " +
          "ORDER BY score DESC"
  )
  List<Workpack> findAllUsingParent(
          Long idWorkpackModel,
          Long idWorkpackParent,
          Long idPlan,
          String term,
          Double searchCutOffScore
  );

  @Query("MATCH (w:Workpack{deleted:false})-[:IS_IN]->(p:Workpack{deleted:false}) " +
          "MATCH (w)-[:IS_INSTANCE_BY]->(wm:WorkpackModel) " +
          "MATCH (w)-[:IS_IN*]->(:Workpack{deleted:false})-[:BELONGS_TO{linked: true}]->(pl:Plan) " +
          "MATCH (w)<-[:FEATURES]-(name:Property)-[:IS_DRIVEN_BY]->(:PropertyModel{name: 'name'}) " +
          "MATCH (w)<-[:FEATURES]-(fullName:Property)-[:IS_DRIVEN_BY]->(:PropertyModel{name: 'fullName'}) " +
          "WITH *, " +
          "apoc.text.levenshteinSimilarity(apoc.text.clean(name.value), apoc.text.clean($term)) AS nameScore, " +
          "apoc.text.levenshteinSimilarity(apoc.text.clean(fullName.value), apoc.text.clean($term)) AS fullNameScore " +
          "WITH *, CASE WHEN nameScore > fullNameScore THEN nameScore ELSE fullNameScore END AS score " +
          "WHERE id(p)=$idWorkpackParent AND id(wm)=$idWorkpackModel AND id(pl)=$idPlan " +
          "AND ($term IS NULL OR $term = '' OR score > $searchCutOffScore) " +
          "WITH collect(w) AS workpackList " +
          "UNWIND workpackList AS workpacks " +
          "RETURN workpacks, [ " +
          "    [ (workpacks)<-[f:FEATURES]-(p:Property)-[d:IS_DRIVEN_BY]->(pm:PropertyModel) | [f, p, d, pm] ], " +
          "    [ (workpacks)-[iib:IS_INSTANCE_BY]->(m1:WorkpackModel) | [iib, m1] ], " +
          "    [ (m1)<-[f2:FEATURES]-(pm2:PropertyModel) | [f2, pm2] ], " +
          "    [ (workpacks)-[ilt:IS_LINKED_TO]->(m2:WorkpackModel) | [ilt, m2] ], " +
          "    [ (m2)<-[f3:FEATURES]-(pm3:PropertyModel) | [f3, pm3] ], " +
          "    [ (workpacks)-[bt:BELONGS_TO]->(pn:Plan) | [bt,pn] ], " +
          "    [ (workpacks)<-[ii:IS_IN]->(z:Workpack) | [ii, z] ], " +
          "    [ (workpacks)-[isw:IS_SHARED_WITH]->(o:Office) | [isw, o] ] " +
          "]")
  List<Workpack> findAllUsingParentLinked(
          Long idWorkpackModel,
          Long idWorkpackParent,
          Long idPlan,
          String term,
          Double searchCutOffScore
  );

  @Query("OPTIONAL MATCH (w:Workpack{deleted:false})-[ro:BELONGS_TO]->(pl:Plan), (w)-[wp:IS_INSTANCE_BY]->" +
         "(wm:WorkpackModel) "
         + "WITH w, ro, pl, wp, wm "
         + "WHERE id(w) = $id "
         + "RETURN w, ro, pl, wp, wm, [ "
         + " [(w)<-[f:FEATURES]-(p:Property)-[d:IS_DRIVEN_BY]->(pm:PropertyModel) | [f, p, d, pm] ], "
         + " [(p)-[v1:VALUES]->(o:Organization) | [v1, o] ], "
         + " [(p)-[v2:VALUES]-(l:Locality) | [v2, l] ], "
         + " [(p)-[v3:VALUES]-(u:UnitMeasure) | [v3, u] ], "
         + " [(w)<-[wfg:FEATURES]-(wg:Group) | [wfg, wg] ], "
         + " [(wg)-[wgps:GROUPS]->(wgp:Property)-[gpd:IS_DRIVEN_BY]->(gpm:PropertyModel) | [wgps, wgp, gpd, gpm] ], "
         + " [(w)<-[wi:IS_IN]-(w2:Workpack{deleted:false}) | [wi, w2] ], "
         + " [(w)-[wi2:IS_IN]->(w3:Workpack{deleted:false})-[wp3:IS_INSTANCE_BY]->(wm3:WorkpackModel) | [wi2, w3, " +
         "wp3, wm3] ],"
         + " [(w)<-[wa:APPLIES_TO]-(ca:CostAccount) | [wa, ca] ],"
         + " [(ca)<-[f1:FEATURES]-(p2:Property)-[d1:IS_DRIVEN_BY]->(pmc:PropertyModel) | [ca, f1, p2, d1, pmc ] ],"
         + " [(wm)<-[wmi:IS_IN]-(wm2:WorkpackModel) | [wmi,wm2] ],"
         + " [(wm)-[wmi2:IS_IN]->(wm3:WorkpackModel) | [wmi2,wm3] ],"
         + " [(wm)<-[f2:FEATURES]-(pm2:PropertyModel) | [f2, pm2] ], "
         + " [(wm)-[featureGroup:FEATURES]->(group:GroupModel) | [featureGroup, group] ], "
         + " [(group)-[groups:GROUPS]->(groupedProperty:PropertyModel) | [groups, groupedProperty] ] "
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
         " [(w)<-[wi:IS_IN*]-(w2:Workpack{deleted:false,canceled:false})<-[f2:FEATURES]-(p2:Property)-[d2:IS_DRIVEN_BY]->(pm2:PropertyModel) | [wi,w2,f2, p2, d2, pm2] ], " +
         " [(w2)-[bt:BELONGS_TO]->(p:Plan) | [bt, p]], " +
         " [(w2)-[ib2:IS_INSTANCE_BY]->(wm2:WorkpackModel)<-[f5:FEATURES]-(pm5:PropertyModel) | [ib2, wm2, f5, pm5] ], " +
         " [(model)<-[f4:FEATURES]-(pm4:PropertyModel) | [f4, pm4] ], " +
         " [(model)<-[wmi:IS_IN*]-(wm2:WorkpackModel)<-[f6:FEATURES]-(pm6:PropertyModel) | [wmi,wm2, f6, pm6] ] " +
         " ]"
  )
  Set<Workpack> findAllByPlanWithProperties(@Param("idPlan") Long idPlan);

  @Query("MATCH (w:Workpack{deleted:false})-[ii:IS_INSTANCE_BY]->(wm:WorkpackModel) "
    + " WHERE id(w) = $id "
    + " RETURN w, ii, wm, [ "
    + " [(w)-[bt:BELONGS_TO]->(pl:Plan) | [bt, pl]], "
    + " [(w)<-[lt:IS_LINKED_TO]-(wml:WorkpackModel) | [lt, wml] ], "
    + " [(wml)<-[mii:IS_IN*]-(wmlc:WorkpackModel) | [mii, wmlc] ], "
    + " [(w)<-[f1:FEATURES]-(p1:Property)-[d1:IS_DRIVEN_BY]->(pm1:PropertyModel) | [f1, p1, d1, pm1] ], "
    + " [(w)-[wi:IS_IN*]->(w2:Workpack)<-[f2:FEATURES]-(p2:Property)-[d2:IS_DRIVEN_BY]->(pm2:PropertyModel) | " +
    "[wi,w2,f2, " +
    "p2, d2, pm2] ], "
    + " [(w)<-[wi2:IS_IN]-(w3:Workpack)-[:BELONGS_TO]->(pl) | [wi2, w3] ], "
    + " [(w)<-[f2:FEATURES]-(l:LocalitySelection)-[v1:VALUES]->(l1:Locality) | [f2,l,v1,l1]], "
    + " [(w)<-[f3:FEATURES]-(o:OrganizationSelection)-[v2:VALUES]->(o1:Organization) | [f3,o,v2,o1]], "
    + " [(w)<-[f4:FEATURES]-(u:UnitSelection)-[v3:VALUES]->(u1:UnitMeasure) | [f4,u,v3,u1]], "
    + " [(w2)-[bt2:BELONGS_TO]->(pl2:Plan) | [bt2, pl2] ], "
    + " [(w2)<-[f5:FEATURES]-(l2:LocalitySelection)-[v4:VALUES]->(l2:Locality) | [f5, l2, v4, l2]], "
    + " [(w2)<-[f6:FEATURES]-(o2:OrganizationSelection)-[v5:VALUES]->(o2:Organization) | [f6,o2,v5,o2]], "
    + " [(w2)<-[f7:FEATURES]-(u2:UnitSelection)-[v6:VALUES]->(u2:UnitMeasure) | [f7,u2,v6,u2]], "
    + " [(w)<-[wfg:FEATURES]-(wg:Group) | [wfg, wg] ], "
    + " [(wg)-[wgps:GROUPS]->(wgp:Property)-[gpd:IS_DRIVEN_BY]->(gpm:PropertyModel) | [wgps, wgp, gpd, gpm] ], "
    + " [(wgp)-[values:VALUES]->(entity) | [values, entity]], "
    + " [(w2)-[ib2:IS_INSTANCE_BY]->(wm2:WorkpackModel)<-[f8:FEATURES]-(pm5:PropertyModel) | [ib2, wm2, f8, pm5]" +
    " ], "
    + " [(wm)<-[f9:FEATURES]-(pm4:PropertyModel) | [f9, pm4] ], "
    + " [(wm)-[featureGroupModel:FEATURES]->(groupModel:GroupModel) | [featureGroupModel, groupModel] ], "
    + " [(groupModel)-[groupModels:GROUPS]->(groupedPropertiesModel:PropertyModel) | [groupModels, " +
    "groupedPropertiesModel]" +
    " ] "
    + " ]"
  )
  Optional<Workpack> findByIdWithParent(@Param("id") Long id);

  @Query("MATCH (w:Workpack{deleted:false})<-[:IS_IN*]-(child:Workpack{deleted:false})<-[canAccess:CAN_ACCESS_WORKPACK]-(p:Person) " +
         " WHERE id(w) = $idWorkpack " +
         " AND id(p) = $idPerson " +
         " AND canAccess.idPlan = $idPlan" +
         " RETURN count(canAccess)")
  Long countCanAccessWorkpack(
    @Param("idWorkpack") Long idWorkpack,
    @Param("idPerson") Long idPerson,
    @Param("idPlan") Long idPlan
  );

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
    @Param("idPerson") Long idPerson
  );

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
    Long idPlan
  );

  @Query("MATCH (w:Workpack) " +
         "WHERE id(w)=$idWorkpack " +
         "RETURN w, [ " +
         " [(w)<-[f1:FEATURES]-(p:Property) | [f1,p]], " +
         " [(w)-[a:IS_INSTANCE_BY]->(m:WorkpackModel) | [a,m]], " +
         " [(w)<-[i:IS_IN*]-(v:Workpack{canceled:false})<-[h:FEATURES]-(q:Property) | [i,v,h,q]], " +
         " [(v)-[ii:IS_INSTANCE_BY]->(n:WorkpackModel) | [ii,n]], " +
         " [(w)<-[f2:FEATURES]-(l:LocalitySelection)-[v1:VALUES]->(l1:Locality) | [f2,l,v1,l1]], " +
         " [(w)<-[f3:FEATURES]-(o:OrganizationSelection)-[v2:VALUES]->(o1:Organization) | [f3,o,v2,o1]], " +
         " [(w)<-[f4:FEATURES]-(u:UnitSelection)-[v3:VALUES]->(u1:UnitMeasure) | [f4,u,v3,u1]], " +
         " [(v)-[f5:FEATURES]-(l:LocalitySelection)-[v1:VALUES]->(l1:Locality) | [f5,l,v1,l1]], " +
         " [(v)-[f6:FEATURES]-(o:OrganizationSelection)-[v2:VALUES]->(o1:Organization) | [f6,o,v2,o1]], " +
         " [(v)-[f7:FEATURES]-(u:UnitSelection)-[v3:VALUES]->(u1:UnitMeasure) | [f7,u,v3,u1]] " +
         "]")
  Optional<Workpack> findWithPropertiesAndModelAndChildrenById(Long idWorkpack);

  @Query("MATCH (workpack:Workpack)-[instanceBy:IS_INSTANCE_BY]->(model:WorkpackModel), " +
         "(model)<-[:FEATURES]-(nameModel:PropertyModel{name:'name'})<-[:IS_DRIVEN_BY]-" +
         "(nameProperty:Property)-[:FEATURES]->(workpack), " +
         "(model)<-[:FEATURES]-(fullNameModel:PropertyModel{name:'fullName'})<-[:IS_DRIVEN_BY]-" +
         "(fullNameProperty:Property)-[:FEATURES]->(workpack) " +
         "WHERE id(workpack)=$idWorkpack " +
         "RETURN id(model) AS idWorkpackModel, " +
         "id(workpack) AS idWorkpack, " +
         "nameProperty.value AS name, " +
         "fullNameProperty.value AS fullName")
  Optional<WorkpackName> findWorkpackNameAndFullname(@Param("idWorkpack") Long idWorkpack);

  @Query("MATCH (workpack:Workpack) " +
         "OPTIONAL MATCH (workpack)<-[snapshotOf:IS_SNAPSHOT_OF]-(:Workpack) " +
         "WITH workpack, snapshotOf " +
         "WHERE id(workpack)=$idWorkpack " +
         "RETURN count(snapshotOf)>0 ")
  boolean hasSnapshot(Long idWorkpack);

  @Query("MATCH (workpack:Workpack)-[:IS_BASELINED_BY]->(baseline:Baseline{active:true,cancelation:false}) " +
         "WHERE id(workpack)=$idWorkpack " +
         "RETURN count(baseline)>0 ")
  boolean hasActiveBaseline(Long idWorkpack);

  @Query("MATCH (w:Workpack)<-[:IS_IN*]-(:Project{deleted:false,canceled:false})-[:IS_BASELINED_BY]->" +
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
    Long idParent
  );

  @Query("MATCH (w:Workpack)-[i:IS_IN]->(p:Workpack) " +
         "WHERE id(w)=$workpackId AND id(p)=$parentId " +
         "DETACH DELETE i")
  void deleteIsInRelationshipByWorkpackIdAndParentId(
    Long workpackId,
    Long parentId
  );

  @Query("MATCH (w:Workpack), (p:Workpack) " +
         "WHERE id(w)=$childId AND id(p)=$parentId " +
         "CREATE (w)-[:IS_IN]->(p)")
  void createIsInRelationship(
    @Param("childId") Long childId,
    @Param("parentId") Long parentId
  );

  @Query("MATCH (w:Workpack), (wm:WorkpackModel) " +
         "WHERE id(w)=$workpackId AND id(wm)=$workpackModelId " +
         "CREATE (w)-[:IS_INSTANCE_BY]->(wm)")
  void createIsInstanceByRelationship(
    Long workpackId,
    Long workpackModelId
  );

  @Query("MATCH (w:Workpack), (p:Property) " +
         "WHERE id(w)=$workpackId AND id(p)=$propertyId " +
         "CREATE (w)<-[:FEATURES]-(p)")
  void createFeaturesRelationship(
    Long workpackId,
    Long propertyId
  );

  @Query("MATCH (workpack:Workpack) " +
         "WHERE id(workpack)=$idWorkpack " +
         "MATCH (workpack)<-[:FEATURES]-(:UnitSelection)-[:VALUES]->(measure:UnitMeasure) " +
         "RETURN measure.name")
  Optional<String> findUnitMeasureNameOfDeliverableWorkpack(Long idWorkpack);

  @Query("MATCH (deliverable:Deliverable{completed:false, deleted:false}) " +
         "WHERE deliverable.category <> 'SNAPSHOT' " +
         "RETURN deliverable")
  Set<Deliverable> findAllDeliverables();

  @Query("MATCH (project:Project)-[:BELONGS_TO{linked:false}]->(plan:Plan) " +
         "WHERE id(project)=$idProject " +
         "MATCH (project)<-[:IS_IN*]-(deliverable:Deliverable{deleted:false})-[:BELONGS_TO{linked:false}]->(plan) " +
         "WHERE deliverable.completed=false " +
         "RETURN count(DISTINCT deliverable) > 0")
  boolean hasDeliverableToComplete(Long idProject);

  @Query("MATCH (project:Project{completed:false, deleted:false}) " +
         "WHERE project.category <> 'SNAPSHOT' " +
         "RETURN project")
  Collection<Project> findAllProjects();

  @Query("MATCH (program:Program{completed:false, deleted:false}) " +
         "WHERE program.category <> 'SNAPSHOT' " +
         "RETURN program")
  Collection<Program> findAllPrograms();

  @Query("MATCH (workpack:Workpack)<-[:FEATURES]-(:Schedule) " +
         "WHERE id(workpack)=$idWorkpack " +
         "RETURN count(DISTINCT workpack) > 0")
  boolean hasScheduleRelated(Long idWorkpack);

  @Query("MATCH (program:Program)-[:BELONGS_TO{linked:false}]->(plan:Plan) " +
         "WHERE id(program)=$idProject " +
         "MATCH (program)<-[:IS_IN*]-(project:Project{deleted:false})-[:BELONGS_TO{linked:false}]->(plan) " +
         "WHERE project.completed=false " +
         "RETURN count(DISTINCT project) > 0")
  boolean hasRemainProjectsToComplete(Long idProgram);

  @Query("MATCH (workpack:Workpack)-[:BELONGS_TO{linked:false}]->(plan:Plan) " +
         "WHERE id(workpack)=$idWorkpack " +
         "OPTIONAL MATCH (workpack)-[:IS_IN*0..]->(project:Project)-[:BELONGS_TO{linked:false}]->(plan) " +
         "WITH workpack, project, plan " +
         "RETURN project")
  Optional<Project> findProjectInParentsOf(Long idWorkpack);

  @Query("MATCH (d:Deliverable)-[:IS_IN*]->(p:Project) " +
         "WHERE id(d)=$idDeliverable " +
         "RETURN p")
  Optional<Project> findProject(Long idDeliverable);

  @Query("MATCH (d:Deliverable)-[:IS_IN*]->(p:Program) " +
         "WHERE id(d)=$idDeliverable " +
         "RETURN p")
  Optional<Program> findProgram(Long idDeliverable);

  @Query("MATCH (w:Workpack)<-[:IS_IN*]-(d:Deliverable{deleted:false}) " +
         "WHERE id(w)=$workpackId " +
         "RETURN id(d)")
  Set<Long> getDeliverablesId(Long workpackId);

  @Query("MATCH (p:Project) WHERE id(p)=$workpackId RETURN count(p)>0")
  boolean isProject(Long workpackId);

  @Query("MATCH (d:Deliverable) WHERE id(d)=$workpackId RETURN count(d)>0")
  boolean isDeliverable(Long workpackId);

  @Query("MATCH (w:Workpack)-[:BELONGS_TO]->(p:Plan) " +
         "WHERE id(p)=$id " +
         "RETURN count(w)>0")
  boolean existsByPlanId(@Param("id") Long id);

  @Query("MATCH (w:Workpack)<-[:IS_IN*]->(v:Workpack) " +
         "WHERE id(w)=$workpackId " +
         "WITH [ id(w) ] + collect( id(v) ) AS list " +
         "UNWIND list AS l " +
         "RETURN l")
  Set<Long> findAllInHierarchy(@Param("workpackId") Long workpackId);

  @Query("MATCH (w:Workpack) WHERE id(w)=$id RETURN w.canceled=true AND w.deleted=false")
  boolean isCanceled(Long id);

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
         "RETURN model.scheduleSessionActive = true AND count(schedule) > 0 "
  )
  Boolean hasScheduleSessionActive(Long idWorkpack);

  @Query(
    "MATCH (workpack:Workpack)-[:IS_INSTANCE_BY]->(workpackModel:WorkpackModel) " +
    "WHERE id(workpackModel)=$idWorkpackModel " +
    "RETURN workpack"
  )
  Set<Workpack> findWorkpacksByWorkpackModel(@Param("idWorkpackModel") Long idWorkpackModel);

  @Query(
    "MATCH (workpack:Workpack)-[belongsTo:BELONGS_TO]->(plan:Plan) " +
    "WHERE id(workpack)=$idWorkpack " +
    "AND id(plan)=$idPlan " +
    "RETURN workpack, [" +
    "  [ (workpack)-[ii:IS_INSTANCE_BY]->(wm:WorkpackModel) | [ii, wm] ] " +
    "]"
  )
  Optional<Workpack> findByIdWorkpackAndIdPlan(
    Long idWorkpack,
    Long idPlan
  );

  @Query(
    "MATCH (workpack:Workpack) " +
    "WHERE id(workpack)=$idWorkpack " +
    "MATCH (workpack)-[:IS_IN*1..]->(parent:Workpack)-[:BELONGS_TO]->(plan:Plan) " +
    "WHERE id(plan)=$idPlan " +
    "RETURN parent"
  )
  Set<Workpack> findWorkpackParentsHierarchy(
    @Param("idPlan") Long idPlan,
    @Param("idWorkpack") Long idWorkpack
  );

  @Query(
    "MATCH (workpack:Workpack) " +
    "OPTIONAL MATCH (children:Workpack)-[:IS_IN]->(workpack) " +
    "WITH * " +
    "WHERE id(workpack)=$idWorkpack " +
    "RETURN count(children) > 0"
  )
  boolean hasChildren(@Param("idWorkpack") Long idWorkpack);

  @Query("MATCH (w:Workpack), (p:Plan) " +
          "WHERE id(w)=$idWorkpack AND id(p)=$idPlan " +
          "CREATE (w)-[:BELONGS_TO{linked: false}]->(p)")
  void createBelongsToRelationship(@Param("idWorkpack") Long idWorkpack, @Param("idPlan") Long idPlan);

  @Override
  List<Workpack> findAll();
}
