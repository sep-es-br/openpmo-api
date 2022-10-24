package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.issue.Issue;
import br.gov.es.openpmo.model.issue.response.IssueResponse;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface IssueResponseRepository extends Neo4jRepository<IssueResponse, Long> {

  @Query("MATCH (issue:Issue)<-[addresses:ADDRESSES]-(response:IssueResponse) " +
         "WHERE id(issue)=$idIssue " +
         "RETURN response")
  Collection<IssueResponse> findAllByIssueId(Long idIssue);

  @Query("match (issue:Issue)<-[addresses:ADDRESSES]-(response:IssueResponse) " +
         "where id(response)=$issueResponseId " +
         "return issue ")
  Optional<Issue> findIssueByIssueResponseId(Long issueResponseId);

}
