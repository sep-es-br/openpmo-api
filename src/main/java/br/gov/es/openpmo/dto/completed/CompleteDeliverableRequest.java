package br.gov.es.openpmo.dto.completed;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class CompleteDeliverableRequest {

  private final Boolean completed;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private final LocalDate date;

  @JsonCreator
  public CompleteDeliverableRequest(
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
