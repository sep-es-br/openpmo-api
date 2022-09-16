package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.baselines.Baseline;
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
           + "  [(st)-[c1:CONSUMES]->(ca:CostAccount) | [c1, ca] ]"
           + "]")
    List<Schedule> findAllByWorkpack(@Param("idWorkpack") Long idWorkpack);

    @Query("MATCH (s:Schedule)-[f:FEATURES]->(w:Workpack) WHERE id(s) = $id "
           + "RETURN s, f, w, [ "
           + "  [(s)<-[c:COMPOSES]-(st:Step) | [c, st] ],"
           + "  [(st)-[c1:CONSUMES]->(ca:CostAccount) | [c1, ca] ]"
           + "]")
    Optional<Schedule> findByIdSchedule(@Param("id") Long id);

    @Query("MATCH (s:Schedule)-[:FEATURES]->(w:Workpack) " +
           "WHERE id(w)=$idWorkpack " +
           "RETURN s, [" +
           "  [(s)<-[c:COMPOSES]-(st:Step) | [c, st] ]," +
           "  [(s)<-[:COMPOSES]-(:Step)-[c1:CONSUMES]->(ca:CostAccount) | [c1, ca] ]  " +
           "]")
    Optional<Schedule> findScheduleByWorkpackId(Long idWorkpack);

    @Query("MATCH (m:Schedule)<-[i:IS_SNAPSHOT_OF]-(s:Schedule)-[c:COMPOSES]->(b:Baseline) " +
           "WHERE id(m)=$idSchedule AND id(b)=$idBaseline " +
           "RETURN s")
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
           "RETURN baseline")
    Optional<Baseline> findActiveBaseline(Long idSchedule);

    @Query("MATCH (m:Schedule)<-[i:IS_SNAPSHOT_OF]-(s:Schedule)-[c:COMPOSES]->(b:Baseline{active:true}) " +
           "WHERE id(m)=$idSchedule " +
           "RETURN s")
    Optional<Schedule> findSnapshotByMasterId(Long idSchedule);

}
