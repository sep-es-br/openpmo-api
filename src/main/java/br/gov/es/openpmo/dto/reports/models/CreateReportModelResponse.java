package br.gov.es.openpmo.dto.reports.models;

public class CreateReportModelResponse {

  private final Long id;

  public CreateReportModelResponse(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

}
