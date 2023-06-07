package br.gov.es.openpmo.dto.completed;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class CompleteWorkpackRequest {

  private final Boolean completed;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private final LocalDate date;

  @JsonCreator
  public CompleteWorkpackRequest(
    final Boolean completed,
    final LocalDate date
  ) {
    this.completed = completed;
    this.date = date;
  }

  public Boolean getCompleted() {
    return this.completed;
  }

  public LocalDate getDate() {
    return date;
  }

}
