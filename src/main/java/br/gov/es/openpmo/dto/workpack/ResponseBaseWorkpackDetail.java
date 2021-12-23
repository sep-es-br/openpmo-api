package br.gov.es.openpmo.dto.workpack;

import io.swagger.annotations.ApiModelProperty;

public class ResponseBaseWorkpackDetail {

  @ApiModelProperty(position = 2)
  private String message;
  @ApiModelProperty(position = 1)
  private boolean success = true;
  @ApiModelProperty(position = 3)
  private WorkpackDetailDto data;

  public String getMessage() {
    return this.message;
  }

  public ResponseBaseWorkpackDetail setMessage(final String message) {
    this.message = message;
    return this;
  }

  public boolean isSuccess() {
    return this.success;
  }

  public ResponseBaseWorkpackDetail setSuccess(final boolean success) {
    this.success = success;
    return this;
  }

  public WorkpackDetailDto getData() {
    return this.data;
  }

  public ResponseBaseWorkpackDetail setData(final WorkpackDetailDto entidade) {
    this.data = entidade;
    return this;
  }
}
