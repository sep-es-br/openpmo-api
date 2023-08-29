package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.CostPerformanceIndex;

import java.math.BigDecimal;

public class CostPerformanceIndexData {

  private BigDecimal indexValue;

  private BigDecimal costVariation;

  public static CostPerformanceIndexData of(final CostPerformanceIndex from) {
    if(from == null) {
      return null;
    }

    final CostPerformanceIndexData to = new CostPerformanceIndexData();

    to.setIndexValue(from.getIndexValue());
    to.setCostVariation(from.getCostVariation());

    return to;
  }

  public static CostPerformanceIndexData of(final PerformanceIndexes from) {
    if(from == null) {
      return null;
    }

    final CostPerformanceIndexData to = new CostPerformanceIndexData();

    to.setIndexValue(from.getCostPerformanceIndexValue());
    to.setCostVariation(from.getCostPerformanceIndexVariation());

    return to;
  }

  public BigDecimal getIndexValue() {
    return this.indexValue;
  }

  public void setIndexValue(final BigDecimal indexValue) {
    this.indexValue = indexValue;
  }

  public BigDecimal getCostVariation() {
    return this.costVariation;
  }

  public void setCostVariation(final BigDecimal costVariation) {
    this.costVariation = costVariation;
  }

  public CostPerformanceIndex getResponse() {
    return new CostPerformanceIndex(
      this.indexValue,
      this.costVariation
    );
  }

}
