package br.gov.es.openpmo.dto.schedule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public class StepParamDto {

  private Long id;
  private BigDecimal actualWork;
  private BigDecimal plannedWork;
  private LocalDate periodFromStart;
  private Set<ConsumesParamDto> consumes;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public BigDecimal getActualWork() {
    return this.actualWork;
  }

  public void setActualWork(final BigDecimal actualWork) {
    this.actualWork = actualWork;
  }

  public BigDecimal getPlannedWork() {
    return this.plannedWork;
  }

  public void setPlannedWork(final BigDecimal plannedWork) {
    this.plannedWork = plannedWork;
  }

  public Set<ConsumesParamDto> getConsumes() {
    return this.consumes;
  }

  public void setConsumes(final Set<ConsumesParamDto> consumes) {
    this.consumes = consumes;
  }

  public LocalDate getPeriodFromStart() {
    return this.periodFromStart;
  }

  public void setPeriodFromStart(final LocalDate periodFromStart) {
    this.periodFromStart = periodFromStart;
  }
}
