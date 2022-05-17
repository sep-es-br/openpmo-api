package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.model.workpacks.Milestone;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DashboardMilestoneRepository extends Neo4jRepository<Milestone, Long> {

    @Query("match " +
            "    (m:Milestone{deleted:false})-[:IS_IN*]->(w:Workpack{deleted:false}), " +
            "    (m)-[:IS_INSTANCE_BY]->(mm:MilestoneModel), " +
            "    (mm)<-[:FEATURES]-(:PropertyModel)<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m), " +
            "    (mm)<-[:FEATURES]-(tm:ToggleModel)<-[:IS_DRIVEN_BY]-(t:Toggle)-[:FEATURES]->(m) " +
            "where " +
            "    ( " +
            "        id(w)=$workpackId " +
            "    ) " +
            "    and " +
            "    ( " +
            "        tm.name in ['Status Completed', 'Concluído'] " +
            "    ) " +
            "return count(distinct m)")
    Long quantity(Long workpackId);

    @Query("match " +
            "    (b:Baseline)<-[:COMPOSES]-(:Milestone)-[:IS_SNAPSHOT_OF]->(m:Milestone{deleted:false})-[:IS_IN*]->(w:Workpack{deleted:false})-[:IS_BASELINED_BY]->(b), " +
            "    (m)-[:IS_INSTANCE_BY]->(wm:MilestoneModel), " +
            "    (wm)<-[:FEATURES]-(dm:DateModel)<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m), " +
            "    (wm)<-[:FEATURES]-(tm:ToggleModel)<-[:IS_DRIVEN_BY]-(t:Toggle)-[:FEATURES]->(m) " +
            "where " +
            "    ( " +
            "        id(w)=$workpackId and id(b)=$baselineId " +
            "    ) " +
            "    and " +
            "    ( " +
            "        tm.name in ['Status Completed', 'Concluído'] " +
            "    ) " +
            "return count(distinct m)")
    Long quantity(Long baselineId, Long workpackId);

    @Query("match " +
            "    (m:Milestone{deleted:false})-[:IS_IN*]->(w:Workpack{deleted:false}), " +
            "    (m)-[:IS_INSTANCE_BY]->(mm:MilestoneModel), " +
            "    (mm)<-[:FEATURES]-(:PropertyModel)<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m) " +
            "optional match " +
            "    (mm)<-[:FEATURES]-(tm:ToggleModel)<-[:IS_DRIVEN_BY]-(t:Toggle)-[:FEATURES]->(m) " +
            "with " +
            "    m, w, mm, d, tm, t " +
            "where " +
            "    ( " +
            "        id(w)=$workpackId " +
            "    ) " +
            "    and " +
            "    ( " +
            "        m.completed is null or m.completed=false " +
            "    ) " +
            "    and " +
            "    ( " +
            "        d.value is null or date($refDate) <= date(datetime(d.value)) " +
            "    ) " +
            "return count(distinct m)")
    Long onTime(Long workpackId, LocalDate refDate);

    @Query("match " +
            "    (b:Baseline)<-[:COMPOSES]-(s:Milestone)-[:IS_SNAPSHOT_OF]->(m:Milestone{deleted:false})-[:IS_IN*]->(w:Workpack{deleted:false})-[:IS_BASELINED_BY]->(b), " +
            "    (m)-[:IS_INSTANCE_BY]->(wm:MilestoneModel), " +
            "    (wm)<-[:FEATURES]-(dm:DateModel)<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m), " +
            "    (s)<-[:FEATURES]-(planDate:Date)-[:IS_SNAPSHOT_OF]->(d) " +
            "optional match " +
            "    (wm)<-[:FEATURES]-(tm:ToggleModel)<-[:IS_DRIVEN_BY]-(t:Toggle)-[:FEATURES]->(m) " +
            "with " +
            "    b, s, m, w, wm, dm, d, planDate, tm, t " +
            "where " +
            "    ( " +
            "        id(w)=$workpackId and id(b)=$baselineId " +
            "    ) " +
            "    and " +
            "    ( " +
            "        m.completed is null or m.completed=false " +
            "    ) " +
            "    and " +
            "    ( " +
            "        d.value is null " +
            "        or " +
            "        ( " +
            "            date(datetime(d.value)) <= date($refDate) and date($refDate) <= date(datetime(planDate.value)) " +
            "        ) " +
            "        or " +
            "        ( " +
            "            date(datetime(d.value)) > date($refDate) and date(datetime(d.value)) <= date(datetime(planDate.value)) " +
            "        ) " +
            "    ) " +
            "return count(distinct m)")
    Long onTime(Long baselineId, Long workpackId, LocalDate refDate);

    @Query("match " +
            "    (m:Milestone{deleted:false})-[:IS_IN*]->(w:Workpack{deleted:false}), " +
            "    (m)-[:IS_INSTANCE_BY]->(mm:MilestoneModel), " +
            "    (mm)<-[:FEATURES]-(:PropertyModel)<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m) " +
            "optional match " +
            "    (mm)<-[:FEATURES]-(tm:ToggleModel)<-[:IS_DRIVEN_BY]-(t:Toggle)-[:FEATURES]->(m) " +
            "with " +
            "    m, w, mm, d, tm, t " +
            "where " +
            "    ( " +
            "       id(w)=$workpackId " +
            "    ) " +
            "    and " +
            "    ( " +
            "        m.completed is null or m.completed=false " +
            "    ) " +
            "    and " +
            "    ( " +
            "        date($refDate) > date(datetime(d.value)) " +
            "    ) " +
            "return count(distinct m)")
    Long late(Long workpackId, LocalDate refDate);

    @Query("match " +
            "    (b:Baseline)<-[:COMPOSES]-(s:Milestone)-[:IS_SNAPSHOT_OF]->(m:Milestone{deleted:false})-[:IS_IN*]->(w:Workpack{deleted:false})-[:IS_BASELINED_BY]->(b), " +
            "    (m)-[:IS_INSTANCE_BY]->(wm:MilestoneModel), " +
            "    (wm)<-[:FEATURES]-(dm:DateModel)<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m), " +
            "    (s)<-[:FEATURES]-(planDate:Date)-[:IS_SNAPSHOT_OF]->(d) " +
            "optional match " +
            "    (wm)<-[:FEATURES]-(tm:ToggleModel)<-[:IS_DRIVEN_BY]-(t:Toggle)-[:FEATURES]->(m) " +
            "with " +
            "    b, s, m, w, wm, dm, d, planDate, tm, t " +
            "where " +
            "    ( " +
            "        id(w)=$workpackId and id(b)=$baselineId " +
            "    ) " +
            "    and " +
            "    ( " +
            "        m.completed is null or m.completed=false " +
            "    ) " +
            "    and " +
            "    ( " +
            "        ( " +
            "            date(datetime(d.value)) <= date($refDate) and date($refDate) > date(datetime(planDate.value)) " +
            "        ) " +
            "        or " +
            "        ( " +
            "            date(datetime(d.value)) > date($refDate) and date(datetime(d.value)) > date(datetime(planDate.value)) " +
            "        ) " +
            "    ) " +
            "return count(distinct m)")
    Long late(Long baselineId, Long workpackId, LocalDate refDate);

    @Query("match " +
            "    (m:Milestone{deleted:false})-[:IS_IN*]->(w:Workpack{deleted:false}), " +
            "    (m)-[:IS_INSTANCE_BY]->(mm:MilestoneModel), " +
            "    (mm)<-[:FEATURES]-(:PropertyModel)<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m) " +
            "optional match " +
            "    (mm)<-[:FEATURES]-(tm:ToggleModel)<-[:IS_DRIVEN_BY]-(t:Toggle)-[:FEATURES]->(m) " +
            "with " +
            "    m, w, mm, d, tm, t " +
            "where " +
            "    ( " +
            "       id(w)=$workpackId " +
            "    ) " +
            "    and " +
            "    ( " +
            "        m.completed is not null and m.completed=true " +
            "    ) " +
            "return count(distinct m)")
    Long concluded(Long workpackId);

    @Query("match " +
            "    (b:Baseline)<-[:COMPOSES]-(s:Milestone)-[:IS_SNAPSHOT_OF]->(m:Milestone{deleted:false})-[:IS_IN*]->(w:Workpack{deleted:false})-[:IS_BASELINED_BY]->(b), " +
            "    (m)-[:IS_INSTANCE_BY]->(wm:MilestoneModel), " +
            "    (wm)<-[:FEATURES]-(dm:DateModel)<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m), " +
            "    (s)<-[:FEATURES]-(planDate:Date)-[:IS_SNAPSHOT_OF]->(d) " +
            "optional match " +
            "    (wm)<-[:FEATURES]-(tm:ToggleModel)<-[:IS_DRIVEN_BY]-(t:Toggle)-[:FEATURES]->(m) " +
            "with " +
            "    b, s, m, w, wm, dm, d, planDate, tm, t " +
            "where " +
            "    ( " +
            "        id(w)=$workpackId and id(b)=$baselineId " +
            "    ) " +
            "    and " +
            "    ( " +
            "        m.completed is not null and m.completed=true " +
            "    ) " +
            "    and " +
            "    ( " +
            "        date(datetime(d.value)) <= date(datetime(planDate.value)) " +
            "    ) " +
            "return count(distinct m)")
    Long concluded(Long baselineId, Long workpackId);

    @Query("match " +
            "    (b:Baseline)<-[:COMPOSES]-(s:Milestone)-[:IS_SNAPSHOT_OF]->(m:Milestone{deleted:false})-[:IS_IN*]->(w:Workpack{deleted:false})-[:IS_BASELINED_BY]->(b), " +
            "    (m)-[:IS_INSTANCE_BY]->(wm:MilestoneModel), " +
            "    (wm)<-[:FEATURES]-(dm:DateModel)<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m), " +
            "    (s)<-[:FEATURES]-(planDate:Date)-[:IS_SNAPSHOT_OF]->(d) " +
            "optional match " +
            "    (wm)<-[:FEATURES]-(tm:ToggleModel)<-[:IS_DRIVEN_BY]-(t:Toggle)-[:FEATURES]->(m) " +
            "with " +
            "    b, s, m, w, wm, dm, d, planDate, tm, t " +
            "where " +
            "    ( " +
            "        id(w)=$workpackId and id(b)=$baselineId " +
            "    ) " +
            "    and " +
            "    ( " +
            "        m.completed is not null and m.completed=true " +
            "    ) " +
            "    and " +
            "    ( " +
            "        date(datetime(d.value)) > date(datetime(planDate.value)) " +
            "    ) " +
            "return count(distinct m)")
    Long lateConcluded(Long baselineId, Long workpackId);

}
