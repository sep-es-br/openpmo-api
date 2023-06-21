package br.gov.es.openpmo.dto.schedule;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public class StepUpdateDto {

  private Long id;
  @Min(value = 0)
  private BigDecimal actualWork;
  @Min(value = 0)
  private BigDecimal plannedWork;
  private LocalDate scheduleEnd;
  private LocalDate scheduleStart;
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

}
