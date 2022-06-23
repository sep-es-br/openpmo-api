package br.gov.es.openpmo.dto.filter;

import br.gov.es.openpmo.model.filter.CustomFilterEnum;

import javax.validation.Valid;

public class CustomFilterCreateRequest {
  @Valid private final CustomFilterDto request;
  private final CustomFilterEnum customFilterEnum;
  private final Long idWorkPackModel;
  private final Long idUser;

  public CustomFilterCreateRequest(
    @Valid final CustomFilterDto request,
    final CustomFilterEnum customFilterEnum,
    final Long idWorkPackModel,
    final Long idUser
  ) {
    this.request = request;
    this.customFilterEnum = customFilterEnum;
    this.idWorkPackModel = idWorkPackModel;
    this.idUser = idUser;
  }

  public Long getIdUser() {
    return this.idUser;
  }

  public CustomFilterDto getRequest() {
    return this.request;
  }

  public CustomFilterEnum getCustomFilterEnum() {
    return this.customFilterEnum;
  }

  public Long getIdWorkPackModel() {
    return this.idWorkPackModel;
  }
}
