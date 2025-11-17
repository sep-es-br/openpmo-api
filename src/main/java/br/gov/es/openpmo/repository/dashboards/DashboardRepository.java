package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardStatusData;
import br.gov.es.openpmo.dto.dashboards.DashboardWorkpackDetailDto;
import br.gov.es.openpmo.model.dashboards.Dashboard;
import java.util.List;
import java.util.Optional;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardRepository extends Neo4jRepository<Dashboard, Long>, DashboardRepositoryCustom {

   

        @Query("MATCH (plan:Plan)<-[:BELONGS_TO]-(m:Milestone{deleted:false,canceled:false}) " +
                        "WHERE (m.category <> 'SNAPSHOT' OR m.category IS NULL) AND m.date IS NOT NULL " +
                        "AND ($planId IS NULL OR ID(plan) = $planId) " +
                        "AND date.truncate('month', date(left(m.date, 10))) <= date.truncate('month', date(plan.finish)) "
                        +
                        "AND date.truncate('month', date(left(m.date, 10))) >= date.truncate('month', date(plan.start)) "
                        +
                        "AND ($workpackIds IS NULL OR ID(m) IN $workpackIds) " +
                        "RETURN ID(m) AS idWorkpack, ID(plan) as idPlan, left(m.date, 10) AS start, left(m.date, 10) AS end, plan.start AS startPlan, plan.finish AS endPlan ")
        List<DashboardWorkpackDetailDto> findAllMilestoneMaster(List<Long> workpackIds, Long planId);

        @Query("MATCH (plan:Plan)<-[:BELONGS_TO]-(master:Milestone)<-[:IS_SNAPSHOT_OF]-(m:Milestone{deleted:false})-[:COMPOSES]->(b:Baseline{status: 'APPROVED'}) "
                        +
                        "WHERE ID(b) IN $ids AND m.date IS NOT NULL " +
                        "AND ($planId IS NULL OR ID(plan) = $planId) " +
                        "AND ($workpackIds IS NULL OR ID(master) IN $workpackIds) " +
                        "AND date.truncate('month', date(left(m.date, 10))) <= date.truncate('month', date(plan.finish)) "
                        +
                        "AND date.truncate('month', date(left(m.date, 10))) >= date.truncate('month', date(plan.start)) "
                        +
                        "RETURN ID(master) AS idWorkpack, ID(plan) as idPlan, left(m.date, 10) AS start, left(m.date, 10) AS end, plan.start AS startPlan, plan.finish AS endPlan ")
        List<DashboardWorkpackDetailDto> findAllMilestoneBaseline(List<Long> ids, List<Long> workpackIds, Long planId);



        
        @Query("MATCH (w:Workpack)<-[:IS_IN*]-(d:Deliverable{canceled: FALSE})<-[:IS_SNAPSHOT_OF]-(:Deliverable {category: 'SNAPSHOT'})-[]-(bl:Baseline)\n" +
                "WHERE id(w) = $workpackId\n" +
                "  AND CASE \n" +
                "        WHEN $baselineId IS NULL THEN bl.active\n" +
                "        ELSE id(bl) = $baselineId \n" +
                "  END\n" +
                "\n" +
                "OPTIONAL MATCH (d)<-[:FEATURES]-(propConcluida:Property {value: 'Concluída'})-[:IS_DRIVEN_BY]->(:PropertyModel {name: 'Situação'})\n" +
                "OPTIONAL MATCH (d)<-[:FEATURES]-(propEmExec:Property {value: 'Em execução'})-[:IS_DRIVEN_BY]->(:PropertyModel {name: 'Situação'})\n" +
                "OPTIONAL MATCH (d)<-[:FEATURES]-(propCancelar:Property {value: 'A cancelar'})-[:IS_DRIVEN_BY]->(:PropertyModel {name: 'Situação'})\n" +
                "OPTIONAL MATCH (d)<-[:FEATURES]-(propParalisada:Property {value: 'Paralisada'})-[:IS_DRIVEN_BY]->(:PropertyModel {name: 'Situação'})\n" +
                "OPTIONAL MATCH (d)<-[:FEATURES]-(propPlanejamento:Property)-[:IS_DRIVEN_BY]->(:PropertyModel {name: 'Situação'})\n" +
                "WHERE propPlanejamento.value STARTS WITH 'Planejamento\\\\'\n" +
                "\n" +
                "RETURN\n" +
                "    count(DISTINCT propConcluida) as statusConcluida,\n" +
                "    count(DISTINCT propEmExec) as statusEmExec,\n" +
                "    count(DISTINCT propCancelar) as statusCancelar,\n" +
                "    count(DISTINCT propPlanejamento) as statusPlanejamento,\n" +
                "    count(DISTINCT propParalisada) as statusParalisada,\n" +
                "    count(DISTINCT d) as totalDeliverable\n" +
                "LIMIT 1")
        Optional<DashboardStatusData> getStatusAmmountData(Long workpackId, Long baselineId);
        
        
}
