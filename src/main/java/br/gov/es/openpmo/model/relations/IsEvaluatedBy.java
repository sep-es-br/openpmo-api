package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.dto.baselines.BaselineEvaluationRequest;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.baselines.Decision;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.time.LocalDateTime;

@RelationshipEntity("IS_EVALUATED_BY")
public class IsEvaluatedBy {

  @Id
  @GeneratedValue
  private Long id;

  private Decision decision;
  private String inRoleWorkLocation;
  private LocalDateTime when;
  private String comment;


  @StartNode
  private Baseline baseline;

  @EndNode
  private Person person;

  public IsEvaluatedBy() {
    this.when = LocalDateTime.now();
  }

  public static IsEvaluatedBy fromMemberEvaluation(
    final Person member,
    final Baseline baseline,
    final BaselineEvaluationRequest request
  ) {
    final IsEvaluatedBy isEvaluatedBy = new IsEvaluatedBy();
    isEvaluatedBy.comment = request.getComment();
    isEvaluatedBy.decision = request.getDecision();
    isEvaluatedBy.person = member;
    isEvaluatedBy.baseline = baseline;
    return isEvaluatedBy;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Decision getDecision() {
    return this.decision;
  }

  public void setDecision(final Decision decision) {
    this.decision = decision;
  }

  public String getInRoleWorkLocation() {
    return this.inRoleWorkLocation;
  }

  public void setInRoleWorkLocation(final String inRoleWorkLocation) {
    this.inRoleWorkLocation = inRoleWorkLocation;
  }

  public LocalDateTime getWhen() {
    return this.when;
  }

  public void setWhen(final LocalDateTime when) {
    this.when = when;
  }

  public String getComment() {
    return this.comment;
  }

  public void setComment(final String comment) {
    this.comment = comment;
  }

  public Baseline getBaseline() {
    return this.baseline;
  }

  public void setBaseline(final Baseline baseline) {
    this.baseline = baseline;
  }

  public String getMemberName() {
    return this.person.getName();
  }

  public Person getPerson() {
    return this.person;
  }

  public void setPerson(final Person person) {
    this.person = person;
  }

  public Long getIdPerson() {
    return this.person.getId();
  }

}
