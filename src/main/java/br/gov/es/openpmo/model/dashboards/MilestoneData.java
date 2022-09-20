package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.MilestoneDataChart;
import br.gov.es.openpmo.model.Entity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.annotation.Transient;

@NodeEntity
public class MilestoneData extends Entity {

  private Long quantity;

  private Long concluded;

  private Long lateConcluded;

  private Long late;

  private Long onTime;

  public static MilestoneData of(final MilestoneDataChart from) {
    if(from == null) {
      return null;
    }

    final MilestoneData to = new MilestoneData();

    to.setQuantity(from.getQuantity());
    to.setConcluded(from.getConcluded());
    to.setLateConcluded(from.getLateConcluded());
    to.setLate(from.getLate());
    to.setOnTime(from.getOnTime());

    return to;
  }

  public Long getQuantity() {
    return this.quantity;
  }

  public void setQuantity(final Long quantity) {
    this.quantity = quantity;
  }

  public Long getConcluded() {
    return this.concluded;
  }

  public void setConcluded(final Long concluded) {
    this.concluded = concluded;
  }

  public Long getLateConcluded() {
    return this.lateConcluded;
  }

  public void setLateConcluded(final Long lateConcluded) {
    this.lateConcluded = lateConcluded;
  }

  public Long getLate() {
    return this.late;
  }

  public void setLate(final Long late) {
    this.late = late;
  }

  public Long getOnTime() {
    return this.onTime;
  }

  public void setOnTime(final Long onTime) {
    this.onTime = onTime;
  }

  @Transient
  public MilestoneDataChart getResponse() {
    return new MilestoneDataChart(
      this.quantity,
      this.concluded,
      this.lateConcluded,
      this.late,
      this.onTime
    );
  }

}
