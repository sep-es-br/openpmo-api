package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.workpacks.CostAccount;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CostAccountRepository extends Neo4jRepository<CostAccount, Long>, CustomRepository {

  @Query(" MATCH (workpack:Workpack{deleted: false})-[belongsTo:BELONGS_TO]->(plan:Plan)  " + 
  "       WHERE id(workpack) = $idWorkpack  " + 
  "       AND belongsTo.linked=false " + 
  " OPTIONAL MATCH (workpack)<-[at:APPLIES_TO]-(c:CostAccount) " + 
  " OPTIONAL MATCH (c)-[ib:IS_INSTANCE_BY]->(cm:CostAccountModel) " + 
  " OPTIONAL MATCH (c)<-[f1:FEATURES]-(p1:Property)-[d1:IS_DRIVEN_BY]->(pm1:PropertyModel) " + 
  " OPTIONAL MATCH (p1)-[v1:VALUES]->(o:Organization) " + 
  " OPTIONAL MATCH (p1)-[v2:VALUES]-(l:Locality) " + 
  " OPTIONAL MATCH (p1)-[v3:VALUES]-(u:UnitMeasure) " + 
  " OPTIONAL MATCH (workpack)-[ii:IS_IN*]->(w2:Workpack)-[:BELONGS_TO]->(plan) " + 
  " OPTIONAL MATCH (w2)<-[at2:APPLIES_TO]-(c2:CostAccount)-[ib2:IS_INSTANCE_BY]->(cm2) " + 
  " OPTIONAL MATCH (c2)<-[f2:FEATURES]-(p2:Property)-[d2:IS_DRIVEN_BY]->(pm2:PropertyModel) " + 
  " RETURN workpack, [ " + 
  "    [ [at, c] ] " + 
  "   ,[ [ib,cm] ]  " + 
  "   ,[ [f1, p1, d1, pm1] ] " + 
  "   ,[ [v1, o] ] " + 
  "   ,[ [v2, l] ] " + 
  "   ,[ [v3, u] ] " + 
  "   ,[ [ii, w2] ] " + 
  "   ,[ [at2, c2, ib2, cm2] ] " + 
  "   ,[ [f2, p2, d2, pm2] ] " +
  " ] ")
  Optional<Workpack> findWorkpackWithCosts(@Param("idWorkpack") Long idWorkpack);

  @Query("MATCH (c:CostAccount)-[i:APPLIES_TO]->(w:Workpack) "
    + " WHERE id(c) = $id "
    + " OPTIONAL MATCH (c)<-[f1:FEATURES]-(p1:Property)-[d1:IS_DRIVEN_BY]->(pm1:PropertyModel) " 
    + " OPTIONAL MATCH (p1)-[v1:VALUES]->(o:Organization) " 
    + " OPTIONAL MATCH (p1)-[v2:VALUES]-(l:Locality) " 
    + " OPTIONAL MATCH (p1)-[v3:VALUES]-(u:UnitMeasure) " 
    + " OPTIONAL MATCH (c)-[ii:IS_INSTANCE_BY]->(cm:CostAccountModel) " 
    + " RETURN c, i, w, [ "
    + " [f1, p1, d1, pm1], "
    + " [v1, o], "
    + " [v2, l], "
    + " [v3, u], "
    + " [ii,cm] "
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

}
