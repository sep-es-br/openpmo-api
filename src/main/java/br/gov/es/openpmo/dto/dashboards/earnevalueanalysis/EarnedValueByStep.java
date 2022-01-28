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

  @JsonIgnore
  private BigDecimal plannedWork;

  @JsonIgnore
  private BigDecimal actualWork;

  private BigDecimal earnedValue;

  @JsonFormat(pattern = "yyyy-MM")
  private YearMonth date;

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

  public BigDecimal getActualCost() {
    return this.actualCost;
  }

  public EarnedValueByStep copy(final boolean showActualWorkAndEarnedValue) {
    final EarnedValueByStep obj = new EarnedValueByStep();
    obj.plannedValue = this.plannedValue;
    obj.plannedWork = this.plannedWork;
    obj.actualWork = this.actualWork;
    obj.date = this.date;

    if (showActualWorkAndEarnedValue) {
      obj.actualCost = this.actualCost;
      obj.earnedValue = this.earnedValue;
    }

    return obj;
  }

  public void setPlannedValue(final BigDecimal plannedValue) {
    this.plannedValue = plannedValue;
  }

  public void setActualCost(final BigDecimal actualCost) {
    this.actualCost = actualCost;
  }

  public void setEarnedValue(final BigDecimal earnedValue) {
    this.earnedValue = earnedValue;
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

  public void setDate(final YearMonth date) {
    this.date = date;
  }

  public BigDecimal getEarnedValue() {
    return this.earnedValue;
  }

  public YearMonth getDate() {
    return this.date;
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
    if (BigDecimal.ZERO.compareTo(this.plannedValue) == 0) {
      return BigDecimal.ZERO;
    }

    return this.plannedWork.multiply(this.actualWork)
        .divide(this.plannedValue, new MathContext(4, RoundingMode.HALF_EVEN));
  }

}
