package br.gov.es.openpmo.dto.baselines;

import br.gov.es.openpmo.model.baselines.Status;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class GetAllBaselinesResponse {

  private final Long id;

  private final Long idWorkpack;

  private final String name;

  private final String projectName;

  private final Status status;

  private final String description;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private final LocalDateTime activationDate;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private final LocalDateTime proposalDate;

  private final String message;

  private final boolean cancelation;

  private final boolean active;

  public GetAllBaselinesResponse(
          final Long id,
          final Long idWorkpack,
          final String name,
          final String projectName,
          final Status status,
          final String description,
          final LocalDateTime activationDate,
          final LocalDateTime proposalDate,
          final String message,
          final boolean cancelation,
          final boolean active
  ) {
    this.id = id;
    this.idWorkpack = idWorkpack;
    this.name = name;
    this.projectName = projectName;
    this.status = status;
    this.description = description;
    this.activationDate = activationDate;
    this.proposalDate = proposalDate;
    this.message = message;
    this.cancelation = cancelation;
    this.active = active;
  }

  public String getName() {
    return this.name;
  }

  public Status getStatus() {
    return this.status;
  }

  public String getDescription() {
    return this.description;
  }

  public LocalDateTime getActivationDate() {
    return this.activationDate;
  }

  public LocalDateTime getProposalDate() {
    return this.proposalDate;
  }

  public String getMessage() {
    return this.message;
  }

  public boolean isCancelation() {
    return this.cancelation;
  }

  public boolean isActive() {
    return this.active;
  }

  public Long getId() {
    return this.id;
  }

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public String getProjectName() {
    return this.projectName;
  }
}
