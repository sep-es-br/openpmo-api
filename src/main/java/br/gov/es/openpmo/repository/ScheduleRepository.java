package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.schedule.ScheduleDto;
import br.gov.es.openpmo.model.schedule.Schedule;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends Neo4jRepository<Schedule, Long> {

  @Query("MATCH (s:Schedule)-[f:FEATURES]->(w:Workpack) WHERE id(w) = $idWorkpack "
         + "RETURN s, f, w, [ "
         + "  [(s)<-[c:COMPOSES]-(st:Step) | [c, st] ],"
         + "  [(s)<-[c1:COMPOSES]-(st1:Step)-[cs1:CONSUMES]->(ca:CostAccount) | [c1, st1, cs1, ca] ]"
         + "]")
  List<Schedule> findAllByWorkpack(@Param("idWorkpack") Long idWorkpack);

  @Query("MATCH (s:Schedule)-[f:FEATURES]->(w:Workpack) WHERE id(w) in $workpackIds "
          + "RETURN s, f, w, [ "
          + "  [(s)<-[c:COMPOSES]-(st:Step) | [c, st] ],"
          + "  [(s)<-[c1:COMPOSES]-(st1:Step)-[cs1:CONSUMES]->(ca:CostAccount) | [c1, st1, cs1, ca] ]"
          + "]")
  List<Schedule> findAllByWorkpacks(@Param("workpackIds") List<Long> workpackIds);

  @Query("MATCH (wp:Workpack)<-[:IS_IN*0..]-(d:Deliverable) " +
          "WHERE id(wp) = $idWorkpack " +
          "RETURN id(d)")
  List<Long> findAllDeliverable(@Param("idWorkpack") Long idWorkpack);

  @Query("MATCH (s:Schedule)-[f:FEATURES]->(w:Workpack) WHERE id(s) = $id "
         + "RETURN s, f, w, [ "
         + "  [(s)<-[c:COMPOSES]-(st:Step) | [c, st] ],"
         + "  [(s)<-[c1:COMPOSES]-(st1:Step)-[cs1:CONSUMES]->(ca:CostAccount) | [c1, st1, cs1, ca] ]"
         + "]")
  Optional<Schedule> findByIdSchedule(@Param("id") Long id);

  @Query("MATCH (s:Schedule)-[f:FEATURES]->(w:Workpack) " +
         "WHERE id(w)=$idWorkpack " +
         "RETURN s, f, w, [" +
         "  [(s)<-[c:COMPOSES]-(st:Step) | [c, st] ]," +
         "  [(s)<-[c1:COMPOSES]-(st1:Step)-[cs1:CONSUMES]->(ca:CostAccount) | [c1, st1, cs1, ca] ]  " +
         "]")
  Optional<Schedule> findScheduleByWorkpackId(Long idWorkpack);

  @Query("MATCH (m:Schedule)<-[i:IS_SNAPSHOT_OF]-(s:Schedule)-[c:COMPOSES]->(b:Baseline) " +
         "WHERE id(m)=$idSchedule AND id(b)=$idBaseline " +
         "RETURN s, [ " +
         "  [ (s)<-[cs:COMPOSES]-(st:Step) | [ cs, st] ], " +
         "  [ (s)<-[cs2:COMPOSES]-(st2:Step)-[c1:CONSUMES]->(ca:CostAccount) | [ cs2, st2, c1, ca ] ], " +
         "  [ (s)<-[cs3:COMPOSES]-(st3:Step)-[c2:CONSUMES]->(ca2:CostAccount)-[cas:IS_SNAPSHOT_OF]->(mca:CostAccount) | [ cs3, st3, c2, ca2, cas, mca ] ] " +
         "]")
  Optional<Schedule> findSnapshotByMasterIdAndBaselineId(
    Long idSchedule,
    Long idBaseline
  );

  @Query("MATCH (a:Schedule)-[:IS_SNAPSHOT_OF]->(m:Schedule)<-[i:IS_SNAPSHOT_OF]-(s:Schedule)-[c:COMPOSES]->(b:Baseline)  " +
         "WHERE id(a)=$idSchedule AND id(b)=$idBaseline  " +
         "RETURN s")
  Optional<Schedule> findAnotherSnapshotOfMasterBySnapshotIdAndAnotherBaselineId(
    Long idSchedule,
    Long idBaseline
  );

  @Query("MATCH (m:Schedule)<-[i:IS_SNAPSHOT_OF]-(s:Schedule) " +
         "WHERE id(s)=$idSnapshot " +
         "RETURN m")
  Optional<Schedule> findMasterBySnapshotId(Long idSnapshot);

  @Query("MATCH (schedule:Schedule)<-[:IS_SNAPSHOT_OF]-(snapshot:Schedule)-[:COMPOSES]->(baseline:Baseline) " +
         "WHERE id(schedule)=$idSchedule AND (baseline.status IN ['PROPOSED'] OR baseline.active)" +
         "RETURN count(schedule) > 0")
  boolean canBeRemoved(Long idSchedule);

  @Query("MATCH (schedule:Schedule)<-[:IS_SNAPSHOT_OF]-(snapshot:Schedule)-[:COMPOSES]->(baseline:Baseline{active:true}) " +
         "WHERE id(schedule)=$idSchedule " +
         "RETURN id(baseline)"
  )
  Long findActiveBaseline(Long idSchedule);

  @Query("MATCH (schedule:Schedule)<-[i:IS_SNAPSHOT_OF]-(snapshot:Schedule)-[c:COMPOSES]->(b:Baseline{active:true}) " +
      "WHERE id(schedule) IN $idSchedule " +
      "RETURN id(schedule) as id, id(snapshot) as idSnapshot, schedule.end as end, schedule.start as start " +
      ", snapshot.end as baselineEnd, snapshot.start as baselineStart "
  )
  List<ScheduleDto> findSnapshotByMasterIds(List<Long> idSchedule);

  @Query("MATCH (master:Schedule), (snapshot:Schedule) " +
      "WHERE ID(master) = $masterId AND ID(snapshot) = $snapshotId " +
      "SET master.category = 'MASTER' " +
      "CREATE (snapshot)-[:IS_SNAPSHOT_OF]->(master) ")
  void createSnapshotRelationshipWithMaster(
      Long masterId,
      Long snapshotId
  );

  @Query(" MATCH (baseline:Baseline), (snapshot:Schedule) " +
      "WHERE ID(baseline) = $baselineId AND ID(snapshot) = $snapshotId " +
      "CREATE (snapshot)-[:COMPOSES]->(baseline) ")
  void createSnapshotRelationshipWithBaseline(
      Long baselineId,
      Long snapshotId
  );

  @Query("MATCH (w:Workpack)<-[:FEATURES]-(sh:Schedule)<-[:COMPOSES]-(st:Step)-[con:CONSUMES]->(ca:CostAccount)-[:APPLIES_TO]->(d:Deliverable) "
          + "WITH date(sh.start) AS startDate, st.periodFromStart AS period, w, sh, st, con, ca, d, date().month AS currentMonth "
          + "OPTIONAL MATCH (d)<-[:IS_SNAPSHOT_OF]-(d2:Deliverable)-[:COMPOSES]->(b:Baseline) "
          + "WITH startDate, period, w, sh, st, con, ca, d, d2, b, currentMonth, "
          + "startDate.month AS startMonth, "
          + "period + startDate.month AS targetMonth "
          + "WITH startDate, startMonth, period, w, sh, st, con, ca, d, d2, b, currentMonth, targetMonth % 12 AS finalMonth "
          + "WHERE finalMonth < currentMonth AND b IS NOT NULL AND b.active = true "
          + "SET con.plannedCost = con.actualCost, "
          + "st.plannedWork = st.actualWork "
          + "RETURN w, sh, st, con, ca, d, d2, b, finalMonth "
          + "ORDER BY finalMonth ASC")
  void updatePlannedCostsFromActualValues();

}
