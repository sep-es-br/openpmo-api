package br.gov.es.openpmo.dto;

import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelDetailDto;
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

    public ResponseBaseWorkpackModelDetail setMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public ResponseBaseWorkpackModelDetail setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public WorkpackModelDetailDto getData() {
        return this.data;
    }

    public ResponseBaseWorkpackModelDetail setData(WorkpackModelDetailDto entidade) {
        this.data = entidade;
       return this;
    }
}