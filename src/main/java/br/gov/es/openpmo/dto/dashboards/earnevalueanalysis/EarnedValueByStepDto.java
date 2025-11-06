package br.gov.es.openpmo.dto.dashboards.earnevalueanalysis;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.YearMonth;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class EarnedValueByStepDto {

  private BigDecimal plannedCost;

  private BigDecimal actualCost;

  private BigDecimal earnedValue;

  @JsonFormat(pattern = "yyyy-MM")
  private YearMonth date;

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

  public YearMonth getDate() {
    return this.date;
  }

  public void setDate(final YearMonth date) {
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
