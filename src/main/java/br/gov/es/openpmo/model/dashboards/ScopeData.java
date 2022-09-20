package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.tripleconstraint.ScopeDataChart;

import java.math.BigDecimal;

public class ScopeData {

  private BigDecimal variation;

  private BigDecimal plannedVariationPercent;

  private BigDecimal foreseenVariationPercent;

  private BigDecimal actualVariationPercent;

  private BigDecimal plannedValue;

  private BigDecimal foreseenValue;

  public static ScopeData of(final ScopeDataChart from) {
    if(from == null) {
      return null;
    }

    final ScopeData to = new ScopeData();
    to.setVariation(from.getVariation());
    to.setPlannedVariationPercent(from.getPlannedVariationPercent());
    to.setForeseenVariationPercent(from.getForeseenVariationPercent());
    to.setActualVariationPercent(from.getActualVariationPercent());
    to.setPlannedValue(from.getPlannedWork());
    to.setForeseenValue(from.getForeseenWork());
    return to;
  }

  public BigDecimal getActualVariationPercent() {
    return this.actualVariationPercent;
  }

  public void setActualVariationPercent(final BigDecimal actualVariationPercent) {
    this.actualVariationPercent = actualVariationPercent;
  }

  public BigDecimal getVariation() {
    return this.variation;
  }

  public void setVariation(final BigDecimal variation) {
    this.variation = variation;
  }

  public BigDecimal getPlannedVariationPercent() {
    return this.plannedVariationPercent;
  }

  public void setPlannedVariationPercent(final BigDecimal plannedVariationPercent) {
    this.plannedVariationPercent = plannedVariationPercent;
  }

  public BigDecimal getForeseenVariationPercent() {
    return this.foreseenVariationPercent;
  }

  public void setForeseenVariationPercent(final BigDecimal foreseenVariationPercent) {
    this.foreseenVariationPercent = foreseenVariationPercent;
  }

  public ScopeDataChart getResponse() {
    final ScopeDataChart scopeDataChart = new ScopeDataChart();
    scopeDataChart.setVariation(this.variation);
    scopeDataChart.setPlannedVariationPercent(this.plannedVariationPercent);
    scopeDataChart.setForeseenVariationPercent(this.foreseenVariationPercent);
    scopeDataChart.setActualVariationPercent(this.actualVariationPercent);
    scopeDataChart.setPlannedWork(this.plannedValue);
    scopeDataChart.setForeseenWork(this.foreseenValue);
    return scopeDataChart;
  }

  public BigDecimal getForeseenValue() {
    return this.foreseenValue;
  }

  public void setForeseenValue(final BigDecimal foreseenValue) {
    this.foreseenValue = foreseenValue;
  }

  public BigDecimal getPlannedValue() {
    return this.plannedValue;
  }

  public void setPlannedValue(final BigDecimal plannedValue) {
    this.plannedValue = plannedValue;
  }

}
