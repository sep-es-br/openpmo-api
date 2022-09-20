package br.gov.es.openpmo.dto.issue.response;

import br.gov.es.openpmo.model.issue.response.IssueResponseStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Set;

public class IssueResponseCreateDto {

  private final String name;

  private final String plan;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private final LocalDate date;

  private final IssueResponseStatus status;

  @JsonProperty("idIssue")
  private final Long issueId;

  private final Set<Long> responsible;

  @JsonCreator
  public IssueResponseCreateDto(
    final String name,
    final String plan,
    final LocalDate date,
    final IssueResponseStatus status,
    final Long issueId,
    final Set<Long> responsible
  ) {
    this.name = name;
    this.plan = plan;
    this.date = date;
    this.status = status;
    this.issueId = issueId;
    this.responsible = responsible;
  }

  public String getName() {
    return this.name;
  }

  public String getPlan() {
    return this.plan;
  }

  public LocalDate getDate() {
    return this.date;
  }

  public IssueResponseStatus getStatus() {
    return this.status;
  }

  public Long getIssueId() {
    return this.issueId;
  }

  public Set<Long> getResponsible() {
    return this.responsible;
  }

}
