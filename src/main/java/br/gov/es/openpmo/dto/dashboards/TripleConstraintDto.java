package br.gov.es.openpmo.dto.dashboards;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TripleConstraintDto {

  private BigDecimal costVariation;
  private BigDecimal costPlannedValue;
  private BigDecimal costForeseenValue;
  private BigDecimal costActualValue;

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

  private BigDecimal scopeActualValue;
  private BigDecimal scopeVariation;
  private BigDecimal scopePlannedVariationPercent;
  private BigDecimal scopeForeseenVariationPercent;
  private BigDecimal scopeActualVariationPercent;
  private BigDecimal scopePlannedValue;
  private BigDecimal scopeForeseenValue;
  private BigDecimal scopeForeseenWorkRefMonth;

  public static TripleConstraintDto of(DashboardDto dashboardDto) {
    final TripleConstraintDto dto = new TripleConstraintDto();
    dto.setCostVariation(dashboardDto.getCostVariation());
    dto.setCostPlannedValue(dashboardDto.getPlannedCost());
    dto.setCostForeseenValue(dashboardDto.getForeseenCost());
    dto.setCostActualValue(dashboardDto.getActualCost());
    dto.setSchedulePlannedStartDate(dashboardDto.getBaselineStart());
    dto.setSchedulePlannedEndDate(dashboardDto.getBaselineEnd());
    dto.setScheduleForeseenStartDate(dashboardDto.getStart());
    dto.setScheduleForeseenEndDate(dashboardDto.getEnd());
    dto.setScheduleActualStartDate(dashboardDto.getScheduleActualStartDate());
    dto.setScheduleActualEndDate(dashboardDto.getScheduleActualEndDate());
    dto.setScheduleVariation(dashboardDto.getScheduleVariation());
    dto.setSchedulePlannedValue(dashboardDto.getSchedulePlannedValue());
    dto.setScheduleForeseenValue(dashboardDto.getScheduleForeseenValue());
    dto.setScheduleActualValue(dashboardDto.getScheduleActualValue());
    dto.setScopePlannedValue(dashboardDto.getPlannedWork());
    dto.setScopeActualValue(dashboardDto.getActualWork());
    dto.setScopeForeseenValue(dashboardDto.getForeseenWork());
    dto.setScopePlannedVariationPercent(dashboardDto.getScopePlannedVariationPercent());
    dto.setScopeForeseenVariationPercent(dashboardDto.getScopeForeseenVariationPercent());
    dto.setScopeVariation(dashboardDto.getScopeVariation());
    dto.setScopeActualVariationPercent(dashboardDto.getScopeActualVariationPercent());
    dto.setScopeForeseenWorkRefMonth(dashboardDto.getForeseenWorkRefMonth());
    return dto;
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

  public BigDecimal getCostActualValue() {
    return costActualValue;
  }

  public void setCostActualValue(BigDecimal costActualValue) {
    this.costActualValue = costActualValue;
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

  public BigDecimal getScopeActualValue() {
    return scopeActualValue;
  }

  public void setScopeActualValue(BigDecimal scopeActualValue) {
    this.scopeActualValue = scopeActualValue;
  }

  public BigDecimal getScopeForeseenWorkRefMonth() {
    return scopeForeseenWorkRefMonth;
  }

  public void setScopeForeseenWorkRefMonth(BigDecimal scopeForeseenWorkRefMonth) {
    this.scopeForeseenWorkRefMonth = scopeForeseenWorkRefMonth;
  }
}
