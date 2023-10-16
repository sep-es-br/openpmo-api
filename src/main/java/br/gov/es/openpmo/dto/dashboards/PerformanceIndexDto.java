package br.gov.es.openpmo.dto.dashboards;

import br.gov.es.openpmo.model.dashboards.PerformanceIndexes;

import java.math.BigDecimal;

public class PerformanceIndexDto {

  private BigDecimal costPerformanceIndexValue;

  private BigDecimal costPerformanceIndexVariation;

  private BigDecimal schedulePerformanceIndexValue;

  private BigDecimal schedulePerformanceIndexVariation;

  private BigDecimal earnedValue;

  private Long idBaseline;

  public static PerformanceIndexDto of(PerformanceIndexes performanceIndexes) {
    final PerformanceIndexDto performanceIndexDto = new PerformanceIndexDto();
    performanceIndexDto.setCostPerformanceIndexValue(performanceIndexes.getCostPerformanceIndexValue());
    performanceIndexDto.setCostPerformanceIndexVariation(performanceIndexes.getCostPerformanceIndexVariation());
    performanceIndexDto.setSchedulePerformanceIndexValue(performanceIndexes.getSchedulePerformanceIndexValue());
    performanceIndexDto.setSchedulePerformanceIndexVariation(performanceIndexes.getSchedulePerformanceIndexVariation());
    performanceIndexDto.setEarnedValue(performanceIndexes.getEarnedValue());
    performanceIndexDto.setIdBaseline(performanceIndexes.getIdBaseline());
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

  public Long getIdBaseline() {
    return this.idBaseline;
  }

  public void setIdBaseline(final Long idBaseline) {
    this.idBaseline = idBaseline;
  }

  public BigDecimal getEarnedValue() {
    return this.earnedValue;
  }

  public void setEarnedValue(final BigDecimal earnedValue) {
    this.earnedValue = earnedValue;
  }

}
