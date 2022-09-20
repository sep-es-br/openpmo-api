package br.gov.es.openpmo.dto.dashboards.earnevalueanalysis;

import java.math.BigDecimal;

public class SchedulePerformanceIndex {

  private final BigDecimal indexValue;

  private final BigDecimal scheduleVariation;

  public SchedulePerformanceIndex(
    final BigDecimal indexValue,
    final BigDecimal scheduleVariation
  ) {
    this.indexValue = indexValue;
    this.scheduleVariation = scheduleVariation;
  }

  public BigDecimal getIndexValue() {
    return this.indexValue;
  }

  public BigDecimal getScheduleVariation() {
    return this.scheduleVariation;
  }

}
