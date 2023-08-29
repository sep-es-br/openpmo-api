package br.gov.es.openpmo.dto.dashboards.earnevalueanalysis;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.Optional;

public class EarnedValueByStep {

  private BigDecimal plannedValue;

  private BigDecimal actualCost;

  private BigDecimal estimatedCost;

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
    result.estimatedCost = BigDecimal.ZERO;
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

    if (all) {
      obj.actualCost = this.actualCost;
      obj.estimatedCost = this.estimatedCost;
      obj.earnedValue = this.earnedValue;
    } else {
      obj.actualCost = BigDecimal.ZERO;
      obj.estimatedCost = BigDecimal.ZERO;
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

  public BigDecimal getEstimatedCost() {
    return this.estimatedCost;
  }

  public void setEstimatedCost(final BigDecimal estimatedCost) {
    this.estimatedCost = estimatedCost;
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
    this.estimatedCost = this.estimatedCost.add(other.estimatedCost);
    this.plannedWork = this.plannedWork.add(other.plannedWork);
    this.actualWork = this.actualWork.add(other.actualWork);
    this.earnedValue = Optional.ofNullable(this.earnedValue)
      .map(earnedValue -> earnedValue.add(this.calculateEarnedValue(other)))
      .orElseGet(() -> this.calculateEarnedValue(other));
    this.date = other.date;
  }

  private BigDecimal calculateEarnedValue(final EarnedValueByStep other) {
    if (BigDecimal.ZERO.compareTo(other.plannedWork) == 0) {
      return BigDecimal.ZERO;
    }

    return other.plannedValue
      .divide(other.plannedWork, new MathContext(4, RoundingMode.HALF_EVEN))
      .multiply(other.actualWork);
  }

}
