package br.gov.es.openpmo.dto.risk.response;

import br.gov.es.openpmo.model.risk.response.RiskResponseStatus;
import br.gov.es.openpmo.model.risk.response.Strategy;
import br.gov.es.openpmo.model.risk.response.When;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

public class RiskResponseCreateDto {

  @NotNull
  @NotEmpty
  private final String name;
  @NotNull
  private final When when;
  @NotNull
  @JsonFormat(pattern = "yyyy-MM-dd")
  private final LocalDate startDate;
  @NotNull
  @JsonFormat(pattern = "yyyy-MM-dd")
  private final LocalDate endDate;
  @NotNull
  private final Strategy strategy;
  @NotNull
  private final RiskResponseStatus status;
  private final String trigger;
  private final String plan;
  private final Set<Long> responsible;
  @NotNull
  private final Long idRisk;

  @JsonCreator
  public RiskResponseCreateDto(
    final Long idRisk,
    final String name,
    final When when,
    final LocalDate startDate,
    final LocalDate endDate,
    final Strategy strategy,
    final RiskResponseStatus status,
    final String trigger,
    final String plan,
    final Set<Long> responsible
  ) {
    this.idRisk = idRisk;
    this.name = name;
    this.when = when;
    this.startDate = startDate;
    this.endDate = endDate;
    this.strategy = strategy;
    this.status = status;
    this.trigger = trigger;
    this.plan = plan;
    this.responsible = responsible;
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

  public Set<Long> getResponsible() {
    return this.responsible;
  }
}
