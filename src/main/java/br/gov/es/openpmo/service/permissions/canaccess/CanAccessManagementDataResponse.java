package br.gov.es.openpmo.service.permissions.canaccess;

public class CanAccessManagementDataResponse implements ICanAccessManagementDataResponse {

  private final Boolean edit;
  private final Boolean read;

  public CanAccessManagementDataResponse(
    final Boolean edit,
    final Boolean read
  ) {
    this.edit = edit;
    this.read = read;
  }

  @Override
  public Boolean getEdit() {
    return this.edit;
  }

  @Override
  public Boolean getRead() {
    return this.read;
  }


}
