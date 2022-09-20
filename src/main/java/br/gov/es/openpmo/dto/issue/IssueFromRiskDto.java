package br.gov.es.openpmo.dto.issue;

import com.fasterxml.jackson.annotation.JsonCreator;

public class IssueFromRiskDto {

  private final Long idRisk;

  @JsonCreator
  public IssueFromRiskDto(final Long idRisk) {
    this.idRisk = idRisk;
  }

  public Long getIdRisk() {
    return this.idRisk;
  }

}
