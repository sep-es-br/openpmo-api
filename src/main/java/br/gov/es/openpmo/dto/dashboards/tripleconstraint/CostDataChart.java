package br.gov.es.openpmo.dto.dashboards.tripleconstraint;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

  public void sumCostData(final CostDataChart cost) {
    this.sumForeseenValue(cost.foreseenValue);
    this.sumPlannedValue(cost.plannedValue);
    this.sumActualValue(cost.actualValue);
    this.calculateVariation();
  }

  private void calculateVariation() {
    final BigDecimal difference = this.plannedValue.subtract(this.foreseenValue);
    if(difference.compareTo(BigDecimal.ZERO) == 0) {
      this.variation = null;
      return;
    }
    this.variation = difference
      .divide(this.plannedValue, 6, RoundingMode.HALF_UP)
      .multiply(ONE_HUNDRED);
  }

  public void sumPlannedValue(final BigDecimal value) {
    if(value == null) return;
    if(this.plannedValue == null) {
      this.plannedValue = BigDecimal.ZERO;
    }
    this.plannedValue = this.plannedValue.add(value);
  }

  public void sumActualValue(final BigDecimal value) {
    if(value == null) return;
    if(this.actualValue == null) {
      this.actualValue = BigDecimal.ZERO;
    }
    this.actualValue = this.actualValue.add(value);
  }

  public void sumForeseenValue(final BigDecimal value) {
    if(value == null) return;
    if(this.foreseenValue == null) {
      this.foreseenValue = BigDecimal.ZERO;
    }
    this.foreseenValue = this.foreseenValue.add(value);
  }

  public BigDecimal getPlannedValue() {
    return this.plannedValue;
  }

  public BigDecimal getForeseenValue() {
    return this.foreseenValue;
  }

  public BigDecimal getActualValue() {
    return this.actualValue;
  }

  public void round() {
    this.variation = roundOneDecimal(this.variation);
    this.plannedValue = roundOneDecimal(this.plannedValue);
    this.foreseenValue = roundOneDecimal(this.foreseenValue);
    this.actualValue = roundOneDecimal(this.actualValue);
  }
}
