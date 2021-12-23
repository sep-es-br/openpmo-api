package br.gov.es.openpmo.dto;

import br.gov.es.openpmo.utils.ApplicationMessage;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Collection;

@ApiModel(description = "Model responsável por encapsular todos responses da api.")
@JsonPropertyOrder({"success", "message", "data", "paginacao"})
public class ResponseBaseItens<T> {

  @ApiModelProperty(position = 2)
  private String message;
  @ApiModelProperty(position = 1)
  private boolean success = true;
  @ApiModelProperty(position = 3)
  private Collection<T> data;

  public static <T> ResponseBaseItens<T> of(final Collection<T> data) {
    return new ResponseBaseItens<T>()
      .setData(data)
      .setMessage(ApplicationMessage.OPERATION_SUCCESS);
  }

  public String getMessage() {
    return this.message;
  }

  public ResponseBaseItens<T> setMessage(final String message) {
    this.message = message;
    return this;
  }

  public boolean isSuccess() {
    return this.success;
  }

  public ResponseBaseItens<T> setSuccess(final boolean success) {
    this.success = success;
    return this;
  }

  public Collection<T> getData() {
    return this.data;
  }

  public ResponseBaseItens<T> setData(final Collection<T> entidade) {
    this.data = entidade;
    return this;
  }
}
