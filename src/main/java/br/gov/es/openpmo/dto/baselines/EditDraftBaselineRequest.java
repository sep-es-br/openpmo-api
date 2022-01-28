package br.gov.es.openpmo.dto.baselines;

import com.fasterxml.jackson.annotation.JsonCreator;

public class EditDraftBaselineRequest {

  private final String name;

  private final String message;

  private final String description;

  @JsonCreator
  public EditDraftBaselineRequest(String name, String message, String description) {
    this.name = name;
    this.message = message;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public String getMessage() {
    return message;
  }

  public String getDescription() {
    return description;
  }

}
