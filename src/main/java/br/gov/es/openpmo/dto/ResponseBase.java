package br.gov.es.openpmo.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Model responsável por encapsular todos responses da api.")
@JsonPropertyOrder({ "sucess", "message", "data", "paginacao" })
public class ResponseBase<T> {

    @ApiModelProperty(position = 2)
    private String message;
    @ApiModelProperty(position = 1)
    private boolean success = true;
    @ApiModelProperty(position = 3)
    private T data;

    public String getMessage() {
        return this.message;
    }

    public ResponseBase<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public ResponseBase<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public T getData() {
        return this.data;
    }

    public ResponseBase<T> setData(T entidade) {
        this.data = entidade;
       return this;
    }
}