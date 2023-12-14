package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.model.workpacks.Milestone;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface DashboardMilestoneRepository extends Neo4jRepository<Milestone, Long> {

  @Query("match (w:Workpack)<-[i:IS_IN*]-(m:Milestone)<-[f:FEATURES]-(d:Date) " +
    "optional match (w)-[ii:IS_IN*0..]-(:Project{deleted:false,canceled:false})-[:IS_BASELINED_BY]->(b:Baseline) " +
    "with * " +
    "where id(w)=$workpackId and ($baselineId is null or id(b)=$baselineId) " +
    "and w.deleted=false and w.canceled=false " +
    "and m.deleted=false and m.canceled=false " +
    "and b.active=true " +
    "OPTIONAL MATCH (m)<-[iso:IS_SNAPSHOT_OF]-(ms:Milestone)-[c:COMPOSES]->(b) " +
    "OPTIONAL MATCH (d)<-[iso2:IS_SNAPSHOT_OF]-(d3:Date)-[f3:FEATURES]->(ms2:Milestone)-[c2:COMPOSES]->(b) " +
    "return m, f, d, [ " +
    "    [[iso,ms,c]], " +
    "    [[iso2,d3,f3,ms2,c2]] " +
    "]")
  List<Milestone> findByParentId(Long workpackId, Long baselineId);

  @Query("match (w:Workpack)<-[i:IS_IN*]-(m:Milestone)<-[f:FEATURES]-(d:Date) " +
    "where id(w)=$workpackId " +
    "and w.deleted=false and w.canceled=false " +
    "and m.deleted=false and m.canceled=false " +
    "OPTIONAL MATCH (m)<-[iso:IS_SNAPSHOT_OF]-(ms:Milestone)-[c:COMPOSES]->(b) " +
    "OPTIONAL MATCH (d)<-[iso2:IS_SNAPSHOT_OF]-(d3:Date)-[c2:COMPOSES]->(b2) " +
    "return m, f, d, [ " +
    "    [[iso,ms,c,b]], " +
    "    [[iso2,d3,c2,b2]] " +
    "]")
  List<Milestone> findByParentId(Long workpackId);

  @Query("match (m:Milestone{deleted:false,canceled:false})-[:IS_IN]->(w:Workpack{deleted:false,canceled:false}) " +
         "where id(m)=$milestoneId " +
         "return id(w)")
  Long findParentIdByMilestoneId(Long milestoneId);

  @Query("match (m:Milestone{deleted:false,canceled:false})-[:IS_IN*]->(w:Workpack{deleted:false,canceled:false})" +
         "-[:IS_BASELINED_BY]->(b:Baseline{active: true}) " +
         "where id(m)=$milestoneId " +
         "return id(w) " +
         "limit 1")
  Long findWorkpackIdByMilestoneId(Long milestoneId);

  @Query("match (m:Milestone{deleted:false,canceled:false})-[:IS_IN*]->(w:Workpack{deleted:false,canceled:false})" +
         "-[:IS_BASELINED_BY]->(b:Baseline{active: true}) " +
         "where id(m)=$milestoneId " +
         "return id(b) " +
         "limit 1")
  Long findBaselineIdByMilestoneId(Long milestoneId);

  @Query("match " +
         "    (m:Milestone{deleted:false,canceled:false})-[:IS_IN*]->(w:Workpack{deleted:false,canceled:false}), " +
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
         "return distinct id(m)")
  Set<Long> onTime(
    Long workpackId,
    LocalDate refDate
  );

  @Query("match " +
         "    (b:Baseline)<-[:COMPOSES]-(s:Milestone{deleted:false,canceled:false})-[:IS_SNAPSHOT_OF]->" +
         "(m:Milestone{deleted:false,canceled:false})-[:IS_IN*]->(w:Workpack{deleted:false,canceled:false})" +
         "-[:IS_BASELINED_BY]->(b), " +
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
         "return distinct id(m)")
  Set<Long> onTime(
    Long baselineId,
    Long workpackId,
    LocalDate refDate
  );

  @Query("match " +
         "    (m:Milestone{deleted:false,canceled:false})-[:IS_IN*]->(w:Workpack{deleted:false,canceled:false}), " +
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
         "return distinct id(m)")
  Set<Long> late(
    Long workpackId,
    LocalDate refDate
  );

  @Query("match " +
         "    (b:Baseline)<-[:COMPOSES]-(s:Milestone{deleted:false,canceled:false})-[:IS_SNAPSHOT_OF]->" +
         "(m:Milestone{deleted:false,canceled:false})-[:IS_IN*]->(w:Workpack{deleted:false,canceled:false})" +
         "-[:IS_BASELINED_BY]->(b), " +
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
         "return distinct id(m)")
  Set<Long> late(
    Long baselineId,
    Long workpackId,
    LocalDate refDate
  );

  @Query("match " +
         "    (m:Milestone{deleted:false,canceled:false})-[:IS_IN*]->(w:Workpack{deleted:false,canceled:false}), " +
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
         "return distinct id(m)")
  Set<Long> concluded(Long workpackId);

  @Query("match " +
         "    (b:Baseline)<-[:COMPOSES]-(s:Milestone{deleted:false,canceled:false})-[:IS_SNAPSHOT_OF]->" +
         "(m:Milestone{deleted:false,canceled:false})-[:IS_IN*]->(w:Workpack{deleted:false,canceled:false})" +
         "-[:IS_BASELINED_BY]->(b), " +
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
         "return distinct id(m)")
  Set<Long> concluded(
    Long baselineId,
    Long workpackId
  );

  @Query("match " +
         "    (b:Baseline)<-[:COMPOSES]-(s:Milestone{deleted:false,canceled:false})-[:IS_SNAPSHOT_OF]->" +
         "(m:Milestone{deleted:false,canceled:false})-[:IS_IN*]->(w:Workpack{deleted:false,canceled:false})" +
         "-[:IS_BASELINED_BY]->(b), " +
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
         "return distinct id(m)")
  Set<Long> lateConcluded(
    Long baselineId,
    Long workpackId
  );

  @Query("match " +
         "    (b:Baseline)<-[:COMPOSES]-(s:Milestone{deleted:false,canceled:false})-[:IS_SNAPSHOT_OF]->" +
         "(m:Milestone{deleted:false,canceled:false})-[:IS_IN*]->(w:Workpack{deleted:false,canceled:false}), " +
         "    (m)-[:IS_INSTANCE_BY]->(wm:MilestoneModel), " +
         "    (wm)<-[:FEATURES]-(dm:DateModel)<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m), " +
         "    (s)<-[:FEATURES]-(planDate:Date)-[:IS_SNAPSHOT_OF]->(d) " +
         "optional match " +
         "    (wm)<-[:FEATURES]-(tm:ToggleModel)<-[:IS_DRIVEN_BY]-(t:Toggle)-[:FEATURES]->(m) " +
         "with " +
         "    b, s, m, w, wm, dm, d, planDate, tm, t " +
         "where " +
         "    ( " +
         "        id(w)=$workpackId " +
         "    ) " +
         "    and " +
         "    ( " +
         "        m.completed is not null and m.completed=true " +
         "    ) " +
         "    and " +
         "    ( " +
         "        date(datetime(d.value)) > date(datetime(planDate.value)) " +
         "    ) " +
         "    and " +
         "    ( " +
         "        (w)-[:IS_BASELINED_BY]->(b) or (w)<-[:IS_IN*]->(:Workpack)-[:IS_BASELINED_BY]->(b) " +
         "    ) " +
         "return distinct id(m)")
  Set<Long> lateConcluded(Long workpackId);

}
