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
         " OPTIONAL MATCH (w)<-[ii:IS_IN*]-(v:Workpack) " +
         "RETURN w, [ " +
         "   [[ii,v]] " +
         "]")
  Optional<Workpack> findByIdWithChildren(Long id);

  @Query(
    "MATCH (w1:Workpack{deleted:false}) " +
    "WHERE id(w1)=$id " +
    " OPTIONAL MATCH (w1)-[iib1:IS_INSTANCE_BY]->(wm1:WorkpackModel)<-[ii1:IS_IN*]-(wm2:WorkpackModel)<-[iib2:IS_INSTANCE_BY]-(w2:Workpack{deleted:false})-[ii2:IS_IN*]->(w1) " +
    " OPTIONAL MATCH (w1)<-[f1:FEATURES]-(p1:Property)-[idb1:IS_DRIVEN_BY]->(pm1:PropertyModel) " +
    " OPTIONAL MATCH (w1)<-[ff1:FEATURES]-(us1:UnitSelection)-[v1:VALUES]->(um1:UnitMeasure) " +
    " OPTIONAL MATCH (w1)<-[:IS_IN*]-(w2)<-[f21:FEATURES]-(p21:Property)-[idb21:IS_DRIVEN_BY]->(pm21:PropertyModel) " +
    " OPTIONAL MATCH (w1)<-[:IS_IN*]-(w2)<-[ff2:FEATURES]-(us2:UnitSelection)-[v2:VALUES]->(um2:UnitMeasure) " +
    " OPTIONAL MATCH (w1)<-[:IS_IN*]-(w2)<-[f22:FEATURES]-(p22:Property)-[idb22:IS_DRIVEN_BY]->(pm22:PropertyModel)<-[isb1:IS_SORTED_BY]-(wm3:WorkpackModel)<-[:IS_INSTANCE_BY]-(w2) " +
    " OPTIONAL MATCH (w1)<-[f3:FEATURES]-(d3:Date)-[c3:COMPOSES]->(b3) " +
    " OPTIONAL MATCH (w1)<-[:IS_IN*]-(w2)<-[f4:FEATURES]-(d4:Date)-[c4:COMPOSES]->(b4) " +
    " OPTIONAL MATCH (w1)<-[b1:BELONGS_TO]-(d1:Dashboard)<-[ipo1:IS_PART_OF]-(dm1:DashboardMonth)<-[ia1:IS_AT]-(nodes1) " +
    " OPTIONAL MATCH (w1)<-[:IS_IN*]-(w2)<-[b2:BELONGS_TO]-(d2:Dashboard)<-[ipo2:IS_PART_OF]-(dm2:DashboardMonth)<-[ia2:IS_AT]-(nodes2) " +
    "RETURN w1, [ " +
    " [ [iib1, wm1, ii1, wm2, iib2, w2, ii2]], " +
    " [ [f1, p1, idb1, pm1]], " +
    " [ [ff1,us1,v1,um1]], " +
    " [ [f21, p21, idb21, pm21]], " +
    " [ [ff2,us2,v2,um2]], " +
    " [ [f22, p22, idb22, pm22, isb1, wm3]], " +
    " [ [f3,d3,c3,b3]], " +
    " [ [f4,d4,c4,b4]], " +
    " [ [b1,d1,ipo1,dm1,ia1,nodes1]], " +
    " [ [b2,d2,ipo2,dm2,ia2,nodes2]] " +
    "]"
  )
  Optional<Workpack> findWorkpackWithModelStructureById(Long id);

  @Query(
       " MATCH  " +
       " (w1:Workpack{deleted:false})-[iib1:IS_INSTANCE_BY]->(wm1:WorkpackModel), (w1)<-[ii2:IS_IN*0..1]-(w:Workpack) " +
       "     WHERE id(w1)=$id " +
       " OPTIONAL MATCH (w)-[iib2:IS_INSTANCE_BY]->(wm:WorkpackModel)-[ii1:IS_IN*0..1]->(wm1) " +
       " OPTIONAL MATCH (w)<-[f:FEATURES]-(p:Property)-[idb:IS_DRIVEN_BY]->(pm:PropertyModel) " +
       " OPTIONAL MATCH (p)<-[isp:IS_SNAPSHOT_OF]-(d:Date)-[c:COMPOSES]->(b:Baseline)  where b.active and not b.cancelation " +
       " OPTIONAL MATCH (w)<-[ff:FEATURES]-(us:UnitSelection)-[v:VALUES]->(um:UnitMeasure) " +
       " OPTIONAL MATCH (w)<-[bt:BELONGS_TO]-(ds:Dashboard)<-[ipo:IS_PART_OF]-(dm:DashboardMonth)<-[ia:IS_AT]-(n) " +
       " OPTIONAL MATCH (pm)<-[isb:IS_SORTED_BY]-(wm) " +
       " OPTIONAL MATCH (w1)<-[ii3:IS_IN*2]-(w3:Workpack{deleted:false}) " +
       " RETURN w1,  " +
       " [   " +
       " 	[ [iib1, wm1, ii1, wm, iib2, w, ii2]],   " +
       " 	case when id(w) = id(w1) then  [ [f, p, idb, pm]] end,   " +
       " 	case when id(w) = id(w1) then  [ [ff,us,v,um]   ] end,   " +
       " 	case when id(w) <> id(w1) then [ [f,p,idb,pm]   ] end,   " +
       " 	case when id(w) <> id(w1) then [ [ff,us,v,um]   ] end,   " +
       " 	case when id(w) <> id(w1) then [ [f,p,idb,pm,isb,wm]] end,   " +
       " 	[ [ii3, w3]],   " +
       " 	[ [f,isp,d,c,b]],   " +
 //      " 	[ [f,d,c,b]],   " +
 //      " 	[ [bt,ds,ipo,dm,ia,n]],   " +
       " 	[ [bt,ds,ipo,dm,ia,n]]    " +
       " ] "
  )
  Optional<Workpack> findWorkpackWithModelStructureByIdFirstLevel(Long id);

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
          + " OPTIONAL MATCH (wm)<-[f2:FEATURES]-(pm1:PropertyModel) "
          + " OPTIONAL MATCH (wm)-[isSortedBy:IS_SORTED_BY]->(pm1) "
          + " OPTIONAL MATCH (w)<-[f1:FEATURES]-(p1:Property)-[idb:IS_DRIVEN_BY]->(pm1) "
          + " OPTIONAL MATCH (w)-[sharedWith:IS_SHARED_WITH]->(office:Office) "
          + " OPTIONAL MATCH (w)-[isLinkedTo:IS_LINKED_TO]->(workpackModel:WorkpackModel) "
          + " OPTIONAL MATCH (w)<-[b:BELONGS_TO]-(d:Dashboard)<-[ipo:IS_PART_OF]-(dm:DashboardMonth)<-[ia:IS_AT]-(nodes) "
          + "RETURN w, rf, p, ii, pm, wm, lt, wm2, bt, [ "
          + " [ [f2, pm1] ], "
          + " [ [isSortedBy] ], "
          + " [ [f1, p1] ], "
          + " [ [sharedWith, office]], "
          + " [ [isLinkedTo, workpackModel] ], "
          + " [ [b,d,ipo,dm,ia,nodes] ] "
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

  @Query(" MATCH (wm:WorkpackModel)<-[:IS_INSTANCE_BY|IS_LINKED_TO]-(w:Workpack{deleted:false})-[:IS_IN]->(p:Workpack)-[:BELONGS_TO]->(pl:Plan), " 
  + " 	  (w)-[bt1:BELONGS_TO]->(pl), " 
  + " 	  (w)<-[:FEATURES]-(name:Property)-[:IS_DRIVEN_BY]->(:PropertyModel{name: 'name'}),   " 
  + " 	  (w)<-[:FEATURES]-(fullName:Property)-[:IS_DRIVEN_BY]->(:PropertyModel{name: 'fullName'}) " 
  + "   WHERE id(pl)=$idPlan AND id(wm)=$idWorkpackModel AND id(p)=$idWorkpackParent   " 
  + "    " 
  + "   WITH *,   " 
  + " 	  apoc.text.levenshteinSimilarity(apoc.text.clean(name.value), apoc.text.clean($term)) AS nameScore,   " 
  + " 	  apoc.text.levenshteinSimilarity(apoc.text.clean(fullName.value), apoc.text.clean($term)) AS fullNameScore   " 
  + "   WITH *, CASE WHEN nameScore > fullNameScore THEN nameScore ELSE fullNameScore END AS score " 
  + "  " 
  + "   WHERE ($term IS NULL OR $term = '' OR score > $searchCutOffScore)  " 
  + "   WITH w, score " 
  + "    OPTIONAL MATCH (w)<-[f:FEATURES]-(p:Property)-[db:IS_DRIVEN_BY]->(pm:PropertyModel)   " 
  + "    OPTIONAL MATCH (w)-[iib:IS_INSTANCE_BY]->(m1:WorkpackModel)<-[f2:FEATURES]-(pm2:PropertyModel)   " 
  + "    OPTIONAL MATCH (m1)-[isb:IS_SORTED_BY]->(pms:PropertyModel)   " 
  + "    OPTIONAL MATCH (w)-[ilt:IS_LINKED_TO]->(m2:WorkpackModel)<-[f3:FEATURES]-(pm3:PropertyModel)   " 
  + "    OPTIONAL MATCH (w)-[bt:BELONGS_TO]->(pn:Plan)   " 
  + "    OPTIONAL MATCH (w)<-[ii:IS_IN]->(z:Workpack)   " 
  + "    OPTIONAL MATCH (w)-[isw:IS_SHARED_WITH]->(o:Office)   " 
  + "    OPTIONAL MATCH (w)<-[b:BELONGS_TO]-(d:Dashboard) /* <-[ipo:IS_PART_OF]-(dm:DashboardMonth)<-[ia:IS_AT]-(n) */  " 
  + "  " 
  + "   RETURN w , [   " 
  + " 	  [ [f, p, db, pm] ],   " 
  + " 	  [ [iib, m1] ],   " 
  + " 	  [ [f2, pm2] ],   " 
  + " 	  [ [isb, pms] ],   " 
  + " 	  [ [ilt, m2] ],   " 
  + " 	  [ [f3, pm3] ],   " 
  + " 	  [ [bt,pn] ],   " 
  + " 	  [ [ii, z] ],   " 
  + " 	  [ [isw, o] ],   " 
  + " 	  [ [b,d /* ,ipo,dm,ia,n */] ]   " 
  + "   ]   " 
  + "   ORDER BY score DESC  "
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
          " OPTIONAL MATCH (workpacks)<-[f:FEATURES]-(p:Property)-[d:IS_DRIVEN_BY]->(pm:PropertyModel) " +
          " OPTIONAL MATCH (workpacks)-[iib:IS_INSTANCE_BY]->(m1:WorkpackModel) " +
          " OPTIONAL MATCH (workpacks)-[:IS_INSTANCE_BY]->(:WorkpackModel)<-[f2:FEATURES]-(pm2:PropertyModel) " +
          " OPTIONAL MATCH (workpacks)-[:IS_INSTANCE_BY]->(:WorkpackModel)-[isb:IS_SORTED_BY]->(pms:PropertyModel) " +
          " OPTIONAL MATCH (workpacks)-[ilt:IS_LINKED_TO]->(m2:WorkpackModel) " +
          " OPTIONAL MATCH (workpacks)-[:IS_LINKED_TO]->(:WorkpackModel)<-[f3:FEATURES]-(pm3:PropertyModel) " +
          " OPTIONAL MATCH (workpacks)-[bt:BELONGS_TO]->(pn:Plan) " +
          " OPTIONAL MATCH (workpacks)<-[ii:IS_IN]->(z:Workpack) " +
          " OPTIONAL MATCH (workpacks)-[isw:IS_SHARED_WITH]->(o:Office) " +
          " OPTIONAL MATCH (workpacks)<-[b:BELONGS_TO]-(d:Dashboard)<-[ipo:IS_PART_OF]-(dm:DashboardMonth)<-[ia:IS_AT]-(nodes) " +
          "RETURN workpacks, [ " +
          "    [ [f, p, d, pm] ], " +
          "    [ [iib, m1] ], " +
          "    [ [f2, pm2] ], " +
          "    [ [isb, pms] ], " +
          "    [ [ilt, m2] ], " +
          "    [ [f3, pm3] ], " +
          "    [ [bt,pn] ], " +
          "    [ [ii, z] ], " +
          "    [ [isw, o] ], " +
          "    [ [b,d,ipo,dm,ia,nodes] ] " +
          "]")
  List<Workpack> findAllUsingParentLinked(
          Long idWorkpackModel,
          Long idWorkpackParent,
          Long idPlan,
          String term,
          Double searchCutOffScore
  );

  @Query("MATCH (w:Workpack{deleted:false})-[ro:BELONGS_TO]->(pl:Plan), (w)-[wp:IS_INSTANCE_BY]->(wm:WorkpackModel) "
//         + "WITH w, ro, pl, wp, wm "
         + "WHERE id(w) = $id " +
          " OPTIONAL MATCH (w)<-[f:FEATURES]-(p:Property)-[d:IS_DRIVEN_BY]->(pm:PropertyModel) " +
          " OPTIONAL MATCH (p)-[v1:VALUES]->(o:Organization) " +
          " OPTIONAL MATCH (p)-[v2:VALUES]-(l:Locality) " +
          " OPTIONAL MATCH (p)-[v3:VALUES]-(u:UnitMeasure) " +
          " OPTIONAL MATCH (w)<-[wfg:FEATURES]-(wg:Group) " +
          " OPTIONAL MATCH (wg)-[wgps:GROUPS]->(wgp:Property)-[gpd:IS_DRIVEN_BY]->(gpm:PropertyModel) " +
          " OPTIONAL MATCH (w)<-[wi:IS_IN]-(w2:Workpack{deleted:false}) " + //-[wp2:IS_INSTANCE_BY]->(wm1:WorkpackModel) " +
       //   " OPTIONAL MATCH (w)-[wi2:IS_IN]->(w3:Workpack{deleted:false})-[wp3:IS_INSTANCE_BY]->(wm3:WorkpackModel) " +
          " OPTIONAL MATCH (w)<-[wa:APPLIES_TO]-(ca:CostAccount) " +
          " OPTIONAL MATCH (ca)<-[f1:FEATURES]-(p2:Property)-[d1:IS_DRIVEN_BY]->(pmc:PropertyModel) " +
       //   " OPTIONAL MATCH (wm)<-[wmi:IS_IN*]-(wm2:WorkpackModel) " +
       //   " OPTIONAL MATCH (wm)-[wmi2:IS_IN]->(wm3:WorkpackModel) " +
          " OPTIONAL MATCH (wm)<-[f2:FEATURES]-(pm2:PropertyModel) " +
          " OPTIONAL MATCH (wm)-[featureGroup:FEATURES]->(group:GroupModel) " +
          " OPTIONAL MATCH (group)-[groups:GROUPS]->(groupedProperty:PropertyModel) " +
          "RETURN w, [ [[ro, pl]] , [[wp, wm]], "
         + " [ [f, p, d, pm] ], "
         + " [ [v1, o] ], "
         + " [ [v2, l] ], "
         + " [ [v3, u] ], "
         + " [ [wfg, wg] ], "
         + " [ [wgps, wgp, gpd, gpm] ], "
         + " [ [wi, w2] ], "
       //  + " [ [wi2, w3, wp3, wm3] ],"
         + " [ [wa, ca] ],"
         + " [ [ca, f1, p2, d1, pmc ] ],"
       //  + " [ [wmi,wm2] ],"
       //  + " [ [wmi2,wm3] ],"
         + " [ [f2, pm2] ], "
         + " [ [featureGroup, group] ], "
         + " [ [groups, groupedProperty] ] "
         + "]")
  Optional<Workpack> findByIdWorkpack(@Param("id") Long id);

  @Query("MATCH (w:Workpack{deleted:false})-[rf:BELONGS_TO]->(p:Plan)-[io:IS_ADOPTED_BY]->(o:Office), "
         + "  (p)-[is:IS_STRUCTURED_BY]->(pm:PlanModel), "
         + "  (w)-[ii:IS_INSTANCE_BY]->(wm:WorkpackModel) "
         + " WHERE id(p) = $idPlan AND NOT (w)-[:IS_IN]->(:Workpack) "
         + " OPTIONAL MATCH (p)<-[cp:CAN_ACCESS_PLAN]-(p2:Person) "
         + " OPTIONAL MATCH (w)<-[ca:CAN_ACCESS_WORKPACK]-(p:Person) "
         + " OPTIONAL MATCH (w)<-[wi:IS_IN*]-(w2:Workpack{deleted:false})-[ii_2:IS_INSTANCE_BY]->(wm_2:WorkpackModel) "
         + " OPTIONAL MATCH (w2)<-[ca2:CAN_ACCESS_WORKPACK]-(p2:Person) "
         + " OPTIONAL MATCH (w2)-[rf_2:BELONGS_TO]->(p_2:Plan)-[io_2:IS_ADOPTED_BY]->(o_2:Office) "
         + " OPTIONAL MATCH (w)-[wi2:IS_IN*]->(w3:Workpack{deleted:false})-[ii_3:IS_INSTANCE_BY]->(wm_3:WorkpackModel) "
         + " OPTIONAL MATCH (w3)-[rf_3:BELONGS_TO]->(p_3:Plan)-[io_3:IS_ADOPTED_BY]->(o_3:Office) "
         + " OPTIONAL MATCH (wm)<-[wmi:IS_IN*]-(wm2:WorkpackModel) "
         + " RETURN w, rf, p, io, o, ii, pm, wm, [ "
         + " [ [cp, p2] ],"
         + " [ [ca, p] ],"
         + " [ [wi, w2, ii_2, wm_2] ],"
         + " [ [ca2, p2] ],"
         + " [ [rf_2, p_2, io_2, o_2] ],"
         + " [ [wi2, w3, ii_3, wm_3] ],"
         + " [ [rf_3, p_3, io_3, o_3] ],"
         + " [ [wmi,wm2] ]"
         + " ]")
  Set<Workpack> findAllUsingPlan(@Param("idPlan") Long idPlan);

  @Query(
       " MATCH (plan:Plan)<-[belongsTo:BELONGS_TO]-(w:Workpack{deleted:false,canceled:false})-[instanceBy:IS_INSTANCE_BY]->(model:WorkpackModel) " +
       ", (plan)-[isStructuredBy:IS_STRUCTURED_BY]->(planModel:PlanModel) " +
       " WHERE id(plan)=$idPlan and NOT (w)-[:IS_IN]->(:Workpack)" +
       " OPTIONAL MATCH (w)-[isLinkedTo:IS_LINKED_TO]-(modelLinked:WorkpackModel)" +
       " OPTIONAL MATCH (w)<-[f1:FEATURES]-(p1:Property)-[d1:IS_DRIVEN_BY]->(pm1:PropertyModel)-[f4:FEATURES]->(model) " +
       "     where pm1.name in ['name', 'fullName']" +
       " OPTIONAL MATCH (w)<-[wi:IS_IN*]-(w2:Workpack{deleted:false,canceled:false})-[bt:BELONGS_TO]->(p:Plan)" +
       " OPTIONAL MATCH (w2)<-[f2:FEATURES]-(p2:Property)-[d2:IS_DRIVEN_BY]->(pm2:PropertyModel)-[f5:FEATURES]->(wm2:WorkpackModel)<-[ib2:IS_INSTANCE_BY]-(w2)-[wmi:IS_IN]->()" +
       "     where pm2.name in ['name', 'fullName']" +
       " RETURN w, belongsTo, isStructuredBy, plan, instanceBy, planModel, model, [ " +
       "  [ [isLinkedTo, modelLinked] ], " +
       "  [ [f1, p1, d1, pm1] ], " +
       "  [ [wi,w2,f2, p2, d2, pm2] ], " +
       "  [ [bt, p]], " +
       "  [ [ib2, wm2, f5, pm2] ], " +
       "  [ [f4, pm1] ], " +
       "  [ [wmi,wm2, f5, pm2] ] " +
       "  ]       " 
  )
  Set<Workpack> findAllByPlanWithProperties(@Param("idPlan") Long idPlan);

  @Query(" MATCH (w:Workpack{deleted:false})"
  + " WHERE id(w) = $id"
  + " optional match (w)-[bt:BELONGS_TO]->(pl:Plan)"
  + " optional match (w)<-[ca:CAN_ACCESS_WORKPACK]-(p:Person)"
  + " optional match (w)<-[lt:IS_LINKED_TO]-(wml:WorkpackModel)"
  + " optional match (wml)<-[mii:IS_IN*]-(wmlc:WorkpackModel)"
  + " optional match (w)<-[f1:FEATURES]-(p1:Property)-[d1:IS_DRIVEN_BY]->(pm1:PropertyModel)"
  + " optional match (w)-[wi:IS_IN*]->(w2:Workpack)"
  + " optional match (w2)<-[f21:FEATURES]-(p21:Property)-[d2:IS_DRIVEN_BY]->(pm2:PropertyModel)"
  + " optional match (w2)<-[ca2:CAN_ACCESS_WORKPACK]-(p22:Person)"
  + " optional match (w)<-[wi2:IS_IN]-(w3:Workpack)-[:BELONGS_TO]->(pl)"
  + " optional match (w)<-[f22:FEATURES]-(l:LocalitySelection)-[v1:VALUES]->(l1:Locality)"
  + " optional match (w)<-[f3:FEATURES]-(o:OrganizationSelection)-[v2:VALUES]->(o1:Organization)"
  + " optional match (w)<-[f4:FEATURES]-(u:UnitSelection)-[v3:VALUES]->(u1:UnitMeasure)"
  + " optional match (w2)-[bt2:BELONGS_TO]->(pl2:Plan)"
  + " optional match (w2)<-[f5:FEATURES]-(l2:LocalitySelection)-[v4:VALUES]->(l3:Locality)"
  + " optional match (w2)<-[f6:FEATURES]-(o2:OrganizationSelection)-[v5:VALUES]->(o3:Organization)"
  + " optional match (w2)<-[f7:FEATURES]-(u2:UnitSelection)-[v6:VALUES]->(u3:UnitMeasure)"
  + " optional match (w)<-[wfg:FEATURES]-(wg:Group)"
  + " optional match (wg)-[wgps:GROUPS]->(wgp:Property)-[gpd:IS_DRIVEN_BY]->(gpm:PropertyModel)"
  + " optional match (wgp)-[values:VALUES]->(entity)"
  + " optional match (w2)-[ib2:IS_INSTANCE_BY]->(wm2:WorkpackModel)<-[f8:FEATURES]-(pm5:PropertyModel)"
  + " optional match (w)-[ii:IS_INSTANCE_BY]->(wm:WorkpackModel)"
  + " optional match (wm)<-[f9:FEATURES]-(pm4:PropertyModel)"
  + " optional match (wm)-[featureGroupModel:FEATURES]->(groupModel:GroupModel)-[groupModels:GROUPS]->(groupedPropertiesModel:PropertyModel)"
  + " RETURN w, [ [ii, wm],"
  + " [bt, pl],[ca, p],[lt, wml],"
  + " [mii, wmlc],"
  + " [f1, p1, d1, pm1], "
  + " [wi,w2], "
  + " [f21, p21, d2, pm2], "
  + " [ca2, p22], "
  + " [wi2, w3], "
  + " [f22,l,v1,l1],"
  + " [f3,o,v2,o1], [f4,u,v3,u1], "
  + " [bt2, pl2], [f5, l2, v4, l3], [f6,o2,v5,o3], [f7,u2,v6,u3], "
  + " [wfg, wg], [wgps, wgp, gpd, gpm],"
  + " [values, entity], "
  + " [ib2, wm2, f8, pm5], "
  + " [f9, pm4], [featureGroupModel, groupModel], [groupModels, groupedPropertiesModel] ]")
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
         " OPTIONAL MATCH (model)<-[modelIsIn:IS_IN*]-(childrenModel:WorkpackModel) " +
         "RETURN workpack, belongsTo, plan, structuredBy, planModel, isLinkedTo, model, modelBelongsTo, [" +
         " [ [modelIsIn, childrenModel]] " +
         "]")
  Optional<WorkpackModel> findWorkpackModeLinkedByWorkpackAndPlan(
    Long idWorkpack,
    Long idPlan
  );

  @Query("MATCH (w:Workpack) " +
         "WHERE id(w)=$idWorkpack " +
         " OPTIONAL MATCH (w)<-[f1:FEATURES]-(p:Property) " +
         " OPTIONAL MATCH (w)-[a:IS_INSTANCE_BY]->(m:WorkpackModel) " +
         " OPTIONAL MATCH (w)<-[f2:FEATURES]-(l)-[v1:VALUES]->(l1) " +
         " OPTIONAL MATCH (w)<-[i:IS_IN*]-(v:Workpack{canceled:false}) " +
         " OPTIONAL MATCH (v)<-[h:FEATURES]-(q:Property) " +
         " OPTIONAL MATCH (v)-[ii:IS_INSTANCE_BY]->(n:WorkpackModel) " +
         " OPTIONAL MATCH (v)-[f5:FEATURES]-(l2)-[v2:VALUES]->(l3) " +
         " RETURN w, [ " +
         " [f1,p], " +
         " [a,m], " +
         " [f2,l,v1,l1], " +
         " [i,v], " +
         " [h,q], " +
         " [ii,n], " +
         " [f5,l2,v2,l3] " +
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

  @Query("MATCH (workpack:Workpack)-[:IS_INSTANCE_BY]->(:WorkpackModel)" +
    "<-[:FEATURES]-(:PropertyModel{name:'name'})" +
    "<-[:IS_DRIVEN_BY]-(nameProperty:Property)-[:FEATURES]->(workpack) " +
    "WHERE id(workpack)=$idWorkpack " +
    "RETURN nameProperty.value")
  String findWorkpackName(@Param("idWorkpack") Long idWorkpack);

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

  @Query("MATCH (w:Workpack) WHERE id(w)=$childId  " +
         "MATCH (p:Workpack) WHERE id(p)=$parentId " +
         "CREATE (w)-[:IS_IN]->(p)")
  void createIsInRelationship(
    @Param("childId") Long childId,
    @Param("parentId") Long parentId
  );

  @Query("MATCH (w:Workpack) WHERE id(w)=$workpackId " +
         "MATCH (wm:WorkpackModel) WHERE id(wm)=$workpackModelId " +
         "CREATE (w)-[:IS_INSTANCE_BY]->(wm)")
  void createIsInstanceByRelationship(
    Long workpackId,
    Long workpackModelId
  );

  @Query("MATCH (w:Workpack) WHERE id(w)=$workpackId  " +
         "MATCH (p:Property) WHERE id(p)=$propertyId " +
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
         "WITH model, count(schedule) as c_sc " +
         "RETURN model.scheduleSessionActive = true AND c_sc > 0 "
  )
  Boolean hasScheduleSessionActive(Long idWorkpack);

  @Query(
       "MATCH (workpack:Workpack)-[belongsTo:BELONGS_TO]->(plan:Plan)"
       + " WHERE id(workpack)=$idWorkpack AND id(plan)=$idPlan"
       + " OPTIONAL MATCH (workpack)-[ii:IS_INSTANCE_BY]->(wm:WorkpackModel)"
       + " OPTIONAL MATCH (workpack)<-[ca:CAN_ACCESS_WORKPACK]-(p:Person)"
       + " OPTIONAL MATCH (workpack)-[i:IS_IN*]->(pw:Workpack)"
       + " OPTIONAL MATCH (pw)-[ii2:IS_INSTANCE_BY]->(wm2:WorkpackModel)"
       + " OPTIONAL MATCH (pw)<-[ca2:CAN_ACCESS_WORKPACK]-(p2:Person)"
       + " RETURN workpack, ["
       + " [ii, wm], [ca, p], [i, pw], [ii2, wm2], [ca2, p2] ]"
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

  @Query("MATCH (w:Workpack) WHERE id(w)=$idWorkpack  " +
         "MATCH (p:Plan) WHERE id(p)=$idPlan " +
         "CREATE (w)-[:BELONGS_TO{linked: false}]->(p)")
  void createBelongsToRelationship(@Param("idWorkpack") Long idWorkpack, @Param("idPlan") Long idPlan);

  @Override
  List<Workpack> findAll();

  @Query("match (w:Workpack)<-[:IS_IN]-(x:Workpack)-[z:IS_INSTANCE_BY|IS_LINKED_TO]->(m:WorkpackModel)-[:IS_IN]->(:WorkpackModel)<-[:IS_INSTANCE_BY|IS_LINKED_TO]-(w) " +
    "where id(w)=$idWorkpackActual and id(m)=$idWorkpackModel " +
       " OPTIONAL MATCH (x)<-[a1:IS_IN]-(b1:Workpack)-[c1:IS_INSTANCE_BY|IS_LINKED_TO]->(d1:WorkpackModel)-[e1:IS_IN]->(f1:WorkpackModel) " +
       " OPTIONAL MATCH (x)-[a2:IS_INSTANCE_BY]->(b2:WorkpackModel)<-[c2:FEATURES]-(d2:PropertyModel{name:'name'})<-[e2:IS_DRIVEN_BY]-(f2:Property)-[g2:FEATURES]->(x) " +
       " OPTIONAL MATCH (x)<-[a3:IS_IN]-(b3:Workpack)-[c3:IS_INSTANCE_BY|IS_LINKED_TO]->(d3:WorkpackModel)<-[e3:FEATURES]-(f3:PropertyModel{name:'name'})<-[g3:IS_DRIVEN_BY]-(h3:Property)-[i3:FEATURES]->(b) " +
       " OPTIONAL MATCH (x)-[:IS_INSTANCE_BY]->(:WorkpackModel)-[a4:IS_SORTED_BY]->(b4:PropertyModel)<-[c4:IS_DRIVEN_BY]-(d4:Property)-[e4:FEATURES]->(x) " +
       " OPTIONAL MATCH (x)<-[a5:IS_IN]-(b5:Workpack)-[c5:IS_INSTANCE_BY]->(d5:WorkpackModel)-[e5:IS_SORTED_BY]->(f5:PropertyModel)<-[g5:IS_DRIVEN_BY]-(h5:Property)-[i5:FEATURES]->(b) " +
    "return x,z,m, [ " +
    " [ [a1,b1,c1,d1,e1,f1]], " +
    " [ [a2,b2,c2,d2,e2,f2,g2]], " +
    " [ [a3,b3,c3,d3,e3,f3,g3,h3,i3]], " +
    " [ [a4,b4,c4,d4,e4]], " +
    " [ [a5,b5,c5,d5,e5,f5,g5,h5,i5]] " +
    "]")
  List<Workpack> findWorkpackByWorkpackModelLevel1(Long idWorkpackActual, Long idWorkpackModel);

  @Query("match (w:Workpack)<-[:IS_IN]-(:Workpack)<-[:IS_IN]-(x:Workpack)-[z:IS_INSTANCE_BY|IS_LINKED_TO]->(m:WorkpackModel)-[:IS_IN]->(:WorkpackModel)-[:IS_IN]->(:WorkpackModel)<-[:IS_INSTANCE_BY|IS_LINKED_TO]-(w) " +
    "where id(w)=$idWorkpackActual and id(m)=$idWorkpackModel " +
       " OPTIONAL MATCH (x)<-[a1:IS_IN]-(b1:Workpack)-[c1:IS_INSTANCE_BY|IS_LINKED_TO]->(d1:WorkpackModel)-[e1:IS_IN]->(f1:WorkpackModel) " +
       " OPTIONAL MATCH (x)-[a2:IS_INSTANCE_BY]->(b2:WorkpackModel)<-[c2:FEATURES]-(d2:PropertyModel{name:'name'})<-[e2:IS_DRIVEN_BY]-(f2:Property)-[g2:FEATURES]->(x) " +
       " OPTIONAL MATCH (x)<-[a3:IS_IN]-(b3:Workpack)-[c3:IS_INSTANCE_BY|IS_LINKED_TO]->(d3:WorkpackModel)<-[e3:FEATURES]-(f3:PropertyModel{name:'name'})<-[g3:IS_DRIVEN_BY]-(h3:Property)-[i3:FEATURES]->(b) " +
       " OPTIONAL MATCH (x)-[:IS_INSTANCE_BY]->(:WorkpackModel)-[a4:IS_SORTED_BY]->(b4:PropertyModel)<-[c4:IS_DRIVEN_BY]-(d4:Property)-[e4:FEATURES]->(x) " +
       " OPTIONAL MATCH (x)<-[a5:IS_IN]-(b5:Workpack)-[c5:IS_INSTANCE_BY]->(d5:WorkpackModel)-[e5:IS_SORTED_BY]->(f5:PropertyModel)<-[g5:IS_DRIVEN_BY]-(h5:Property)-[i5:FEATURES]->(b) " +
       "return x,z,m, [ " +
    " [ [a1,b1,c1,d1,e1,f1]], " +
    " [ [a2,b2,c2,d2,e2,f2,g2]], " +
    " [ [a3,b3,c3,d3,e3,f3,g3,h3,i3]], " +
    " [ [a4,b4,c4,d4,e4]], " +
    " [ [a5,b5,c5,d5,e5,f5,g5,h5,i5]] " +
    "]")
  List<Workpack> findWorkpackByWorkpackModelLevel2(Long idWorkpackActual, Long idWorkpackModel);

}
