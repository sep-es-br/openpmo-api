package br.gov.es.openpmo.dto.baselines;

import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.baselines.Decision;
import br.gov.es.openpmo.model.relations.IsEvaluatedBy;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.Objects;

public class EvaluationItem {

  @JsonIgnore
  private final Long id;
  private final String ccbMemberName;

  private final Decision decision;

  private final String inRoleWorkLocation;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private final LocalDateTime when;

  private final String comment;

  private Boolean myEvaluation;

  public EvaluationItem(
    final Long id,
    final String ccbMemberName,
    final Decision decision,
    final String inRoleWorkLocation,
    final LocalDateTime when,
    final String comment
  ) {
    this.id = id;
    this.ccbMemberName = ccbMemberName;
    this.decision = decision;
    this.inRoleWorkLocation = inRoleWorkLocation;
    this.when = when;
    this.comment = comment;
  }

  public static EvaluationItem fromBaselineNotEvaluated(final Person isCCBMember) {
    return new EvaluationItem(
      isCCBMember.getId(),
      isCCBMember.getName(),
      null,
      null,
      null,
      null
    );
  }

  public static EvaluationItem fromBaselineEvaluated(final IsEvaluatedBy isEvaluatedBy) {
    return new EvaluationItem(
      isEvaluatedBy.getIdPerson(),
      isEvaluatedBy.getMemberName(),
      isEvaluatedBy.getDecision(),
      isEvaluatedBy.getInRoleWorkLocation(),
      isEvaluatedBy.getWhen(),
      isEvaluatedBy.getComment()
    );
  }

  public String getCcbMemberName() {
    return this.ccbMemberName;
  }

  public Decision getDecision() {
    return this.decision;
  }

  public String getInRoleWorkLocation() {
    return this.inRoleWorkLocation;
  }

  public LocalDateTime getWhen() {
    return this.when;
  }

  public String getComment() {
    return this.comment;
  }

  public Boolean getMyEvaluation() {
    return this.myEvaluation;
  }

  public void setMyEvaluation(final Boolean myEvaluation) {
    this.myEvaluation = myEvaluation;
  }

  public void applySelfEvaluation(final Long idPerson) {
    this.myEvaluation = Objects.equals(this.id, idPerson);
  }

}
