package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.*;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.springframework.stereotype.Service;

import java.util.*;

import static br.gov.es.openpmo.utils.ApplicationMessage.BASELINE_NOT_FOUND;

@Service
public class DashboardEarnedValueAnalysisService implements IDashboardEarnedValueAnalysisService {

    private final IDashboardEarnedValueByStepsService getEarnedValueBySteps;
    private final WorkpackRepository workpackRepository;
    private final BaselineRepository baselineRepository;

    public DashboardEarnedValueAnalysisService(
            final IDashboardEarnedValueByStepsService getEarnedValueBySteps,
            WorkpackRepository workpackRepository,
            BaselineRepository baselineRepository
    ) {
        this.getEarnedValueBySteps = getEarnedValueBySteps;
        this.workpackRepository = workpackRepository;
        this.baselineRepository = baselineRepository;
    }

    private static EarnedValueAnalysisVariables getVariables(final EarnedValueByStep earnedValueByStep) {
        final EarnedValueAnalysisVariables variables = new EarnedValueAnalysisVariables();
        variables.setPlannedValue(earnedValueByStep.getPlannedValue());
        variables.setActualCost(earnedValueByStep.getActualCost());
        variables.setEarnedValue(earnedValueByStep.getEarnedValue());
        return variables;
    }

    private static EarnedValueAnalysisDerivedVariables getDerivedVariables(final EarnedValueAnalysisVariables variables) {
        return EarnedValueAnalysisDerivedVariables.create(variables);
    }

    private static CostPerformanceIndex getCostPerformanceIndex(
            final EarnedValueAnalysisDerivedVariables variables,
            boolean baselinesEmpty
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
            boolean baselinesEmpty
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
    public DashboardEarnedValueAnalysis build(final DashboardParameters parameters) {
        final List<EarnedValueByStep> earnedValueByStepList = this.getEarnedValueByStep(parameters);
        final List<PerformanceIndexes> performanceIndexesList = new ArrayList<>();

        final Long workpackId = parameters.getWorkpackId();
        final boolean baselinesEmpty = isBaselinesEmpty(workpackId);

        final Optional<EarnedValueByStep> atCompletion = earnedValueByStepList.stream()
                .max(Comparator.comparing(EarnedValueByStep::getDate));

        for (EarnedValueByStep step : earnedValueByStepList) {
            final EarnedValueAnalysisVariables variables = getVariables(step);
            final EarnedValueAnalysisDerivedVariables derivedVariables = getDerivedVariables(variables);

            final PerformanceIndexes performanceIndexes = new PerformanceIndexes(
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
                earnedValueByStepList,
                performanceIndexesList
        );
    }

    private boolean isBaselinesEmpty(Long workpackId) {
        List<Baseline> baselines = hasActiveBaseline(workpackId)
                ? findActiveBaseline(workpackId)
                : findAllActiveBaselines(workpackId);

        return baselines.isEmpty();
    }

    private boolean hasActiveBaseline(Long workpackId) {
        return this.workpackRepository.hasActiveBaseline(workpackId);
    }

    private List<Baseline> findActiveBaseline(Long workpackId) {
        return this.baselineRepository.findActiveBaseline(workpackId)
                .map(Collections::singletonList)
                .orElseThrow(() -> new NegocioException(BASELINE_NOT_FOUND));
    }

    private List<Baseline> findAllActiveBaselines(Long workpackId) {
        return this.baselineRepository.findAllActiveBaselines(workpackId);
    }

    @Override
    public DashboardEarnedValueAnalysis calculate(final Long workpackId) {
        final List<EarnedValueByStep> earnedValueByStepList = this.getEarnedValueBySteps.calculate(workpackId);
        final List<PerformanceIndexes> performanceIndexesList = new ArrayList<>();
        final boolean baselinesEmpty = isBaselinesEmpty(workpackId);

        final Optional<EarnedValueByStep> atCompletion = earnedValueByStepList.stream()
                .max(Comparator.comparing(EarnedValueByStep::getDate));

        for (EarnedValueByStep step : earnedValueByStepList) {
            final EarnedValueAnalysisVariables variables = getVariables(step);
            final EarnedValueAnalysisDerivedVariables derivedVariables = getDerivedVariables(variables);

            final PerformanceIndexes performanceIndexes = new PerformanceIndexes(
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
                earnedValueByStepList,
                performanceIndexesList
        );
    }

    private List<EarnedValueByStep> getEarnedValueByStep(final DashboardParameters parameters) {
        return this.getEarnedValueBySteps.build(parameters);
    }

}
