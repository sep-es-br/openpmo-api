package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.workpack.MilestoneDateQueryResult;
import br.gov.es.openpmo.model.properties.Date;
import br.gov.es.openpmo.model.workpacks.Milestone;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MilestoneRepository extends Neo4jRepository<Milestone, Long> {

    @Query("match " +
            "    (m:Milestone)-[:IS_IN*]->(w:Workpack), " +
            "    (m)-[:IS_INSTANCE_BY]->(mm:MilestoneModel), " +
            "    (mm)<-[:FEATURES]-(dm:DateModel)<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m), " +
            "    (mm)<-[:FEATURES]-(tm:ToggleModel)<-[:IS_DRIVEN_BY]-(t:Toggle)-[:FEATURES]->(m) " +
            "optional match " +
            "    (w)-[:IS_BASELINED_BY]->(b:Baseline{active: true}), " +
            "    (b)<-[:COMPOSES]-(s:Milestone)<-[:FEATURES]-(planDate:Date)-[:IS_SNAPSHOT_OF]->(d) " +
            "with " +
            "    distinct m, tm, t, planDate, d " +
            "with " +
            "    ( " +
            "        tm.name in ['Status Completed', 'Concluído'] and t.value=true " +
            "    ) " +
            "    and " +
            "    ( " +
            "        planDate is null or date(datetime(d.value)) <= date(datetime(planDate.value)) " +
            "    ) " +
            "    as concluded " +
            "where id(m)=$milestoneId " +
            "with collect(concluded) as list " +
            "return all(x in list where x=true)")
    Optional<Boolean> isConcluded(Long milestoneId);

    @Query("match " +
            "    (m:Milestone)-[:IS_IN*]->(w:Workpack), " +
            "    (m)-[:IS_INSTANCE_BY]->(mm:MilestoneModel), " +
            "    (mm)<-[:FEATURES]-(dm:DateModel)<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m), " +
            "    (mm)<-[:FEATURES]-(tm:ToggleModel)<-[:IS_DRIVEN_BY]-(t:Toggle)-[:FEATURES]->(m) " +
            "optional match " +
            "    (w)-[:IS_BASELINED_BY]->(b:Baseline{active: true}), " +
            "    (b)<-[:COMPOSES]-(s:Milestone)<-[:FEATURES]-(planDate:Date)-[:IS_SNAPSHOT_OF]->(d) " +
            "with " +
            "    distinct m, tm, t, planDate, d, date() as now " +
            "with " +
            "    ( " +
            "        tm.name in ['Status Completed', 'Concluído'] and t.value=false " +
            "    ) " +
            "    and " +
            "    ( " +
            "        planDate is null " +
            "        or " +
            "        ( " +
            "            date(datetime(d.value)) <= now and now <= date(datetime(planDate.value)) " +
            "        ) " +
            "        or " +
            "        ( " +
            "            date(datetime(d.value)) > now and date(datetime(d.value)) <= date(datetime(planDate.value)) " +
            "        ) " +
            "    ) " +
            "    as onTime " +
            "where id(m)=$milestoneId " +
            "with collect(onTime) as list " +
            "return all(x in list where x=true)")
    Optional<Boolean> isOnTime(Long milestoneId);

    @Query("match " +
            "    (m:Milestone)-[:IS_IN*]->(w:Workpack), " +
            "    (m)-[:IS_INSTANCE_BY]->(mm:MilestoneModel), " +
            "    (mm)<-[:FEATURES]-(dm:DateModel)<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m), " +
            "    (mm)<-[:FEATURES]-(tm:ToggleModel)<-[:IS_DRIVEN_BY]-(t:Toggle)-[:FEATURES]->(m) " +
            "optional match " +
            "    (w)-[:IS_BASELINED_BY]->(b:Baseline{active: true}), " +
            "    (b)<-[:COMPOSES]-(s:Milestone)<-[:FEATURES]-(planDate:Date)-[:IS_SNAPSHOT_OF]->(d) " +
            "with " +
            "    distinct m, tm, t, planDate, d, date() as now " +
            "with " +
            "    ( " +
            "        tm.name in ['Status Completed', 'Concluído'] and t.value=false " +
            "    ) " +
            "    and " +
            "    ( " +
            "        planDate is null " +
            "        or " +
            "        ( " +
            "            date(datetime(d.value)) <= now and now > date(datetime(planDate.value)) " +
            "        ) " +
            "        or " +
            "        ( " +
            "            date(datetime(d.value)) > now and date(datetime(d.value)) > date(datetime(planDate.value)) " +
            "        ) " +
            "    ) " +
            "    as onLate " +
            "where id(m)=$milestoneId " +
            "with collect(onLate) as list " +
            "return all(x in list where x=true)")
    Optional<Boolean> isLate(Long milestoneId);

    @Query("match " +
            "    (m:Milestone)-[:IS_IN*]->(w:Workpack), " +
            "    (m)-[:IS_INSTANCE_BY]->(mm:MilestoneModel), " +
            "    (mm)<-[:FEATURES]-(dm:DateModel)<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m), " +
            "    (mm)<-[:FEATURES]-(tm:ToggleModel)<-[:IS_DRIVEN_BY]-(t:Toggle)-[:FEATURES]->(m) " +
            "optional match " +
            "    (w)-[:IS_BASELINED_BY]->(b:Baseline{active: true}), " +
            "    (b)<-[:COMPOSES]-(s:Milestone)<-[:FEATURES]-(planDate:Date)-[:IS_SNAPSHOT_OF]->(d) " +
            "with " +
            "    distinct m, tm, t, planDate, d " +
            "with " +
            "    ( " +
            "        tm.name in ['Status Completed', 'Concluído'] and t.value=true " +
            "    ) " +
            "    and " +
            "    ( " +
            "        planDate is not null and date(datetime(d.value)) > date(datetime(planDate.value)) " +
            "    ) " +
            "    as onLateConcluded " +
            "where id(m)=$milestoneId " +
            "with collect(onLateConcluded) as list " +
            "return all(x in list where x=true)")
    Optional<Boolean> isLateConcluded(Long milestoneId);

    @Query("match (m:Milestone)<-[:FEATURES]-(d:Date) " +
            "where " +
            "    id(m)=$milestoneId and d.value is not null " +
            "with " +
            "    datetime(d.value) as expirationDate " +
            "with " +
            "    expirationDate, " +
            "    expirationDate - duration({days: 7}) as warningDate " +
            "with " +
            "    expirationDate, " +
            "    warningDate <= datetime() as isWithinAWeek " +
            "return expirationDate, isWithinAWeek")
    MilestoneDateQueryResult getMilestoneDateQueryResult(Long milestoneId);

    @Query("match (m:Milestone)<-[:FEATURES]-(d:Date) " +
            "where id(m)=$milestoneId " +
            "return d")
    Optional<Date> fetchMilestoneDate(Long milestoneId);

}
