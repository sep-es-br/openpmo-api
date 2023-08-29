package br.gov.es.openpmo.dto.person;

import com.fasterxml.jackson.annotation.JsonCreator;

public class LocalWorkRequest {

  private final Long idOffice;
  private final Long idPlan;
  private final Long idWorkpack;
  private final Long idWorkpackModelLinked;

  @JsonCreator
  public LocalWorkRequest(Long idOffice, Long idPlan, Long idWorkpack, Long idWorkpackModelLinked) {
    this.idOffice = idOffice;
    this.idPlan = idPlan;
    this.idWorkpack = idWorkpack;
    this.idWorkpackModelLinked = idWorkpackModelLinked;
  }

  public Long getIdOffice() {
    return idOffice;
  }

  public Long getIdPlan() {
    return idPlan;
  }

  public Long getIdWorkpack() {
    return idWorkpack;
  }

  public Long getIdWorkpackModelLinked() {
    return idWorkpackModelLinked;
  }
}
