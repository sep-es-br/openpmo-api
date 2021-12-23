package br.gov.es.openpmo.dto.baselines;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

public class SubmitBaselineRequest {

  private final List<UpdateRequest> updates;

  @JsonCreator
  public SubmitBaselineRequest(final List<UpdateRequest> updates) {
    this.updates = updates;
  }

  public List<UpdateRequest> getUpdates() {
    return this.updates;
  }

}
