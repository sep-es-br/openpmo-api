package br.gov.es.openpmo.dto.dashboards.earnevalueanalysis;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.neo4j.annotation.QueryResult;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@QueryResult
public class EarnedValueByStepDto {

  private BigDecimal plannedCost;

  private BigDecimal actualCost;

  private BigDecimal estimatedCost;

  private BigDecimal earnedValue;

  @JsonFormat(pattern = "yyyy-MM")
  private LocalDate date;

  @JsonIgnore
  private BigDecimal plannedWork;

  @JsonIgnore
  private BigDecimal actualWork;

  public BigDecimal getPlannedCost() {
    return this.plannedCost;
  }

  public void setPlannedCost(final BigDecimal plannedCost) {
    this.plannedCost = plannedCost;
  }

  public BigDecimal getActualCost() {
    return this.actualCost;
  }

  public void setActualCost(final BigDecimal actualCost) {
    this.actualCost = actualCost;
  }

  public BigDecimal getPlannedWork() {
    return this.plannedWork;
  }

  public void setPlannedWork(final BigDecimal plannedWork) {
    this.plannedWork = plannedWork;
  }

  public BigDecimal getActualWork() {
    return this.actualWork;
  }

  public void setActualWork(final BigDecimal actualWork) {
    this.actualWork = actualWork;
  }

  public BigDecimal getEarnedValue() {
    return this.earnedValue;
  }

  public void setEarnedValue(final BigDecimal earnedValue) {
    this.earnedValue = earnedValue;
  }

  public BigDecimal getEstimatedCost() {
    return this.estimatedCost;
  }

  public void setEstimatedCost(final BigDecimal estimatedCost) {
    this.estimatedCost = estimatedCost;
  }

  public LocalDate getDate() {
    return this.date;
  }

  public void setDate(final LocalDate date) {
    this.date = date;
  }

  public void calculateEarnedValue() {
    if (BigDecimal.ZERO.compareTo(plannedWork) == 0) {
      this.earnedValue = BigDecimal.ZERO;
      return;
    }
    this.earnedValue = plannedCost
        .divide(plannedWork, new MathContext(4, RoundingMode.HALF_EVEN))
        .multiply(actualWork);
  }

}
