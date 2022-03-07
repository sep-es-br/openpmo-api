package br.gov.es.openpmo.dto.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.MilestoneDataChart;
import br.gov.es.openpmo.dto.dashboards.RiskDataChart;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.CostPerformanceIndex;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.SchedulePerformanceIndex;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.TripleConstraintDataChart;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.math.BigDecimal;

public class SimpleDashboard {

    private final RiskDataChart risk;

    private final MilestoneDataChart milestone;

    private final TripleConstraintDataChart tripleConstraint;

    private final CostPerformanceIndex costPerformanceIndex;

    private final SchedulePerformanceIndex schedulePerformanceIndex;

    private final BigDecimal earnedValue;

    @JsonCreator
    public SimpleDashboard(
            RiskDataChart risk,
            MilestoneDataChart milestone,
            TripleConstraintDataChart tripleConstraint,
            CostPerformanceIndex costPerformanceIndex,
            SchedulePerformanceIndex schedulePerformanceIndex,
            BigDecimal earnedValue
    ) {
        this.risk = risk;
        this.milestone = milestone;
        this.tripleConstraint = tripleConstraint;
        this.costPerformanceIndex = costPerformanceIndex;
        this.schedulePerformanceIndex = schedulePerformanceIndex;
        this.earnedValue = earnedValue;
    }

    public RiskDataChart getRisk() {
        return risk;
    }

    public MilestoneDataChart getMilestone() {
        return milestone;
    }

    public TripleConstraintDataChart getTripleConstraint() {
        return tripleConstraint;
    }

    public CostPerformanceIndex getCostPerformanceIndex() {
        return costPerformanceIndex;
    }

    public SchedulePerformanceIndex getSchedulePerformanceIndex() {
        return schedulePerformanceIndex;
    }

    public BigDecimal getEarnedValue() {
        return earnedValue;
    }
}
