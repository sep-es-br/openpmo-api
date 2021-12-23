package br.gov.es.openpmo.model.risk;

public enum StatusOfRisk {

  OPEN("open"),
  NOT_GONNA_HAPPEN("not gonna happen"),
  HAPPENED("happened");

  private final String status;

  StatusOfRisk(final String status) {
    this.status = status;
  }

  public String getStatus() {
    return this.status;
  }
}
