package br.gov.es.openpmo.dto;

import br.gov.es.openpmo.utils.ApplicationMessage;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Model responsável por encapsular todos responses da api.")
@JsonPropertyOrder({"success", "message", "data", "paginacao"})
public class ResponseBase<T> {

  @ApiModelProperty(position = 2)
  private String message;

  @ApiModelProperty(position = 1)
  private boolean success = true;

  @ApiModelProperty(position = 3)
  private T data;

  public static <T> ResponseBase<T> of(final T data) {
    return new ResponseBase<T>()
      .setData(data)
      .setMessage(ApplicationMessage.OPERATION_SUCCESS)
      .setSuccess(true);
  }

  public static <T> ResponseBase<T> of() {
    return new ResponseBase<T>()
      .setData(null)
      .setMessage(ApplicationMessage.OPERATION_SUCCESS)
      .setSuccess(true);
  }

  public static ResponseBase<Void> success() {
    final ResponseBase<Void> responseBase = new ResponseBase<>();
    responseBase.setSuccess(true);
    return responseBase;
  }

  public String getMessage() {
    return this.message;
  }

  public ResponseBase<T> setMessage(final String message) {
    this.message = message;
    return this;
  }

  public boolean isSuccess() {
    return this.success;
  }

  public ResponseBase<T> setSuccess(final boolean success) {
    this.success = success;
    return this;
  }

  public T getData() {
    return this.data;
  }

  public ResponseBase<T> setData(final T entidade) {
    this.data = entidade;
    return this;
  }

}
