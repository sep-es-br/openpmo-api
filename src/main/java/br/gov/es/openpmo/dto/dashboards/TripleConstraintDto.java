package br.gov.es.openpmo.dto.dashboards;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TripleConstraintDto {

  private BigDecimal costPlannedValue;
  private BigDecimal costActualValue;

  private LocalDate schedulePlannedStartDate;
  private LocalDate schedulePlannedEndDate;
  private LocalDate scheduleActualStartDate;
  private LocalDate scheduleActualEndDate;
  private BigDecimal schedulePlannedValue;
  private BigDecimal scheduleActualValue;

  private BigDecimal scopeActualValue;
  private BigDecimal scopePlannedVariationPercent;
  private BigDecimal scopeActualVariationPercent;
  private BigDecimal scopePlannedValue;

  public static TripleConstraintDto of(DashboardDto dashboardDto) {
    final TripleConstraintDto dto = new TripleConstraintDto();
    dto.setCostPlannedValue(dashboardDto.getPlannedCost());
    dto.setCostActualValue(dashboardDto.getActualCost());
    dto.setSchedulePlannedStartDate(dashboardDto.getBaselineStart());
    dto.setSchedulePlannedEndDate(dashboardDto.getBaselineEnd());
    dto.setScheduleActualStartDate(dashboardDto.getScheduleActualStartDate());
    dto.setScheduleActualEndDate(dashboardDto.getScheduleActualEndDate());
    dto.setSchedulePlannedValue(dashboardDto.getSchedulePlannedValue());
    dto.setScheduleActualValue(dashboardDto.getScheduleActualValue());
    dto.setScopePlannedValue(dashboardDto.getPlannedWork());
    dto.setScopeActualValue(dashboardDto.getActualWork());
    dto.setScopePlannedVariationPercent(dashboardDto.getScopePlannedVariationPercent());
    dto.setScopeActualVariationPercent(dashboardDto.getScopeActualVariationPercent());
    return dto;
  }

  public BigDecimal getCostPlannedValue() {
    return costPlannedValue;
  }

  public void setCostPlannedValue(BigDecimal costPlannedValue) {
    this.costPlannedValue = costPlannedValue;
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

  public BigDecimal getSchedulePlannedValue() {
    return schedulePlannedValue;
  }

  public void setSchedulePlannedValue(BigDecimal schedulePlannedValue) {
    this.schedulePlannedValue = schedulePlannedValue;
  }

  public BigDecimal getScheduleActualValue() {
    return scheduleActualValue;
  }

  public void setScheduleActualValue(BigDecimal scheduleActualValue) {
    this.scheduleActualValue = scheduleActualValue;
  }


  public BigDecimal getScopePlannedVariationPercent() {
    return scopePlannedVariationPercent;
  }

  public void setScopePlannedVariationPercent(BigDecimal scopePlannedVariationPercent) {
    this.scopePlannedVariationPercent = scopePlannedVariationPercent;
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

  public BigDecimal getScopeActualValue() {
    return scopeActualValue;
  }

  public void setScopeActualValue(BigDecimal scopeActualValue) {
    this.scopeActualValue = scopeActualValue;
  }

}
