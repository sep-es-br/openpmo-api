package br.gov.es.openpmo.dto.dashboards;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class DashboardWorkpackDetailDto {

    private Long idWorkpack;
    private Long idPlan;
    private LocalDate start;
    private LocalDate end;
    private LocalDate startPlan;
    private LocalDate endPlan;
    private LocalDate baselineStart;
    private LocalDate baselineEnd;
    private BigDecimal foreseenCost;
    private BigDecimal foreseenWork;
    private BigDecimal plannedCost;
    private BigDecimal plannedWork;
    private BigDecimal actualWork;
    private BigDecimal actualCost;
    private BigDecimal earnedValue;
    private BigDecimal foreseenWorkRefMonth;
    private BigDecimal plannedCostRefMonth;

    public DashboardWorkpackDetailDto() {
    }

    public DashboardWorkpackDetailDto(DashboardWorkpackDetailDto dto) {
        if (dto == null) {
            return;
        }
        this.idWorkpack = dto.getIdWorkpack();
        this.idPlan = dto.getIdPlan();
        this.start = dto.getStart();
        this.end = dto.getEnd();
        this.startPlan = dto.getStartPlan();
        this.endPlan = dto.getEndPlan();
        this.baselineStart = dto.getBaselineStart();
        this.baselineEnd = dto.getBaselineEnd();
        this.foreseenCost = dto.getForeseenCost();
        this.foreseenWork = dto.getForeseenWork();
        this.plannedCost = dto.getPlannedCost();
        this.plannedWork = dto.getPlannedWork();
        this.actualWork = dto.getActualWork();
        this.actualCost = dto.getActualCost();
        this.earnedValue = dto.getEarnedValue();
        this.foreseenWorkRefMonth = dto.getForeseenWorkRefMonth();
        this.plannedCostRefMonth = dto.getPlannedCostRefMonth();
    }

    public BigDecimal getEarnedValue() {
        return earnedValue;
    }

    public void setEarnedValue(BigDecimal earnedValue) {
        this.earnedValue = earnedValue;
    }

    public BigDecimal getValueReferenceVariationScope() {
        if (isValidBigdecimal(plannedCost)) {
            return plannedCost;
        }
        if (isValidBigdecimal(foreseenCost)) {
            return foreseenCost;
        }
        if (isValidBigdecimal(plannedWork)) {
            return plannedWork;
        }

        if (isValidBigdecimal(foreseenWork)) {
            return foreseenWork;
        }
        return null;
    }

    public BigDecimal getVariationForeSeen() {
        return getVariation(plannedWork, foreseenWork, plannedCost);
    }

    public BigDecimal getVariation(BigDecimal b1, BigDecimal b2, BigDecimal cost) {
        if (!isValidBigdecimal(b1) && b2 != null) {
            return BigDecimal.ZERO.subtract(b2);
        }
        if (isValidBigdecimal(b1) && b2 != null) {
            if (!isValidBigdecimal(cost)) {
                return b1.subtract(b2);
            }
            BigDecimal variation = b1.subtract(b2).divide(b1, 6, RoundingMode.HALF_UP);
            return variation.multiply(cost);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getVariationActual() {
        if (isValidBigdecimal(plannedWork) && actualWork != null) {
            return getVariation(plannedWork, actualWork, plannedCost);
        }
        if (isValidBigdecimal(foreseenWork) && actualWork != null) {
            return getVariation(foreseenWork, actualWork, foreseenCost);
        }
        return BigDecimal.ZERO;
    }

    private boolean isValidBigdecimal(BigDecimal value) {
        return value != null && BigDecimal.ZERO.compareTo(value) != 0;
    }

    public Long getIdWorkpack() {
        return idWorkpack;
    }

    public void setIdWorkpack(Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public Long getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Long idPlan) {
        this.idPlan = idPlan;
    }

    public LocalDate getStart() {
        return start.isAfter(getStartPlan()) ? start : getStartPlan();
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {

        return end.isBefore(getEndPlan()) ? end : getEndPlan();
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public LocalDate getStartPlan() {
        return startPlan;
    }

    public void setStartPlan(LocalDate startPlan) {
        this.startPlan = startPlan;
    }

    public LocalDate getEndPlan() {
        return endPlan;
    }

    public void setEndPlan(LocalDate endPlan) {
        this.endPlan = endPlan;
    }

    public LocalDate getBaselineStart() {
        if (baselineStart != null) {
            return baselineStart.isAfter(getStartPlan()) ? baselineStart : getStartPlan();
        }
        return baselineStart;
    }

    public void setBaselineStart(LocalDate baselineStart) {
        this.baselineStart = baselineStart;
    }

    public LocalDate getBaselineEnd() {
        if (baselineEnd != null) {
            return baselineEnd.isBefore(getEndPlan()) ? baselineEnd : getEndPlan();
        }
        return baselineEnd;
    }

    public void setBaselineEnd(LocalDate baselineEnd) {
        this.baselineEnd = baselineEnd;
    }

    public BigDecimal getForeseenCost() {
        return foreseenCost;
    }

    public void setForeseenCost(BigDecimal foreseenCost) {
        this.foreseenCost = foreseenCost;
    }

    public BigDecimal getForeseenWork() {
        return foreseenWork;
    }

    public void setForeseenWork(BigDecimal foreseenWork) {
        this.foreseenWork = foreseenWork;
    }

    public BigDecimal getPlannedCost() {
        return plannedCost;
    }

    public void setPlannedCost(BigDecimal plannedCost) {
        this.plannedCost = plannedCost;
    }

    public BigDecimal getPlannedWork() {
        return plannedWork;
    }

    public void setPlannedWork(BigDecimal plannedWork) {
        this.plannedWork = plannedWork;
    }

    public BigDecimal getActualWork() {
        return actualWork;
    }

    public void setActualWork(BigDecimal actualWork) {
        this.actualWork = actualWork;
    }

    public BigDecimal getActualCost() {
        return actualCost;
    }

    public void setActualCost(BigDecimal actualCost) {
        this.actualCost = actualCost;
    }

    public BigDecimal getForeseenWorkRefMonth() {
        return foreseenWorkRefMonth;
    }

    public void setForeseenWorkRefMonth(BigDecimal foreseenWorkRefMonth) {
        this.foreseenWorkRefMonth = foreseenWorkRefMonth;
    }

    public BigDecimal getPlannedCostRefMonth() {
        return plannedCostRefMonth;
    }

    public void setPlannedCostRefMonth(BigDecimal plannedCostRefMonth) {
        this.plannedCostRefMonth = plannedCostRefMonth;
    }
}
