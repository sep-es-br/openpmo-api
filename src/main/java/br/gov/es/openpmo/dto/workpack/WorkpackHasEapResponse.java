package br.gov.es.openpmo.dto.workpack;

public class WorkpackHasEapResponse {

  private final Boolean hasEap;

  public WorkpackHasEapResponse(final Boolean hasEap) {
    this.hasEap = hasEap;
  }

  public Boolean getHasEap() {
    return this.hasEap;
  }

}
