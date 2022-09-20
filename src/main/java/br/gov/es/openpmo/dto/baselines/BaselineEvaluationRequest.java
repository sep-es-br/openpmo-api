package br.gov.es.openpmo.dto.baselines;

import br.gov.es.openpmo.model.baselines.Decision;
import com.fasterxml.jackson.annotation.JsonCreator;

public class BaselineEvaluationRequest {

  private final String comment;
  private final Decision decision;

  @JsonCreator
  public BaselineEvaluationRequest(
    final String comment,
    final Decision decision
  ) {
    this.comment = comment;
    this.decision = decision;
  }

  public String getComment() {
    return this.comment;
  }

  public Decision getDecision() {
    return this.decision;
  }

}
