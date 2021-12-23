package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.workpacks.CostAccount;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CostAccountRepository extends Neo4jRepository<CostAccount, Long>, CustomRepository {

  @Query("MATCH (workpack:Workpack)-[belongsTo:BELONGS_TO]->(plan:Plan) "
         + " WHERE id(workpack) = $idWorkpack "
         + " AND belongsTo.linked=false "
         + " RETURN workpack, [ "
         + " [(workpack)<-[i:APPLIES_TO]-(c:CostAccount) | [i, c] ], "
         + " [(c)<-[f1:FEATURES]-(p1:Property)-[d1:IS_DRIVEN_BY]->(pm1:PropertyModel) | [f1, p1, d1, pm1] ], "
         + " [(p2)-[v1:VALUES]->(o:Organization) | [v1, o] ], "
         + " [(p2)-[v2:VALUES]-(l:Locality) | [v2, l] ], "
         + " [(p2)-[v3:VALUES]-(u:UnitMeasure) | [v3, u] ], "
         + " [(workpack)-[i:IS_IN*]->(w2:Workpack)-[:BELONGS_TO]->(plan) | [i, w2] ], "
         + " [(w2)<-[i:APPLIES_TO]-(c2:CostAccount) | [i, c2] ], "
         + " [(c2)<-[f2:FEATURES]-(p2:Property)-[d2:IS_DRIVEN_BY]->(pm2:PropertyModel)  | [f2, p2, d2, pm2] ] "
         + "]")
  Optional<Workpack> findWorkpackWithCosts(@Param("idWorkpack") Long idWorkpack);

  @Query("MATCH (c:CostAccount)-[i:APPLIES_TO]->(w:Workpack) "
         + " WHERE id(c) = $id "
         + " RETURN c, i, w, [ "
         + " [(c)<-[f1:FEATURES]-(p1:Property)-[d1:IS_DRIVEN_BY]->(pm1:PropertyModel) | [f1, p1, d1, pm1] ], "
         + " [(p1)-[v1:VALUES]->(o:Organization) | [v1, o] ], "
         + " [(p1)-[v2:VALUES]-(l:Locality) | [v2, l] ], "
         + " [(p1)-[v3:VALUES]-(u:UnitMeasure) | [v3, u] ] "
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

  @Query("MATCH (cam:CostAccount)<-[:IS_SNAPSHOT_OF]-(cas:CostAccount)-[:COMPOSES]->(b:Baseline) " +
         "WHERE id(cam)=$idCostAccount AND id(b)=$idBaseline " +
         "RETURN cas, [ " +
         "  [ (cas)<-[con:CONSUMES]-(step:Step)-[c2:COMPOSES]->(b) | [con, step, c2] ]" +
         "]"
  )
  Optional<CostAccount> findSnapshotWithStepsByMasterIdAndBaselineId(
    Long idCostAccount,
    Long idBaseline
  );

  @Query("MATCH (a:CostAccount)-[:IS_SNAPSHOT_OF]->(m:CostAccount)<-[i:IS_SNAPSHOT_OF]-(s:CostAccount)-[c:COMPOSES]->(b:Baseline) " +
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

  @Query("MATCH (workpack:Workpack)<-[:APPLIES_TO]-(costAccount:CostAccount) " +
         "WHERE id(workpack)=$idWorkpack " +
         "RETURN costAccount")
  List<CostAccount> findAllByWorkpackId(Long idWorkpack);

}
