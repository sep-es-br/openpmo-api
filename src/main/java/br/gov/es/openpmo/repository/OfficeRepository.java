package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.treeview.query.OfficeTreeViewQuery;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OfficeRepository extends Neo4jRepository<Office, Long>, CustomRepository {

    @Query("MATCH (office:Office) " +
            "WHERE id(office)=$idOffice " +
            "OPTIONAL MATCH (office)<-[:IS_ADOPTED_BY]-(plan:Plan) " +
            "WITH office, plan " +
            "OPTIONAL MATCH (plan)<-[:BELONGS_TO]-(:Workpack) " +
            "WITH office, plan " +
            "RETURN office, collect(DISTINCT plan) AS plans")
    Optional<OfficeTreeViewQuery> findOfficeTreeViewById(Long idOffice);

    @Query("MATCH (plan:Plan)-[isAdoptedBy:IS_ADOPTED_BY]->(office:Office) " +
            "WHERE id(plan)=$planId " +
            "RETURN office")
    Optional<Office> findOfficeByPlanId(Long planId);

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
    List<Office> findAllOfficeByNameOrFullName(@Param("name") String name,
                                               @Param("searchCutOffScore") double searchCutOffScore);

}
