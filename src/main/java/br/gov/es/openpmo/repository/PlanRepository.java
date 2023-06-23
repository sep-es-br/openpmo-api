package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.relations.BelongsTo;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlanRepository extends Neo4jRepository<Plan, Long>, CustomRepository {

  @Query("MATCH (p: Plan)-[r:IS_ADOPTED_BY]->(o:Office) "
         + ", (p)-[sb:IS_STRUCTURED_BY]->(pm:PlanModel) "
         + "WHERE id(o)= $id RETURN p,r,o,sb,pm")
  List<Plan> findAllInOffice(Long id);

  @Query("MATCH (workpack:Workpack)-[belongsTo:BELONGS_TO]->(plan:Plan) " +
         "MATCH (plan)-[adoptedBy:IS_ADOPTED_BY]->(office:Office) " +
         "WHERE id(workpack)=$id AND (belongsTo.linked is null or belongsTo.linked=false) " +
         "RETURN plan, workpack, belongsTo, adoptedBy, office"
  )
  Optional<Plan> findPlanWithNotLinkedBelongsToRelationship(Long id);

  @Query("MATCH (plan:Plan)  " +
         "WHERE id(plan)=$idPlan " +
         "MATCH (workpack:Workpack)-[isIn:IS_IN]->(parent:Workpack) " +
         "OPTIONAL MATCH (workpack)-[belongsTo:BELONGS_TO]->(plan)  " +
         "OPTIONAL MATCH (parent)-[parentBelongsTo:BELONGS_TO]->(plan)  " +
         "WITH plan, workpack, isIn, parent, belongsTo, parentBelongsTo " +
         "WHERE id(workpack)=$idWorkpack  " +
         "RETURN workpack, parent, belongsTo, parentBelongsTo, plan "
  )
  List<BelongsTo> hasLinkWithWorkpack(
    Long idWorkpack,
    Long idPlan
  );

  @Query("MATCH (p: Plan)-[r:IS_ADOPTED_BY]->(o:Office) " +
          ", (p)-[sb:IS_STRUCTURED_BY]->(pm:PlanModel) " +
          "WHERE id(o)= $id " +
          "WITH p,r,o,sb,pm, " +
          "apoc.text.levenshteinSimilarity(apoc.text.clean(p.name), apoc.text.clean($term)) AS nameScore, " +
          "apoc.text.levenshteinSimilarity(apoc.text.clean(p.fullName), apoc.text.clean($term)) AS fullNameScore " +
          "WITH *, CASE WHEN nameScore > fullNameScore THEN nameScore ELSE fullNameScore END AS score " +
          "WHERE score > $searchCutOffScore " +
          "RETURN p,r,o,sb,pm " +
          "ORDER BY score DESC")
  List<Plan> findAllInOfficeByTerm(
          @Param("id") Long id,
          @Param("term") String term,
          @Param("searchCutOffScore") double searchCutOffScore
  );

  @Query(
    "match (p:Plan) " +
    "where id(p)=$idPlan " +
    "match (p)<-[bt:BELONGS_TO{linked:false}]-(w:Workpack{deleted:false}) " +
    "where NOT (w)-[:IS_IN]->(:Workpack) " +
    "return p, bt, w, [ " +
    "    [ (w)<-[isIn:IS_IN*]-(children:Workpack{deleted:false}) | [ isIn, children] ], " +
    "    [ (w)-[iib:IS_INSTANCE_BY]->(wm) | [iib, wm] ], " +
    "    [ (children)-[iib2:IS_INSTANCE_BY]->(wm2) | [iib2, wm2] ], " +
    "    [ (w)<-[wf1:FEATURES]-(wName:Property)-[ii1:IS_DRIVEN_BY]->(pm1:PropertyModel{name: 'name'}) | [wf1, wName, ii1, pm1] ], " +
    "    [ (w)<-[wf2:FEATURES]-(wFullName:Property)-[ii2:IS_DRIVEN_BY]->(pm2:PropertyModel{name: 'fullName'}) | [wf2, wFullName, ii2, pm2] ], " +
    "    [ (children)<-[cf1:FEATURES]-(cName:Property)-[ii3:IS_DRIVEN_BY]->(pm3:PropertyModel{name: 'name'}) | [cf1, cName, ii3, pm3] ], " +
    "    [ (children)<-[cf2:FEATURES]-(cFullName:Property)-[ii4:IS_DRIVEN_BY]->(pm4:PropertyModel{name: 'fullName'}) | [cf2, cFullName, ii4, pm4] ] " +
    "] "
  )
  Optional<Plan> findFirstLevelStructurePlanById(@Param("idPlan") Long idPlan);

}
