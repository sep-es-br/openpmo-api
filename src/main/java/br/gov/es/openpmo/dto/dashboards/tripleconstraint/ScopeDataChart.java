package br.gov.es.openpmo.dto.dashboards.tripleconstraint;

import br.gov.es.openpmo.dto.baselines.ccbmemberview.UnitCostCalculator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.ONE_HUNDRED;
import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.roundOneDecimal;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

public class ScopeDataChart {

  private BigDecimal variation;
  private BigDecimal plannedVariationPercent;
  private BigDecimal foreseenVariationPercent;
  private BigDecimal actualVariationPercent;
  @JsonProperty("plannedValue")
  private BigDecimal plannedWork;
  @JsonProperty("foreseenValue")
  private BigDecimal foreseenWork;
  @JsonProperty
  private BigDecimal totalPlannedWork;
  @JsonIgnore
  private BigDecimal actualWork;
  @JsonIgnore
  private CostDataChart costDataChart;
  @JsonIgnore
  private BigDecimal unitCost;
  @JsonIgnore
  private BigDecimal totalForeseenVariation;
  @JsonIgnore
  private BigDecimal totalUnitCost;
  @JsonIgnore
  private BigDecimal actualVariation;
  @JsonIgnore
  private BigDecimal totalActualVariation;
  @JsonIgnore
  private BigDecimal foreseenVariation;

  private static void nullSafetyAccumulate(
    final Consumer<? super BigDecimal> updateValue,
    final Supplier<BigDecimal> currentValue,
    final Supplier<? extends BigDecimal> newValue
  ) {
    if(newValue.get() == null) return;
    updateValue.accept(
      Optional.ofNullable(currentValue.get())
        .orElse(ZERO)
        .add(newValue.get())
    );
  }

  public void setCostDataChart(final CostDataChart costDataChart) {
    this.costDataChart = costDataChart;
  }

  public BigDecimal getPlannedVariationPercent() {
    return this.plannedVariationPercent;
  }

  public void setPlannedVariationPercent(final BigDecimal plannedVariationPercent) {
    this.plannedVariationPercent = plannedVariationPercent;
  }

  public BigDecimal getForeseenVariationPercent() {
    return this.foreseenVariationPercent;
  }

  public void setForeseenVariationPercent(final BigDecimal foreseenVariationPercent) {
    this.foreseenVariationPercent = foreseenVariationPercent;
  }

  public BigDecimal getActualVariationPercent() {
    return this.actualVariationPercent;
  }

  public void setActualVariationPercent(final BigDecimal actualVariationPercent) {
    this.actualVariationPercent = actualVariationPercent;
  }

  public BigDecimal getPlannedWork() {
    return this.plannedWork;
  }

  public void setPlannedWork(final BigDecimal plannedWork) {
    this.plannedWork = plannedWork;
  }

  public BigDecimal getForeseenWork() {
    return this.foreseenWork;
  }

  public void setForeseenWork(final BigDecimal foreseenWork) {
    this.foreseenWork = foreseenWork;
  }

  public BigDecimal getActualWork() {
    return this.actualWork;
  }

  public void setActualWork(final BigDecimal actualWork) {
    this.actualWork = actualWork;
  }

  public void sumScopeData(final ScopeDataChart scopeData) {
    scopeData.calculateVariation();
    this.sumPlannedValue(scopeData.plannedWork);
    this.sumActualValue(scopeData.actualWork);
    this.sumForeseenValue(scopeData.foreseenWork);
    this.sumVariationCost(scopeData);
    this.sumTotalUnitCost(scopeData);
    this.calculateTotalVariation(scopeData);
  }

  private void calculateTotalVariation(final ScopeDataChart scopeData) {
    this.totalPlannedWork = Optional.ofNullable(this.totalPlannedWork)
      .orElse(ZERO)
      .add(Optional.ofNullable(scopeData.plannedWork).orElse(ZERO));

    this.totalForeseenVariation = Optional.ofNullable(this.totalForeseenVariation)
      .orElse(ZERO)
      .add(scopeData.foreseenVariation);

    this.variation = this.totalForeseenVariation
      .divide(this.totalUnitCost, 6, RoundingMode.HALF_UP)
      .multiply(ONE_HUNDRED);

    this.totalActualVariation = Optional.ofNullable(this.totalActualVariation)
      .orElse(ZERO)
      .add(scopeData.actualVariation);

    if(this.totalPlannedWork.compareTo(ZERO) == 0) {
      this.foreseenVariationPercent = ONE_HUNDRED;
      this.variation = null;
      this.plannedVariationPercent = null;
    }
    else {
      this.plannedVariationPercent = ONE_HUNDRED;
      this.foreseenVariationPercent = this.plannedVariationPercent.add(this.variation);
    }

    final BigDecimal totalActualVariationMultipliedByTotalUnitCost = this.totalActualVariation
      .divide(this.totalUnitCost, 6, RoundingMode.HALF_UP)
      .multiply(ONE_HUNDRED);

    this.actualVariationPercent =
      Optional.ofNullable(this.plannedVariationPercent)
        .orElse(this.foreseenVariationPercent)
        .add(totalActualVariationMultipliedByTotalUnitCost);
  }

  private void sumTotalUnitCost(final ScopeDataChart scopeData) {
    if(this.totalUnitCost == null) {
      this.calculateUnitCost();
      this.totalUnitCost = this.unitCost;
      return;
    }
    scopeData.calculateUnitCost();
    this.totalUnitCost = this.totalUnitCost.add(scopeData.unitCost);
  }

  public void sumPlannedValue(final BigDecimal value) {
    nullSafetyAccumulate(
      this::setPlannedWork,
      this::getPlannedWork,
      () -> value
    );
  }

  public void sumActualValue(final BigDecimal value) {
    nullSafetyAccumulate(
      this::setActualWork,
      this::getActualWork,
      () -> value
    );
  }

  public void sumForeseenValue(final BigDecimal value) {
    nullSafetyAccumulate(
      this::setForeseenWork,
      this::getForeseenWork,
      () -> value
    );
  }

  private void sumVariationCost(final ScopeDataChart scopeData) {
    if(this.costDataChart == null) {
      this.costDataChart = new CostDataChart();
    }
    this.costDataChart.sumCostData(scopeData.costDataChart);
  }

  private void calculateVariation() {
    this.calculateUnitCost();

    if(this.plannedWork == null || this.plannedWork.compareTo(ZERO) == 0) {
      this.foreseenVariation = this.unitCost;
    }
    else {
      final BigDecimal plannedProportional = Optional.ofNullable(this.foreseenWork)
        .orElse(ZERO)
        .divide(this.plannedWork, 6, RoundingMode.HALF_UP);

      this.foreseenVariation = plannedProportional.subtract(ONE)
        .multiply(this.unitCost);
    }

    final BigDecimal actualProportional;

    if(this.actualWork == null || this.actualWork.compareTo(ZERO) == 0) {
      actualProportional = ZERO;
    }
    else {
      actualProportional = this.actualWork.divide(
        Optional.ofNullable(this.plannedWork)
          .orElse(this.foreseenWork),
        6,
        RoundingMode.HALF_UP
      );
    }

    this.actualVariation = actualProportional.subtract(ONE)
      .multiply(this.unitCost);
  }

  private void calculateUnitCost() {
    final UnitCostCalculator calculator = new UnitCostCalculator(
      this.costDataChart.getForeseenValue(),
      this.foreseenWork,
      this.costDataChart.getPlannedValue(),
      this.plannedWork
    );

    this.unitCost = calculator.calculate();
  }

  public void round() {
    this.variation = roundOneDecimal(this.variation);
    this.plannedVariationPercent = roundOneDecimal(this.plannedVariationPercent);
    this.actualVariationPercent = roundOneDecimal(this.actualVariationPercent);
    this.foreseenVariationPercent = roundOneDecimal(this.foreseenVariationPercent);
  }

  public BigDecimal getVariation() {
    return this.variation;
  }

  public void setVariation(final BigDecimal variation) {
    this.variation = variation;
  }

}
