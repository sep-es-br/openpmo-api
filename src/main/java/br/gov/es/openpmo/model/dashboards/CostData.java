package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.tripleconstraint.CostDataChart;

import java.math.BigDecimal;

public class CostData {

  private BigDecimal actualValue;

  private BigDecimal variation;

  private BigDecimal plannedValue;

  private BigDecimal foreseenValue;

  public static CostData of(final CostDataChart from) {
    if(from == null) {
      return null;
    }

    final CostData to = new CostData();
    to.setActualValue(from.getActualValue());
    to.setVariation(from.getVariation());
    to.setPlannedValue(from.getPlannedValue());
    to.setForeseenValue(from.getForeseenValue());
    return to;
  }

  public BigDecimal getActualValue() {
    return this.actualValue;
  }

  public void setActualValue(final BigDecimal actualValue) {
    this.actualValue = actualValue;
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

  public CostDataChart getResponse() {
    final CostDataChart costDataChart = new CostDataChart();
    costDataChart.setActualValue(this.actualValue);
    costDataChart.setForeseenValue(this.foreseenValue);
    costDataChart.setPlannedValue(this.plannedValue);
    costDataChart.setVariation(this.variation);
    return costDataChart;
  }

}
