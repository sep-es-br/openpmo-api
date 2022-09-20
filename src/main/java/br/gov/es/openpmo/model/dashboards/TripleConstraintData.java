package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.tripleconstraint.CostDataChart;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.ScheduleDataChart;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.ScopeDataChart;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.TripleConstraintDataChart;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;
import java.util.Optional;

import static br.gov.es.openpmo.model.dashboards.DashboardUtils.apply;

public class TripleConstraintData {

  private Long idBaseline;

  private LocalDate mesAno;

  private CostData cost;

  private ScheduleData schedule;

  private ScopeData scope;

  public static TripleConstraintData of(final TripleConstraintDataChart from) {
    if(from == null) {
      return null;
    }

    final TripleConstraintData to = new TripleConstraintData();

    to.setIdBaseline(from.getIdBaseline());
    to.setMesAno(from.getMesAno());

    apply(from.getCost(), CostData::of, to::setCost);
    apply(from.getSchedule(), ScheduleData::of, to::setSchedule);
    apply(from.getScope(), ScopeData::of, to::setScope);

    return to;
  }

  public CostData getCost() {
    return this.cost;
  }

  public void setCost(final CostData cost) {
    this.cost = cost;
  }

  public ScheduleData getSchedule() {
    return this.schedule;
  }

  public void setSchedule(final ScheduleData schedule) {
    this.schedule = schedule;
  }

  public ScopeData getScope() {
    return this.scope;
  }

  public void setScope(final ScopeData scope) {
    this.scope = scope;
  }

  @Transient
  public TripleConstraintDataChart getResponse() {
    final TripleConstraintDataChart response = new TripleConstraintDataChart();

    response.setIdBaseline(this.idBaseline);
    response.setMesAno(this.mesAno);

    response.setCost(this.getCostDataChart());
    response.setSchedule(this.getScheduleDataChart());
    response.setScope(this.getScopeDataChart());

    return response;
  }

  @Transient
  private CostDataChart getCostDataChart() {
    return Optional.ofNullable(this.cost)
      .map(CostData::getResponse)
      .orElse(null);
  }

  @Transient
  private ScheduleDataChart getScheduleDataChart() {
    return Optional.ofNullable(this.schedule)
      .map(ScheduleData::getResponse)
      .orElse(null);
  }

  @Transient
  private ScopeDataChart getScopeDataChart() {
    return Optional.ofNullable(this.scope)
      .map(ScopeData::getResponse)
      .orElse(null);
  }

  public Long getIdBaseline() {
    return this.idBaseline;
  }

  public void setIdBaseline(final Long idBaseline) {
    this.idBaseline = idBaseline;
  }

  public LocalDate getMesAno() {
    return this.mesAno;
  }

  public void setMesAno(final LocalDate mesAno) {
    this.mesAno = mesAno;
  }

}
