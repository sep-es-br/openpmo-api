package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.process.Process;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessRepository extends Neo4jRepository<Process, Long>, CustomRepository {

  @Query("MATCH (process:Process)-[:IS_BELONGS_TO]->(workpack:Workpack) " +
         "WHERE id(workpack)=$idWorkpack " +
         "RETURN process"
  )
  List<Process> findAllByWorkpack(Long idWorkpack);
}
