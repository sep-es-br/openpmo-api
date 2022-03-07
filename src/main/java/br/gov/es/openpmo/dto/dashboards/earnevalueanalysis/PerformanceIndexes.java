package br.gov.es.openpmo.dto.dashboards.earnevalueanalysis;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.YearMonth;

public class PerformanceIndexes {

    private final BigDecimal actualCost;

    private final BigDecimal plannedValue;

    private final BigDecimal earnedValue;

    private final BigDecimal estimatesAtCompletion;

    private final BigDecimal estimateToComplete;

    private final CostPerformanceIndex costPerformanceIndex;

    private final SchedulePerformanceIndex schedulePerformanceIndex;

    @JsonFormat(pattern = "yyyy-MM")
    private final YearMonth date;

    public PerformanceIndexes(
            BigDecimal actualCost,
            BigDecimal plannedValue,
            BigDecimal earnedValue,
            BigDecimal estimatesAtCompletion,
            BigDecimal estimateToComplete,
            CostPerformanceIndex costPerformanceIndex,
            SchedulePerformanceIndex schedulePerformanceIndex,
            YearMonth date
    ) {
        this.actualCost = actualCost;
        this.plannedValue = plannedValue;
        this.earnedValue = earnedValue;
        this.estimatesAtCompletion = estimatesAtCompletion;
        this.estimateToComplete = estimateToComplete;
        this.costPerformanceIndex = costPerformanceIndex;
        this.schedulePerformanceIndex = schedulePerformanceIndex;
        this.date = date;
    }

    public BigDecimal getActualCost() {
        return actualCost;
    }

    public BigDecimal getPlannedValue() {
        return plannedValue;
    }

    public BigDecimal getEarnedValue() {
        return earnedValue;
    }

    public BigDecimal getEstimatesAtCompletion() {
        return estimatesAtCompletion;
    }

    public BigDecimal getEstimateToComplete() {
        return estimateToComplete;
    }

    public CostPerformanceIndex getCostPerformanceIndex() {
        return costPerformanceIndex;
    }

    public SchedulePerformanceIndex getSchedulePerformanceIndex() {
        return schedulePerformanceIndex;
    }

    public YearMonth getDate() {
        return date;
    }
}
