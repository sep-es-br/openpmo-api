package br.gov.es.openpmo.dto;

import br.gov.es.openpmo.dto.workpack.WorkpackDetailDto;
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

    public ResponseBaseWorkpackDetail setMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public ResponseBaseWorkpackDetail setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public WorkpackDetailDto getData() {
        return this.data;
    }

    public ResponseBaseWorkpackDetail setData(WorkpackDetailDto entidade) {
        this.data = entidade;
       return this;
    }
}