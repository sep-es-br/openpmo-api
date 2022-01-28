package br.gov.es.openpmo.dto.workpack;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class EndDeliverableManagementRequest {

  @JsonFormat(pattern = "yyyy-MM-dd")
  private final LocalDate endManagementDate;

  @JsonCreator
  public EndDeliverableManagementRequest(final LocalDate endManagementDate) {
    this.endManagementDate = endManagementDate;
  }

  public LocalDate getEndManagementDate() {
    return this.endManagementDate;
  }
}
