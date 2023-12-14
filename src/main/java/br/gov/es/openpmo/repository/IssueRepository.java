package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.issue.Issue;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRepository extends Neo4jRepository<Issue, Long>, CustomRepository {

  @Query("MATCH (issue:Issue)-[reported:IS_REPORTED_FOR]->(workpack:Workpack) " +
         "OPTIONAL MATCH (issue)-[isTriggeredBy:IS_TRIGGERED_BY]->(risk:Risk) " +
         "WITH *, apoc.text.levenshteinSimilarity(apoc.text.clean(issue.name), apoc.text.clean($term)) AS score " +
         "WHERE id(workpack)=$idWorkpack AND (id(risk)=$idRisk OR $idRisk IS NULL) " +
         "AND ($term is null OR $term = '' OR score > $searchCutOffScore) " +
         "RETURN issue " +
         "ORDER BY score DESC"
  )
  List<Issue> findAllAsIssueCardDto(
    Long idWorkpack,
    Long idRisk,
    String term,
    Double searchCutOffScore
  );

  @Query(
    "MATCH (issue:Issue) " +
    "OPTIONAL MATCH (issue)-[reportedFor:IS_REPORTED_FOR]->(reportedForWorkpack:Workpack)  " +
    "OPTIONAL MATCH (issue)-[triggeredBy:IS_TRIGGERED_BY]->(risk:Risk)  " +
    "OPTIONAL MATCH (risk)-[forSeenOn:IS_FORSEEN_ON]->(forSeenWorkpack)  " +
    "WITH issue, reportedFor, reportedForWorkpack, triggeredBy, risk, forSeenOn, forSeenWorkpack  " +
    "WHERE id(issue)=$id " +
    " OPTIONAL MATCH (issue)<-[addresses:ADDRESSES]-(issueResponse:IssueResponse) " +
    " OPTIONAL MATCH (risk)<-[mitigates:MITIGATES]-(riskResponse) " +
    "RETURN issue, reportedFor, reportedForWorkpack, triggeredBy, risk, forSeenOn, forSeenWorkpack, [ " +
    "   [ [addresses, issueResponse] ],  " +
    "   [ [mitigates, riskResponse] ]  " +
    "]"
  )
  Optional<Issue> findIssueDetailById(Long id);

  @Query("match (i:Issue)-[:IS_REPORTED_FOR]->(w:Workpack) " +
         "where id(i)=$issueId " +
         "return id(w) ")
  Optional<Long> findWorkpackIdByIssueId(Long issueId);

}
