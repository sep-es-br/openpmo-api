package br.gov.es.openpmo.dto.issue;

import br.gov.es.openpmo.model.issue.Issue;
import br.gov.es.openpmo.model.issue.StatusOfIssue;
import br.gov.es.openpmo.model.risk.Importance;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class IssueCardDto {

  private final Long id;
  private final String name;
  private final Importance importance;
  private final StatusOfIssue status;

  public IssueCardDto(
    final Long id,
    final String name,
    final Importance importance,
    final StatusOfIssue status
  ) {
    this.id = id;
    this.name = name;
    this.importance = importance;
    this.status = status;
  }

  public static IssueCardDto of(final Issue issue) {
    return new IssueCardDto(
      issue.getId(),
      issue.getName(),
      issue.getImportance(),
      issue.getStatus()
    );
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public Importance getImportance() {
    return this.importance;
  }

  public StatusOfIssue getStatus() {
    return this.status;
  }

}
