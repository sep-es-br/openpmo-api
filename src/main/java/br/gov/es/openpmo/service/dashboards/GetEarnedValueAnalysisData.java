package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardDataParameters;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.CostPerformanceIndex;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.DashboardEarnedValueAnalysis;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueAnalysisDerivedVariables;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueAnalysisVariables;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStep;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.SchedulePerformanceIndex;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GetEarnedValueAnalysisData implements IGetEarnedValueAnalysisData {

  private final IGetEarnedValueBySteps getEarnedValueBySteps;

  public GetEarnedValueAnalysisData(final IGetEarnedValueBySteps getEarnedValueBySteps) {
    this.getEarnedValueBySteps = getEarnedValueBySteps;
  }

  @Override
  public DashboardEarnedValueAnalysis get(final DashboardDataParameters parameters) {
    final List<EarnedValueByStep> earnedValueByStep = this.getEarnedValueByStep(parameters);
    final EarnedValueAnalysisVariables variables = getVariables(earnedValueByStep);
    final EarnedValueAnalysisDerivedVariables derivedVariables = getDerivedVariables(variables);

    final CostPerformanceIndex costPerformanceIndex = getCostPerformanceIndex(derivedVariables);
    final SchedulePerformanceIndex schedulePerformanceIndex = getSchedulePerformanceIndex(derivedVariables);
    final BigDecimal estimatesAtCompletion = getEstimatesAtCompletion(derivedVariables);
    final BigDecimal estimateToComplete = getEstimateToComplete(derivedVariables);

    return new DashboardEarnedValueAnalysis(
        earnedValueByStep,
        costPerformanceIndex,
        schedulePerformanceIndex,
        variables,
        estimatesAtCompletion,
        estimateToComplete
    );
  }

  @Nullable
  private List<EarnedValueByStep> getEarnedValueByStep(final DashboardDataParameters parameters) {
    return this.getEarnedValueBySteps.get(parameters);
  }

  @Nullable
  private static EarnedValueAnalysisVariables getVariables(final Iterable<? extends EarnedValueByStep> earnedValueBySteps) {
    if (Objects.isNull(earnedValueBySteps)) {
      return null;
    }

    final EarnedValueAnalysisVariables variables = new EarnedValueAnalysisVariables();

    for (final EarnedValueByStep step : earnedValueBySteps) {
      Optional.ofNullable(step.getPlannedValue()).ifPresent(variables::setPlannedValue);
      Optional.ofNullable(step.getActualCost()).ifPresent(variables::setActualCost);
      Optional.ofNullable(step.getEarnedValue()).ifPresent(variables::setEarnedValue);
    }

    return variables;
  }

  private static EarnedValueAnalysisDerivedVariables getDerivedVariables(final EarnedValueAnalysisVariables variables) {
    return EarnedValueAnalysisDerivedVariables.create(variables);
  }

  private static CostPerformanceIndex getCostPerformanceIndex(final EarnedValueAnalysisDerivedVariables variables) {
    return new CostPerformanceIndex(
        variables.getCostPerformanceIndex(),
        variables.getCostVariance()
    );
  }

  private static SchedulePerformanceIndex getSchedulePerformanceIndex(final EarnedValueAnalysisDerivedVariables variables) {
    return new SchedulePerformanceIndex(
        variables.getSchedulePerformanceIndex(),
        variables.getScheduleVariance()
    );
  }

  @Nullable
  private static BigDecimal getEstimatesAtCompletion(final EarnedValueAnalysisDerivedVariables variables) {
    return variables.getEstimateAtCompletion();
  }

  @Nullable
  private static BigDecimal getEstimateToComplete(final EarnedValueAnalysisDerivedVariables variables) {
    return variables.getEstimateAtComplete();

  }

}
