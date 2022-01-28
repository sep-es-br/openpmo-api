package br.gov.es.openpmo.dto.dashboards.datasheet;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DatasheetStakeholderResponse {

  @JsonProperty("person")
  private final DatasheetActor actor;

  private final String role;

  public DatasheetStakeholderResponse(final DatasheetActor actor, final String role) {
    this.actor = actor;
    this.role = role;
  }

  public DatasheetActor getActor() {
    return this.actor;
  }

  public String getRole() {
    return this.role;
  }

}
