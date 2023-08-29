package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.model.Entity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.math.BigDecimal;

@NodeEntity
public class PerformanceIndexes extends Entity {

  @Relationship("IS_AT")
  private DashboardMonth month;

  private BigDecimal actualCost;

  private BigDecimal plannedValue;

  private BigDecimal earnedValue;

  private BigDecimal estimatesAtCompletion;

  private BigDecimal estimateToComplete;

  private BigDecimal costPerformanceIndexValue;

  private BigDecimal costPerformanceIndexVariation;

  private BigDecimal schedulePerformanceIndexValue;

  private BigDecimal schedulePerformanceIndexVariation;

  public DashboardMonth getMonth() {
    return month;
  }

  public void setMonth(DashboardMonth month) {
    this.month = month;
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

  public BigDecimal getEarnedValue() {
    return earnedValue;
  }

  public void setEarnedValue(BigDecimal earnedValue) {
    this.earnedValue = earnedValue;
  }

  public BigDecimal getEstimatesAtCompletion() {
    return estimatesAtCompletion;
  }

  public void setEstimatesAtCompletion(BigDecimal estimatesAtCompletion) {
    this.estimatesAtCompletion = estimatesAtCompletion;
  }

  public BigDecimal getEstimateToComplete() {
    return estimateToComplete;
  }

  public void setEstimateToComplete(BigDecimal estimateToComplete) {
    this.estimateToComplete = estimateToComplete;
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

  public void retain(PerformanceIndexes perfomanceIndexes) {
    this.setActualCost(perfomanceIndexes.getActualCost());
    this.setPlannedValue(perfomanceIndexes.getPlannedValue());
    this.setEarnedValue(perfomanceIndexes.getEarnedValue());
    this.setEstimatesAtCompletion(perfomanceIndexes.getEstimatesAtCompletion());
    this.setEstimateToComplete(perfomanceIndexes.getEstimateToComplete());
    this.setCostPerformanceIndexValue(perfomanceIndexes.getCostPerformanceIndexValue());
    this.setCostPerformanceIndexVariation(perfomanceIndexes.getCostPerformanceIndexVariation());
    this.setSchedulePerformanceIndexValue(perfomanceIndexes.getSchedulePerformanceIndexValue());
    this.setSchedulePerformanceIndexVariation(perfomanceIndexes.getSchedulePerformanceIndexVariation());
  }

  public static PerformanceIndexes of(PerformanceIndexesData data) {
    final PerformanceIndexes performanceIndexes = new PerformanceIndexes();
    performanceIndexes.setMonth(new DashboardMonth(data.getDate().atEndOfMonth()));
    performanceIndexes.setActualCost(data.getActualCost());
    performanceIndexes.setPlannedValue(data.getPlannedValue());
    performanceIndexes.setEarnedValue(data.getEarnedValue());
    performanceIndexes.setEstimatesAtCompletion(data.getEstimatesAtCompletion());
    performanceIndexes.setEstimateToComplete(data.getEstimateToComplete());
    if (data.getCostPerformanceIndex() != null) {
      performanceIndexes.setCostPerformanceIndexValue(data.getCostPerformanceIndex().getIndexValue());
      performanceIndexes.setCostPerformanceIndexVariation(data.getCostPerformanceIndex().getCostVariation());
    }
    if (data.getSchedulePerformanceIndex() != null) {
      performanceIndexes.setSchedulePerformanceIndexValue(data.getSchedulePerformanceIndex().getIndexValue());
      performanceIndexes.setSchedulePerformanceIndexVariation(data.getSchedulePerformanceIndex().getScheduleVariation());
    }
    return performanceIndexes;
  }

}
