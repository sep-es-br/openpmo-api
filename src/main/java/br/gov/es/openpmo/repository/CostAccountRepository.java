package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.schedule.ConsumesDto;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CostAccountRepository extends Neo4jRepository<CostAccount, Long>, CustomRepository {

  @Query("MATCH (c:CostAccount)-[ap:APPLIES_TO]->(workpack:Workpack{deleted: false})-[belongsTo:BELONGS_TO]->(plan:Plan) " +
      ", (c)<-[f1:FEATURES]-(p1:Property)-[d1:IS_DRIVEN_BY]->(pm1:PropertyModel) " +
      "WHERE id(workpack) IN $ids  AND belongsTo.linked=false " +
      "RETURN c,f1,p1,d1,pm1,ap, workpack, [  " +
      " [(p1)-[v1:VALUES]->(o:Organization) | [v1, o] ], " +
      " [(p1)-[v2:VALUES]->(l:Locality) | [v2, l] ],  " +
      " [(p1)-[v3:VALUES]->(u:UnitMeasure) | [v3, u] ] " +
      "]"
  )
  List<CostAccount> findAllByWorkpackId(List<Long> ids);

  @Query("MATCH (c:CostAccount)-[i:APPLIES_TO]->(w:Workpack) "
    + " WHERE id(c) = $id "
    + " RETURN c, i, w, [ "
    + " [(c)<-[f1:FEATURES]-(p1:Property)-[d1:IS_DRIVEN_BY]->(pm1:PropertyModel) | [f1, p1, d1, pm1] ], "
    + " [(c)<-[fo:FEATURES]-(po:Property)-[v1:VALUES]->(o:Organization) | [fo, po, v1, o] ], "
    + " [(c)<-[fl:FEATURES]-(pl:Property)-[v2:VALUES]-(l:Locality) | [fl, pl, v2, l] ], "
    + " [(c)<-[fu:FEATURES]-(pu:Property)-[v3:VALUES]-(u:UnitMeasure) | [fu, pu, v3, u] ], "
    + " [(c)-[ii:IS_INSTANCE_BY]->(cm:CostAccountModel) | [ii,cm]] "
    + "]")
  Optional<CostAccount> findByIdWithPropertyModel(@Param("id") Long id);

  @Query("MATCH (step:Step)-[:CONSUMES]->(costAccount:CostAccount) " +
    "WHERE id(step)=$idStep " +
    "RETURN costAccount")
  List<CostAccount> findAllByStepId(Long idStep);

  @Query("MATCH (m:CostAccount)<-[i:IS_SNAPSHOT_OF]-(s:CostAccount)-[c:COMPOSES]->(b:Baseline) " +
    "WHERE id(m)=$idCostAccount AND id(b)=$idBaseline " +
    "RETURN s")
  Optional<CostAccount> findSnapshotByMasterIdAndBaselineId(
    Long idCostAccount,
    Long idBaseline
  );

  @Query("MATCH (a:CostAccount)-[:IS_SNAPSHOT_OF]->(m:CostAccount)<-[i:IS_SNAPSHOT_OF]-(s:CostAccount)-[c:COMPOSES]->" +
    "(b:Baseline) " +
    "WHERE id(a)=$idCostAccount AND id(b)=$idBaseline " +
    "RETURN s")
  Optional<CostAccount> findAnotherSnapshotOfMasterBySnapshotIdAndAnotherBaselineId(
    Long idCostAccount,
    Long idBaseline
  );

  @Query("MATCH (m:CostAccount)<-[i:IS_SNAPSHOT_OF]-(s:CostAccount) " +
    "WHERE id(s)=$idSnapshot " +
    "RETURN m")
  Optional<CostAccount> findMasterBySnapshotId(Long idSnapshot);

  @Query("MATCH (c:CostAccount)<-[:FEATURES]-(name:Property)-[:IS_DRIVEN_BY]->(:PropertyModel{name: 'name'}) " +
    "WHERE id(c)=$idCostAccount " +
    "RETURN name.value")
  String findCostAccountNameById(Long idCostAccount);

  @Query("match (c:CostAccount)<-[:FEATURES]-(p:Property)-[:IS_DRIVEN_BY]->(pm:PropertyModel) " +
    "where id(c)=$id and toLower(pm.name) = 'limit' " +
    "return p.value")
  BigDecimal findCostAccountLimitById(Long id);

  @Query(
      "MATCH (snapshot:Step)-[i2:IS_SNAPSHOT_OF]->(step:Step) " +
          ", (snapshot)-[consume:CONSUMES]->(ca2:CostAccount)-[cas:IS_SNAPSHOT_OF]->(mca:CostAccount) " +
          " WHERE id(snapshot) IN $snapshotStepIds " +
          " RETURN id(consume) as id, id(snapshot) as stepSnapshotId, id(mca) as costAccountMasterId " +
          ", consume.actualCost as actualCost, consume.plannedCost as plannedCost "
  )
  List<ConsumesDto> findAllConsumesByStepIds(List<Long> snapshotStepIds);

  @Query("MATCH (s:Schedule)<-[c1:COMPOSES]-(st1:Step)-[cs1:CONSUMES]->(c:CostAccount)<-[:FEATURES]-(name:Property)-[:IS_DRIVEN_BY]->(:PropertyModel{name: 'name'}) " +
      "WHERE id(s) IN $ids " +
      "RETURN id(c) as id, name.value as name ")
  List<EntityDto> findCostAccountByScheduleIds(List<Long> ids);

  @Query(" MATCH (master:CostAccount), (snapshot:CostAccount) " +
      "WHERE ID(master) = $masterId AND ID(snapshot) = $snapshotId " +
      "SET master.category = 'MASTER' " +
      "CREATE (snapshot)-[:IS_SNAPSHOT_OF]->(master) ")
  void createSnapshotRelationshipWithMaster(
      Long masterId,
      Long snapshotId
  );

  @Query(" MATCH (baseline:Baseline), (snapshot:CostAccount) " +
      "WHERE ID(baseline) = $baselineId AND ID(snapshot) = $snapshotId " +
      "CREATE (snapshot)-[:COMPOSES]->(baseline) ")
  void createSnapshotRelationshipWithBaseline(
      Long baselineId,
      Long snapshotId
  );

}
