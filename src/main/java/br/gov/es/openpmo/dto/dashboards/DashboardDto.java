package br.gov.es.openpmo.dto.dashboards;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DashboardDto {

    @JsonIgnore
    private List<DashboardWorkpackDetailDto> workpacks = new ArrayList<>(0);

    private BigDecimal scopeVariation;
    private BigDecimal earnedValue;
    private BigDecimal plannedCost;
    private BigDecimal actualCost;
    private BigDecimal plannedCostRefMonth;

    @JsonIgnore
    private LocalDate dateParam;

    public DashboardDto() {
    }

    public DashboardDto(LocalDate dateParam) {
        this.dateParam = dateParam;
    }


    public List<DashboardWorkpackDetailDto> getWorkpacks() {
        return workpacks;
    }

    public void setWorkpacks(List<DashboardWorkpackDetailDto> workpacks) {
        this.workpacks = workpacks;
    }

    public BigDecimal getScopePlannedVariationPercent() {
        BigDecimal planned = getPlannedWork();
        if (planned != null && !BigDecimal.ZERO.equals(planned)) {
            return new BigDecimal("100");
        }
        return null;
    }

    public BigDecimal getScopeForeseenVariationPercent() {
        if (getScopeVariation() != null) {
            BigDecimal ref = new BigDecimal("100");
            return ref.subtract(getScopeVariation());
        }
        return null;
    }

    public BigDecimal getScopeActualVariationPercent() {
        BigDecimal v1 = getVariationActual();
        BigDecimal v2 = getValueReferenceVariationScope();
        if (v1 != null && isValidBigdecimal(v2)) {
            BigDecimal ref = new BigDecimal("100");
            BigDecimal valuePercent = v1.divide(v2, 6, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
            return ref.subtract(valuePercent);
        }
        return null;
    }

    public BigDecimal getScopeVariation() {
        if (scopeVariation == null) {
            scopeVariation = BigDecimal.ZERO;
            BigDecimal planned = getPlannedWork();
            if (planned == null || BigDecimal.ZERO.equals(planned)) {
                return new BigDecimal("100");
            }
            BigDecimal v1 = getVariationForeSeen();
            BigDecimal v2 = getValueReferenceVariationScope();
            if (isValidBigdecimal(v1) && isValidBigdecimal(v2)) {
                scopeVariation = v1.divide(v2, 6, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
            }
        }
        return scopeVariation;
    }

    private boolean isValidBigdecimal(BigDecimal value) {
        return value != null && BigDecimal.ZERO.compareTo(value) != 0;
    }


    public BigDecimal getScheduleVariation() {
        LocalDate plannedStart = getBaselineStart();
        LocalDate plannedEnd = getBaselineEnd();
        BigDecimal plannedValue = null;
        if (plannedStart != null && plannedEnd != null) {
            long l = ChronoUnit.DAYS.between(plannedStart, plannedEnd);
            plannedValue = new BigDecimal(l);
        }
        LocalDate foreseenStart = getStart();
        LocalDate foreseendEnd = getEnd();
        BigDecimal foreseenValue = null;
        if (foreseenStart != null && foreseendEnd != null) {
            long l = ChronoUnit.DAYS.between(foreseenStart, foreseendEnd);
            foreseenValue = new BigDecimal(l);
        }
        if (plannedValue != null && foreseenValue != null) {
            return plannedValue.subtract(foreseenValue);
        }
        return null;
    }

    public BigDecimal getCostVariation() {
        BigDecimal planned = getPlannedCost();
        BigDecimal foreseen = getForeseenCost();
        if (isValidBigdecimal(planned) && isValidBigdecimal(foreseen)) {
            return planned.subtract(foreseen).divide(planned, 6, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        }
        return null;
    }

    public LocalDate getScheduleActualStartDate() {
        LocalDate planned = getBaselineStart();
        LocalDate foreseen = getStart();
        if (planned == null && foreseen != null) return foreseen;
        if (planned != null ) {
            if (foreseen == null) return planned;
            if (planned.isBefore(foreseen)) {
                return planned;
            }
            return foreseen;
        }

        return null;
    }

    public LocalDate getScheduleActualEndDate() {
        LocalDate start = getScheduleActualStartDate();
        LocalDate end = dateParam != null ? dateParam : LocalDate.now().minusMonths(1L).with(TemporalAdjusters.lastDayOfMonth());
        LocalDate endSchedule = getEnd();
        if (start != null && end.isBefore(start))  {
            end = start.with(TemporalAdjusters.lastDayOfMonth());
        }
        if (endSchedule.isBefore(end)) {
            return endSchedule;
        }
        return end;
    }

    public BigDecimal getSchedulePlannedValue() {
        LocalDate plannedStart = getBaselineStart();
        LocalDate plannedEnd = getBaselineEnd();
        if (plannedStart != null && plannedEnd != null) {
            long value = (ChronoUnit.MONTHS.between(plannedStart, plannedEnd)) + 1;
            if (value == 0) {
                value = ChronoUnit.DAYS.between(plannedStart, plannedEnd);
                if (value > 0) {
                    value = 1;
                }
            }
            return new BigDecimal(value).setScale(1, RoundingMode.CEILING);
        }
        return null;
    }

    public BigDecimal getScheduleForeseenValue() {
        LocalDate foreseenStart = getStart();
        LocalDate foreseendEnd = getEnd();
        if (foreseenStart != null && foreseendEnd != null) {
            long value = ((ChronoUnit.MONTHS.between(foreseenStart, foreseendEnd)) + 1) ;
            if (value == 0) {
                value = ChronoUnit.DAYS.between(foreseenStart, foreseendEnd);
                if (value > 0) {
                    value = 1;
                }
            }
            return new BigDecimal(value).setScale(1, RoundingMode.CEILING);
        }
        return null;
    }

    public BigDecimal getScheduleActualValue() {
        LocalDate actualStart = getScheduleActualStartDate();
        LocalDate actualEnd = getScheduleActualEndDate();
        if (actualStart != null && actualEnd != null) {
            long value = (ChronoUnit.MONTHS.between(actualStart, actualEnd)) + 1;
            if (value == 0) {
                value = ChronoUnit.DAYS.between(actualStart, actualEnd);
                if (value > 0) {
                    value = 1;
                }
            }
            return new BigDecimal(value).setScale(1, RoundingMode.CEILING);
        }
        return null;
    }

    public BigDecimal getEstimatesAtCompletion() {
        BigDecimal plannedCostRef = getPlannedCost();
        BigDecimal costPerformanceIndexValue = getCostPerformanceIndexValue();
        if (plannedCostRef != null && isValidBigdecimal(costPerformanceIndexValue)) {
            return plannedCostRef.divide(costPerformanceIndexValue, 6 , RoundingMode.HALF_UP);
        }
        return null;
    }

    public BigDecimal getEstimateToComplete() {
        BigDecimal estimated = getEstimatesAtCompletion();
        BigDecimal actualCostRef = getActualCost();
        if (estimated != null && actualCostRef != null) {
            return estimated.subtract(actualCostRef);
        }
        return null;
    }

    public BigDecimal getCostPerformanceIndexValue() {
        if (isValidBigdecimal(getEarnedValue()) && isValidBigdecimal(getActualCost())) {
            return getEarnedValue().divide(getActualCost(), 6, RoundingMode.HALF_UP);
        }
        return null;
    }

    public BigDecimal getCostPerformanceIndexVariation() {
        if (isValidBigdecimal(getEarnedValue()) && isValidBigdecimal(getActualCost())) {
            return getEarnedValue().subtract(getActualCost());
        }
        return null;
    }

    public BigDecimal getSchedulePerformanceIndexValue() {
        if (isValidBigdecimal(getEarnedValue()) && isValidBigdecimal(getPlannedCostRefMonth())) {
            return getEarnedValue().divide(getPlannedCostRefMonth(), 6, RoundingMode.HALF_UP);
        }
        return null;
    }

    public BigDecimal getSchedulePerformanceIndexVariation() {
        if (isValidBigdecimal(getEarnedValue()) && isValidBigdecimal(getPlannedCostRefMonth())) {
            return getEarnedValue().subtract(getPlannedCostRefMonth());
        }
        return null;
    }


    public LocalDate getStart() {
        return workpacks.stream().map(DashboardWorkpackDetailDto::getStart).filter(Objects::nonNull)
                        .min(LocalDate::compareTo).orElse(null);
    }

    public LocalDate getEnd() {
        return workpacks.stream().map(DashboardWorkpackDetailDto::getEnd).filter(Objects::nonNull)
                        .max(LocalDate::compareTo).orElse(null);
    }

    public LocalDate getBaselineStart() {
        return workpacks.stream().map(DashboardWorkpackDetailDto::getBaselineStart).filter(Objects::nonNull)
                        .min(LocalDate::compareTo).orElse(null);
    }

    public LocalDate getBaselineEnd() {
        return workpacks.stream().map(DashboardWorkpackDetailDto::getBaselineEnd).filter(Objects::nonNull)
                        .max(LocalDate::compareTo).orElse(null);
    }

    public BigDecimal getForeseenCost() {
        return workpacks.stream().map(DashboardWorkpackDetailDto::getForeseenCost).filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getForeseenWork() {
        return workpacks.stream().map(DashboardWorkpackDetailDto::getForeseenWork).filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getPlannedCost() {
        if (this.plannedCost == null) {
            this.plannedCost = workpacks.stream().map(DashboardWorkpackDetailDto::getPlannedCost).filter(Objects::nonNull)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return this.plannedCost;
    }

    public BigDecimal getPlannedWork() {
        return workpacks.stream().map(DashboardWorkpackDetailDto::getPlannedWork).filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getActualWork() {
        return workpacks.stream().map(DashboardWorkpackDetailDto::getActualWork).filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getActualCost() {
        if (this.actualCost == null) {
            this.actualCost = workpacks.stream().map(DashboardWorkpackDetailDto::getActualCost).filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

        }
        return this.actualCost;
    }

    public BigDecimal getVariationForeSeen() {
        return workpacks.stream().map(DashboardWorkpackDetailDto::getVariationForeSeen).filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getVariationActual() {
        return workpacks.stream().map(DashboardWorkpackDetailDto::getVariationActual).filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValueReferenceVariationScope() {
        return workpacks.stream().map(DashboardWorkpackDetailDto::getValueReferenceVariationScope).filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getEarnedValue() {
        if (this.earnedValue == null) {
            this.earnedValue = workpacks.stream().map(DashboardWorkpackDetailDto::getEarnedValue).filter(Objects::nonNull)
                                   .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return this.earnedValue;
    }

    public BigDecimal getForeseenWorkRefMonth() {
        return workpacks.stream().map(DashboardWorkpackDetailDto::getForeseenWorkRefMonth).filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getPlannedCostRefMonth() {
        if (this.plannedCostRefMonth == null) {
            this.plannedCostRefMonth = workpacks.stream().map(DashboardWorkpackDetailDto::getPlannedCostRefMonth).filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return this.plannedCostRefMonth;
    }


}
