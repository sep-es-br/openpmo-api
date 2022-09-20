package br.gov.es.openpmo.dto.dashboards.earnevalueanalysis;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.YearMonth;

public class EarnedValueByStep {

  private BigDecimal plannedValue;

  private BigDecimal actualCost;

  private BigDecimal earnedValue;

  @JsonFormat(pattern = "yyyy-MM")
  private YearMonth date;

  @JsonIgnore
  private BigDecimal plannedWork;

  @JsonIgnore
  private BigDecimal actualWork;

  public static EarnedValueByStep zeroValue() {
    final EarnedValueByStep result = new EarnedValueByStep();
    result.plannedValue = BigDecimal.ZERO;
    result.actualCost = BigDecimal.ZERO;
    result.plannedWork = BigDecimal.ZERO;
    result.actualWork = BigDecimal.ZERO;
    return result;
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

  public EarnedValueByStep copy(final boolean all) {
    final EarnedValueByStep obj = new EarnedValueByStep();

    obj.plannedValue = this.plannedValue;
    obj.plannedWork = this.plannedWork;
    obj.actualWork = this.actualWork;
    obj.date = this.date;

    if(all) {
      obj.actualCost = this.actualCost;
      obj.earnedValue = this.earnedValue;
    }
    else {
      obj.actualCost = BigDecimal.ZERO;
      obj.earnedValue = BigDecimal.ZERO;
    }

    return obj;
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

  public BigDecimal getEarnedValue() {
    return this.earnedValue;
  }

  public void setEarnedValue(final BigDecimal earnedValue) {
    this.earnedValue = earnedValue;
  }

  public YearMonth getDate() {
    return this.date;
  }

  public void setDate(final YearMonth date) {
    this.date = date;
  }

  public void add(final EarnedValueByStep other) {
    this.plannedValue = this.plannedValue.add(other.plannedValue);
    this.actualCost = this.actualCost.add(other.actualCost);
    this.plannedWork = this.plannedWork.add(other.plannedWork);
    this.actualWork = this.actualWork.add(other.actualWork);
    this.earnedValue = this.calculateEarnedValue();
    this.date = other.date;
  }

  private BigDecimal calculateEarnedValue() {
    if(BigDecimal.ZERO.compareTo(this.plannedWork) == 0) {
      return BigDecimal.ZERO;
    }

    return this.plannedValue
      .divide(this.plannedWork, new MathContext(4, RoundingMode.HALF_EVEN))
      .multiply(this.actualWork);
  }

}
