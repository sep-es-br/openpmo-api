package br.gov.es.openpmo.dto.schedule;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public class ScheduleParamDto {

  private Long id;
  @NotNull
  private Long idWorkpack;

  @NotNull
  private LocalDate end;

  @NotNull
  private LocalDate start;

  @NotNull
  private BigDecimal plannedWork;
  private BigDecimal actualWork;
  private Set<CostSchedule> costs;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public void setIdWorkpack(final Long idWorkpack) {
    this.idWorkpack = idWorkpack;
  }

  public LocalDate getEnd() {
    return this.end;
  }

  public void setEnd(final LocalDate end) {
    this.end = end;
  }

  public LocalDate getStart() {
    return this.start;
  }

  public void setStart(final LocalDate start) {
    this.start = start;
  }

  public BigDecimal getPlannedWork() {
    return this.plannedWork;
  }

  public void setPlannedWork(final BigDecimal plannedWork) {
    this.plannedWork = plannedWork;
  }

  public BigDecimal getActualWork() {
    return this.actualWork;
  }

  public void setActualWork(final BigDecimal actualWork) {
    this.actualWork = actualWork;
  }

  public Set<CostSchedule> getCosts() {
    return this.costs;
  }

  public void setCosts(final Set<CostSchedule> costs) {
    this.costs = costs;
  }
}
