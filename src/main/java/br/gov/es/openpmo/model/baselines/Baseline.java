package br.gov.es.openpmo.model.baselines;

import br.gov.es.openpmo.dto.baselines.IncludeBaselineRequest;
import br.gov.es.openpmo.dto.baselines.SubmitCancellingRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.relations.IsBaselinedBy;
import br.gov.es.openpmo.model.relations.IsEvaluatedBy;
import br.gov.es.openpmo.model.relations.IsProposedBy;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static br.gov.es.openpmo.model.baselines.Status.*;
import static br.gov.es.openpmo.utils.ApplicationMessage.BASELINE_IS_NOT_DRAFT_INVALID_STATE_ERROR;

@NodeEntity
public class Baseline extends Entity {

  private String name;

  private Status status;

  private String description;

  private LocalDateTime activationDate;

  private LocalDateTime proposalDate;

  private String message;

  private boolean cancelation;

  private boolean active;

  @Relationship(type = "IS_BASELINED_BY", direction = Relationship.INCOMING)
  private IsBaselinedBy baselinedBy;

  @Relationship(type = "IS_PROPOSED_BY", direction = Relationship.INCOMING)
  private IsProposedBy proposer;

  @Relationship(type = "IS_EVALUATED_BY")
  private Set<IsEvaluatedBy> evaluations;

  public Baseline() {
  }

  public Baseline(
      final String name,
      final String description,
      final String message
  ) {
    this.name = name;
    this.description = description;
    this.message = message;
    this.status = DRAFT;
  }

  public Baseline(final IncludeBaselineRequest request) {
    this.name = request.getName();
    this.description = request.getDescription();
    this.message = request.getMessage();
    this.status = DRAFT;
  }

  public static Baseline of(final SubmitCancellingRequest request) {
    final Baseline baselineCancelled = new Baseline();
    baselineCancelled.status = request.getStatus();
    baselineCancelled.description = request.getDescription();
    baselineCancelled.name = request.getName();
    baselineCancelled.message = request.getMessage();
    baselineCancelled.cancelation = request.getCancelation();
    baselineCancelled.proposalDate = LocalDateTime.now();
    return baselineCancelled;
  }

  public boolean isActive() {
    return this.active;
  }

  public void setActive(final boolean active) {
    this.active = active;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Status getStatus() {
    return this.status;
  }

  public void setStatus(final Status status) {
    this.status = status;
  }

  public IsProposedBy getProposer() {
    return this.proposer;
  }

  public void setProposer(final IsProposedBy proposer) {
    this.proposer = proposer;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public LocalDateTime getActivationDate() {
    return this.activationDate;
  }

  public void setActivationDate(final LocalDateTime activationDate) {
    this.activationDate = activationDate;
  }

  public LocalDateTime getProposalDate() {
    return this.proposalDate;
  }

  public void setProposalDate(final LocalDateTime proposalDate) {
    this.proposalDate = proposalDate;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  public boolean isCancelation() {
    return this.cancelation;
  }

  public void setCancelation(final boolean cancelation) {
    this.cancelation = cancelation;
  }

  public IsBaselinedBy getBaselinedBy() {
    return this.baselinedBy;
  }

  public void setBaselinedBy(final IsBaselinedBy baselinedBy) {
    this.baselinedBy = baselinedBy;
  }

  @Transient
  public Long getIdWorkpack() {
    return Optional.ofNullable(this.baselinedBy).map(IsBaselinedBy::getIdWorkpack).orElse(null);
  }

  public Baseline ifIsNotDraftThrowsException() {
    if (this.isDraft()) {
      return this;
    }
    throw new NegocioException(BASELINE_IS_NOT_DRAFT_INVALID_STATE_ERROR);
  }

  @Transient
  public boolean isDraft() {
    return this.status == DRAFT;
  }

  @Transient
  public String getWorkpackName() {
    return Optional.ofNullable(this.baselinedBy)
        .map(IsBaselinedBy::getWorkpack)
        .map(Workpack::getWorkpackModelInstance)
        .map(WorkpackModel::getModelName)
        .orElse(null);
  }

  public String getFormattedRole() {
    return Optional.ofNullable(this.proposer)
        .map(IsProposedBy::getFormattedRole)
        .orElse(null);
  }

  public void approve() {
    this.active = true;
    this.status = APPROVED;
    this.activationDate = LocalDateTime.now();
  }

  public void reject() {
    this.status = REJECTED;
  }

}
