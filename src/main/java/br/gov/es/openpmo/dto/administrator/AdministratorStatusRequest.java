package br.gov.es.openpmo.dto.administrator;

import javax.validation.constraints.NotNull;

public class AdministratorStatusRequest {

  @NotNull
  private final Boolean administrator;

  public AdministratorStatusRequest(
    final Long id,
    final boolean administrator
  ) {
    this.administrator = administrator;
  }

  public Boolean getAdministrator() {
    return this.administrator;
  }

}
