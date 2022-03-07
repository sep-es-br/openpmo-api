package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.tripleconstraint.ScheduleDataChart;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ScheduleData {

    private LocalDate plannedStartDate;

    private LocalDate plannedEndDate;

    private LocalDate foreseenStartDate;

    private LocalDate foreseenEndDate;

    private LocalDate actualStartDate;

    private LocalDate actualEndDate;

    private BigDecimal variation;

    private BigDecimal plannedValue;

    private BigDecimal foreseenValue;

    private BigDecimal actualValue;

    public static ScheduleData of(ScheduleDataChart from) {
        if (from == null) {
            return null;
        }

        final ScheduleData to = new ScheduleData();
        to.setPlannedStartDate(from.getPlannedStartDate());
        to.setPlannedEndDate(from.getPlannedEndDate());
        to.setForeseenStartDate(from.getForeseenStartDate());
        to.setForeseenEndDate(from.getForeseenEndDate());
        to.setActualStartDate(from.getActualStartDate());
        to.setActualEndDate(from.getActualEndDate());
        to.setVariation(from.getVariation());
        to.setPlannedValue(from.getPlannedValue());
        to.setForeseenValue(from.getForeseenValue());
        to.setActualValue(from.getActualValue());
        return to;
    }

    public LocalDate getPlannedStartDate() {
        return plannedStartDate;
    }

    public void setPlannedStartDate(LocalDate plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }

    public LocalDate getPlannedEndDate() {
        return plannedEndDate;
    }

    public void setPlannedEndDate(LocalDate plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }

    public LocalDate getForeseenStartDate() {
        return foreseenStartDate;
    }

    public void setForeseenStartDate(LocalDate foreseenStartDate) {
        this.foreseenStartDate = foreseenStartDate;
    }

    public LocalDate getForeseenEndDate() {
        return foreseenEndDate;
    }

    public void setForeseenEndDate(LocalDate foreseenEndDate) {
        this.foreseenEndDate = foreseenEndDate;
    }

    public LocalDate getActualStartDate() {
        return actualStartDate;
    }

    public void setActualStartDate(LocalDate actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public LocalDate getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(LocalDate actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public BigDecimal getVariation() {
        return variation;
    }

    public void setVariation(BigDecimal variation) {
        this.variation = variation;
    }

    public BigDecimal getPlannedValue() {
        return plannedValue;
    }

    public void setPlannedValue(BigDecimal plannedValue) {
        this.plannedValue = plannedValue;
    }

    public BigDecimal getForeseenValue() {
        return foreseenValue;
    }

    public void setForeseenValue(BigDecimal foreseenValue) {
        this.foreseenValue = foreseenValue;
    }

    public BigDecimal getActualValue() {
        return actualValue;
    }

    public void setActualValue(BigDecimal actualValue) {
        this.actualValue = actualValue;
    }

    public ScheduleDataChart getResponse() {
        return new ScheduleDataChart(
                this.plannedStartDate,
                this.plannedEndDate,
                this.foreseenStartDate,
                this.foreseenEndDate,
                this.actualStartDate,
                this.actualEndDate,
                this.variation,
                this.plannedValue,
                this.foreseenValue,
                this.actualValue
        );
    }

}
