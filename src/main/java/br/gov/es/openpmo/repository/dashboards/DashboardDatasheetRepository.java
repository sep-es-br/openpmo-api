package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetStakeholderQueryResult;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DashboardDatasheetRepository extends Neo4jRepository<Workpack, Long> {

  @Query("MATCH (a:Actor)-[s:IS_STAKEHOLDER_IN{active:true}]->(w:Workpack{deleted:false,canceled:false}) " +
         "OPTIONAL MATCH (w)-[:IS_IN*]->(v:Workpack{deleted:false,canceled:false}) " +
         "OPTIONAL MATCH (wm:WorkpackModel)<-[:IS_INSTANCE_BY]-(w) " +
         "OPTIONAL MATCH (a)<-[:IS_A_PORTRAIT_OF]-(file:File) " +
         "WITH * " +
         "ORDER BY " +
         "    (s.role IN wm.organizationRoles), " +
         "    CASE " +
         "      WHEN wm.dashboardShowStakeholders IS NOT NULL " +
         "      THEN [i IN range(0, size(wm.dashboardShowStakeholders)-1) WHERE toLower(wm.dashboardShowStakeholders[i]) = toLower(s.role)][0] " +
         "      ELSE 0 " +
         "    END, " +
         "    a.name " +
         "WHERE " +
         "    ( " +
         "        (a)-[s]->(w) AND id(w)=$workpackId " +
         "        AND any(role IN wm.dashboardShowStakeholders WHERE toLower(role) = toLower(s.role)) " +
         "        AND (s.from IS NULL OR date(s.from) <= date()) " +
         "        AND (s.to IS NULL OR date(s.to) >= date()) " +
         "    ) " +
         "RETURN " +
         "    DISTINCT id(a) AS id, " +
         "    a.name AS name, " +
         "    a.fullName AS fullName, " +
         "    s.role AS role, " +
         "    file, " +
         "    'Organization' IN labels(a) AS organization")
  List<DatasheetStakeholderQueryResult> stakeholders(Long workpackId);

  @Query(
    "MATCH (wp:Workpack)-[:IS_INSTANCE_BY]->(wmp:WorkpackModel) " +
    "MATCH (wmp)<-[:IS_IN]-(wmc:WorkpackModel) " +
    "WHERE id(wp)=$workpackId AND id(wmp)=$workpackModelId " +
    "RETURN wmc, [" +
    "  [ (wp)<-[rel1:IS_IN*..2]-(wc1:Workpack) | [rel1, wc1] ], " +
    "  [ (wp)<-[:IS_IN*..2]-(wc1)-[rel2:IS_INSTANCE_BY]->(wmc1:WorkpackModel) | [rel2, wmc1] ], " +
    "  [ (wp)<-[:IS_IN*..2]-(wc1)-[rel3:IS_LINKED_TO]->(wmc2:WorkpackModel) | [rel3, wmc2] ], " +
    "  [ (wp)<-[:IS_IN*..2]-(wc1)-[rel4:BELONGS_TO]->(plan:Plan) | [rel4, plan] ], " +
    "  [ (wmc)<-[rel5:IS_IN]-(wmc3:WorkpackModel) | [rel5, wmc3] ], " +
    "  [ (wp)<-[:IS_IN*..2]-(wc1)<-[f1:FEATURES]-(p1:Property)-[idb1:IS_DRIVEN_BY]->(pm1:PropertyModel{name: 'name'}) | [f1, p1, idb1, pm1] ]" +
    "] "
  )
  List<WorkpackModel> findFirstLayerWorkpackModelChildren(Long workpackId, Long workpackModelId);


  @Query(
    "MATCH (wp:Workpack)-[:IS_INSTANCE_BY]->(wmp:WorkpackModel) " +
    "MATCH (wp)-[:BELONGS_TO{linked:false}]->(plan:Plan) " +
    "MATCH (wmp)<-[:IS_IN]-(wmc:WorkpackModel)  " +
    "WHERE id(wmp)=$workpackModelId AND id(plan)=$planId " +
    "RETURN wmc, [ " +
    " [ (wp)<-[rel1:IS_IN*..2]-(wc1:Workpack)-[:BELONGS_TO]->(plan) | [rel1, wc1] ], " +
    " [ (wp)<-[:IS_IN*..2]-(wc1)-[rel2:IS_INSTANCE_BY]->(wmc1:WorkpackModel) | [rel2, wmc1] ], " +
    " [ (wp)<-[:IS_IN*..2]-(wc1)-[rel3:IS_LINKED_TO]->(wmc2:WorkpackModel) | [rel3, wmc2] ], " +
    " [ (wmc)<-[rel4:IS_IN]-(wmc3:WorkpackModel) | [rel4, wmc3] ], " +
    " [ (wp)<-[:IS_IN*..2]-(wc1)<-[f1:FEATURES]-(p1:Property)-[idb1:IS_DRIVEN_BY]->(pm1:PropertyModel{name: 'name'}) | [f1, p1, idb1, pm1] ] " +
    "] "
  )
  List<WorkpackModel> findFirstLayerWorkpackModelLinkedChildren(Long workpackModelId, Long planId);


  @Query(
    "MATCH (parent:Workpack)<-[:IS_IN*]-(wp:Workpack)-[:IS_INSTANCE_BY]->(wmp:WorkpackModel) " +
    "MATCH (wp)-[:BELONGS_TO{linked:false}]->(plan:Plan) " +
    "MATCH (wmp)<-[:IS_IN]-(wmc:WorkpackModel) " +
    "WHERE id(parent)=$parentId AND id(wmp)=$workpackModelId AND id(plan)=$planId " +
    "RETURN wmc, [ " +
    "  [ (wp)<-[rel1:IS_IN*..2]-(wc1:Workpack) | [rel1, wc1] ], " +
    "  [ (wp)<-[:IS_IN*..2]-(wc1)-[rel2:IS_INSTANCE_BY]->(wmc1:WorkpackModel) | [rel2, wmc1] ], " +
    "  [ (wp)<-[:IS_IN*..2]-(wc1)-[rel3:IS_LINKED_TO]->(wmc2:WorkpackModel) | [rel3, wmc2] ], " +
    "  [ (wp)<-[:IS_IN*..2]-(wc1)-[rel4:BELONGS_TO]->(plan:Plan) | [rel4, plan] ], " +
    "  [ (wmc)<-[rel5:IS_IN]-(wmc3:WorkpackModel) | [rel5, wmc3] ], " +
    "  [ (wp)<-[:IS_IN*..2]-(wc1)<-[f1:FEATURES]-(p1:Property)-[idb1:IS_DRIVEN_BY]->(pm1:PropertyModel{name: 'name'}) | [f1, p1, idb1, pm1] ]" +
    "] "
  )
  Set<WorkpackModel> findWorkpackModelChildren(
    Long workpackModelId,
    Long parentId,
    Long planId
  );

  @Query(
    "MATCH (plan:Plan) " +
    "WHERE id(plan)=$planId " +
    "MATCH (wp:Workpack)-[:IS_INSTANCE_BY]->(wmp:WorkpackModel) " +
    "MATCH (wmp)<-[:IS_IN]-(wmc:WorkpackModel) " +
    "WHERE id(wmp)=$workpackModelId AND ( " +
    "  (wp)-[:IS_IN*]->(:Workpack)-[:BELONGS_TO{linked: true}]->(plan) OR " +
    "  (wp)-[:BELONGS_TO{linked:true}]->(plan) " +
    ") " +
    "RETURN wmc, [ " +
    "  [ (wp)<-[rel1:IS_IN*..2]-(wc1:Workpack) | [rel1, wc1] ], " +
    "  [ (wp)<-[:IS_IN*..2]-(wc1)-[rel2:IS_INSTANCE_BY]->(wmc1:WorkpackModel) | [rel2, wmc1] ], " +
    "  [ (wp)<-[:IS_IN*..2]-(wc1)-[rel3:IS_LINKED_TO]->(wmc2:WorkpackModel) | [rel3, wmc2] ], " +
    "  [ (wp)<-[:IS_IN*..2]-(wc1)-[rel4:BELONGS_TO]->(plan:Plan) | [rel4, plan] ], " +
    "  [ (wmc)<-[rel5:IS_IN]-(wmc3:WorkpackModel) | [rel5, wmc3] ], " +
    "  [ (wp)<-[:IS_IN*..2]-(wc1)<-[f1:FEATURES]-(p1:Property)-[idb1:IS_DRIVEN_BY]->(pm1:PropertyModel{name: 'name'}) | [f1, p1, idb1, pm1] ]" +
    "] "
  )
  Set<WorkpackModel> findWorkpackModelLinkedChildren(Long workpackModelId, Long planId);


  @Query(
    "MATCH (wp:Workpack)-[:IS_INSTANCE_BY]->(wmp:WorkpackModel) " +
    "MATCH (wp)-[:BELONGS_TO{linked:false}]->(plan:Plan) " +
    "MATCH (wmp)<-[:IS_IN]-(wmc:WorkpackModel) " +
    "WHERE id(wmp)=$workpackModelId AND id(plan)=$planId " +
    "RETURN wmc, [ " +
    "  [ (wp)<-[rel1:IS_IN*..2]-(wc1:Workpack) | [rel1, wc1] ], " +
    "  [ (wp)<-[:IS_IN*..2]-(wc1)-[rel2:IS_INSTANCE_BY]->(wmc1:WorkpackModel) | [rel2, wmc1] ], " +
    "  [ (wp)<-[:IS_IN*..2]-(wc1)-[rel3:IS_LINKED_TO]->(wmc2:WorkpackModel) | [rel3, wmc2] ], " +
    "  [ (wp)<-[:IS_IN*..2]-(wc1)-[rel4:BELONGS_TO]->(plan:Plan) | [rel4, plan] ], " +
    "  [ (wmc)<-[rel5:IS_IN]-(wmc3:WorkpackModel) | [rel5, wmc3] ], " +
    "  [ (wp)<-[:IS_IN*..2]-(wc1)<-[f1:FEATURES]-(p1:Property)-[idb1:IS_DRIVEN_BY]->(pm1:PropertyModel{name: 'name'}) | [f1, p1, idb1, pm1] ]" +
    "] "
  )
  Set<WorkpackModel> findWorkpackModelLinkedEquivalentChildren(
    Long workpackModelId,
    Long planId
  );

}
