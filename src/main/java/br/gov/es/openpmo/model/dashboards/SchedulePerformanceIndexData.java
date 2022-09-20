package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.SchedulePerformanceIndex;

import java.math.BigDecimal;

public class SchedulePerformanceIndexData {

  private BigDecimal indexValue;

  private BigDecimal scheduleVariation;

  public static SchedulePerformanceIndexData of(final SchedulePerformanceIndex from) {
    if(from == null) {
      return null;
    }

    final SchedulePerformanceIndexData to = new SchedulePerformanceIndexData();

    to.setIndexValue(from.getIndexValue());
    to.setScheduleVariation(from.getScheduleVariation());

    return to;
  }

  public BigDecimal getIndexValue() {
    return this.indexValue;
  }

  public void setIndexValue(final BigDecimal indexValue) {
    this.indexValue = indexValue;
  }

  public BigDecimal getScheduleVariation() {
    return this.scheduleVariation;
  }

  public void setScheduleVariation(final BigDecimal scheduleVariation) {
    this.scheduleVariation = scheduleVariation;
  }

  public SchedulePerformanceIndex getResponse() {
    return new SchedulePerformanceIndex(
      this.indexValue,
      this.scheduleVariation
    );
  }

}
