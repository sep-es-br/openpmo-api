package br.gov.es.openpmo.repository.baselines;

import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaselineHelperWorkpackRepository extends Neo4jRepository<Workpack, Long> {

  @Query("match (w:Workpack) where id(w)=$childId " +
         "match (p:Workpack) where id(p)=$parentId " +
         "create (w)-[:IS_IN]->(p)")
  void createIsInRelationship(
    Long childId,
    Long parentId
  );

  @Query("match (w:Workpack) where id(w)=$workpackId " +
         "match (c:CostAccount) where id(c)=$costAccountId " +
         "create (w)<-[:APPLIES_TO]-(c)")
  void createAppliesToRelationship(
    Long workpackId,
    Long costAccountId
  );

  @Query("match (w:Workpack) where id(w)=$workpackId  " +
         "match (s:Schedule) where id(s)=$scheduleId " +
         "create (w)<-[:FEATURES]-(s)")
  void createFeaturesRelationship(
    Long workpackId,
    Long scheduleId
  );

  @Query(
    "MATCH (snapshot) WHERE id(snapshot)=$snapshotId " +
    "MATCH (baseline:Baseline) WHERE id(baseline)=$baselineId " +
    "CREATE (baseline)<-[:COMPOSES]-(snapshot) "
  )
  void createBaselineComposesRelationship(Long baselineId, Long snapshotId);

  @Query(
    "match (stepMaster:Step)-[consumes:CONSUMES]->(costAccountMaster:CostAccount) " +
    "where id(stepMaster)=$stepId and id(costAccountMaster)=$costAccountId " +
    "match (step:Step) where id(step)=$stepSnapshotId " +
    "match (costAccount:CostAccount) where id(costAccount)=$costAccountSnapshotId " +
    "create (costAccount)<-[:CONSUMES{ actualCost: consumes.actualCost, plannedCost: consumes.plannedCost }]-(step) "
  )
  void createCostAccountConsumesRelationship(Long stepId, Long costAccountId, Long stepSnapshotId, Long costAccountSnapshotId);

}
