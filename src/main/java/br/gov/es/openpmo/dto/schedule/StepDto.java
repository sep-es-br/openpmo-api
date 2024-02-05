package br.gov.es.openpmo.dto.schedule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.neo4j.annotation.QueryResult;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.gov.es.openpmo.model.schedule.Step;

@QueryResult
public class StepDto {

  private Long id;

  private Long idSchedule;

  private BigDecimal actualWork;

  private BigDecimal plannedWork;

  private BigDecimal baselinePlannedWork;

  private LocalDate periodFromStart;

  private LocalDate baselinePeriodFromStart;

  private Set<ConsumesDto> consumes;

  private LocalDate scheduleEnd;

  private LocalDate scheduleStart;

  @JsonIgnore
  private Long stepMasterId;

  @JsonIgnore
  private Long periodFromStartNumber;

  public StepDto() {
  }

  public StepDto(Step step) {
    this.id = step.getId();
    this.idSchedule = step.getScheduleId();
    this.actualWork = step.getActualWork();
    this.plannedWork = step.getPlannedWork();
    this.periodFromStart = step.getPeriodFromStartDate();
    if (step.getSchedule() != null) {
      this.scheduleEnd = step.getScheduleEnd();
      this.scheduleStart = step.getScheduleStart();
    }
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

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

  public LocalDate getPeriodFromStart() {
    return this.periodFromStart;
  }

  public void setPeriodFromStart(final LocalDate periodFromStart) {
    this.periodFromStart = periodFromStart;
  }

  public Set<ConsumesDto> getConsumes() {
    return this.consumes;
  }

  public void setConsumes(final Set<ConsumesDto> consumes) {
    this.consumes = consumes;
  }

  public BigDecimal getBaselinePlannedWork() {
    return this.baselinePlannedWork;
  }

  public void setBaselinePlannedWork(final BigDecimal baselinePlannedWork) {
    this.baselinePlannedWork = baselinePlannedWork;
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

  public LocalDate getBaselinePeriodFromStart() {
    return this.baselinePeriodFromStart;
  }

  public void setBaselinePeriodFromStart(final LocalDate baselinePeriodFromStart) {
    this.baselinePeriodFromStart = baselinePeriodFromStart;
  }

  public Long getPeriodFromStartNumber() {
    return periodFromStartNumber;
  }

  public void setPeriodFromStartNumber(Long periodFromStartNumber) {
    this.periodFromStartNumber = periodFromStartNumber;
  }

  public void setStepMasterId(Long stepMasterId) {
    this.stepMasterId = stepMasterId;
  }

  public Long getStepMasterId() {
    return stepMasterId;
  }

  public BigDecimal getBaselinePlannedCost() {
    return Optional.ofNullable(this.consumes)
      .map(c -> c.stream()
        .map(ConsumesDto::getBaselinePlannedCost)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add))
      .orElse(BigDecimal.ZERO);
  }

}
