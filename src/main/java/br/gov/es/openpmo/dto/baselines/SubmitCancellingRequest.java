package br.gov.es.openpmo.dto.baselines;

import br.gov.es.openpmo.model.baselines.Status;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotNull;

public class SubmitCancellingRequest {

  @NotNull
  private final Long idWorkpack;

  @NotNull
  private final String name;

  @NotNull
  private final String description;

  @NotNull
  private final String message;

  @NotNull
  private final Boolean cancelation;

  @NotNull
  private final Status status;

  @JsonCreator
  public SubmitCancellingRequest(
    final Long idWorkpack,
    final String name,
    final String description,
    final String message,
    final Boolean cancelation,
    final Status status
  ) {
    this.idWorkpack = idWorkpack;
    this.name = name;
    this.description = description;
    this.message = message;
    this.cancelation = cancelation;
    this.status = status;
  }

  public Status getStatus() {
    return this.status;
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public String getMessage() {
    return this.message;
  }

  public Boolean getCancelation() {
    return this.cancelation;
  }

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

}
