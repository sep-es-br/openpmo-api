package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OfficeRepository extends Neo4jRepository<Office, Long>, CustomRepository {

  @Query(
    "MATCH (office:Office) " +
    "WHERE id(office)=$idOffice " +
    "MATCH (office)<-[adoptedBy:IS_ADOPTED_BY]-(plan) " +
    "MATCH (plan)<-[belongsTo:BELONGS_TO]-(parent:Workpack{deleted:false, canceled:false}) " +
    "WHERE NOT EXISTS( (parent)-[:IS_IN]->(:Workpack) ) " +
    " OPTIONAL MATCH (parent)<-[rel1:FEATURES]-(name:Property)-[rel7:IS_DRIVEN_BY]->(pm1:PropertyModel{name:'name'}) " +
    " OPTIONAL MATCH (parent)<-[rel2:FEATURES]-(fullName:Property)-[rel8:IS_DRIVEN_BY]->(pm2:PropertyModel{name:'fullName'}) " +
    " OPTIONAL MATCH (parent)-[rel3:IS_INSTANCE_BY]->(parentWorkpackModel:WorkpackModel) " +
    " OPTIONAL MATCH (parent)<-[rel4:IS_IN*]-(children:Workpack{deleted:false, canceled:false}) " +
    " OPTIONAL MATCH (children)<-[rel5:FEATURES]-(cName:Property)-[rel9:IS_DRIVEN_BY]->(pm3:PropertyModel{name:'name'}) " +
    " OPTIONAL MATCH (children)<-[rel6:FEATURES]-(cFullName:Property)-[rel10:IS_DRIVEN_BY]->(pm4:PropertyModel{name:'fullName'}) " +
    " OPTIONAL MATCH (children)-[rel11:IS_INSTANCE_BY]->(childrenWorkpackModel:WorkpackModel) " +
    "RETURN office, adoptedBy, plan, belongsTo, parent, [" +
    "  [  [rel1, name, rel7, pm1] ], " +
    "  [  [rel2, fullName, rel8, pm2] ], " +
    "  [  [rel3, parentWorkpackModel] ], " +
    "  [  [rel4, children] ], " +
    "  [  [rel5, cName, rel9, pm3] ], " +
    "  [  [rel6, cFullName, rel10, pm4] ], " +
    "  [  [rel11, childrenWorkpackModel] ] " +
    "] "
  )
  Optional<Office> findOfficeTreeViewById(Long idOffice);

  @Query("MATCH (plan:Plan)-[isAdoptedBy:IS_ADOPTED_BY]->(office:Office) " +
         "WHERE id(plan)=$planId " +
         "RETURN office")
  Optional<Office> findOfficeByPlanId(@Param("planId") Long planId);

  @Query("MATCH (plan:Plan)-[isAdoptedBy:IS_ADOPTED_BY]->(office:Office) " +
         "MATCH (workpack:Workpack) " +
         "OPTIONAL MATCH (workpack)-[isIn:IS_IN]->(parent:Workpack)  " +
         "OPTIONAL MATCH (workpack)-[belongsTo:BELONGS_TO]->(plan) " +
         "OPTIONAL MATCH (parent)-[parentBelongsTo:BELONGS_TO]->(plan) " +
         "WITH plan, isAdoptedBy, office, workpack, isIn, parent, belongsTo, parentBelongsTo " +
         "WHERE id(workpack)=$idWorkpack AND id(plan)=$idPlan " +
         "RETURN office")
  Optional<Office> findOfficeByWorkpackId(
    Long idWorkpack,
    Long idPlan
  );

  @Query("MATCH (o:Office)<-[:IS_ADOPTED_BY]-(pm:PlanModel) " +
         "WHERE id(o)=$idOffice " +
         "RETURN pm")
  List<PlanModel> findAllPlanModelsByOfficeId(Long idOffice);

  @Query("MATCH (o:Office) " +
         "WITH o, " +
         "apoc.text.levenshteinSimilarity(apoc.text.clean(o.name), apoc.text.clean($name)) AS nameScore, " +
         "apoc.text.levenshteinSimilarity(apoc.text.clean(o.fullName), apoc.text.clean($name)) AS fullNameScore " +
         "WITH *, CASE WHEN nameScore > fullNameScore THEN nameScore ELSE fullNameScore END AS score " +
         "WHERE score > $searchCutOffScore " +
         "RETURN o " +
         "ORDER BY score DESC")
  List<Office> findAllOfficeByNameOrFullName(
    @Param("name") String name,
    @Param("searchCutOffScore") double searchCutOffScore
  );

}
