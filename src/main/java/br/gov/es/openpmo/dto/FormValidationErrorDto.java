package br.gov.es.openpmo.dto;

public class FormValidationErrorDto {

    private String field;
    private String error;

    public FormValidationErrorDto(String field, String error) {
        this.field = field;
        this.error = error;
    }

    public String getField() {
        return field;
    }

    public String getError() {
        return error;
    }
}
