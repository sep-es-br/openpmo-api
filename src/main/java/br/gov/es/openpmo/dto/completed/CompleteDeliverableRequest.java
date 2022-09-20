package br.gov.es.openpmo.dto.completed;

import com.fasterxml.jackson.annotation.JsonCreator;

public class CompleteDeliverableRequest {

  private final Boolean completed;

  @JsonCreator
  public CompleteDeliverableRequest(final Boolean completed) {
    this.completed = completed;
  }

  public Boolean getCompleted() {
    return this.completed;
  }

}
