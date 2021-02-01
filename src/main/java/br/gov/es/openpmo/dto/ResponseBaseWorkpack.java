package br.gov.es.openpmo.dto;

import java.util.List;

import br.gov.es.openpmo.dto.workpack.WorkpackDetailDto;
import io.swagger.annotations.ApiModelProperty;

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

    public ResponseBaseWorkpack setMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public ResponseBaseWorkpack setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public List<WorkpackDetailDto> getData() {
        return this.data;
    }

    public ResponseBaseWorkpack setData(List<WorkpackDetailDto> entidade) {
        this.data = entidade;
       return this;
    }
}