package br.gov.es.openpmo.dto.preprojects;

import br.gov.es.openpmo.enumerator.CriteriaOperation;
import javax.validation.constraints.NotNull;

public class UpdatePreProjectModelRequest {

  @NotNull
  private Boolean active;

  @NotNull
  private CriteriaOperation operation;

  public Boolean getActive() {
    return this.active;
  }

  public void setActive(final Boolean active) {
    this.active = active;
  }

  public CriteriaOperation getOperation() {
    return this.operation;
  }

  public void setOperation(final CriteriaOperation operation) {
    this.operation = operation;
  }

}
