package br.gov.es.openpmo.dto.person.favorite;

import br.gov.es.openpmo.utils.ApplicationMessage;

import javax.validation.constraints.NotNull;

public class WorkpackFavoritedRequest {

  @NotNull(message = ApplicationMessage.PLAN_NOT_FOUND)
  private Long idPlan;

  public Long getIdPlan() {
    return this.idPlan;
  }

  public void setIdPlan(final Long idPlan) {
    this.idPlan = idPlan;
  }

}
