package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.utils.ApplicationMessage;
import io.swagger.annotations.ApiModelProperty;

public class ResponseBaseWorkpackDetail {

  @ApiModelProperty(position = 2)
  private String message;
  @ApiModelProperty(position = 1)
  private boolean success = true;
  @ApiModelProperty(position = 3)
  private WorkpackDetailDto data;


  public static ResponseBaseWorkpackDetail of(final WorkpackDetailDto data) {
    return new ResponseBaseWorkpackDetail()
      .setData(data)
      .setMessage(ApplicationMessage.OPERATION_SUCCESS)
      .setSuccess(true);
  }

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
