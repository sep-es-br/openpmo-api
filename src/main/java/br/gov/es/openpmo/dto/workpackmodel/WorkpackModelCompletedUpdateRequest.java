package br.gov.es.openpmo.dto.workpackmodel;

import com.fasterxml.jackson.annotation.JsonCreator;

public class WorkpackModelCompletedUpdateRequest {

  private final Boolean completed;

  @JsonCreator
  public WorkpackModelCompletedUpdateRequest(final Boolean completed) {
    this.completed = completed;
  }

  public Boolean getCompleted() {
    return this.completed;
  }
}
