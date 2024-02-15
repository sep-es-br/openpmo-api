package br.gov.es.openpmo.dto.dashboards;


import java.math.BigDecimal;

public class PerformanceIndexDto {

  private BigDecimal costPerformanceIndexValue;

  private BigDecimal costPerformanceIndexVariation;

  private BigDecimal schedulePerformanceIndexValue;

  private BigDecimal schedulePerformanceIndexVariation;

  private BigDecimal estimateToComplete;

  private BigDecimal estimatesAtCompletion;

  private BigDecimal earnedValue;

  private BigDecimal actualCost;

  private BigDecimal plannedValue;

  public static PerformanceIndexDto of(DashboardDto dashboardDto) {
    final PerformanceIndexDto performanceIndexDto = new PerformanceIndexDto();
    performanceIndexDto.setEarnedValue(dashboardDto.getEarnedValue());
    performanceIndexDto.setCostPerformanceIndexValue(dashboardDto.getCostPerformanceIndexValue());
    performanceIndexDto.setCostPerformanceIndexVariation(dashboardDto.getCostPerformanceIndexVariation());
    performanceIndexDto.setSchedulePerformanceIndexValue(dashboardDto.getSchedulePerformanceIndexValue());
    performanceIndexDto.setSchedulePerformanceIndexVariation(dashboardDto.getSchedulePerformanceIndexVariation());
    performanceIndexDto.setActualCost(dashboardDto.getActualCost());
    performanceIndexDto.setPlannedValue(dashboardDto.getPlannedCost());
    performanceIndexDto.setEstimatesAtCompletion(dashboardDto.getEstimatesAtCompletion());
    performanceIndexDto.setEstimateToComplete(dashboardDto.getEstimateToComplete());
    return performanceIndexDto;
  }

  public BigDecimal getCostPerformanceIndexValue() {
    return costPerformanceIndexValue;
  }

  public void setCostPerformanceIndexValue(BigDecimal costPerformanceIndexValue) {
    this.costPerformanceIndexValue = costPerformanceIndexValue;
  }

  public BigDecimal getCostPerformanceIndexVariation() {
    return costPerformanceIndexVariation;
  }

  public void setCostPerformanceIndexVariation(BigDecimal costPerformanceIndexVariation) {
    this.costPerformanceIndexVariation = costPerformanceIndexVariation;
  }

  public BigDecimal getSchedulePerformanceIndexValue() {
    return schedulePerformanceIndexValue;
  }

  public void setSchedulePerformanceIndexValue(BigDecimal schedulePerformanceIndexValue) {
    this.schedulePerformanceIndexValue = schedulePerformanceIndexValue;
  }

  public BigDecimal getSchedulePerformanceIndexVariation() {
    return schedulePerformanceIndexVariation;
  }

  public void setSchedulePerformanceIndexVariation(BigDecimal schedulePerformanceIndexVariation) {
    this.schedulePerformanceIndexVariation = schedulePerformanceIndexVariation;
  }

  public BigDecimal getEarnedValue() {
    return this.earnedValue;
  }

  public void setEarnedValue(final BigDecimal earnedValue) {
    this.earnedValue = earnedValue;
  }

  public BigDecimal getEstimateToComplete() {
    return estimateToComplete;
  }

  public void setEstimateToComplete(BigDecimal estimateToComplete) {
    this.estimateToComplete = estimateToComplete;
  }

  public BigDecimal getEstimatesAtCompletion() {
    return estimatesAtCompletion;
  }

  public void setEstimatesAtCompletion(BigDecimal estimatesAtCompletion) {
    this.estimatesAtCompletion = estimatesAtCompletion;
  }

  public BigDecimal getActualCost() {
    return actualCost;
  }

  public void setActualCost(BigDecimal actualCost) {
    this.actualCost = actualCost;
  }

  public BigDecimal getPlannedValue() {
    return plannedValue;
  }

  public void setPlannedValue(BigDecimal plannedValue) {
    this.plannedValue = plannedValue;
  }
}
