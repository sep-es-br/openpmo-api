package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.office.UnitMeasure;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UnitMeasureRepository extends Neo4jRepository<UnitMeasure, Long>, CustomRepository {

  @Query(
    "MATCH (u:UnitMeasure)-[a:AVAILABLE_IN]->(o:Office) " +
    "WITH *, apoc.text.levenshteinSimilarity(apoc.text.clean(u.name + u.fullName), apoc.text.clean($term)) AS score " +
    "WHERE id(o) = $idOffice AND ($term IS NULL OR $term = '' OR score > $searchCutOffScore) " +
    "RETURN u, a, o " +
    "ORDER BY score DESC"
  )
  List<UnitMeasure> findByOffice(
    @Param("idOffice") Long idOffice,
    @Param("term") String term,
    @Param("searchCutOffScore") Double searchCutOffScore
  );

}
