package br.gov.es.openpmo.model.risk.response;

import br.gov.es.openpmo.dto.risk.response.RiskResponseCreateDto;
import br.gov.es.openpmo.dto.risk.response.RiskResponseUpdateDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderCardViewDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.utils.ObjectUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.START_DATE_AFTER_END_DATE;

@NodeEntity
public class RiskResponse extends Entity {

  private String name;

  private When when;

  private LocalDate startDate;

  private LocalDate endDate;

  private Strategy strategy;

  private RiskResponseStatus status;

  private String trigger;

  private String plan;

  @Relationship("MITIGATES")
  private Risk risk;

  @Relationship(value = "IS_RESPONSIBLE_FOR", direction = Relationship.INCOMING)
  private Set<Person> responsible;

  public RiskResponse() {
  }

  private RiskResponse(
      final String name,
      final When when,
      final LocalDate startDate,
      final LocalDate endDate,
      final Strategy strategy,
      final RiskResponseStatus status,
      final String trigger,
      final String plan,
      final Risk risk,
      final Set<Person> responsible
  ) {
    this.name = name;
    this.when = when;
    this.strategy = strategy;
    this.status = status;
    this.trigger = trigger;
    this.plan = plan;
    this.risk = risk;
    this.responsible = responsible;
    this.ifStartDateIsAfterEndDateThrowException(startDate, endDate);
    this.startDate = startDate;
    this.endDate = endDate;
  }

  private void ifStartDateIsAfterEndDateThrowException(
      final ChronoLocalDate startDate,
      final ChronoLocalDate endDate
  ) {
    if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
      throw new NegocioException(START_DATE_AFTER_END_DATE);
    }
  }

  public static RiskResponse of(
      final RiskResponseCreateDto request,
      final Risk risk,
      final Set<Person> responsible
  ) {
    return new RiskResponse(
        request.getName(),
        request.getWhen(),
        request.getStartDate(),
        request.getEndDate(),
        request.getStrategy(),
        request.getStatus(),
        request.getTrigger(),
        request.getPlan(),
        risk,
        responsible
    );
  }

  public String getName() {
    return this.name;
  }

  public When getWhen() {
    return this.when;
  }

  public LocalDate getStartDate() {
    return this.startDate;
  }

  public LocalDate getEndDate() {
    return this.endDate;
  }

  public Strategy getStrategy() {
    return this.strategy;
  }

  public RiskResponseStatus getStatus() {
    return this.status;
  }

  public String getTrigger() {
    return this.trigger;
  }

  public String getPlan() {
    return this.plan;
  }

  public Risk getRisk() {
    return this.risk;
  }

  public void setRisk(final Risk risk) {
    this.risk = risk;
  }

  public Set<Person> getResponsible() {
    if (this.responsible == null) return Collections.emptySet();
    return Collections.unmodifiableSet(this.responsible);
  }

  @Transient
  public void update(final RiskResponseUpdateDto request, final Set<Person> responsible) {
    ObjectUtils.updateIfPresent(() -> responsible, this::setResponsible);
    ObjectUtils.updateIfPresent(request::getName, this::setName);
    ObjectUtils.updateIfPresent(request::getWhen, this::setWhen);
    ObjectUtils.updateIfPresent(request::getStrategy, this::setStrategy);
    ObjectUtils.updateIfPresent(request::getStatus, this::setStatus);
    ObjectUtils.updateIfPresent(request::getTrigger, this::setTrigger);
    ObjectUtils.updateIfPresent(request::getPlan, this::setPlan);

    this.ifStartDateIsAfterEndDateThrowException(this.startDate, this.endDate);

    ObjectUtils.updateIfPresent(request::getStartDate, this::setStartDate);
    ObjectUtils.updateIfPresent(request::getEndDate, this::setEndDate);
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setWhen(final When when) {
    this.when = when;
  }

  public void setStartDate(final LocalDate startDate) {
    this.startDate = startDate;
  }

  public void setEndDate(final LocalDate endDate) {
    this.endDate = endDate;
  }

  public void setStrategy(final Strategy strategy) {
    this.strategy = strategy;
  }

  public void setStatus(final RiskResponseStatus status) {
    this.status = status;
  }

  public void setTrigger(final String trigger) {
    this.trigger = trigger;
  }

  public void setPlan(final String plan) {
    this.plan = plan;
  }

  public void setResponsible(final Set<Person> responsible) {
    this.responsible = responsible;
  }

  @Transient
  public Long getIdRisk() {
    return this.risk.getId();
  }

  @Transient
  public Set<StakeholderCardViewDto> getResponsibleAsStakeholderCardDto() {
    if (this.responsible == null) return Collections.emptySet();
    return this.responsible.stream()
        .map(StakeholderCardViewDto::of)
        .collect(Collectors.toSet());
  }

  @Transient
  public Long getIdWorkpack() {
    return Optional.ofNullable(this.risk).map(Risk::getIdWorkpack).orElse(null);
  }

}
