package br.gov.es.openpmo.dto.workpackmodel.details;

import io.swagger.annotations.ApiModelProperty;

public class ResponseBaseWorkpackModelDetail {

  @ApiModelProperty(position = 2)
  private String message;

  @ApiModelProperty(position = 1)
  private boolean success = true;

  @ApiModelProperty(position = 3)
  private WorkpackModelDetailDto data;

  public String getMessage() {
    return this.message;
  }

  public ResponseBaseWorkpackModelDetail setMessage(final String message) {
    this.message = message;
    return this;
  }

  public boolean isSuccess() {
    return this.success;
  }

  public ResponseBaseWorkpackModelDetail setSuccess(final boolean success) {
    this.success = success;
    return this;
  }

  public WorkpackModelDetailDto getData() {
    return this.data;
  }

  public ResponseBaseWorkpackModelDetail setData(final WorkpackModelDetailDto entidade) {
    this.data = entidade;
    return this;
  }

}
