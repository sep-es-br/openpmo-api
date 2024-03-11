package br.gov.es.openpmo.dto.dashboards.earnevalueanalysis;

import java.math.BigDecimal;

public class CostPerformanceIndex {

  private final BigDecimal indexValue;

  private final BigDecimal costVariation;

  public CostPerformanceIndex(
    final BigDecimal indexValue,
    final BigDecimal costVariation
  ) {
    this.indexValue = indexValue;
    this.costVariation = costVariation;
  }


}
