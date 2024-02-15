package br.gov.es.openpmo.dto.dashboards;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class DashboardWorkpackDetailDto {

    private Long idWorkpack;
    private LocalDate start;
    private LocalDate end;
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

    public BigDecimal getEarnedValue() {
        if (earnedValue == null) {
            if (isValidBigdecimal(plannedCost) && isValidBigdecimal(actualWork) && isValidBigdecimal(plannedWork)) {
                earnedValue = actualWork.divide(plannedWork, 6, RoundingMode.HALF_UP).multiply(plannedCost);
            }
        }
        return earnedValue;
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

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public LocalDate getBaselineStart() {
        return baselineStart;
    }

    public void setBaselineStart(LocalDate baselineStart) {
        this.baselineStart = baselineStart;
    }

    public LocalDate getBaselineEnd() {
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
}
