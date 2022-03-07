package br.gov.es.openpmo.dto.dashboards;

import com.fasterxml.jackson.annotation.JsonCreator;

public class RiskDataChart {

    private final Long total;
    private final Long high;
    private final Long low;
    private final Long closed;
    private final Long medium;

    @JsonCreator
    public RiskDataChart(
            final Long total,
            final Long high,
            final Long low,
            final Long medium,
            final Long closed
    ) {
        this.total = total;
        this.high = high;
        this.low = low;
        this.closed = closed;
        this.medium = medium;
    }

    public Long getTotal() {
        return this.total;
    }

    public Long getHigh() {
        return this.high;
    }

    public Long getLow() {
        return this.low;
    }

    public Long getClosed() {
        return this.closed;
    }

    public Long getMedium() {
        return this.medium;
    }

}
