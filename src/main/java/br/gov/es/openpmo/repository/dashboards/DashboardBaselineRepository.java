package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardBaselineResponse;
import br.gov.es.openpmo.model.baselines.Baseline;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardBaselineRepository extends Neo4jRepository<Baseline, Long> {

  @Query("match (w:Workpack{deleted:false,canceled:false})-[:IS_BASELINED_BY]->(b:Baseline) " +
         "where id(w)=$workpackId and (b.status='PROPOSED' or b.status='APPROVED') " +
         "with collect(datetime(b.proposalDate)) as collection " +
         "match (w:Workpack{deleted:false,canceled:false})-[:IS_BASELINED_BY]->(b:Baseline) " +
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

}
