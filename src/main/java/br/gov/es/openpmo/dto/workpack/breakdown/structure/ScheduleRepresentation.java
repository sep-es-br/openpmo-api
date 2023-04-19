package br.gov.es.openpmo.dto.workpack.breakdown.structure;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ScheduleRepresentation {

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate end;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate start;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate baselineEnd;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate baselineStart;

  private BigDecimal baselinePlanned = BigDecimal.ZERO;

  private BigDecimal baselineCost = BigDecimal.ZERO;

  private BigDecimal planed = BigDecimal.ZERO;

  private BigDecimal actual = BigDecimal.ZERO;

  private BigDecimal planedCost = BigDecimal.ZERO;

  private BigDecimal actualCost = BigDecimal.ZERO;

  private ScheduleMeasureUnit unitMeasure;

  public LocalDate getEnd() {
    return this.end;
  }

  public void setEnd(final LocalDate end) {
    this.end = end;
  }

  public LocalDate getStart() {
    return this.start;
  }

  public void setStart(final LocalDate start) {
    this.start = start;
  }

  public LocalDate getBaselineEnd() {
    return this.baselineEnd;
  }

  public void setBaselineEnd(final LocalDate baselineEnd) {
    this.baselineEnd = baselineEnd;
  }

  public LocalDate getBaselineStart() {
    return this.baselineStart;
  }

  public void setBaselineStart(final LocalDate baselineStart) {
    this.baselineStart = baselineStart;
  }

  public BigDecimal getBaselinePlanned() {
    return this.baselinePlanned;
  }

  public void setBaselinePlanned(final BigDecimal baselinePlanned) {
    this.baselinePlanned = baselinePlanned;
  }

  public BigDecimal getBaselineCost() {
    return this.baselineCost;
  }

  public void setBaselineCost(final BigDecimal baselineCost) {
    this.baselineCost = baselineCost;
  }

  public BigDecimal getPlaned() {
    return this.planed;
  }

  public void setPlaned(final BigDecimal planed) {
    this.planed = planed;
  }

  public BigDecimal getActual() {
    return this.actual;
  }

  public void setActual(final BigDecimal actual) {
    this.actual = actual;
  }

  public BigDecimal getPlanedCost() {
    return this.planedCost;
  }

  public void setPlanedCost(final BigDecimal planedCost) {
    this.planedCost = planedCost;
  }

  public BigDecimal getActualCost() {
    return this.actualCost;
  }

  public void setActualCost(final BigDecimal actualCost) {
    this.actualCost = actualCost;
  }

  public ScheduleMeasureUnit getUnitMeasure() {
    return this.unitMeasure;
  }

  public void setUnitMeasure(final ScheduleMeasureUnit unitMeasure) {
    this.unitMeasure = unitMeasure;
  }

}
