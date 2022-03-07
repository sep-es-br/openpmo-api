package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.treeview.query.OfficeTreeViewQuery;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface OfficeRepository extends Neo4jRepository<Office, Long>, CustomRepository {

    @Query("match (office:Office) " +
            "where id(office)=$idOffice " +
            "optional match (office)<-[:IS_ADOPTED_BY]-(plan:Plan) " +
            "with office, plan " +
            "optional match (plan)<-[:BELONGS_TO]-(:Workpack) " +
            "with office, plan " +
            "return office, collect(distinct plan) as plans")
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
    Optional<Office> findOfficeByWorkpackId(Long idWorkpack, Long idPlan);

}
