package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardDataResponse;
import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.MilestoneDataChart;
import br.gov.es.openpmo.dto.dashboards.RiskDataChart;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.DashboardEarnedValueAnalysis;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.TripleConstraintDataChart;
import br.gov.es.openpmo.service.dashboards.v2.IDashboardMilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetDashboardData implements IGetDashboardData {

    private final IGetRiskDashboardData getRiskDashboardData;

    private final IDashboardMilestoneService getMilestoneDashboardData;

    private final IGetTripleConstraintData getTripleConstraintData;

    private final IGetDatasheetDashboardData getDatasheetDashboardData;

    private final IGetEarnedValueAnalysisData getEarnedValueAnalysisData;

    @Autowired
    public GetDashboardData(
            final IGetRiskDashboardData getRiskDashboardData,
            final IDashboardMilestoneService getMilestoneDashboardData,
            final IGetTripleConstraintData getTripleConstraintData,
            final IGetDatasheetDashboardData getDatasheetDashboardData,
            final IGetEarnedValueAnalysisData getEarnedValueAnalysisData
    ) {
        this.getRiskDashboardData = getRiskDashboardData;
        this.getMilestoneDashboardData = getMilestoneDashboardData;
        this.getTripleConstraintData = getTripleConstraintData;
        this.getDatasheetDashboardData = getDatasheetDashboardData;
        this.getEarnedValueAnalysisData = getEarnedValueAnalysisData;
    }

    @Override
    public DashboardDataResponse get(final DashboardParameters parameters) {
        final RiskDataChart risk = this.getRisk(parameters);
        final MilestoneDataChart milestone = this.getMilestone(parameters);
        final TripleConstraintDataChart tripleConstraint = this.getTripleConstraint(parameters);
        final DatasheetResponse datasheet = parameters.getShowHeader() ? this.getDatasheet(parameters) : null;
        final DashboardEarnedValueAnalysis analysis = this.getEarnedValueAnalysis(parameters);

        return new DashboardDataResponse(
                risk,
                milestone,
                tripleConstraint,
                datasheet,
                analysis
        );
    }

    private RiskDataChart getRisk(final DashboardParameters parameters) {
        return this.getRiskDashboardData.get(parameters.getWorkpackId());
    }

    private MilestoneDataChart getMilestone(final DashboardParameters parameters) {
        return this.getMilestoneDashboardData.build(parameters);
    }

    private TripleConstraintDataChart getTripleConstraint(final DashboardParameters parameters) {
        final TripleConstraintDataChart tripleConstraintDataChart = this.getTripleConstraintData.get(parameters);
        Optional.ofNullable(tripleConstraintDataChart).ifPresent(TripleConstraintDataChart::round);
        return tripleConstraintDataChart;
    }

    private DatasheetResponse getDatasheet(final DashboardParameters parameters) {
        return this.getDatasheetDashboardData.get(parameters);
    }

    private DashboardEarnedValueAnalysis getEarnedValueAnalysis(final DashboardParameters parameters) {
        return this.getEarnedValueAnalysisData.get(parameters);
    }

}
