package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.CostPerformanceIndex;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.DashboardEarnedValueAnalysis;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueAnalysisDerivedVariables;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueAnalysisVariables;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStep;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.PerformanceIndexesByStep;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.SchedulePerformanceIndex;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.DateIntervalQuery;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static br.gov.es.openpmo.utils.ApplicationMessage.BASELINE_NOT_FOUND;

@Service
public class DashboardEarnedValueAnalysisService implements IDashboardEarnedValueAnalysisService {

  private final IDashboardEarnedValueByStepsService getEarnedValueBySteps;
  private final WorkpackRepository workpackRepository;
  private final BaselineRepository baselineRepository;

  public DashboardEarnedValueAnalysisService(
    final IDashboardEarnedValueByStepsService getEarnedValueBySteps,
    final WorkpackRepository workpackRepository,
    final BaselineRepository baselineRepository
  ) {
    this.getEarnedValueBySteps = getEarnedValueBySteps;
    this.workpackRepository = workpackRepository;
    this.baselineRepository = baselineRepository;
  }

  private static EarnedValueAnalysisVariables getVariables(final EarnedValueByStep earnedValueByStep) {
    final EarnedValueAnalysisVariables variables = new EarnedValueAnalysisVariables();
    variables.setPlannedValue(earnedValueByStep.getPlannedValue());
    variables.setActualCost(earnedValueByStep.getActualCost());
    variables.setEstimatedCost(earnedValueByStep.getEstimatedCost());
    variables.setEarnedValue(earnedValueByStep.getEarnedValue());
    return variables;
  }

  private static EarnedValueAnalysisDerivedVariables getDerivedVariables(final EarnedValueAnalysisVariables variables) {
    return EarnedValueAnalysisDerivedVariables.create(variables);
  }

  private static CostPerformanceIndex getCostPerformanceIndex(
    final EarnedValueAnalysisDerivedVariables variables,
    final boolean baselinesEmpty
  ) {
    if (baselinesEmpty) {
      return null;
    }

    return new CostPerformanceIndex(
      variables.getCostPerformanceIndex(),
      variables.getCostVariance()
    );
  }

  private static SchedulePerformanceIndex getSchedulePerformanceIndex(
    final EarnedValueAnalysisDerivedVariables variables,
    final boolean baselinesEmpty
  ) {
    if (baselinesEmpty) {
      return null;
    }

    return new SchedulePerformanceIndex(
      variables.getSchedulePerformanceIndex(),
      variables.getScheduleVariance()
    );
  }

  @Override
  public DashboardEarnedValueAnalysis build(final DashboardParameters parameters, Optional<DateIntervalQuery> dateIntervalQuery) {
    final List<EarnedValueByStep> earnedValueByStepList = this.getEarnedValueByStep(parameters, dateIntervalQuery);
    Long idBaseline = null;
    if (!earnedValueByStepList.isEmpty()) {
      idBaseline = earnedValueByStepList.get(0).getIdBaseline();
    }
    final List<PerformanceIndexesByStep> performanceIndexesList = new ArrayList<>();

    final Long workpackId = parameters.getWorkpackId();
    final boolean baselinesEmpty = this.isBaselinesEmpty(workpackId);

    final Optional<EarnedValueByStep> atCompletion = earnedValueByStepList.stream()
      .max(Comparator.comparing(EarnedValueByStep::getDate));

    for (final EarnedValueByStep step : earnedValueByStepList) {
      final EarnedValueAnalysisVariables variables = getVariables(step);
      final EarnedValueAnalysisDerivedVariables derivedVariables = getDerivedVariables(variables);

      final PerformanceIndexesByStep performanceIndexes = new PerformanceIndexesByStep(
        idBaseline,
        variables.getActualCost(),
        variables.getPlannedValue(),
        variables.getEarnedValue(),
        derivedVariables.getEstimateAtCompletion(atCompletion.map(EarnedValueByStep::getPlannedValue).orElse(null)),
        derivedVariables.getEstimateToComplete(atCompletion.map(EarnedValueByStep::getPlannedValue).orElse(null)),
        getCostPerformanceIndex(derivedVariables, baselinesEmpty),
        getSchedulePerformanceIndex(derivedVariables, baselinesEmpty),
        step.getDate()
      );

      performanceIndexesList.add(performanceIndexes);
    }

    return new DashboardEarnedValueAnalysis(
      earnedValueByStepList
    );
  }

  @Override
  public DashboardEarnedValueAnalysis calculate(final Long workpackId, final Optional<DateIntervalQuery> dateIntervalQuery) {
    final List<EarnedValueByStep> earnedValueByStepList = this.getEarnedValueBySteps.calculate(workpackId, dateIntervalQuery);
    Long idBaseline = null;
    if (!earnedValueByStepList.isEmpty()) {
      idBaseline = earnedValueByStepList.get(0).getIdBaseline();
    }
    final List<PerformanceIndexesByStep> performanceIndexesList = new ArrayList<>();
    final boolean baselinesEmpty = this.isBaselinesEmpty(workpackId);

    final Optional<EarnedValueByStep> atCompletion = earnedValueByStepList.stream()
      .max(Comparator.comparing(EarnedValueByStep::getDate));

    for (final EarnedValueByStep step : earnedValueByStepList) {
      final EarnedValueAnalysisVariables variables = getVariables(step);
      final EarnedValueAnalysisDerivedVariables derivedVariables = getDerivedVariables(variables);

      final PerformanceIndexesByStep performanceIndexes = new PerformanceIndexesByStep(
        idBaseline,
        variables.getActualCost(),
        variables.getPlannedValue(),
        variables.getEarnedValue(),
        derivedVariables.getEstimateAtCompletion(atCompletion.map(EarnedValueByStep::getPlannedValue).orElse(null)),
        derivedVariables.getEstimateToComplete(atCompletion.map(EarnedValueByStep::getPlannedValue).orElse(null)),
        getCostPerformanceIndex(derivedVariables, baselinesEmpty),
        getSchedulePerformanceIndex(derivedVariables, baselinesEmpty),
        step.getDate()
      );

      performanceIndexesList.add(performanceIndexes);
    }

    return new DashboardEarnedValueAnalysis(
      earnedValueByStepList
    );
  }

  private boolean isBaselinesEmpty(final Long workpackId) {
    final List<Baseline> baselines = this.hasActiveBaseline(workpackId)
      ? this.findActiveBaseline(workpackId)
      : this.findAllActiveBaselines(workpackId);

    return baselines.isEmpty();
  }

  private boolean hasActiveBaseline(final Long workpackId) {
    return this.workpackRepository.hasActiveBaseline(workpackId);
  }

  private List<Baseline> findActiveBaseline(final Long workpackId) {
    return this.baselineRepository.findActiveBaseline(workpackId)
      .map(Collections::singletonList)
      .orElseThrow(() -> new NegocioException(BASELINE_NOT_FOUND));
  }

  private List<Baseline> findAllActiveBaselines(final Long workpackId) {
    return this.baselineRepository.findAllActiveBaselines(workpackId);
  }

  private List<EarnedValueByStep> getEarnedValueByStep(final DashboardParameters parameters, Optional<DateIntervalQuery> dateIntervalQuery) {
    return this.getEarnedValueBySteps.build(parameters, dateIntervalQuery);
  }

}
