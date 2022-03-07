package br.gov.es.openpmo.dto.dashboards.tripleconstraint;

import br.gov.es.openpmo.dto.baselines.ccbmemberview.UnitCostCalculator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.ONE_HUNDRED;
import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.roundOneDecimal;

public class ScopeDataChart {
    private BigDecimal variation;
    private BigDecimal plannedVariationPercent;
    private BigDecimal foreseenVariationPercent;
    private BigDecimal actualVariationPercent;
    @JsonProperty("plannedValue")
    private BigDecimal plannedWork;
    @JsonProperty("foreseenValue")
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

    public void setVariation(BigDecimal variation) {
        this.variation = variation;
    }

    public BigDecimal getPlannedVariationPercent() {
        return this.plannedVariationPercent;
    }

    public void setPlannedVariationPercent(BigDecimal plannedVariationPercent) {
        this.plannedVariationPercent = plannedVariationPercent;
    }

    public BigDecimal getForeseenVariationPercent() {
        return this.foreseenVariationPercent;
    }

    public void setForeseenVariationPercent(BigDecimal foreseenVariationPercent) {
        this.foreseenVariationPercent = foreseenVariationPercent;
    }

    public BigDecimal getActualVariationPercent() {
        return this.actualVariationPercent;
    }

    public void setActualVariationPercent(BigDecimal actualVariationPercent) {
        this.actualVariationPercent = actualVariationPercent;
    }

    public BigDecimal getPlannedWork() {
        return this.plannedWork;
    }

    public void setPlannedWork(BigDecimal plannedWork) {
        this.plannedWork = plannedWork;
    }

    public BigDecimal getForeseenWork() {
        return this.foreseenWork;
    }

    public void setForeseenWork(BigDecimal foreseenWork) {
        this.foreseenWork = foreseenWork;
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

    public void sumPlannedValue(final BigDecimal value) {
        if (value == null) {
            return;
        }

        this.plannedWork = Optional.ofNullable(this.plannedWork)
                .orElse(BigDecimal.ZERO)
                .add(value);
    }

    public void sumActualValue(final BigDecimal value) {
        if (value == null) {
            return;
        }

        this.actualWork = Optional.ofNullable(this.actualWork)
                .orElse(BigDecimal.ZERO)
                .add(value);
    }

    public void sumForeseenValue(final BigDecimal value) {
        if (value == null) {
            return;
        }

        this.foreseenWork = Optional.ofNullable(this.foreseenWork)
                .orElse(BigDecimal.ZERO)
                .add(value);
    }

    private void sumVariationCost(final ScopeDataChart scopeData) {
        if (this.costDataChart == null) {
            this.costDataChart = new CostDataChart();
        }

        this.costDataChart.sumCostData(scopeData.costDataChart);

        if (this.variationCostBetweenPlannedAndForeseen == null) {
            this.variationCostBetweenPlannedAndForeseen = BigDecimal.ZERO;
        }

        if (this.variationCostBetweenPlannedAndActual == null) {
            this.variationCostBetweenPlannedAndActual = BigDecimal.ZERO;
        }

        final BigDecimal deltaPlannedForeseen = scopeData.calculateDeltaBetweenPlannedAndForeseen();
        final BigDecimal deltaPlannedActual = scopeData.calculateDeltaBetweenPlannedAndActual();

        this.variationCostBetweenPlannedAndForeseen =
                this.variationCostBetweenPlannedAndForeseen.add(deltaPlannedForeseen);

        this.variationCostBetweenPlannedAndActual =
                this.variationCostBetweenPlannedAndActual.add(deltaPlannedActual);
    }

    private void calculateVariation() {
        if (this.costDataChart.getPlannedValue() == null
                || Objects.equals(this.costDataChart.getPlannedValue(), BigDecimal.ZERO)
        ) {
            this.plannedVariationPercent = null;
            this.foreseenVariationPercent = ONE_HUNDRED;
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

    private BigDecimal calculateDeltaBetweenPlannedAndForeseen() {
        this.calculateUnitCost();
        final BigDecimal difference;
        if (this.plannedWork == null) {
            difference = Optional.ofNullable(this.foreseenWork)
                    .map(BigDecimal::negate)
                    .orElse(BigDecimal.ZERO);
        } else {
            difference = this.plannedWork.subtract(this.foreseenWork);
        }
        return difference.multiply(this.unitCost);
    }

    private BigDecimal calculateDeltaBetweenPlannedAndActual() {
        this.calculateUnitCost();
        final BigDecimal difference;
        if (this.plannedWork == null) {
            difference = Optional.ofNullable(this.actualWork)
                    .map(BigDecimal::negate)
                    .orElse(BigDecimal.ZERO);
        } else {
            difference = this.plannedWork.subtract(Optional.ofNullable(this.actualWork).orElse(BigDecimal.ZERO));
        }
        return difference.multiply(this.unitCost);
    }

    public void calculateUnitCost() {
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

}
