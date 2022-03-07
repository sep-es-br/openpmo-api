package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.PerformanceIndexes;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

import static br.gov.es.openpmo.model.dashboards.DashboardUtils.apply;

public class PerformanceIndexesData {

    private BigDecimal actualCost;

    private BigDecimal plannedValue;

    private BigDecimal earnedValue;

    private BigDecimal estimatesAtCompletion;

    private BigDecimal estimateToComplete;

    private CostPerformanceIndexData costPerformanceIndex;

    private SchedulePerformanceIndexData schedulePerformanceIndex;

    @JsonFormat(pattern = "yyyy-MM")
    private YearMonth date;

    public static PerformanceIndexesData of(PerformanceIndexes from) {
        if (from == null) {
            return null;
        }

        final PerformanceIndexesData to = new PerformanceIndexesData();

        to.setActualCost(from.getActualCost());
        to.setPlannedValue(from.getPlannedValue());
        to.setEarnedValue(from.getEarnedValue());
        to.setEstimatesAtCompletion(from.getEstimatesAtCompletion());
        to.setEstimateToComplete(from.getEstimateToComplete());
        to.setDate(from.getDate());

        apply(from.getCostPerformanceIndex(), CostPerformanceIndexData::of, to::setCostPerformanceIndex);
        apply(from.getSchedulePerformanceIndex(), SchedulePerformanceIndexData::of, to::setSchedulePerformanceIndex);

        return to;
    }

    public BigDecimal getActualCost() {
        return actualCost;
    }

    public void setActualCost(BigDecimal actualCost) {
        this.actualCost = actualCost;
    }

    public BigDecimal getPlannedValue() {
        return plannedValue;
    }

    public void setPlannedValue(BigDecimal plannedValue) {
        this.plannedValue = plannedValue;
    }

    public BigDecimal getEarnedValue() {
        return earnedValue;
    }

    public void setEarnedValue(BigDecimal earnedValue) {
        this.earnedValue = earnedValue;
    }

    public BigDecimal getEstimatesAtCompletion() {
        return estimatesAtCompletion;
    }

    public void setEstimatesAtCompletion(BigDecimal estimatesAtCompletion) {
        this.estimatesAtCompletion = estimatesAtCompletion;
    }

    public BigDecimal getEstimateToComplete() {
        return estimateToComplete;
    }

    public void setEstimateToComplete(BigDecimal estimateToComplete) {
        this.estimateToComplete = estimateToComplete;
    }

    public CostPerformanceIndexData getCostPerformanceIndex() {
        return costPerformanceIndex;
    }

    public void setCostPerformanceIndex(CostPerformanceIndexData costPerformanceIndex) {
        this.costPerformanceIndex = costPerformanceIndex;
    }

    public SchedulePerformanceIndexData getSchedulePerformanceIndex() {
        return schedulePerformanceIndex;
    }

    public void setSchedulePerformanceIndex(SchedulePerformanceIndexData schedulePerformanceIndex) {
        this.schedulePerformanceIndex = schedulePerformanceIndex;
    }

    public YearMonth getDate() {
        return date;
    }

    public void setDate(YearMonth date) {
        this.date = date;
    }

    public PerformanceIndexes getResponse() {
        return new PerformanceIndexes(
                this.actualCost,
                this.plannedValue,
                this.earnedValue,
                this.estimatesAtCompletion,
                this.estimateToComplete,
                Optional.ofNullable(this.costPerformanceIndex).map(CostPerformanceIndexData::getResponse).orElse(null),
                Optional.ofNullable(this.schedulePerformanceIndex).map(SchedulePerformanceIndexData::getResponse).orElse(null),
                this.date
        );
    }
}
