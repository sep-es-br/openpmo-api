package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.indicators.Indicator;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface IndicatorRepository extends Neo4jRepository<Indicator, Long>, CustomRepository {


    @Query("match (w:Workpack{deleted:false,canceled:false}) " +
            "where id(w)=$workpackId " +
            "optional match (w)<-[:RELATED_TO]-(i:Indicator) " +
            "with *, apoc.text.levenshteinSimilarity(apoc.text.clean(i.name), apoc.text.clean($term)) AS score " +
            "where ($term is null OR $term = '' OR score > $searchCutOffScore) " +
            "return i " +
            "order by score desc"
    )
    Set<Indicator> findAll(
            Long workpackId,
            String term,
            Double searchCutOffScore
    );

    @Query("MATCH (indicator:Indicator) " +
            "WHERE id(indicator) = $indicatorId " +
            "OPTIONAL MATCH (indicator)-[isReportedFor:IS_FORSEEN_ON]->(workpack:Workpack {deleted: false, canceled: false}) " +
            "RETURN indicator, isReportedFor, workpack")
    Optional<Indicator> findIndicatorDetailById(Long indicatorId);
}
