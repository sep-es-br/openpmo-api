package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.RiskDataChart;
import br.gov.es.openpmo.model.Entity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.annotation.Transient;

@NodeEntity
public class RiskData extends Entity {

    private Long total;

    private Long high;

    private Long low;

    private Long closed;

    private Long medium;

    public static RiskData of(RiskDataChart from) {
        if (from == null) {
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

    public void setTotal(Long total) {
        this.total = total;
    }

    public void setHigh(Long high) {
        this.high = high;
    }

    public void setLow(Long low) {
        this.low = low;
    }

    public void setClosed(Long closed) {
        this.closed = closed;
    }

    public void setMedium(Long medium) {
        this.medium = medium;
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
        return total;
    }

    public Long getHigh() {
        return high;
    }

    public Long getLow() {
        return low;
    }

    public Long getClosed() {
        return closed;
    }

    public Long getMedium() {
        return medium;
    }

}
