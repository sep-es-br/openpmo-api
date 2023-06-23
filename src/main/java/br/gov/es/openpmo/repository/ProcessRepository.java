package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.process.Process;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessRepository extends Neo4jRepository<Process, Long>, CustomRepository {

  @Query("MATCH (process:Process)-[:IS_BELONGS_TO]->(workpack:Workpack{deleted:false}) " +
          "WITH *, " +
          "apoc.text.levenshteinSimilarity(apoc.text.clean(process.name), apoc.text.clean($term)) AS nameScore, " +
          "apoc.text.levenshteinSimilarity(apoc.text.clean(process.subject), apoc.text.clean($term)) AS subjectScore " +
          "WITH *, CASE WHEN nameScore > subjectScore THEN nameScore ELSE subjectScore END AS score " +
          "WHERE id(workpack)=$idWorkpack AND ($term IS NULL OR $term = '' OR score > $searchCutOffScore) " +
          "RETURN process " +
          "ORDER BY score, process.name DESC"
  )
  List<Process> findAllByWorkpack(
          Long idWorkpack,
          String term,
          Double searchCutOffScore
  );



}
