package br.gov.es.openpmo.dto.schedule;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public class StepStoreParamDto {

  @NotNull
  private Long idSchedule;

  @Min(value = 0)
  private BigDecimal actualWork;

  @Min(value = 0)
  private BigDecimal plannedWork;

  @NotNull
  private Boolean endStep;

  private Set<ConsumesParamDto> consumes;

  private LocalDate scheduleEnd;

  private LocalDate scheduleStart;

  private DistributionStrategy distribution;

  public Long getIdSchedule() {
    return this.idSchedule;
  }

  public void setIdSchedule(final Long idSchedule) {
    this.idSchedule = idSchedule;
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

  public Boolean getEndStep() {
    return this.endStep;
  }

  public void setEndStep(final Boolean endStep) {
    this.endStep = endStep;
  }

  public Set<ConsumesParamDto> getConsumes() {
    return this.consumes;
  }

  public void setConsumes(final Set<ConsumesParamDto> consumes) {
    this.consumes = consumes;
  }

  public LocalDate getScheduleEnd() {
    return this.scheduleEnd;
  }

  public void setScheduleEnd(final LocalDate scheduleEnd) {
    this.scheduleEnd = scheduleEnd;
  }

  public LocalDate getScheduleStart() {
    return this.scheduleStart;
  }

  public void setScheduleStart(final LocalDate scheduleStart) {
    this.scheduleStart = scheduleStart;
  }

  public DistributionStrategy getDistribution() {
    return this.distribution;
  }

  public void setDistribution(final DistributionStrategy distribution) {
    this.distribution = distribution;
  }
}
