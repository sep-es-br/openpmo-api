package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardBaselineResponse;
import br.gov.es.openpmo.model.baselines.Baseline;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardBaselineRepository extends Neo4jRepository<Baseline, Long> {

    @Query("match (w:Workpack)-[:IS_BASELINED_BY]->(b:Baseline) " +
            "where id(w)=$workpackId and (b.status='PROPOSED' or b.status='APPROVED') " +
            "with collect(datetime(b.proposalDate)) as collection " +
            "match (w:Workpack)-[:IS_BASELINED_BY]->(b:Baseline) " +
            "where id(w)=$workpackId and (b.status='PROPOSED' or b.status='APPROVED') " +
            "return id(b) as id, b.name as name, b.status as status, " +
            "    ( " +
            "        exists((w)-[:IS_BASELINED_BY]->(:Baseline{active:true})) " +
            "        and " +
            "        b.active=true " +
            "    ) " +
            "    or " +
            "    ( " +
            "        not exists((w)-[:IS_BASELINED_BY]->(:Baseline{active:true})) " +
            "        and " +
            "        all(x in collection where x <= datetime(b.proposalDate)) " +
            "    ) as defaultBaseline")
    List<DashboardBaselineResponse> findAllByWorkpackId(Long workpackId);

    @Query("match (w:Workpack) " +
            "where id(w)=$workpackId " +
            "optional match (w)-[:IS_BASELINED_BY]->(b1:Baseline) " +
            "with w,b1 " +
            "optional match (w)<-[:IS_IN*]->(v:Workpack)-[:IS_BASELINED_BY]->(b2:Baseline) " +
            "with b1, b2 " +
            "with collect(distinct b1) as baselines1, collect(distinct b2) as baselines2 " +
            "unwind case baselines1 when [] then baselines2 else baselines1 end as baselines " +
            "return baselines")
    List<Baseline> findAllByAnyWorkpackId(Long workpackId);
}
