package br.gov.es.openpmo.dto.dashboards;

import com.fasterxml.jackson.annotation.JsonCreator;

public class MilestoneDataChart {

  private final Long quantity;

  private final Long concluded;

  private final Long lateConcluded;

  private final Long late;

  private final Long onTime;

  @JsonCreator
  public MilestoneDataChart(
    final Long quantity,
    final Long concluded,
    final Long lateConcluded,
    final Long late,
    final Long onTime
  ) {
    this.quantity = quantity;
    this.concluded = concluded;
    this.lateConcluded = lateConcluded;
    this.late = late;
    this.onTime = onTime;
  }

  public Long getQuantity() {
    return this.quantity;
  }

  public Long getConcluded() {
    return this.concluded;
  }

  public Long getLateConcluded() {
    return this.lateConcluded;
  }

  public Long getLate() {
    return this.late;
  }

  public Long getOnTime() {
    return this.onTime;
  }

}
