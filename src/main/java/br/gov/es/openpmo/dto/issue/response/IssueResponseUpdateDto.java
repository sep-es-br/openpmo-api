package br.gov.es.openpmo.dto.issue.response;

import br.gov.es.openpmo.model.issue.response.IssueResponseStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class IssueResponseUpdateDto {

  private final Long id;

  @NotNull
  @NotEmpty
  private final String name;

  @NotNull
  @NotEmpty
  private final String plan;

  @JsonFormat(pattern = "dd/MM/yyyy")
  private final LocalDate date;

  @NotNull
  private final IssueResponseStatus status;

  public IssueResponseUpdateDto(
      final Long id,
      final String name,
      final String plan,
      final LocalDate date,
      final IssueResponseStatus status
  ) {
    this.id = id;
    this.name = name;
    this.plan = plan;
    this.date = date;
    this.status = status;
  }

  public Long getId() {
    return this.id;
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

}
