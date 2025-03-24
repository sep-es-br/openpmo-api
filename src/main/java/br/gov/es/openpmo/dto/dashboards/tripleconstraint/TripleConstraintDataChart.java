package br.gov.es.openpmo.dto.dashboards.tripleconstraint;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class TripleConstraintDataChart {

  private Long idBaseline;

  @JsonFormat(pattern = "MM-yyyy")
  private LocalDate mesAno;

  private CostDataChart cost = new CostDataChart();

  private ScheduleDataChart schedule;

  private ScopeDataChart scope = new ScopeDataChart();

  public CostDataChart getCost() {
    return this.cost;
  }

  public void setCost(final CostDataChart cost) {
    this.cost = cost;
  }

  public ScheduleDataChart getSchedule() {
    return this.schedule;
  }

  public void setSchedule(final ScheduleDataChart schedule) {
    this.schedule = schedule;
  }

  public ScopeDataChart getScope() {
    return this.scope;
  }

  public void setScope(final ScopeDataChart scope) {
    this.scope = scope;
  }

  public Long getIdBaseline() {
    return this.idBaseline;
  }

  public void setIdBaseline(final Long idBaseline) {
    this.idBaseline = idBaseline;
  }


}
