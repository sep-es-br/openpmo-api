package br.gov.es.openpmo.dto.risk.response;

import br.gov.es.openpmo.dto.stakeholder.StakeholderCardViewDto;
import br.gov.es.openpmo.model.risk.response.RiskResponse;
import br.gov.es.openpmo.model.risk.response.RiskResponseStatus;
import br.gov.es.openpmo.model.risk.response.Strategy;
import br.gov.es.openpmo.model.risk.response.When;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

public class RiskResponseDetailDto {

  private final Long id;
  private final Long idRisk;
  private final String name;
  private final When when;
  private final LocalDate startDate;
  private final LocalDate endDate;
  private final Strategy strategy;
  private final RiskResponseStatus status;
  private final String trigger;
  private final String plan;
  private final Set<StakeholderCardViewDto> responsible;

  public RiskResponseDetailDto(
    final Long id,
    final Long idRisk,
    final String name,
    final When when,
    final LocalDate startDate,
    final LocalDate endDate,
    final Strategy strategy,
    final RiskResponseStatus status,
    final String trigger,
    final String plan,
    final Set<StakeholderCardViewDto> responsible
  ) {
    this.id = id;
    this.idRisk = idRisk;
    this.name = name;
    this.when = when;
    this.startDate = startDate;
    this.endDate = endDate;
    this.strategy = strategy;
    this.status = status;
    this.trigger = trigger;
    this.plan = plan;
    this.responsible = Collections.unmodifiableSet(responsible);
  }

  public static RiskResponseDetailDto of(final RiskResponse riskResponse) {
    return new RiskResponseDetailDto(
      riskResponse.getId(),
      riskResponse.getIdRisk(),
      riskResponse.getName(),
      riskResponse.getWhen(),
      riskResponse.getStartDate(),
      riskResponse.getEndDate(),
      riskResponse.getStrategy(),
      riskResponse.getStatus(),
      riskResponse.getTrigger(),
      riskResponse.getPlan(),
      riskResponse.getResponsibleAsStakeholderCardDto()
    );
  }

  public Long getId() {
    return this.id;
  }

  public Long getIdRisk() {
    return this.idRisk;
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

  public Set<StakeholderCardViewDto> getResponsible() {
    return this.responsible;
  }

}
