package br.gov.es.openpmo.dto.dashboards.earnevalueanalysis;

import java.util.List;

public class DashboardEarnedValueAnalysis {

    private List<EarnedValueByStep> earnedValueByStep;

    private List<PerformanceIndexes> performanceIndexes;

    public DashboardEarnedValueAnalysis(
            List<EarnedValueByStep> earnedValueByStep,
            List<PerformanceIndexes> performanceIndexes
    ) {
        this.earnedValueByStep = earnedValueByStep;
        this.performanceIndexes = performanceIndexes;
    }

    public List<EarnedValueByStep> getEarnedValueByStep() {
        return earnedValueByStep;
    }

    public void setEarnedValueByStep(List<EarnedValueByStep> earnedValueByStep) {
        this.earnedValueByStep = earnedValueByStep;
    }

    public List<PerformanceIndexes> getPerformanceIndexes() {
        return performanceIndexes;
    }

    public void setPerformanceIndexes(List<PerformanceIndexes> performanceIndexes) {
        this.performanceIndexes = performanceIndexes;
    }
}
