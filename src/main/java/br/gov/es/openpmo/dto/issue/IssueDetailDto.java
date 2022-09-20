package br.gov.es.openpmo.dto.issue;


import br.gov.es.openpmo.dto.issue.response.IssueResponseDetailDto;
import br.gov.es.openpmo.dto.risk.RiskDetailDto;
import br.gov.es.openpmo.model.issue.Issue;
import br.gov.es.openpmo.model.issue.NatureOfIssue;
import br.gov.es.openpmo.model.issue.StatusOfIssue;
import br.gov.es.openpmo.model.risk.Importance;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static br.gov.es.openpmo.utils.ApplicationMessage.ISSUE_NOT_NULL;

public class IssueDetailDto {


  private final Long id;
  private final String name;
  private final String description;
  private final Importance importance;
  private final StatusOfIssue status;
  private final NatureOfIssue nature;
  private final Long idWorkpack;
  private final RiskDetailDto triggeredBy;
  private final Set<IssueResponseDetailDto> responses;

  public IssueDetailDto(
    final Long id,
    final String name,
    final String description,
    final Importance importance,
    final StatusOfIssue status,
    final NatureOfIssue nature,
    final Long idWorkpack,
    final RiskDetailDto triggeredBy,
    final Set<IssueResponseDetailDto> responses
  ) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.importance = importance;
    this.status = status;
    this.nature = nature;
    this.idWorkpack = idWorkpack;
    this.triggeredBy = triggeredBy;
    this.responses = responses;
  }

  public static IssueDetailDto of(final Issue issue) {
    Objects.requireNonNull(issue, ISSUE_NOT_NULL);
    return new IssueDetailDto(
      issue.getId(),
      issue.getName(),
      issue.getDescription(),
      issue.getImportance(),
      issue.getStatus(),
      issue.getNature(),
      issue.getWorkpackId(),
      Optional.ofNullable(issue.getTriggeredBy())
        .map(RiskDetailDto::of)
        .orElse(null),
      issue.getResponsesAsDetailDto()
    );
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public Importance getImportance() {
    return this.importance;
  }

  public StatusOfIssue getStatus() {
    return this.status;
  }

  public NatureOfIssue getNature() {
    return this.nature;
  }

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public RiskDetailDto getTriggeredBy() {
    return this.triggeredBy;
  }

  public Set<IssueResponseDetailDto> getResponses() {
    return this.responses;
  }

}
