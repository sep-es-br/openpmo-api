package br.gov.es.openpmo.dto.dashboards.tripleconstraint;

import br.gov.es.openpmo.dto.baselines.ccbmemberview.UnitCostCalculator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.ONE_HUNDRED;
import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.roundOneDecimal;

public class ScopeDataChart {
  private BigDecimal variation;
  private BigDecimal plannedVariationPercent;
  private BigDecimal foreseenVariationPercent;
  private BigDecimal actualVariationPercent;
  @JsonIgnore
  private BigDecimal plannedWork;
  @JsonIgnore
  private BigDecimal foreseenWork;
  @JsonIgnore
  private BigDecimal actualWork;
  @JsonIgnore
  private CostDataChart costDataChart;

  @JsonIgnore
  private BigDecimal unitCost;
  @JsonIgnore
  private BigDecimal variationCostBetweenPlannedAndForeseen;
  @JsonIgnore
  private BigDecimal variationCostBetweenPlannedAndActual;

  public BigDecimal getUnitCost() {
    return this.unitCost;
  }

  public CostDataChart getCostDataChart() {
    return this.costDataChart;
  }

  public void setCostDataChart(final CostDataChart costDataChart) {
    this.costDataChart = costDataChart;
  }

  public BigDecimal getVariation() {
    return this.variation;
  }

  public BigDecimal getPlannedVariationPercent() {
    return this.plannedVariationPercent;
  }

  public BigDecimal getForeseenVariationPercent() {
    return this.foreseenVariationPercent;
  }

  public BigDecimal getActualVariationPercent() {
    return this.actualVariationPercent;
  }

  public BigDecimal getPlannedWork() {
    return this.plannedWork;
  }

  public BigDecimal getForeseenWork() {
    return this.foreseenWork;
  }

  public BigDecimal getActualWork() {
    return this.actualWork;
  }

  public void sumScopeData(final ScopeDataChart scopeData) {
    this.sumPlannedValue(scopeData.plannedWork);
    this.sumActualValue(scopeData.actualWork);
    this.sumForeseenValue(scopeData.foreseenWork);
    this.sumVariationCost(scopeData);
    this.calculateVariation();
  }

  private void sumVariationCost(final ScopeDataChart scopeData) {
    if(this.costDataChart == null) {
      this.costDataChart = new CostDataChart();
    }
    this.costDataChart.sumCostData(scopeData.costDataChart);

    if(this.variationCostBetweenPlannedAndForeseen == null) {
      this.variationCostBetweenPlannedAndForeseen = BigDecimal.ZERO;
    }

    if(this.variationCostBetweenPlannedAndActual == null) {
      this.variationCostBetweenPlannedAndActual = BigDecimal.ZERO;
    }

    final BigDecimal deltaPlannedForeseen = scopeData.calculateDeltaBetweenPlannedAndForeseen();
    this.variationCostBetweenPlannedAndForeseen =
      this.variationCostBetweenPlannedAndForeseen.add(deltaPlannedForeseen);

    final BigDecimal deltaPlannedActual = scopeData.calculateDeltaBetweenPlannedAndActual();

    this.variationCostBetweenPlannedAndActual =
      this.variationCostBetweenPlannedAndActual.add(deltaPlannedActual);
  }

  private BigDecimal calculateDeltaBetweenPlannedAndActual() {
    this.calculateUnitCost();
    final BigDecimal difference;
    if(this.plannedWork == null) {
      difference = Optional.ofNullable(this.actualWork)
        .map(BigDecimal::negate)
        .orElse(BigDecimal.ZERO);
    }
    else {
      difference = this.plannedWork.subtract(this.actualWork);
    }
    return difference.multiply(this.unitCost);
  }

  private BigDecimal calculateDeltaBetweenPlannedAndForeseen() {
    this.calculateUnitCost();
    final BigDecimal difference;
    if(this.plannedWork == null) {
      difference = Optional.ofNullable(this.foreseenWork)
        .map(BigDecimal::negate)
        .orElse(BigDecimal.ZERO);
    }
    else {
      difference = this.plannedWork.subtract(this.foreseenWork);
    }
    return difference.multiply(this.unitCost);
  }

  public void calculateUnitCost() {
    this.unitCost = new UnitCostCalculator(
      this.costDataChart.getForeseenValue(),
      this.foreseenWork,
      this.costDataChart.getPlannedValue(),
      this.plannedWork
    ).calculate();
  }

  private void calculateVariation() {

    if(this.costDataChart.getPlannedValue() == null) {
      this.plannedVariationPercent = ONE_HUNDRED;
      this.foreseenVariationPercent = null;
      this.actualVariationPercent = null;
      return;
    }

    this.variation = this.variationCostBetweenPlannedAndForeseen
      .divide(this.costDataChart.getPlannedValue(), 6, RoundingMode.HALF_UP)
      .multiply(ONE_HUNDRED);

    this.plannedVariationPercent = ONE_HUNDRED;
    this.foreseenVariationPercent = this.plannedVariationPercent.subtract(this.variation);


    final BigDecimal actualVariation = this.variationCostBetweenPlannedAndActual
      .divide(this.costDataChart.getPlannedValue(), 6, RoundingMode.HALF_UP)
      .multiply(ONE_HUNDRED);

    this.actualVariationPercent = this.plannedVariationPercent.subtract(actualVariation);
  }

  public void sumPlannedValue(final BigDecimal value) {
    if(value == null) return;
    if(this.plannedWork == null) this.plannedWork = BigDecimal.ZERO;
    this.plannedWork = this.plannedWork.add(value);
  }

  public void sumActualValue(final BigDecimal value) {
    if(value == null) return;
    if(this.actualWork == null) this.actualWork = BigDecimal.ZERO;

    this.actualWork = this.actualWork.add(value);
  }

  public void sumForeseenValue(final BigDecimal value) {
    if(value == null) return;
    if(this.foreseenWork == null) this.foreseenWork = BigDecimal.ZERO;

    this.foreseenWork = this.foreseenWork.add(value);
  }

  public void round() {
    this.variation = roundOneDecimal(this.variation);
    this.plannedVariationPercent = roundOneDecimal(this.plannedVariationPercent);
    this.actualVariationPercent = roundOneDecimal(this.actualVariationPercent);
    this.foreseenVariationPercent = roundOneDecimal(this.foreseenVariationPercent);
  }
}
