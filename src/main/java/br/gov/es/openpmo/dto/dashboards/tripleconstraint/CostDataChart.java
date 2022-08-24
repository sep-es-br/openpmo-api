package br.gov.es.openpmo.dto.dashboards.tripleconstraint;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.ONE_HUNDRED;
import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.roundOneDecimal;

public class CostDataChart {

  private BigDecimal actualValue;
  private BigDecimal variation;
  private BigDecimal plannedValue;
  private BigDecimal foreseenValue;

  public CostDataChart() {
  }

  public BigDecimal getVariation() {
    return this.variation;
  }

  public void setVariation(final BigDecimal variation) {
    this.variation = variation;
  }

  public void sumCostData(final CostDataChart cost) {
    this.sumForeseenValue(cost.foreseenValue);
    this.sumPlannedValue(cost.plannedValue);
    this.sumActualValue(cost.actualValue);
    this.calculateVariation();

    this.actualValue = Optional.ofNullable(this.actualValue).orElse(BigDecimal.ZERO);
    this.variation = Optional.ofNullable(this.variation).orElse(BigDecimal.ZERO);
    this.plannedValue = Optional.ofNullable(this.plannedValue).orElse(BigDecimal.ZERO);
    this.foreseenValue = Optional.ofNullable(this.foreseenValue).orElse(BigDecimal.ZERO);
  }

  public void sumForeseenValue(final BigDecimal value) {
    if(value == null) {
      return;
    }

    this.foreseenValue = Optional.ofNullable(this.foreseenValue)
      .orElse(BigDecimal.ZERO)
      .add(value);
  }

  public void sumPlannedValue(final BigDecimal value) {
    if(value == null) {
      return;
    }

    this.plannedValue = Optional.ofNullable(this.plannedValue)
      .orElse(BigDecimal.ZERO)
      .add(value);
  }

  public void sumActualValue(final BigDecimal value) {
    if(value == null) {
      return;
    }

    this.actualValue = Optional.ofNullable(this.actualValue)
      .orElse(BigDecimal.ZERO)
      .add(value);
  }

  private void calculateVariation() {
    if(this.plannedValue == null || BigDecimal.ZERO.compareTo(this.plannedValue) == 0) {
      return;
    }

    final BigDecimal difference = this.plannedValue.subtract(this.foreseenValue);

    if(difference.compareTo(BigDecimal.ZERO) == 0) {
      this.variation = null;
      return;
    }

    this.variation = difference
      .divide(this.plannedValue, 6, RoundingMode.HALF_UP)
      .multiply(ONE_HUNDRED);
  }

  public BigDecimal getPlannedValue() {
    return this.plannedValue;
  }

  public void setPlannedValue(final BigDecimal plannedValue) {
    this.plannedValue = plannedValue;
  }

  public BigDecimal getForeseenValue() {
    return this.foreseenValue;
  }

  public void setForeseenValue(final BigDecimal foreseenValue) {
    this.foreseenValue = foreseenValue;
  }

  public BigDecimal getActualValue() {
    return this.actualValue;
  }

  public void setActualValue(final BigDecimal actualValue) {
    this.actualValue = actualValue;
  }

  public void round() {
    this.variation = roundOneDecimal(this.variation);
    this.plannedValue = roundOneDecimal(this.plannedValue);
    this.foreseenValue = roundOneDecimal(this.foreseenValue);
    this.actualValue = roundOneDecimal(this.actualValue);
  }

}
