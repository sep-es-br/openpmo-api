package br.gov.es.openpmo.model.issue;

import br.gov.es.openpmo.dto.issue.IssueCreateDto;
import br.gov.es.openpmo.dto.issue.IssueUpdateDto;
import br.gov.es.openpmo.dto.issue.response.IssueResponseDetailDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.issue.response.IssueResponse;
import br.gov.es.openpmo.model.risk.Importance;
import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.utils.ObjectUtils;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;
import org.springframework.data.annotation.Transient;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_NULL;

@Node
public class Issue extends Entity {

  private String name;

  private String description;

  private Importance importance;

  private StatusOfIssue status;

  private NatureOfIssue nature;

  @Relationship("IS_REPORTED_FOR")
  private Workpack workpack;

  @Relationship("IS_TRIGGERED_BY")
  private Risk triggeredBy;

  @Relationship(value = "ADDRESSES", direction = INCOMING)
  private Set<IssueResponse> responses;

  public Issue() {
  }

  public Issue(
    final String name,
    final String description,
    final Importance importance,
    final StatusOfIssue status,
    final NatureOfIssue nature,
    final Risk risk,
    final Workpack workpack
  ) {
    this.ifWorkpackNullThrowsException(workpack);
    this.name = name;
    this.description = description;
    this.importance = importance;
    this.status = status;
    this.nature = nature;
    this.triggeredBy = risk;
    this.workpack = workpack;
  }

  public static Issue of(
    final IssueCreateDto request,
    final Workpack workpack
  ) {
    return new Issue(
      request.getName(),
      request.getDescription(),
      request.getImportance(),
      request.getStatus(),
      request.getNature(),
      null,
      workpack
    );
  }

  public static Issue of(final Risk risk) {
    final Issue issue = new Issue(
      risk.getName(),
      risk.getDescription(),
      risk.getImportance(),
      StatusOfIssue.OPEN,
      risk.getNatureAsIssueNature(),
      risk,
      risk.getWorkpack()
    );
    final Set<IssueResponse> responses = risk.getResponsesPostOccurrenceAsIssueResponse(issue);
    issue.setResponses(responses);
    return issue;
  }

  private void ifWorkpackNullThrowsException(final Workpack workpack) {
    if(workpack == null) {
      throw new IllegalStateException(WORKPACK_NOT_NULL);
    }
  }

  public void setResponses(final Set<IssueResponse> responses) {
    this.responses = Collections.unmodifiableSet(responses);
  }

  @Transient
  public Long getWorkpackId() {
    return Optional.ofNullable(this.workpack)
      .map(Entity::getId)
      .orElse(null);
  }

  @Transient
  public Long getTriggeredById() {
    return this.triggeredBy.getId();
  }

  @Transient
  public void update(final IssueUpdateDto request) {
    ObjectUtils.updateIfPresent(request::getName, this::setName);
    ObjectUtils.updateIfPresent(request::getDescription, this::setDescription);
    ObjectUtils.updateIfPresent(request::getNature, this::setNature);
    ObjectUtils.updateIfPresent(request::getStatus, this::setStatus);
    ObjectUtils.updateIfPresent(request::getImportance, this::setImportance);
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public void setStatus(final StatusOfIssue status) {
    this.status = status;
  }

  public void setNature(final NatureOfIssue nature) {
    this.nature = nature;
  }

  @Transient
  public Set<IssueResponseDetailDto> getResponsesAsDetailDto() {
    if(this.responses == null) return Collections.emptySet();
    return this.responses.stream()
      .map(IssueResponseDetailDto::of)
      .collect(Collectors.toSet());
  }

  public Risk getTriggeredBy() {
    return this.triggeredBy;
  }

  public void setTriggeredBy(final Risk triggeredBy) {
    this.triggeredBy = triggeredBy;
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public Importance getImportance() {
    return this.importance;
  }

  public void setImportance(final Importance importance) {
    this.importance = importance;
  }

  public StatusOfIssue getStatus() {
    return this.status;
  }

  public NatureOfIssue getNature() {
    return this.nature;
  }

  public Set<IssueResponse> getResponses() {
    return Collections.unmodifiableSet(this.responses);
  }

  public Workpack getWorkpack() {
    return this.workpack;
  }

  public void setWorkpack(final Workpack workpack) {
    this.workpack = workpack;
  }

}
