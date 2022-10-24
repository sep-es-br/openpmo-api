package br.gov.es.openpmo.dto.dashboards.earnevalueanalysis;

import org.springframework.data.neo4j.repository.query.QueryResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@QueryResult
public class EarnedValueByStepQueryResult {

  private Double plannedValue;

  private Double actualCost;

  private Double plannedWork;

  private Double actualWork;

  private LocalDate date;

  public void setPlannedValue(final Double plannedValue) {
    this.plannedValue = plannedValue;
  }

  public void setActualCost(final Double actualCost) {
    this.actualCost = actualCost;
  }

  public void setPlannedWork(final Double plannedWork) {
    this.plannedWork = plannedWork;
  }

  public void setActualWork(final Double actualWork) {
    this.actualWork = actualWork;
  }

  public void setDate(final LocalDate date) {
    this.date = date;
  }

  public EarnedValueByStep toEarnedValueByStep() {
    final EarnedValueByStep result = new EarnedValueByStep();
    result.setPlannedValue(this.getPlannedValue());
    result.setActualCost(this.getActualCost());
    result.setPlannedWork(this.getPlannedWork());
    result.setActualWork(this.getActualWork());
    result.setDate(this.getDate());
    return result;
  }

  private BigDecimal getPlannedValue() {
    return new BigDecimal(this.plannedValue);
  }

  private BigDecimal getActualCost() {
    return new BigDecimal(this.actualCost);
  }

  public BigDecimal getPlannedWork() {
    return new BigDecimal(this.plannedWork);
  }

  public BigDecimal getActualWork() {
    return new BigDecimal(this.actualWork);
  }

  private YearMonth getDate() {
    return YearMonth.from(this.date);
  }

}
