package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.tripleconstraint.CostAndScopeData;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.CostDataChart;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.ScopeDataChart;
import br.gov.es.openpmo.model.relations.Consumes;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.repository.ConsumesRepository;
import br.gov.es.openpmo.repository.StepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DashboardCostScopeService implements IDashboardCostScopeService {

    private final StepRepository stepRepository;
    private final ConsumesRepository consumesRepository;

    @Autowired
    public DashboardCostScopeService(StepRepository stepRepository, final ConsumesRepository consumesRepository) {
        this.stepRepository = stepRepository;
        this.consumesRepository = consumesRepository;
    }

    @Override
    public CostAndScopeData build(
            Long deliverableId,
            final Long baselineId,
            final YearMonth referenceDate,
            final Set<? extends Step> steps
    ) {
        final CostDataChart costDataChart = new CostDataChart();
        final ScopeDataChart scopeDataChart = new ScopeDataChart();

        totalActualCost(referenceDate, costDataChart, scopeDataChart, steps);
        totalPlannedCost(baselineId, costDataChart, scopeDataChart, steps);
        totalForeseenCost(costDataChart, scopeDataChart, steps);

        scopeDataChart.setCostDataChart(costDataChart);
        return new CostAndScopeData(costDataChart, scopeDataChart);
    }

    private static void totalActualCost(
            final YearMonth referenceDate,
            final CostDataChart costDataChart,
            final ScopeDataChart scopeDataChart,
            final Set<? extends Step> steps
    ) {
        final Set<Step> filteredSteps = steps.stream()
                .filter(step -> isBeforeOrEquals(step, referenceDate))
                .collect(Collectors.toSet());

        final BigDecimal sumOfActualWork = sumValuesOf(filteredSteps, Step::getActualWork);
        scopeDataChart.sumActualValue(sumOfActualWork);

        filteredSteps.stream()
                .map(step -> sumValuesOf(step.getConsumes(), Consumes::getActualCost))
                .forEach(costDataChart::sumActualValue);
    }

    private void totalPlannedCost(
            final Long baselineId,
            final CostDataChart costDataChart,
            final ScopeDataChart scopeDataChart,
            final Set<? extends Step> steps
    ) {
        for (final Step step : steps) {
            final Long baselineIdToWorkWith = getBaselineIdOrNull(baselineId, step);

            if (baselineIdToWorkWith == null) {
                continue;
            }

            final Set<Consumes> consumesSnapshot = getConsumesSnapshot(step, baselineIdToWorkWith);
            final Set<Step> stepsSnapshot = getStepsSnapshot(consumesSnapshot);

            final BigDecimal sumOfPlannedCost = sumValuesOf(consumesSnapshot, Consumes::getPlannedCost);
            final BigDecimal sumOfPlannedWork = sumValuesOf(stepsSnapshot, Step::getPlannedWork);

            costDataChart.sumPlannedValue(sumOfPlannedCost);
            scopeDataChart.sumPlannedValue(sumOfPlannedWork);
        }
    }

    private static void totalForeseenCost(
            final CostDataChart costDataChart,
            final ScopeDataChart scopeDataChart,
            final Set<? extends Step> steps
    ) {
        final BigDecimal sumOfPlannedWork = sumValuesOf(steps, Step::getPlannedWork);
        scopeDataChart.sumForeseenValue(sumOfPlannedWork);

        for (Step step : steps) {
            BigDecimal bigDecimal = sumValuesOf(step.getConsumes(), Consumes::getPlannedCost);
            costDataChart.sumForeseenValue(bigDecimal);
        }
    }

    private static boolean isBeforeOrEquals(Step step, YearMonth referenceDate) {
        return !asYearMonth(step).isAfter(referenceDate);
    }

    private static <T> BigDecimal sumValuesOf(
            final Set<? extends T> itens,
            final Function<? super T, BigDecimal> mapper
    ) {
        if (itens == null || itens.isEmpty()) {
            return null;
        }

        return itens.stream()
                .map(mapper)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Long getBaselineIdOrNull(Long baselineId, Step step) {
        return Optional.ofNullable(baselineId)
                .orElse(getActiveBaselineIdOrNull(step));
    }

    private Set<Consumes> getConsumesSnapshot(Step step, Long idBaseline) {
        return this.consumesRepository
                .findAllSnapshotConsumesOfStepMaster(idBaseline, step.getId());
    }

    private Set<Step> getStepsSnapshot(Set<Consumes> consumesSnapshot) {
        return consumesSnapshot.stream()
                .map(Consumes::getStep)
                .collect(Collectors.toSet());
    }

    private static YearMonth asYearMonth(final Step step) {
        return YearMonth.from(step.getPeriodFromStart());
    }

    private Long getActiveBaselineIdOrNull(Step step) {
        return this.stepRepository.findActiveBaseline(step.getId());
    }

}
