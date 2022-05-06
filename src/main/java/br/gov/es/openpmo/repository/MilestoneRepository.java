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
            "    (mm)<-[:FEATURES]-(dm:DateModel)<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m) " +
            "optional match " +
            "    (m)<-[:FEATURES]-(t:Toggle)-[:IS_DRIVEN_BY]->(tm:ToggleModel) " +
            "with " +
            "    distinct m, w, mm, dm, d, t, tm " +
            "optional match " +
            "    (w)-[:IS_BASELINED_BY]->(b:Baseline{active: true}) " +
            "with " +
            "    distinct m, w, mm, dm, d, t, tm, b " +
            "optional match " +
            "    (b)<-[:COMPOSES]-(s:Milestone)<-[:FEATURES]-(planDate:Date)-[:IS_SNAPSHOT_OF]->(d) " +
            "with " +
            "    distinct m, w, mm, dm, d, t, tm, b, planDate, date() as now " +
            "with " +
            "    ( " +
            "        m.completed is not null and m.completed=true " +
            "    ) " +
            "    and " +
            "    ( " +
            "        planDate is null or date(datetime(d.value)) <= date(datetime(planDate.value)) " +
            "    ) " +
            "    as onConcluded " +
            "where id(m)=$milestoneId " +
            "with collect(onConcluded) as list " +
            "return all(x in list where x=true) ")
    boolean isConcluded(Long milestoneId);

    @Query("match " +
            "    (m:Milestone)-[:IS_IN*]->(w:Workpack), " +
            "    (m)-[:IS_INSTANCE_BY]->(mm:MilestoneModel), " +
            "    (mm)<-[:FEATURES]-(dm:DateModel)<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m) " +
            "optional match " +
            "    (m)<-[:FEATURES]-(t:Toggle)-[:IS_DRIVEN_BY]->(tm:ToggleModel) " +
            "with " +
            "    distinct m, w, mm, dm, d, t, tm " +
            "optional match " +
            "    (w)-[:IS_BASELINED_BY]->(b:Baseline{active: true}) " +
            "with " +
            "    distinct m, w, mm, dm, d, t, tm, b " +
            "optional match " +
            "    (b)<-[:COMPOSES]-(s:Milestone)<-[:FEATURES]-(planDate:Date)-[:IS_SNAPSHOT_OF]->(d) " +
            "with " +
            "    distinct m, w, mm, dm, d, t, tm, b, planDate, date() as now " +
            "with " +
            "    ( " +
            "        m.completed is null or m.completed=false " +
            "    ) " +
            "    and " +
            "    ( " +
            "        ( " +
            "            d.value is null " +
            "        ) " +
            "        or " +
            "        ( " +
            "            planDate is null and now <= date(datetime(d.value)) " +
            "        ) " +
            "        or " +
            "        ( " +
            "            planDate is not null " +
            "            and " +
            "            ( " +
            "                ( " +
            "                    date(datetime(d.value)) <= now and now <= date(datetime(planDate.value)) " +
            "                ) " +
            "                or " +
            "                ( " +
            "                    date(datetime(d.value)) > now and date(datetime(d.value)) <= date(datetime(planDate.value)) " +
            "                ) " +
            "            ) " +
            "        ) " +
            "    ) " +
            "    as onTime " +
            "where id(m)=$milestoneId " +
            "with collect(onTime) as list " +
            "return all(x in list where x=true) ")
    boolean isOnTime(Long milestoneId);

    @Query("match " +
            "    (m:Milestone)-[:IS_IN*]->(w:Workpack), " +
            "    (m)-[:IS_INSTANCE_BY]->(mm:MilestoneModel), " +
            "    (mm)<-[:FEATURES]-(dm:DateModel)<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m) " +
            "optional match " +
            "    (m)<-[:FEATURES]-(t:Toggle)-[:IS_DRIVEN_BY]->(tm:ToggleModel) " +
            "with " +
            "    distinct m, w, mm, dm, d, t, tm " +
            "optional match " +
            "    (w)-[:IS_BASELINED_BY]->(b:Baseline{active: true}) " +
            "with " +
            "    distinct m, w, mm, dm, d, t, tm, b " +
            "optional match " +
            "    (b)<-[:COMPOSES]-(s:Milestone)<-[:FEATURES]-(planDate:Date)-[:IS_SNAPSHOT_OF]->(d) " +
            "with " +
            "    distinct m, w, mm, dm, d, t, tm, b, planDate, date() as now " +
            "with " +
            "    ( " +
            "        m.completed is null or m.completed=false " +
            "    ) " +
            "    and " +
            "    ( " +
            "        ( " +
            "            planDate is null and now > date(datetime(d.value)) " +
            "        ) " +
            "        or " +
            "        ( " +
            "            planDate is not null " +
            "            and " +
            "            ( " +
            "                (date(datetime(d.value)) <= now and now > date(datetime(planDate.value))) " +
            "                or " +
            "                (date(datetime(d.value)) > now and date(datetime(d.value)) > date(datetime(planDate.value))) " +
            "            ) " +
            "        ) " +
            "    ) " +
            "    as onLate " +
            "where id(m)=$milestoneId " +
            "with collect(onLate) as list " +
            "return all(x in list where x=true) ")
    boolean isLate(Long milestoneId);

    @Query("match " +
            "    (m:Milestone)-[:IS_IN*]->(w:Workpack), " +
            "    (m)-[:IS_INSTANCE_BY]->(mm:MilestoneModel), " +
            "    (mm)<-[:FEATURES]-(dm:DateModel)<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m) " +
            "optional match " +
            "    (m)<-[:FEATURES]-(t:Toggle)-[:IS_DRIVEN_BY]->(tm:ToggleModel) " +
            "with " +
            "    distinct m, w, mm, dm, d, t, tm " +
            "optional match " +
            "    (w)-[:IS_BASELINED_BY]->(b:Baseline{active: true}) " +
            "with " +
            "    distinct m, w, mm, dm, d, t, tm, b " +
            "optional match " +
            "    (b)<-[:COMPOSES]-(s:Milestone)<-[:FEATURES]-(planDate:Date)-[:IS_SNAPSHOT_OF]->(d) " +
            "with " +
            "    distinct m, w, mm, dm, d, t, tm, b, planDate, date() as now " +
            "with " +
            "    ( " +
            "        planDate is null " +
            "    ) " +
            "    or " +
            "    ( " +
            "        ( " +
            "            m.completed is not null and m.completed=true " +
            "        ) " +
            "        and " +
            "        ( " +
            "            date(datetime(d.value)) > date(datetime(planDate.value)) " +
            "        ) " +
            "    ) " +
            "    as onLateConcluded " +
            "where id(m)=$milestoneId " +
            "with collect(onLateConcluded) as list " +
            "return all(x in list where x=true) ")
    boolean isLateConcluded(Long milestoneId);

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
            "return expirationDate, isWithinAWeek ")
    MilestoneDateQueryResult getMilestoneDateQueryResult(Long milestoneId);

    @Query("match (m:Milestone)<-[:FEATURES]-(d:Date) " +
            "where id(m)=$milestoneId " +
            "return d ")
    Optional<Date> fetchMilestoneDate(Long milestoneId);

}
