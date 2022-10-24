package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.RiskDataChart;
import br.gov.es.openpmo.model.Entity;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.annotation.Transient;

@Node
public class RiskData extends Entity {

  private Long total;

  private Long high;

  private Long low;

  private Long closed;

  private Long medium;

  public static RiskData of(final RiskDataChart from) {
    if(from == null) {
      return null;
    }

    final RiskData to = new RiskData();

    to.setTotal(from.getTotal());
    to.setHigh(from.getHigh());
    to.setLow(from.getLow());
    to.setClosed(from.getClosed());
    to.setMedium(from.getMedium());

    return to;
  }

  @Transient
  public RiskDataChart getResponse() {
    return new RiskDataChart(
      this.total,
      this.high,
      this.low,
      this.medium,
      this.closed
    );
  }

  public Long getTotal() {
    return this.total;
  }

  public void setTotal(final Long total) {
    this.total = total;
  }

  public Long getHigh() {
    return this.high;
  }

  public void setHigh(final Long high) {
    this.high = high;
  }

  public Long getLow() {
    return this.low;
  }

  public void setLow(final Long low) {
    this.low = low;
  }

  public Long getClosed() {
    return this.closed;
  }

  public void setClosed(final Long closed) {
    this.closed = closed;
  }

  public Long getMedium() {
    return this.medium;
  }

  public void setMedium(final Long medium) {
    this.medium = medium;
  }

}
