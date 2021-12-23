package br.gov.es.openpmo.dto;

public class FormValidationErrorDto {

  private final String field;
  private final String error;

  public FormValidationErrorDto(final String field, final String error) {
    this.field = field;
    this.error = error;
  }

  public String getField() {
    return this.field;
  }

  public String getError() {
    return this.error;
  }
}
