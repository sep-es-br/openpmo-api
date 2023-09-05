package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlanModelRepository extends Neo4jRepository<PlanModel, Long>, CustomRepository {

  @Query("MATCH (p: PlanModel)-[r:IS_ADOPTED_BY]->(o:Office) WHERE id(o)= $id RETURN p,r,o")
  List<PlanModel> findAllInOffice(@Param("id") Long id);

  @Query("MATCH (planModel:PlanModel) " +
         "OPTIONAL MATCH (planModel)-[isSharedWith:IS_SHARED_WITH]->(officeSharedWith:Office)  " +
         "OPTIONAL MATCH (planModel)-[isAdoptedBy:IS_ADOPTED_BY]->(officeAdoptedBy:Office)  " +
         "WITH planModel, isSharedWith, officeSharedWith, isAdoptedBy, officeAdoptedBy  " +
         "WHERE id(officeSharedWith)=$id OR planModel.public = true " +
         "RETURN planModel, isSharedWith, officeSharedWith, isAdoptedBy, officeAdoptedBy"
  )
  List<PlanModel> findAllSharedWithOffice(@Param("id") Long id);

  @Query("MATCH (w:WorkpackModel)-[wp:BELONGS_TO]->(pm:PlanModel) "
         + "WHERE id(pm) = $id AND NOT (w)-[:IS_IN]->(:WorkpackModel) "
         + " RETURN w, wp, pm , ["
         + "  [(wm)<-[i:IS_IN*]-(wm2:WorkpackModel)-[:BELONGS_TO]->(:PlanModel) |[i,wm2] ]"
         + "] ")
  PlanModel findByIdWithChildren(@Param("id") Long id);

  @Query("MATCH (pm:PlanModel)<-[:BELONGS_TO]-(wm:WorkpackModel) " +
         "WHERE id(pm)=$idPlanModel AND NOT (wm)-[:IS_IN]->() " +
         "RETURN wm, [ " +
         "    (wm)<-[ii:IS_IN*]-(wm2:WorkpackModel) | [ii,wm2] " +
         "]")
  List<WorkpackModel> findAllWorkpackModelsByPlanModelId(Long idPlanModel);

  @Query("MATCH (p: PlanModel)-[r:IS_ADOPTED_BY]->(o:Office) " +
          "WHERE id(o)= $id " +
          "WITH p,r,o, " +
          "apoc.text.levenshteinSimilarity(apoc.text.clean(p.name), apoc.text.clean($term)) AS nameScore, " +
          "apoc.text.levenshteinSimilarity(apoc.text.clean(p.fullName), apoc.text.clean($term)) AS fullNameScore " +
          "WITH *, CASE WHEN nameScore > fullNameScore THEN nameScore ELSE fullNameScore END AS score " +
          "WHERE score > $searchCutOffScore OR $term IS NULL OR $term = '' " +
          "RETURN p,r,o " +
          "ORDER BY score DESC")
  List<PlanModel> findAllInOfficeByTerm(
          @Param("id") Long id,
          @Param("term") String term,
          @Param("searchCutOffScore") double searchCutOffScore
  );

  @Query("MATCH (pm:PlanModel)<-[:BELONGS_TO]-(wm:WorkpackModel) " +
         "WHERE id(pm)=$idPlanModel AND NOT exists((wm)-[:IS_IN]->(:WorkpackModel)) " +
         "RETURN max(wm.position)")
  Long findActualPosition(@Param("idPlanModel") Long idPlanModel);

}
