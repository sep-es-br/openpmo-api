package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.tripleconstraint.DateIntervalQuery;
import br.gov.es.openpmo.model.dashboards.Dashboard;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DashboardRepository extends Neo4jRepository<Dashboard, Long> {

    @Query("match (d:Dashboard)-[:BELONGS_TO]->(w:Workpack) " +
            "where id(w)=$workpackId " +
            "return d")
    Optional<Dashboard> findByWorkpackId(Long workpackId);

    @Query("match (b:Baseline)<-[:IS_BASELINED_BY]-(p:Project)<-[:IS_SNAPSHOT_OF]-(ps:Project) " +
            "where id(b) in $baselineIds " +
            "optional match (w:Workpack)<-[:IS_IN*]->(p) " +
            "where id(w)=$workpackId " +
            "with b,p,ps,w " +
            "optional match (d:Deliverable)-[:IS_IN*]->(p) " +
            "where id(d)=$workpackId " +
            "with b,p,ps,w,d " +
            "optional match (m:Milestone)-[:IS_IN*]->(p) " +
            "where id(m)=$workpackId " +
            "with b,p,ps,w,d,m " +
            "optional match (w)<-[:IS_IN*]-(:Deliverable)-[:FEATURES]-(:Schedule)<-[:IS_SNAPSHOT_OF]-(s1:Schedule)-[:COMPOSES]->(b) " +
            "with b,p,ps,w,d,m,s1 " +
            "optional match (w)<-[:IS_IN*]-(:Milestone)<-[:FEATURES]-(:Date)<-[:IS_SNAPSHOT_OF]-(d1:Date)-[:COMPOSES]->(b) " +
            "with b,p,ps,w,d,m,s1,d1 " +
            "optional match (d)-[:FEATURES]-(:Schedule)<-[:IS_SNAPSHOT_OF]-(s2:Schedule)-[:COMPOSES]->(b) " +
            "with b,p,ps,w,d,m,s1,d1,s2 " +
            "optional match (m)<-[:FEATURES]-(:Date)<-[:IS_SNAPSHOT_OF]-(d2:Date)-[:COMPOSES]->(baseline) " +
            "with b,p,ps,w,d,m,s1,d1,s2,d2 " +
            "optional match (p)<-[:IS_IN*]-(:Deliverable)-[:FEATURES]-(:Schedule)<-[:IS_SNAPSHOT_OF]-(s3:Schedule)-[:COMPOSES]->(b) " +
            "with b,p,ps,w,d,m,s1,d1,s2,d2,s3 " +
            "optional match (p)<-[:IS_IN*]-(:Milestone)<-[:FEATURES]-(:Date)<-[:IS_SNAPSHOT_OF]-(d3:Date)-[:COMPOSES]->(b) " +
            "with b,p,ps,w,d,m,s1,d1,s2,d2,s3,d3 " +
            "with " +
            "    case w when null then [] else collect(distinct datetime(s1.start)) end + " +
            "    case d when null then [] else collect(distinct datetime(s2.start)) end + " +
            "    case p when null then [] else collect(distinct datetime(s3.start)) end + " +
            "    case w when null then [] else collect(distinct datetime(d1.value)) end + " +
            "    case m when null then [] else collect(distinct datetime(d2.value)) end + " +
            "    case p when null then [] else collect(distinct datetime(d3.value)) end as startDatesList, " +
            "    case w when null then [] else collect(distinct datetime(s1.end)) end + " +
            "    case d when null then [] else collect(distinct datetime(s2.end)) end + " +
            "    case p when null then [] else collect(distinct datetime(s3.end)) end as endDatesList " +
            "unwind startDatesList as startDates " +
            "unwind endDatesList as endDates " +
            "return min(startDates) as initialDate, max(endDates) as endDate ")
    Optional<DateIntervalQuery> fetchIntervalOfSchedules(Long workpackId, List<Long> baselineIds);
}
