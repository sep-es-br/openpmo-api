package br.gov.es.openpmo.dto;

import java.util.List;

import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelDto;
import io.swagger.annotations.ApiModelProperty;

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

    public ResponseBaseWorkpackModel setMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public ResponseBaseWorkpackModel setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public List<WorkpackModelDto> getData() {
        return this.data;
    }

    public ResponseBaseWorkpackModel setData(List<WorkpackModelDto> entidade) {
        this.data = entidade;
       return this;
    }
}