package br.gov.es.openpmo.dto.baselines;

import com.fasterxml.jackson.annotation.JsonCreator;

public class IncludeBaselineRequest {

  private final String name;

  private final String message;

  private final String description;

  private Long idWorkpack;

  @JsonCreator
  public IncludeBaselineRequest(final Long idWorkpack, final String name, final String message, final String description) {
    this.idWorkpack = idWorkpack;
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

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public void setIdWorkpack(final Long idWorkpack) {
    this.idWorkpack = idWorkpack;
  }

}
