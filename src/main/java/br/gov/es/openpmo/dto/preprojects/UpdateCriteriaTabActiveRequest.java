package br.gov.es.openpmo.dto.preprojects;

import javax.validation.constraints.NotNull;

public class UpdateCriteriaTabActiveRequest {

  @NotNull
  private Boolean active;

  public Boolean getActive() {
    return this.active;
  }

  public void setActive(final Boolean active) {
    this.active = active;
  }
}
