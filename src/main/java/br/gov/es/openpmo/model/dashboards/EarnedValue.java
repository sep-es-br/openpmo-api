package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.model.Entity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.math.BigDecimal;

@NodeEntity
public class EarnedValue extends Entity {

  @Relationship("IS_AT")
  private DashboardMonth month;

  private BigDecimal plannedValue;

  private BigDecimal actualCost;
  private BigDecimal estimatedCost;
  private BigDecimal earnedValue;

  @Transient
  public static EarnedValue of(final EarnedValueByStepData data) {
    final EarnedValue earnedValue = new EarnedValue();
    earnedValue.month = new DashboardMonth(data.getDate().atEndOfMonth());
    earnedValue.plannedValue = data.getPlannedValue();
    earnedValue.actualCost = data.getActualCost();
    earnedValue.estimatedCost = data.getEstimatedCost();
    earnedValue.earnedValue = data.getEarnedValue();
    return earnedValue;
  }

  public BigDecimal getEstimatedCost() {
    return this.estimatedCost;
  }

  public void setEstimatedCost(final BigDecimal estimatedCost) {
    this.estimatedCost = estimatedCost;
  }

  public DashboardMonth getMonth() {
    return this.month;
  }

  public void setMonth(final DashboardMonth month) {
    this.month = month;
  }

  public BigDecimal getPlannedValue() {
    return this.plannedValue;
  }

  public void setPlannedValue(final BigDecimal plannedValue) {
    this.plannedValue = plannedValue;
  }

  public BigDecimal getActualCost() {
    return this.actualCost;
  }

  public void setActualCost(final BigDecimal actualCost) {
    this.actualCost = actualCost;
  }

  public BigDecimal getEarnedValue() {
    return this.earnedValue;
  }

  public void setEarnedValue(final BigDecimal earnedValue) {
    this.earnedValue = earnedValue;
  }

  @Transient
  public void retain(final EarnedValue earnedValue) {
    this.plannedValue = earnedValue.plannedValue;
    this.actualCost = earnedValue.actualCost;
    this.estimatedCost = earnedValue.estimatedCost;
    this.earnedValue = earnedValue.earnedValue;
  }
}
