package br.gov.es.openpmo.dto.issue;

import br.gov.es.openpmo.model.issue.NatureOfIssue;
import br.gov.es.openpmo.model.issue.StatusOfIssue;
import br.gov.es.openpmo.model.risk.Importance;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class IssueUpdateDto {

  @NotEmpty
  @NotNull
  private final String name;

  @NotEmpty
  @NotNull
  private final String description;

  @NotNull
  private final Importance importance;

  @NotNull
  private final StatusOfIssue status;

  @NotNull
  private final NatureOfIssue nature;

  private final Long id;

  @JsonCreator
  public IssueUpdateDto(
    final String name,
    final String description,
    final Importance importance,
    final StatusOfIssue status,
    final NatureOfIssue nature,
    final Long id
  ) {
    this.name = name;
    this.description = description;
    this.importance = importance;
    this.status = status;
    this.nature = nature;
    this.id = id;
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

  public Long getId() {
    return this.id;
  }

}
