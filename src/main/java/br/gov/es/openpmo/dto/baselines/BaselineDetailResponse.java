package br.gov.es.openpmo.dto.baselines;

import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.baselines.Status;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class BaselineDetailResponse {

  private final Long id;
  private final Long idWorkpack;
  private final String name;
  private final Status status;
  private final String description;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private final LocalDateTime activationDate;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private final LocalDateTime proposalDate;
  private final String message;
  private final boolean cancelation;
  private final boolean active;
  private final String proposer;
  private List<UpdateResponse> updates;
  private List<? extends EvaluationItem> evaluations;

  public BaselineDetailResponse(
    final Long id,
    final Long idWorkpack,
    final String name,
    final Status status,
    final String description,
    final LocalDateTime activationDate,
    final LocalDateTime proposalDate,
    final String message,
    final boolean cancelation,
    final boolean active,
    final String proposer
  ) {
    this.id = id;
    this.idWorkpack = idWorkpack;
    this.name = name;
    this.status = status;
    this.description = description;
    this.activationDate = activationDate;
    this.proposalDate = proposalDate;
    this.message = message;
    this.cancelation = cancelation;
    this.active = active;
    this.proposer = proposer;
  }

  public static BaselineDetailResponse of(final Baseline baseline) {
    return new BaselineDetailResponse(
      baseline.getId(),
      baseline.getBaselinedBy().getIdWorkpack(),
      baseline.getName(),
      baseline.getStatus(),
      baseline.getDescription(),
      baseline.getActivationDate(),
      baseline.getProposalDate(),
      baseline.getMessage(),
      baseline.isCancelation(),
      baseline.isActive(),
      baseline.getProposer().getFormattedRole()
    );
  }

  public List<EvaluationItem> getEvaluations() {
    return Collections.unmodifiableList(this.evaluations);
  }

  public void setEvaluations(final List<? extends EvaluationItem> evaluations) {
    this.evaluations = evaluations;
  }

  public Long getId() {
    return this.id;
  }

  public Long getIdWorkpack() {
    return this.idWorkpack;
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

  public String getProposer() {
    return this.proposer;
  }

  public List<UpdateResponse> getUpdates() {
    return this.updates;
  }

  public void setUpdates(final List<UpdateResponse> updates) {
    this.updates = updates;
  }

}
