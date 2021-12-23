package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.schedule.Step;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;
import java.util.Optional;

public interface StepRepository extends Neo4jRepository<Step, Long> {

  @Query(
    "MATCH (step:Step)-[:COMPOSES]->(schedule:Schedule) " +
    "WHERE id(schedule)=$idSchedule " +
    "RETURN step"
  )
  List<Step> findAllByScheduleId(Long idSchedule);

  @Query(
    "MATCH (m:Step)<-[i:IS_SNAPSHOT_OF]-(s:Step)-[c:COMPOSES]->(b:Baseline) " +
    "WHERE id(m)=$idStep AND id(b)=$idBaseline " +
    "RETURN s"
  )
  Optional<Step> findSnapshotByMasterIdAndBaselineId(
    Long idStep,
    Long idBaseline
  );

  @Query(
    "MATCH (a:Step)-[:IS_SNAPSHOT_OF]->(m:Step)<-[i:IS_SNAPSHOT_OF]-(s:Step)-[c:COMPOSES]->(b:Baseline) " +
    "WHERE id(a)=$idStep AND id(b)=$idBaseline " +
    "RETURN s"
  )
  Optional<Step> findAnotherSnapshotOfMasterBySnapshotIdAndAnotherBaselineId(
    Long idStep,
    Long idBaseline
  );

  @Query("MATCH (m:Step)<-[i:IS_SNAPSHOT_OF]-(s:Step) " +
         "WHERE id(s)=$idSnapshot " +
         "RETURN m")
  Optional<Step> findMasterBySnapshotId(Long idSnapshot);

  @Query(
    "MATCH (step:Step)  " +
    "OPTIONAL MATCH (step)<-[:IS_SNAPSHOT_OF]-(snapshot:Step)-[:COMPOSES]->(baseline:Baseline{active:true}) " +
    "WITH step, snapshot, baseline " +
    "WHERE id(step)=$idStep  " +
    "RETURN snapshot, [ " +
    "    [(snapshot)-[consumes:CONSUMES]->(snapshotCostAccount:CostAccount) | [consumes, snapshotCostAccount]]," +
    "    [(snapshotCostAccount)-[isSnapshotOf:IS_SNAPSHOT_OF]->(costAccount:CostAccount) | [isSnapshotOf, costAccount]] " +
    "]"
  )
  Optional<Step> findSnapshotOfActiveBaseline(Long idStep);
}
