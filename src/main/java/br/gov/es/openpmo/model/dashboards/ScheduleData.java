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

  public static ScheduleData of(final ScheduleDataChart from) {
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

  public static ScheduleData of(final TripleConstraint from) {
    if (from == null) {
      return null;
    }

    final ScheduleData to = new ScheduleData();
    to.setPlannedStartDate(from.getSchedulePlannedStartDate());
    to.setPlannedEndDate(from.getSchedulePlannedEndDate());
    to.setForeseenStartDate(from.getScheduleForeseenStartDate());
    to.setForeseenEndDate(from.getScheduleForeseenEndDate());
    to.setActualStartDate(from.getScheduleActualStartDate());
    to.setActualEndDate(from.getScheduleActualEndDate());
    to.setVariation(from.getScheduleVariation());
    to.setPlannedValue(from.getSchedulePlannedValue());
    to.setForeseenValue(from.getScheduleForeseenValue());
    to.setActualValue(from.getScheduleActualValue());
    return to;
  }

  public LocalDate getPlannedStartDate() {
    return this.plannedStartDate;
  }

  public void setPlannedStartDate(final LocalDate plannedStartDate) {
    this.plannedStartDate = plannedStartDate;
  }

  public LocalDate getPlannedEndDate() {
    return this.plannedEndDate;
  }

  public void setPlannedEndDate(final LocalDate plannedEndDate) {
    this.plannedEndDate = plannedEndDate;
  }

  public LocalDate getForeseenStartDate() {
    return this.foreseenStartDate;
  }

  public void setForeseenStartDate(final LocalDate foreseenStartDate) {
    this.foreseenStartDate = foreseenStartDate;
  }

  public LocalDate getForeseenEndDate() {
    return this.foreseenEndDate;
  }

  public void setForeseenEndDate(final LocalDate foreseenEndDate) {
    this.foreseenEndDate = foreseenEndDate;
  }

  public LocalDate getActualStartDate() {
    return this.actualStartDate;
  }

  public void setActualStartDate(final LocalDate actualStartDate) {
    this.actualStartDate = actualStartDate;
  }

  public LocalDate getActualEndDate() {
    return this.actualEndDate;
  }

  public void setActualEndDate(final LocalDate actualEndDate) {
    this.actualEndDate = actualEndDate;
  }

  public BigDecimal getVariation() {
    return this.variation;
  }

  public void setVariation(final BigDecimal variation) {
    this.variation = variation;
  }

  public BigDecimal getPlannedValue() {
    return this.plannedValue;
  }

  public void setPlannedValue(final BigDecimal plannedValue) {
    this.plannedValue = plannedValue;
  }

  public BigDecimal getForeseenValue() {
    return this.foreseenValue;
  }

  public void setForeseenValue(final BigDecimal foreseenValue) {
    this.foreseenValue = foreseenValue;
  }

  public BigDecimal getActualValue() {
    return this.actualValue;
  }

  public void setActualValue(final BigDecimal actualValue) {
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
