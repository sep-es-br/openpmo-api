package br.gov.es.openpmo.dto.baselines;

import com.fasterxml.jackson.annotation.JsonCreator;

public class EditDraftBaselineRequest {

  private final String name;

  private final String message;

  private final String description;

  @JsonCreator
  public EditDraftBaselineRequest(final String name, final String message, final String description) {
    this.name = name;
    this.message = message;
    this.description = description;
  }

  public String getName() {
    return this.name;
  }

  public String getMessage() {
    return this.message;
  }

  public String getDescription() {
    return this.description;
  }

}
