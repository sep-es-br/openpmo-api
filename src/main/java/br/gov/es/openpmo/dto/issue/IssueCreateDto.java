package br.gov.es.openpmo.dto.issue;

import br.gov.es.openpmo.model.issue.NatureOfIssue;
import br.gov.es.openpmo.model.issue.StatusOfIssue;
import br.gov.es.openpmo.model.risk.Importance;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class IssueCreateDto {

  @NotEmpty
  @NotNull
  @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres")
  private final String name;

  @NotEmpty
  @NotNull
  @Size(max = 600, message = "A descrição deve ter no máximo 600 caracteres")
  private final String description;

  @NotNull
  private final Importance importance;

  @NotNull
  private final StatusOfIssue status;

  @NotNull
  private final NatureOfIssue nature;

  @NotNull
  private final Long idWorkpack;

  private final Long triggeredBy;

  @JsonCreator
  public IssueCreateDto(
    final String name,
    final String description,
    final Importance importance,
    final StatusOfIssue status,
    final NatureOfIssue nature,
    final Long idWorkpack,
    final Long triggeredBy
  ) {
    this.name = name;
    this.description = description;
    this.importance = importance;
    this.status = status;
    this.nature = nature;
    this.idWorkpack = idWorkpack;
    this.triggeredBy = triggeredBy;
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

  public Long getTriggeredBy() {
    return this.triggeredBy;
  }

}
