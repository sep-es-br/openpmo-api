package br.gov.es.openpmo.model.risk;


import br.gov.es.openpmo.dto.risk.RiskCreateDto;
import br.gov.es.openpmo.dto.risk.RiskUpdateDto;
import br.gov.es.openpmo.dto.risk.response.RiskResponseDetailDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.issue.Issue;
import br.gov.es.openpmo.model.issue.NatureOfIssue;
import br.gov.es.openpmo.model.issue.response.IssueResponse;
import br.gov.es.openpmo.model.risk.response.RiskResponse;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.utils.ObjectUtils;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.model.risk.response.When.POST_OCCURRENCE;
import static br.gov.es.openpmo.utils.ApplicationMessage.HAPPEN_FROM_AFTER_HAPPEN_TO;
import static br.gov.es.openpmo.utils.ApplicationMessage.HAPPEN_TO_BEFORE_HAPPEN_FROM;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;


@Node
public class Risk extends Entity {

  private String name;
  private String description;
  private Importance importance;
  private NatureOfRisk nature;
  private StatusOfRisk status;
  private LocalDate likelyToHappenFrom;
  private LocalDate likelyToHappenTo;
  private LocalDate happenedIn;

  @Relationship("IS_FORSEEN_ON")
  private Workpack workpack;

  @Relationship(value = "MITIGATES", direction = INCOMING)
  private Set<RiskResponse> responses;

  public Risk() {
  }

  public Risk(
    final String name,
    final String description,
    final Importance importance,
    final NatureOfRisk nature,
    final StatusOfRisk status,
    final LocalDate likelyToHappenFrom,
    final LocalDate likelyToHappenTo,
    final LocalDate happenedIn,
    final Workpack workpack
  ) {
    this.name = name;
    this.description = description;
    this.importance = importance;
    this.nature = nature;
    this.status = status;
    this.workpack = workpack;
    this.happenedIn = happenedIn;

    ifLikelyToHappenFromIsAfterLikelyToHappenToThrowException(likelyToHappenFrom, likelyToHappenTo);
    ifLikelyToHappenToIsBeforeLikelyToHappenFromThrowsException(likelyToHappenFrom, likelyToHappenTo);
    this.likelyToHappenFrom = likelyToHappenFrom;
    this.likelyToHappenTo = likelyToHappenTo;
  }

  private static void ifLikelyToHappenFromIsAfterLikelyToHappenToThrowException(
    final ChronoLocalDate likelyToHappenFrom,
    final ChronoLocalDate likelyToHappenTo
  ) {
    if(
      likelyToHappenFrom != null
      && likelyToHappenTo != null
      && likelyToHappenFrom.isAfter(likelyToHappenTo)
    ) {
      throw new NegocioException(HAPPEN_FROM_AFTER_HAPPEN_TO);
    }
  }

  private static void ifLikelyToHappenToIsBeforeLikelyToHappenFromThrowsException(
    final ChronoLocalDate likelyToHappenFrom,
    final ChronoLocalDate likelyToHappenTo
  ) {
    if(
      likelyToHappenFrom != null
      && likelyToHappenTo != null
      && likelyToHappenTo.isBefore(likelyToHappenFrom)
    ) {
      throw new NegocioException(HAPPEN_TO_BEFORE_HAPPEN_FROM);
    }
  }

  public static Risk of(
    final RiskCreateDto request,
    final Workpack workpack
  ) {
    return new Risk(
      request.getName(),
      request.getDescription(),
      request.getImportance(),
      request.getNature(),
      request.getStatus(),
      request.getLikelyToHappenFrom(),
      request.getLikelyToHappenTo(),
      request.getHappenedIn(),
      workpack
    );
  }

  @Transient
  public Long getIdWorkpack() {
    return Optional.ofNullable(this.workpack)
      .map(Entity::getId)
      .orElse(null);
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public Importance getImportance() {
    return this.importance;
  }

  public void setImportance(final Importance importance) {
    this.importance = importance;
  }

  public NatureOfRisk getNature() {
    return this.nature;
  }

  public void setNature(final NatureOfRisk nature) {
    this.nature = nature;
  }

  public StatusOfRisk getStatus() {
    return this.status;
  }

  public void setStatus(final StatusOfRisk status) {
    this.status = status;
  }

  public LocalDate getLikelyToHappenFrom() {
    return this.likelyToHappenFrom;
  }

  public void setLikelyToHappenFrom(final LocalDate likelyToHappenFrom) {
    this.likelyToHappenFrom = likelyToHappenFrom;
  }

  public LocalDate getLikelyToHappenTo() {
    return this.likelyToHappenTo;
  }

  public void setLikelyToHappenTo(final LocalDate likelyToHappenTo) {
    this.likelyToHappenTo = likelyToHappenTo;
  }

  public Workpack getWorkpack() {
    return this.workpack;
  }

  public void setWorkpack(final Workpack workpack) {
    this.workpack = workpack;
  }

  public LocalDate getHappenedIn() {
    return this.happenedIn;
  }

  public void setHappenedIn(final LocalDate happenedIn) {
    this.happenedIn = happenedIn;
  }

  public Set<RiskResponse> getResponses() {
    return this.responses;
  }

  public void setResponses(final Set<RiskResponse> responses) {
    this.responses = responses;
  }

  @Transient
  public void update(final RiskUpdateDto request) {
    ObjectUtils.updateIfPresent(request::getName, this::setName);
    ObjectUtils.updateIfPresent(request::getDescription, this::setDescription);
    ObjectUtils.updateIfPresent(request::getImportance, this::setImportance);
    ObjectUtils.updateIfPresent(request::getNature, this::setNature);
    ObjectUtils.updateIfPresent(request::getStatus, this::setStatus);
    ObjectUtils.updateIfPresent(request::getHappenedIn, this::setHappenedIn);

    ifLikelyToHappenFromIsAfterLikelyToHappenToThrowException(
      request.getLikelyToHappenFrom(),
      request.getLikelyToHappenTo()
    );
    ifLikelyToHappenToIsBeforeLikelyToHappenFromThrowsException(
      request.getLikelyToHappenFrom(),
      request.getLikelyToHappenTo()
    );

    ObjectUtils.updateIfPresent(request::getLikelyToHappenFrom, this::setLikelyToHappenFrom);
    ObjectUtils.updateIfPresent(request::getLikelyToHappenTo, this::setLikelyToHappenTo);
  }

  @Transient
  public Set<RiskResponseDetailDto> getResponsesAsDetailDto() {
    if(this.responses == null) return Collections.emptySet();
    return this.responses.stream()
      .map(RiskResponseDetailDto::of)
      .collect(Collectors.toSet());
  }

  @Transient
  public Set<IssueResponse> getResponsesPostOccurrenceAsIssueResponse(final Issue issue) {
    if(this.responses == null) return Collections.emptySet();
    return this.responses.stream()
      .filter(response -> response.getWhen() == POST_OCCURRENCE)
      .map(response -> IssueResponse.of(response, issue))
      .collect(Collectors.toSet());
  }

  @Transient
  public NatureOfIssue getNatureAsIssueNature() {
    return this.nature.issue();
  }

}
