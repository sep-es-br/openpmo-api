package br.gov.es.openpmo.model.issue.response;

import br.gov.es.openpmo.dto.issue.response.IssueResponseCreateDto;
import br.gov.es.openpmo.dto.issue.response.IssueResponseUpdateDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderCardViewDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.issue.Issue;
import br.gov.es.openpmo.model.risk.response.RiskResponse;
import br.gov.es.openpmo.utils.ObjectUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@NodeEntity
public class IssueResponse extends Entity {

  private String name;

  private String plan;

  private LocalDate date;

  private IssueResponseStatus status;

  @Relationship("ADDRESSES")
  private Issue issue;

  @Relationship(value = "IS_RESPONSIBLE_FOR", direction = Relationship.INCOMING)
  private Set<Person> responsible;

  public IssueResponse() {
  }

  public IssueResponse(
    final String name,
    final String plan,
    final LocalDate date,
    final IssueResponseStatus status,
    final Issue issue,
    final Set<Person> responsible
  ) {
    this.name = name;
    this.plan = plan;
    this.date = date;
    this.status = status;
    this.issue = issue;
    this.responsible = responsible;
  }

  public static IssueResponse of(final RiskResponse response, final Issue issue) {
    return new IssueResponse(
      response.getName(),
      response.getPlan(),
      LocalDate.now(),
      IssueResponseStatus.RUNNING,
      issue,
      response.getResponsible()
    );
  }

  public static IssueResponse of(
    final IssueResponseCreateDto request,
    final Issue issue,
    final Set<Person> responsible
  ) {
    return new IssueResponse(
      request.getName(),
      request.getPlan(),
      request.getDate(),
      request.getStatus(),
      issue,
      responsible
    );
  }

  @Transient
  public Long getIssueId() {
    return this.issue.getId();
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getPlan() {
    return this.plan;
  }

  public void setPlan(final String plan) {
    this.plan = plan;
  }

  public LocalDate getDate() {
    return this.date;
  }

  public void setDate(final LocalDate date) {
    this.date = date;
  }

  public IssueResponseStatus getStatus() {
    return this.status;
  }

  public void setStatus(final IssueResponseStatus status) {
    this.status = status;
  }

  public Issue getIssue() {
    return this.issue;
  }

  public void setIssue(final Issue issue) {
    this.issue = issue;
  }

  public Set<Person> getResponsible() {
    return this.responsible;
  }

  public void setResponsible(final Set<Person> responsible) {
    this.responsible = responsible;
  }

  @Transient
  public Set<StakeholderCardViewDto> getResponsibleAsCardView() {
    if(this.responsible == null) return Collections.emptySet();
    return this.responsible.stream()
      .map(StakeholderCardViewDto::of)
      .collect(Collectors.toSet());
  }

  @Transient
  public void update(final IssueResponseUpdateDto request) {
    ObjectUtils.updateIfPresent(request::getName, this::setName);
    ObjectUtils.updateIfPresent(request::getPlan, this::setName);
    ObjectUtils.updateIfPresent(request::getStatus, this::setStatus);
    ObjectUtils.updateIfPresent(request::getDate, this::setDate);
  }

  @Transient
  public Long getWorkpackId() {
    return Optional.ofNullable(this.issue).map(Issue::getWorkpackId).orElse(null);
  }

}
