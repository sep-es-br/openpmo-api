package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.relations.Consumes;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConsumesRepository extends Neo4jRepository<Consumes, Long> {

  @Query("MATCH (c:CostAccount)<-[co:CONSUMES]-(s:Step)"
         + ", (c)-[:APPLIES_TO]->(w:Workpack) "
         + " WHERE id(w) = $idWorkpack AND (id(c) = $id OR $id IS NULL) RETURN c,co,s,w")
  List<Consumes> findAllByIdAndWorkpack(@Param("id") Long id, @Param("idWorkpack") Long idWorkpack);

  @Query("MATCH (c:CostAccount)<-[co:CONSUMES]-(s:Step)-[cp:COMPOSES]->(sc:Schedule)"
         + " WHERE id(c) = $id RETURN c,co,s ")
  List<Consumes> findAllByIdCostAccount(@Param("id") Long id);

  @Query("MATCH (ca:CostAccount)<-[c:CONSUMES]-(st:Step) " +
         "WHERE id(ca)=$idCostAccount AND id(st)=$idStep " +
         "RETURN ca,c,st")
  Optional<Consumes> findByStepIdAndCostAccountId(Long idStep, Long idCostAccount);

  @Query(
    "MATCH (master:Step)<-[:IS_SNAPSHOT_OF]-(snapshot:Step)-[:COMPOSES]->(baseline:Baseline) " +
    "WHERE id(master)=$idStepMaster AND id(baseline)=$idBaseline " +
    "MATCH (snapshot)-[consumes:CONSUMES]->(costAccount:CostAccount)-[:COMPOSES]->(baseline) " +
    "RETURN snapshot, consumes, costAccount"
  )
  List<Consumes> findAllSnapshotConsumesOfStepMaster(Long idBaseline, Long idStepMaster);


}
