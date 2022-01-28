package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.relations.BelongsTo;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BelongsToRepository extends Neo4jRepository<BelongsTo, Long> {

  @Query("match (w:Workpack)-[b:BELONGS_TO]->(p:Plan) " +
      "where id(w)=$idWorkpack and id(p)=$idPlan " +
      "return count(b)>0")
  boolean workpackBelongsToPlan(Long idWorkpack, Long idPlan);

  @Query("match (w:Workpack)-[b:BELONGS_TO]->(:Plan) " +
      "where id(w)=$workpackId " +
      "detach delete b")
  void deleteByWorkpackId(Long workpackId);

}
