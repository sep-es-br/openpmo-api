package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.SchedulePerformanceIndex;

import java.math.BigDecimal;

public class SchedulePerformanceIndexData {

    private BigDecimal indexValue;

    private BigDecimal scheduleVariation;

    public static SchedulePerformanceIndexData of(SchedulePerformanceIndex from) {
        if (from == null) {
            return null;
        }

        final SchedulePerformanceIndexData to = new SchedulePerformanceIndexData();

        to.setIndexValue(from.getIndexValue());
        to.setScheduleVariation(from.getScheduleVariation());

        return to;
    }

    public BigDecimal getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(BigDecimal indexValue) {
        this.indexValue = indexValue;
    }

    public BigDecimal getScheduleVariation() {
        return scheduleVariation;
    }

    public void setScheduleVariation(BigDecimal scheduleVariation) {
        this.scheduleVariation = scheduleVariation;
    }

    public SchedulePerformanceIndex getResponse() {
        return new SchedulePerformanceIndex(
                this.indexValue,
                this.scheduleVariation
        );
    }
}
