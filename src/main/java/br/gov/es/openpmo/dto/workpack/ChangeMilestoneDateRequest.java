package br.gov.es.openpmo.dto.workpack;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.LocalDate;

public class ChangeMilestoneDateRequest {

  private final LocalDate date;

  private final String reason;

  @JsonCreator
  public ChangeMilestoneDateRequest(
    LocalDate date,
    String reason
  ) {
    this.date = date;
    this.reason = reason;
  }

  public LocalDate getDate() {
    return date;
  }

  public String getReason() {
    return reason;
  }

}
