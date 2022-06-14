package br.gov.es.openpmo.dto.dashboards.tripleconstraint;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.Temporal;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.*;

public class ScheduleDataChart {

    private final LocalDate plannedStartDate;
    private final LocalDate plannedEndDate;
    private final LocalDate foreseenStartDate;
    private final LocalDate foreseenEndDate;
    private final LocalDate actualStartDate;
    private final LocalDate actualEndDate;
    private BigDecimal variation;
    private BigDecimal plannedValue;
    private BigDecimal foreseenValue;
    private BigDecimal actualValue;

    public ScheduleDataChart(
            LocalDate plannedStartDate,
            LocalDate plannedEndDate,
            LocalDate foreseenStartDate,
            LocalDate foreseenEndDate,
            LocalDate actualStartDate,
            LocalDate actualEndDate,
            BigDecimal variation,
            BigDecimal plannedValue,
            BigDecimal foreseenValue,
            BigDecimal actualValue
    ) {
        this.plannedStartDate = plannedStartDate;
        this.plannedEndDate = plannedEndDate;
        this.foreseenStartDate = foreseenStartDate;
        this.foreseenEndDate = foreseenEndDate;
        this.actualStartDate = actualStartDate;
        this.actualEndDate = actualEndDate;
        this.variation = variation;
        this.plannedValue = plannedValue;
        this.foreseenValue = foreseenValue;
        this.actualValue = actualValue;
    }

    public ScheduleDataChart(
            final LocalDate plannedStartDate,
            final LocalDate plannedEndDate,
            final LocalDate foreseenStartDate,
            final LocalDate foreseenEndDate,
            final LocalDate actualEndDate
    ) {
        this.plannedStartDate = plannedStartDate;
        this.plannedEndDate = plannedEndDate;
        this.foreseenStartDate = foreseenStartDate;
        this.foreseenEndDate = foreseenEndDate;
        this.actualStartDate = this.minimumBetweenStartDate();
        this.actualEndDate = actualEndDate;
        this.variation = daysBetween(this.plannedEndDate, this.foreseenEndDate);
        this.calculateValue(this.plannedStartDate, this.plannedEndDate, this::setPlannedValue);
        this.calculateValue(this.foreseenStartDate, this.foreseenEndDate, this::setForeseenValue);
        this.calculateValue(this.actualStartDate, this.actualEndDate, this::setActualValue);
    }

    public static ScheduleDataChart ofIntervals(
            final DateIntervalQuery plannedInterval,
            final DateIntervalQuery foreseenInterval,
            final YearMonth referenceDate
    ) {
        LocalDate mesAno = getMesAno(referenceDate);
        return new ScheduleDataChart(
                plannedInterval.getInitialDate(),
                plannedInterval.getEndDate(),
                foreseenInterval.getInitialDate(),
                foreseenInterval.getEndDate(),
                mesAno
        );
    }

    public static LocalDate getMesAno(YearMonth yearMonth) {
        LocalDate now = LocalDate.now();
        if (yearMonth == null) {
            return now;
        }
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        if (now.isBefore(endOfMonth)) {
            return now;
        }
        return endOfMonth;
    }

    private void calculateValue(final Temporal startDate, final Temporal endDate, final Consumer<? super BigDecimal> updateValue) {
        final BigDecimal daysBetween = daysBetween(startDate, endDate);
        if (daysBetween != null) {
            updateValue.accept(daysBetween.divide(ONE_MONTH, 1, RoundingMode.HALF_UP));
        }
    }

    private LocalDate minimumBetweenStartDate() {
        return Stream.of(this.plannedStartDate, this.foreseenStartDate)
                .filter(Objects::nonNull)
                .min(Comparator.comparing(LocalDate::toEpochDay))
                .orElse(null);
    }

    public BigDecimal getVariation() {
        return this.variation;
    }

    public LocalDate getPlannedStartDate() {
        return this.plannedStartDate;
    }

    public LocalDate getPlannedEndDate() {
        return this.plannedEndDate;
    }

    public LocalDate getForeseenStartDate() {
        return this.foreseenStartDate;
    }

    public LocalDate getActualStartDate() {
        return this.actualStartDate;
    }

    public LocalDate getForeseenEndDate() {
        return this.foreseenEndDate;
    }

    public LocalDate getActualEndDate() {
        return this.actualEndDate;
    }

    public BigDecimal getPlannedValue() {
        return this.plannedValue;
    }

    private void setPlannedValue(final BigDecimal plannedValue) {
        this.plannedValue = plannedValue;
    }

    public BigDecimal getForeseenValue() {
        return this.foreseenValue;
    }

    private void setForeseenValue(final BigDecimal foreseenValue) {
        this.foreseenValue = foreseenValue;
    }

    public BigDecimal getActualValue() {
        if (this.actualValue == null) {
            return null;
        }

        return this.actualValue.compareTo(BigDecimal.ZERO) > 0
                ? this.actualValue
                : BigDecimal.ZERO;
    }

    private void setActualValue(final BigDecimal actualValue) {
        this.actualValue = actualValue;
    }

    public void round() {
        this.actualValue = roundOneDecimal(this.actualValue);
        this.foreseenValue = roundOneDecimal(this.foreseenValue);
        this.plannedValue = roundOneDecimal(this.plannedValue);
        this.variation = roundOneDecimal(this.variation);
    }
}
