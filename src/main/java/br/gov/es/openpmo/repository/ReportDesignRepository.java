package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.model.reports.ReportDesign;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ReportDesignRepository extends Neo4jRepository<ReportDesign, Long> {

  @Query("MATCH (r:ReportDesign)-[p:IS_DESIGNED_FOR]->(pm:PlanModel) " +
         "WHERE id(pm) = $planModelId " +
         "RETURN r, p, pm")
  List<ReportDesign> findAllByPlanModelId(Long planModelId);

  @Query("MATCH (r:ReportDesign)<-[s]->(t) " +
         "WHERE id(r) = $id " +
         "RETURN r, s, t, [(t)-[p:IS_COMPILATION_OF]->(c:File) | [p, c]]")
  Optional<ReportDesign> findByIdWithRelationships(@Param("id") Long id);

  @Query(
    "MATCH (plan:Plan) " +
    "WHERE id(plan)=$idPlan " +
    "MATCH (plan)-[:IS_STRUCTURED_BY]->(model)<-[:IS_DESIGNED_FOR]-(r:ReportDesign{active:true}) " +
    "RETURN r"
  )
  Set<ReportDesign> findAllReportsActiveByPlan(@Param("idPlan") Long idPlan);
  
  @Query("MATCH (r:ReportDesign)-[p:IS_DESIGNED_FOR]->(pm:PlanModel) " +
         "WHERE id(pm) = $planModelId AND r.active = TRUE " +
         "RETURN r, p, pm")
  List<ReportDesign> findActiveByPlanModelId(@Param("planModelId") Long planModelId);

  @Query(
    "MATCH (plan:Plan)-[:IS_STRUCTURED_BY]->(model:PlanModel) " +
    "WHERE id(plan) = $idPlan AND EXISTS((model)<-[:IS_DESIGNED_FOR]-(:ReportDesign{active:true})) " +
    "RETURN model")
  Optional<PlanModel> hasModelFromReportsActiveAndPlan(@Param("idPlan") Long idPlan);

}
