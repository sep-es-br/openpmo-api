package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.tripleconstraint.TripleConstraintDataChart;
import br.gov.es.openpmo.model.Entity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.math.BigDecimal;
import java.time.LocalDate;

@NodeEntity
public class TripleConstraint extends Entity {

  @Relationship("IS_AT")
  private DashboardMonth month;

  private Long idBaseline;

  private BigDecimal costActualValue;

  private BigDecimal costVariation;

  private BigDecimal costPlannedValue;

  private BigDecimal costForeseenValue;

  private LocalDate schedulePlannedStartDate;

  private LocalDate schedulePlannedEndDate;

  private LocalDate scheduleForeseenStartDate;

  private LocalDate scheduleForeseenEndDate;

  private LocalDate scheduleActualStartDate;

  private LocalDate scheduleActualEndDate;

  private BigDecimal scheduleVariation;

  private BigDecimal schedulePlannedValue;

  private BigDecimal scheduleForeseenValue;

  private BigDecimal scheduleActualValue;

  private BigDecimal scopeVariation;

  private BigDecimal scopePlannedVariationPercent;

  private BigDecimal scopeForeseenVariationPercent;

  private BigDecimal scopeActualVariationPercent;

  private BigDecimal scopePlannedValue;

  private BigDecimal scopeForeseenValue;

  public DashboardMonth getMonth() {
    return month;
  }

  public void setMonth(DashboardMonth month) {
    this.month = month;
  }

  public Long getIdBaseline() {
    return idBaseline;
  }

  public void setIdBaseline(Long idBaseline) {
    this.idBaseline = idBaseline;
  }

  public BigDecimal getCostActualValue() {
    return costActualValue;
  }

  public void setCostActualValue(BigDecimal costActualValue) {
    this.costActualValue = costActualValue;
  }

  public BigDecimal getCostVariation() {
    return costVariation;
  }

  public void setCostVariation(BigDecimal costVariation) {
    this.costVariation = costVariation;
  }

  public BigDecimal getCostPlannedValue() {
    return costPlannedValue;
  }

  public void setCostPlannedValue(BigDecimal costPlannedValue) {
    this.costPlannedValue = costPlannedValue;
  }

  public BigDecimal getCostForeseenValue() {
    return costForeseenValue;
  }

  public void setCostForeseenValue(BigDecimal costForeseenValue) {
    this.costForeseenValue = costForeseenValue;
  }

  public LocalDate getSchedulePlannedStartDate() {
    return schedulePlannedStartDate;
  }

  public void setSchedulePlannedStartDate(LocalDate schedulePlannedStartDate) {
    this.schedulePlannedStartDate = schedulePlannedStartDate;
  }

  public LocalDate getSchedulePlannedEndDate() {
    return schedulePlannedEndDate;
  }

  public void setSchedulePlannedEndDate(LocalDate schedulePlannedEndDate) {
    this.schedulePlannedEndDate = schedulePlannedEndDate;
  }

  public LocalDate getScheduleForeseenStartDate() {
    return scheduleForeseenStartDate;
  }

  public void setScheduleForeseenStartDate(LocalDate scheduleForeseenStartDate) {
    this.scheduleForeseenStartDate = scheduleForeseenStartDate;
  }

  public LocalDate getScheduleForeseenEndDate() {
    return scheduleForeseenEndDate;
  }

  public void setScheduleForeseenEndDate(LocalDate scheduleForeseenEndDate) {
    this.scheduleForeseenEndDate = scheduleForeseenEndDate;
  }

  public LocalDate getScheduleActualStartDate() {
    return scheduleActualStartDate;
  }

  public void setScheduleActualStartDate(LocalDate scheduleActualStartDate) {
    this.scheduleActualStartDate = scheduleActualStartDate;
  }

  public LocalDate getScheduleActualEndDate() {
    return scheduleActualEndDate;
  }

  public void setScheduleActualEndDate(LocalDate scheduleActualEndDate) {
    this.scheduleActualEndDate = scheduleActualEndDate;
  }

  public BigDecimal getScheduleVariation() {
    return scheduleVariation;
  }

  public void setScheduleVariation(BigDecimal scheduleVariation) {
    this.scheduleVariation = scheduleVariation;
  }

  public BigDecimal getSchedulePlannedValue() {
    return schedulePlannedValue;
  }

  public void setSchedulePlannedValue(BigDecimal schedulePlannedValue) {
    this.schedulePlannedValue = schedulePlannedValue;
  }

  public BigDecimal getScheduleForeseenValue() {
    return scheduleForeseenValue;
  }

  public void setScheduleForeseenValue(BigDecimal scheduleForeseenValue) {
    this.scheduleForeseenValue = scheduleForeseenValue;
  }

  public BigDecimal getScheduleActualValue() {
    return scheduleActualValue;
  }

  public void setScheduleActualValue(BigDecimal scheduleActualValue) {
    this.scheduleActualValue = scheduleActualValue;
  }

  public BigDecimal getScopeVariation() {
    return scopeVariation;
  }

  public void setScopeVariation(BigDecimal scopeVariation) {
    this.scopeVariation = scopeVariation;
  }

  public BigDecimal getScopePlannedVariationPercent() {
    return scopePlannedVariationPercent;
  }

  public void setScopePlannedVariationPercent(BigDecimal scopePlannedVariationPercent) {
    this.scopePlannedVariationPercent = scopePlannedVariationPercent;
  }

  public BigDecimal getScopeForeseenVariationPercent() {
    return scopeForeseenVariationPercent;
  }

  public void setScopeForeseenVariationPercent(BigDecimal scopeForeseenVariationPercent) {
    this.scopeForeseenVariationPercent = scopeForeseenVariationPercent;
  }

  public BigDecimal getScopeActualVariationPercent() {
    return scopeActualVariationPercent;
  }

  public void setScopeActualVariationPercent(BigDecimal scopeActualVariationPercent) {
    this.scopeActualVariationPercent = scopeActualVariationPercent;
  }

  public BigDecimal getScopePlannedValue() {
    return scopePlannedValue;
  }

  public void setScopePlannedValue(BigDecimal scopePlannedValue) {
    this.scopePlannedValue = scopePlannedValue;
  }

  public BigDecimal getScopeForeseenValue() {
    return scopeForeseenValue;
  }

  public void setScopeForeseenValue(BigDecimal scopeForeseenValue) {
    this.scopeForeseenValue = scopeForeseenValue;
  }

  @Transient
  public void retain(TripleConstraint tripleConstraint) {
    this.setIdBaseline(tripleConstraint.getIdBaseline());
    this.setCostActualValue(tripleConstraint.getCostActualValue());
    this.setCostVariation(tripleConstraint.getCostVariation());
    this.setCostPlannedValue(tripleConstraint.getCostPlannedValue());
    this.setCostForeseenValue(tripleConstraint.getCostForeseenValue());
    this.setSchedulePlannedStartDate(tripleConstraint.getSchedulePlannedStartDate());
    this.setSchedulePlannedEndDate(tripleConstraint.getSchedulePlannedEndDate());
    this.setScheduleForeseenStartDate(tripleConstraint.getScheduleForeseenStartDate());
    this.setScheduleForeseenEndDate(tripleConstraint.getScheduleForeseenEndDate());
    this.setScheduleActualStartDate(tripleConstraint.getScheduleActualStartDate());
    this.setScheduleActualEndDate(tripleConstraint.getScheduleActualEndDate());
    this.setScheduleVariation(tripleConstraint.getScheduleVariation());
    this.setSchedulePlannedValue(tripleConstraint.getSchedulePlannedValue());
    this.setScheduleForeseenValue(tripleConstraint.getScheduleForeseenValue());
    this.setScheduleActualValue(tripleConstraint.getScheduleActualValue());
    this.setScopeVariation(tripleConstraint.getScopeVariation());
    this.setScopePlannedVariationPercent(tripleConstraint.getScopePlannedVariationPercent());
    this.setScopeForeseenVariationPercent(tripleConstraint.getScopeForeseenVariationPercent());
    this.setScopeActualVariationPercent(tripleConstraint.getScopeActualVariationPercent());
    this.setScopePlannedValue(tripleConstraint.getScopePlannedValue());
    this.setScopeForeseenValue(tripleConstraint.getScopeForeseenValue());
  }

  @Transient
  public static TripleConstraint of(TripleConstraintDataChart dataChart) {
    if (dataChart == null) {
      return null;
    }
    final TripleConstraint tripleConstraint = new TripleConstraint();
    tripleConstraint.setMonth(new DashboardMonth(dataChart.getMesAno()));
    tripleConstraint.setIdBaseline(dataChart.getIdBaseline());
    tripleConstraint.setCostActualValue(dataChart.getCost().getActualValue());
    tripleConstraint.setCostVariation(dataChart.getCost().getVariation());
    tripleConstraint.setCostPlannedValue(dataChart.getCost().getPlannedValue());
    tripleConstraint.setCostForeseenValue(dataChart.getCost().getForeseenValue());
    tripleConstraint.setSchedulePlannedStartDate(dataChart.getSchedule().getPlannedStartDate());
    tripleConstraint.setSchedulePlannedEndDate(dataChart.getSchedule().getPlannedEndDate());
    tripleConstraint.setScheduleForeseenStartDate(dataChart.getSchedule().getForeseenStartDate());
    tripleConstraint.setScheduleForeseenEndDate(dataChart.getSchedule().getForeseenEndDate());
    tripleConstraint.setScheduleActualStartDate(dataChart.getSchedule().getActualStartDate());
    tripleConstraint.setScheduleActualEndDate(dataChart.getSchedule().getActualEndDate());
    tripleConstraint.setScheduleVariation(dataChart.getSchedule().getVariation());
    tripleConstraint.setSchedulePlannedValue(dataChart.getSchedule().getPlannedValue());
    tripleConstraint.setScheduleForeseenValue(dataChart.getSchedule().getForeseenValue());
    tripleConstraint.setScheduleActualValue(dataChart.getSchedule().getActualValue());
    tripleConstraint.setScopeVariation(dataChart.getScope().getVariation());
    tripleConstraint.setScopePlannedVariationPercent(dataChart.getScope().getPlannedVariationPercent());
    tripleConstraint.setScopeForeseenVariationPercent(dataChart.getScope().getForeseenVariationPercent());
    tripleConstraint.setScopeActualVariationPercent(dataChart.getScope().getActualVariationPercent());
    tripleConstraint.setScopePlannedValue(dataChart.getScope().getPlannedWork());
    tripleConstraint.setScopeForeseenValue(dataChart.getScope().getForeseenWork());
    return tripleConstraint;
  }
}
