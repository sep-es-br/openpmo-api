package br.gov.es.openpmo.dto.workpackmodel;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class ResponseBaseWorkpackModel {

  @ApiModelProperty(position = 2)
  private String message;

  @ApiModelProperty(position = 1)
  private boolean success = true;

  @ApiModelProperty(position = 3)
  private List<WorkpackModelDto> data;

  public String getMessage() {
    return this.message;
  }

  public ResponseBaseWorkpackModel setMessage(final String message) {
    this.message = message;
    return this;
  }

  public boolean isSuccess() {
    return this.success;
  }

  public ResponseBaseWorkpackModel setSuccess(final boolean success) {
    this.success = success;
    return this;
  }

  public List<WorkpackModelDto> getData() {
    return this.data;
  }

  public ResponseBaseWorkpackModel setData(final List<WorkpackModelDto> entidade) {
    this.data = entidade;
    return this;
  }

}
