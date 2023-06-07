package br.gov.es.openpmo.dto.baselines.ccbmemberview;

import br.gov.es.openpmo.dto.baselines.EvaluationItem;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.baselines.Status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BaselineDetailCCBMemberResponse {

  private final Long id;

  private final Long idWorkpack;

  private final String projectName;

  private final String projectFullName;

  private final String description;

  private final String message;

  private final String proposer;

  private final Status status;

  private final String name;

  private final Boolean cancelation;

  private final List<EvaluationItem> evaluations = new ArrayList<>();

  private BaselineCostDetail cost;

  private BaselineScheduleDetail schedule;

  private BaselineScopeDetail scope;

  public BaselineDetailCCBMemberResponse(
    final Long id,
    final Long idWorkpack,
    final String projectName,
    final String projectFullName,
    final String description,
    final String message,
    final String proposer,
    final Status status,
    final String name,
    final Boolean cancelation
  ) {
    this.id = id;
    this.idWorkpack = idWorkpack;
    this.projectName = projectName;
    this.projectFullName = projectFullName;
    this.description = description;
    this.message = message;
    this.proposer = proposer;
    this.status = status;
    this.name = name;
    this.cancelation = cancelation;
  }

  public static BaselineDetailCCBMemberResponse of(
    final Baseline baseline,
    final String name,
    final String fullName,
    final TripleConstraintOutput output,
    final Collection<? extends EvaluationItem> evaluations
  ) {
    final BaselineDetailCCBMemberResponse response = new BaselineDetailCCBMemberResponse(
      baseline.getId(),
      baseline.getIdWorkpack(),
      name,
      fullName,
      baseline.getDescription(),
      baseline.getMessage(),
      baseline.getFormattedRole(),
      baseline.getStatus(),
      baseline.getName(),
      baseline.isCancelation()
    );
    if (output != null) {
      response.schedule = output.getScheduleDetail();
      response.cost = output.getCostDetail();
      response.scope = output.getScopeDetail();
      response.scope.roundData();
      response.cost.roundData();
      response.schedule.roundData();
    }

    response.addEvaluation(evaluations);

    return response;
  }

  public void addEvaluation(final Collection<? extends EvaluationItem> evaluations) {
    this.evaluations.addAll(evaluations);
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

  public String getProjectFullName() {
    return projectFullName;
  }

  public String getDescription() {
    return this.description;
  }

  public String getMessage() {
    return this.message;
  }

  public String getProposer() {
    return this.proposer;
  }

  public Status getStatus() {
    return this.status;
  }

  public String getName() {
    return this.name;
  }

  public Boolean getCancelation() {
    return this.cancelation;
  }

  public BaselineCostDetail getCost() {
    return this.cost;
  }

  public void setCost(final BaselineCostDetail cost) {
    this.cost = cost;
  }

  public BaselineScheduleDetail getSchedule() {
    return this.schedule;
  }

  public void setSchedule(final BaselineScheduleDetail schedule) {
    this.schedule = schedule;
  }

  public BaselineScopeDetail getScope() {
    return this.scope;
  }

  public void setScope(final BaselineScopeDetail scope) {
    this.scope = scope;
  }

  public List<EvaluationItem> getEvaluations() {
    return Collections.unmodifiableList(this.evaluations);
  }

  public void addEvaluation(final EvaluationItem evaluation) {
    this.evaluations.add(evaluation);
  }

}
