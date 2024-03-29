package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;
import java.util.Optional;

public interface StepRepository extends Neo4jRepository<Step, Long> {

  @Query("MATCH (step:Step)-[:COMPOSES]->(schedule:Schedule) " +
         "WHERE id(schedule)=$idSchedule " +
         "RETURN step")
  List<Step> findAllByScheduleId(Long idSchedule);

  @Query("MATCH (m:Step)<-[i:IS_SNAPSHOT_OF]-(s:Step)-[c:COMPOSES]->(b:Baseline) " +
         "WHERE id(m)=$idStep AND id(b)=$idBaseline " +
         "RETURN s")
  Optional<Step> findSnapshotByMasterIdAndBaselineId(
    Long idStep,
    Long idBaseline
  );

  @Query("MATCH (a:Step)-[:IS_SNAPSHOT_OF]->(m:Step)<-[i:IS_SNAPSHOT_OF]-(s:Step)-[c:COMPOSES]->(b:Baseline) " +
         "WHERE id(a)=$idStep AND id(b)=$idBaseline " +
         "RETURN s")
  Optional<Step> findAnotherSnapshotOfMasterBySnapshotIdAndAnotherBaselineId(
    Long idStep,
    Long idBaseline
  );

  @Query("MATCH (m:Step)<-[i:IS_SNAPSHOT_OF]-(s:Step) " +
         "WHERE id(s)=$idSnapshot " +
         "RETURN m")
  Optional<Step> findMasterBySnapshotId(Long idSnapshot);

  @Query("MATCH (step:Step)  " +
         "OPTIONAL MATCH (step)<-[:IS_SNAPSHOT_OF]-(snapshot:Step)-[:COMPOSES]->(baseline:Baseline{active:true}) " +
         "WITH step, snapshot, baseline " +
         "WHERE id(step)=$idStep  " +
         " OPTIONAL MATCH (snapshot)-[consumes:CONSUMES]->(snapshotCostAccount:CostAccount) " +
         " OPTIONAL MATCH (snapshotCostAccount)-[isSnapshotOf:IS_SNAPSHOT_OF]->(costAccount:CostAccount) " +
         "RETURN snapshot, [ " +
         "    [ [consumes, snapshotCostAccount]]," +
         "    [ [isSnapshotOf, costAccount]] " +
         "]")
  Optional<Step> findSnapshotOfActiveBaseline(Long idStep);

  @Query("MATCH (deliverable:Deliverable) " +
         "WHERE id(deliverable)=$idDeliverable  " +
         "MATCH (deliverable)<-[:FEATURES]-(:Schedule)<-[:COMPOSES]-(step:Step)  " +
         "WITH step,  " +
         "   toFloat(step.plannedWork) AS estimedWork,  " +
         "   toFloat(step.actualWork) AS actualWork  " +
         "WHERE actualWork < estimedWork  " +
         "WITH step, estimedWork " +
         "RETURN count(DISTINCT step) > 0")
  boolean hasWorkToCompleteComparingWithMaster(Long idDeliverable);

  @Query("MATCH (deliverable:Deliverable)<-[:FEATURES]-(:Schedule)<-[:COMPOSES]-(step:Step) " +
         "MATCH (step)<-[:IS_SNAPSHOT_OF]-(snapshot:Step)-[:COMPOSES]->(baseline:Baseline{active:true}) " +
         "WITH step, snapshot, baseline, " +
         "    toFloat(step.actualWork) AS actualWork, " +
         "    toFloat(snapshot.plannedWork) AS plannedWork  " +
         "WHERE id(deliverable)=$idDeliverable AND actualWork < plannedWork " +
         "WITH step, actualWork, plannedWork, snapshot, baseline " +
         "RETURN count(DISTINCT step) > 0")
  boolean hasWorkToCompleteComparingWithActiveBaseline(Long idDeliverable);

  @Query("match (s:Step)-[:COMPOSES]->(:Schedule)-[:FEATURES]->(d:Deliverable) " +
         "where id(s)=$stepId " +
         "return d")
  List<Deliverable> findAllDeliverablesByStepId(Long stepId);

  @Query("match (s:Schedule)-[:FEATURES]->(d:Deliverable) " +
         "where id(s)=$scheduleId " +
         "return d")
  List<Deliverable> findDeliverablesByScheduleId(Long scheduleId);

  @Query("match (step:Step)<-[:IS_SNAPSHOT_OF]-(snapshot:Step)-[:COMPOSES]->(baseline:Baseline{active:true}) " +
         "where id(step)=$stepId " +
         "return id(baseline)")
  Long findActiveBaseline(Long stepId);

  @Query("match (d:Deliverable)-[:IS_IN*]->(w:Workpack) " +
         "where id(d) in $deliverablesId " +
         "return d,w")
  List<Workpack> findAllDeliverablesAndAscendents(List<Long> deliverablesId);

  @Query("match (s:Step)-[c:CONSUMES]->(ca:CostAccount) " +
         "where id(s) = $idStep and id(ca)=$idCostAccount " +
         "return s, c, ca")
  Optional<Step> findByStepIdAndCostAccountsId(
    Long idStep,
    Long idCostAccount
  );

}
