package br.gov.es.openpmo.dto.workpack;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class ResponseBaseWorkpack {

  @ApiModelProperty(position = 2)
  private String message;

  @ApiModelProperty(position = 1)
  private boolean success = true;

  @ApiModelProperty(position = 3)
  private List<WorkpackDetailDto> data;

  public String getMessage() {
    return this.message;
  }

  public ResponseBaseWorkpack setMessage(final String message) {
    this.message = message;
    return this;
  }

  public boolean isSuccess() {
    return this.success;
  }

  public ResponseBaseWorkpack setSuccess(final boolean success) {
    this.success = success;
    return this;
  }

  public List<WorkpackDetailDto> getData() {
    return this.data;
  }

  public ResponseBaseWorkpack setData(final List<WorkpackDetailDto> entidade) {
    this.data = entidade;
    return this;
  }

}
