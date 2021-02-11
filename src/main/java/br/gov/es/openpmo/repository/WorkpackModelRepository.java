package br.gov.es.openpmo.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.openpmo.model.Workpack;
import br.gov.es.openpmo.model.WorkpackModel;

public interface WorkpackModelRepository extends Neo4jRepository<WorkpackModel, Long> {

    @Query("MATCH (w:WorkpackModel)-[wp:IS_ROOT_OF]->(pm:PlanModel) "
               + "WHERE ID(pm) = $id AND NOT (w)-[:IS_IN]->(:WorkpackModel) "
               + " RETURN w, wp, pm, ["
               + "  [(w)-[is:IS_SORTED_BY]->(ps:PropertyModel) | [is, ps] ] "
               + "]")
    List<WorkpackModel> findAllByIdPlanModel(@Param("id") Long id);

    @Query("MATCH (w:WorkpackModel)-[wp:IS_ROOT_OF]->(pm:PlanModel) "
               + "WHERE ID(w) = $id  "
               + " RETURN w, [ "
               + "  [(w)-[i:IS_IN]-(w2:WorkpackModel) | [i,w2] ],"
//               + "  [(w)-[i2:IS_IN]->(wp:WorkpackModel) | [i2,wp] ],"
               + "  [(w)<-[f:FEATURES]-(p:PropertyModel) | [f, p] ], "
               + "  [(w)-[is:IS_SORTED_BY]->(ps:PropertyModel) | [is, ps] ], "
               + "  [(p)-[dl:DEFAULTS_TO]->(l:Locality) | [dl, l] ], "
               + "  [(p)-[ir:IS_ROOT_OF]->(dm:Domain) | [ir, dm] ], "
               + "  [(p)-[du:DEFAULTS_TO]->(u:UnitMeasure) | [du, u] ], "
               + "  [(p)-[d:DEFAULTS_TO]->(o:Organization) | [d, o] ] "
               + "] ")
    Optional<WorkpackModel> findAllByIdWorkpackModel(@Param("id") Long id);

    @Query("MATCH (w:Workpack)-[wp:IS_INSTANCE_BY]->(wm:WorkpackModel) "
               + "WHERE ID(w) = $idWorkpack "
               + " RETURN wm , ["
               + "  [(wm)-[is:IS_SORTED_BY]->(ps:PropertyModel) | [is, ps] ], "
               + "  [(wm)<-[i:IS_IN]-(wm2:WorkpackModel) |[i,wm2] ],"
               + "  [(wm)<-[f:FEATURES]-(p:PropertyModel) |[f, p] ]"
               + "] ")
    Optional<WorkpackModel> findByIdWorkpack(@Param("idWorkpack") Long idWorkpack);

    @Query("MATCH (w:WorkpackModel)-[rf:IS_ROOT_OF]->(p:PlanModel) "
               + " WHERE ID(w) = $id "
               + " RETURN  w, rf, p, [ "
               + "  [(w)-[wi:IS_IN*]->(w2:WorkpackModel) | [wi, w2] ]"
               + " ]")
    WorkpackModel findByIdWithParents(@Param("id") Long id);

    @Query("MATCH (w:WorkpackModel)-[wp:IS_ROOT_OF]->(pm:PlanModel) "
               + "WHERE ID(pm) = $id AND NOT (w)-[:IS_IN]->(:WorkpackModel) "
               + " RETURN w, wp, pm , ["
               + "  [(wm)<-[i:IS_IN*]-(wm2:WorkpackModel)-[:IS_ROOT_OF]->(:PlanModel) |[i,wm2] ]"
               + "] ")
    Set<WorkpackModel> findAllByIdPlanModelWithChildren(@Param("id") Long id);

}
