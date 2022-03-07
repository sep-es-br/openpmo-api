package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.CostPerformanceIndex;

import java.math.BigDecimal;

public class CostPerformanceIndexData {

    private BigDecimal indexValue;

    private BigDecimal costVariation;

    public static CostPerformanceIndexData of(CostPerformanceIndex from) {
        if (from == null) {
            return null;
        }

        final CostPerformanceIndexData to = new CostPerformanceIndexData();

        to.setIndexValue(from.getIndexValue());
        to.setCostVariation(from.getCostVariation());

        return to;
    }

    public BigDecimal getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(BigDecimal indexValue) {
        this.indexValue = indexValue;
    }

    public BigDecimal getCostVariation() {
        return costVariation;
    }

    public void setCostVariation(BigDecimal costVariation) {
        this.costVariation = costVariation;
    }

    public CostPerformanceIndex getResponse() {
        return new CostPerformanceIndex(
                this.indexValue,
                this.costVariation
        );
    }
}
