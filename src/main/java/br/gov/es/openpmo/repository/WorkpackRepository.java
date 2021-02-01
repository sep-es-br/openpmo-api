package br.gov.es.openpmo.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.openpmo.model.Workpack;

public interface WorkpackRepository extends Neo4jRepository<Workpack, Long> {

    @Query("    MATCH	(w:Workpack)-[rf:IS_ROOT_OF]->(p:Plan) "
            + "		, (p)-[is:IS_STRUCTURED_BY]->(pm:PlanModel) "
            + "		,   (w)-[ii:IS_INSTANCE_BY]->(wm:WorkpackModel) "
            + " WHERE ID(p) = $idPlan "
            + " AND	(ID(pm) = $idPlanModel OR $idPlanModel is null) "
            + " AND	(ID(wm) = $idWorkPackModel OR $idWorkPackModel is null) "
            + " RETURN  w, rf, p, ii, pm, wm, [ "
               + "  [(w)<-[f:FEATURES]-(p:Property)-[d:IS_DRIVEN_BY]->(pm:PropertyModel) | [f, p, d, pm] ], "
               + "  [(w)<-[wi:IS_IN]-(w2:Workpack) | [wi, w2] ],"
               + "  [(w)-[wi2:IS_IN]->(w3:Workpack) | [wi2, w3] ],"
               + "  [(w)<-[wa:APPLIES_TO]-(ca:CostAccount) | [wa, ca] ],"
               + "  [(ca)<-[f1:FEATURES]-(p2:Property)-[d1:IS_DRIVEN_BY]->(pmc:PropertyModel) | [ca, f1, p2, d1, pmc ] ],"
               + "  [(wm)<-[wmi:IS_IN]-(wm2:WorkpackModel) | [wmi,wm2] ],"
               + "  [(wm)-[wmi2:IS_IN]->(wm3:WorkpackModel) | [wmi2,wm3] ],"
               + "  [(wm)<-[f2:FEATURES]-(pm2:PropertyModel) | [f2, pm2] ] "
               + " ]")
    List<Workpack> findAll(@Param("idPlan") Long idPlan, @Param("idPlanModel") Long idPlanModel,
            @Param("idWorkPackModel") Long idWorkPackModel);

    @Query("    MATCH	(w:Workpack)-[rf:IS_ROOT_OF]->(p:Plan) "
               + "		, (p)-[is:IS_STRUCTURED_BY]->(pm:PlanModel) "
               + "		, (w)-[ii:IS_INSTANCE_BY]->(wm:WorkpackModel) "
               + "      , (w)-[wc:IS_IN]->(wp:Workpack) "
               + " WHERE ID(p) = $idPlan "
               + " AND	(ID(pm) = $idPlanModel OR $idPlanModel is null) "
               + " AND	(ID(wm) = $idWorkPackModel OR $idWorkPackModel is null) "
               + " AND ID(wp) = $idWorkPackParent "
               + " RETURN  w, rf, p, ii, pm, wm, [ "
               + "  [(w)<-[f:FEATURES]-(p:Property)-[d:IS_DRIVEN_BY]->(pm:PropertyModel) | [f, p, d, pm] ], "
               + "  [(w)<-[wi:IS_IN]-(w2:Workpack) | [wi, w2] ],"
               + "  [(w)-[wi2:IS_IN]->(w3:Workpack) | [wi2, w3] ],"
               + "  [(w)<-[wa:APPLIES_TO]-(ca:CostAccount) | [wa, ca] ],"
               + "  [(ca)<-[f1:FEATURES]-(p2:Property)-[d1:IS_DRIVEN_BY]->(pmc:PropertyModel) | [ca, f1, p2, d1, pmc ] ],"
               + "  [(wm)<-[wmi:IS_IN]-(wm2:WorkpackModel) | [wmi,wm2] ],"
               + "  [(wm)-[wmi2:IS_IN]->(wm3:WorkpackModel) | [wmi2,wm3] ],"
               + "  [(wm)<-[f2:FEATURES]-(pm2:PropertyModel) | [f2, pm2] ] "
               + " ]")
    List<Workpack> findAll(@Param("idPlan") Long idPlan, @Param("idPlanModel") Long idPlanModel,
                           @Param("idWorkPackModel") Long idWorkPackModel, @Param("idWorkPackParent") Long idWorkPackParent);

    @Query("MATCH (w:Workpack)-[ro:IS_ROOT_OF]->(pl:Plan), (w)-[wp:IS_INSTANCE_BY]->(wm:WorkpackModel) "
            + "WHERE ID(w) = $id " + "RETURN w, ro, pl, wp, wm, [ "
            + "  [(w)<-[f:FEATURES]-(p:Property)-[d:IS_DRIVEN_BY]->(pm:PropertyModel) | [f, p, d, pm] ], "
            + "  [(p)-[v1:VALUES]->(o:Organization) | [v1, o] ], "
            + "  [(p)-[v2:VALUES]-(l:Locality) | [v2, l] ], "
            + "  [(p)-[v3:VALUES]-(u:UnitMeasure) | [v3, u] ], "
            + "  [(w)<-[wi:IS_IN]-(w2:Workpack) | [wi, w2] ]," + "  [(w)-[wi2:IS_IN]->(w3:Workpack)-[wp3:IS_INSTANCE_BY]->(wm3:WorkpackModel) | [wi2, w3, wp3, wm3] ],"
            + "  [(w)<-[wa:APPLIES_TO]-(ca:CostAccount) | [wa, ca] ],"
            + "  [(ca)<-[f1:FEATURES]-(p2:Property)-[d1:IS_DRIVEN_BY]->(pmc:PropertyModel) | [ca, f1, p2, d1, pmc ] ],"
            + "  [(wm)<-[wmi:IS_IN]-(wm2:WorkpackModel) | [wmi,wm2] ],"
            + "  [(wm)-[wmi2:IS_IN]->(wm3:WorkpackModel) | [wmi2,wm3] ],"
            + "  [(wm)<-[f2:FEATURES]-(pm2:PropertyModel) | [f2, pm2] ] " + " ]")
    Optional<Workpack> findByIdWorkpack(@Param("id") Long id);

    @Query("MATCH (w:Workpack)-[rf:IS_ROOT_OF]->(p:Plan) "
               + "        , (p)-[is:IS_STRUCTURED_BY]->(pm:PlanModel) "
               + "        , (w)-[ii:IS_INSTANCE_BY]->(wm:WorkpackModel) "
               + " WHERE ID(p) = $idPlan "
               + " AND NOT (w)-[:IS_IN]->(:Workpack)"
               + " RETURN  w, rf, p, ii, pm, wm, [ "
               + "  [(p)<-[cp:CAN_ACCESS_PLAN]-(p2:Person) | [cp, p2] ],"
               + "  [(w)<-[ca:CAN_ACCESS_WORKPACK]-(p:Person) | [ca, p] ],"
               + "  [(w)<-[wi:IS_IN*]-(w2:Workpack)-[ii_2:IS_INSTANCE_BY]->(wm_2:WorkpackModel) | [wi, w2, ii_2, wm_2] ],"
               + "  [(w2)<-[ca2:CAN_ACCESS_WORKPACK]-(p2:Person) | [ca2, p2] ],"
               + "  [(w2)-[rf_2:IS_ROOT_OF]->(p_2:Plan) | [rf_2, p_2] ],"
               + "  [(w)-[wi2:IS_IN*]->(w3:Workpack)-[ii_3:IS_INSTANCE_BY]->(wm_3:WorkpackModel) | [wi2, w3, ii_3, wm_3] ],"
               + "  [(w3)-[rf_3:IS_ROOT_OF]->(p_3:Plan) | [rf_3, p_3] ],"
               + "  [(wm)<-[wmi:IS_IN*]-(wm2:WorkpackModel) | [wmi,wm2] ]"
               + " ]")
    Set<Workpack> findAll(@Param("idPlan") Long idPlan);

    @Query("MATCH (w:Workpack)-[rf:IS_ROOT_OF]->(p:Plan) "
               + "        , (p)-[is:IS_STRUCTURED_BY]->(pm:PlanModel) "
               + "        , (w)-[ii:IS_INSTANCE_BY]->(wm:WorkpackModel) "
               + " WHERE ID(p) = $idPlan "
               + " AND NOT (w)-[:IS_IN]->(:Workpack)"
               + " RETURN  w, rf, p, ii, pm, wm, [ "
               + "  [(w)<-[f1:FEATURES]-(p1:Property)-[d1:IS_DRIVEN_BY]->(pm1:PropertyModel) | [f1, p1, d1, pm1] ], "
               + "  [(w)<-[wi:IS_IN*]-(w2:Workpack)<-[f2:FEATURES]-(p2:Property)-[d2:IS_DRIVEN_BY]->(pm2:PropertyModel) | [wi,w2,f2, p2, d2, pm2]  ], "
               + "  [(w2)-[ib2:IS_INSTANCE_BY]->(wm2:WorkpackModel)<-[f5:FEATURES]-(pm5:PropertyModel) | [ib2, wm2, f5, pm5]  ], "
               + "  [(wm)<-[f4:FEATURES]-(pm4:PropertyModel) | [f4, pm4] ], "
               + "  [(wm)<-[wmi:IS_IN*]-(wm2:WorkpackModel)<-[f6:FEATURES]-(pm6:PropertyModel) | [wmi,wm2, f6, pm6] ]"
               + " ]")
    Set<Workpack> findAllByPlanWithProperties(@Param("idPlan") Long idPlan);

}
