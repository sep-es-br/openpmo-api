package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.indicators.Indicator;
import br.gov.es.openpmo.model.indicators.PeriodGoal;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Query("MATCH (period:PeriodGoal)<-[:HAS_ACHIEVED_GOAL]-(indicator:Indicator) " +
            "WHERE id(indicator) = $indicatorId " +
            "RETURN collect(period) AS achievedGoals")
    Optional<List<PeriodGoal>> findAchievedGoalsByIndicatorId(Long indicatorId);

    @Query("MATCH (period:PeriodGoal)<-[:HAS_EXPECTED_GOAL]-(indicator:Indicator) " +
            "WHERE id(indicator) = $indicatorId " +
            "RETURN collect(period) as expectedGoals")
    Optional<List<PeriodGoal>> findExpectedGoalsByIndicatorId(Long indicatorId);

    @Query("MATCH (project:Workpack {deleted: false})<-[:IS_IN*]-(children:Workpack {deleted: false}) " +
            "WHERE id(project) = $idWorkpack " +
            "AND ANY(label IN labels(children) WHERE label IN ['Deliverable']) " +
            "MATCH (children)<-[:FEATURES]-(schedule:Schedule) " +
            "WITH toInteger(substring(schedule.start, 0, 4)) AS startYear, " +
            "toInteger(substring(schedule.end, 0, 4)) AS endYear " +
            "WITH range(startYear, endYear) AS years " +
            "UNWIND years AS year " +
            "RETURN DISTINCT year " +
            "ORDER BY year ASC")
    List<Integer> findUniqueYearsByProjectId(Long idWorkpack);

    @Query("MATCH(o:Organization)-[:IS_REGISTERED_IN]->(office:Office) " +
            "WHERE id(office) = $idOffice " +
            "RETURN o.name")
    List<String> findAllOrganizationFromOffice(Long idOffice);
}
