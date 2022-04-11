package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.tripleconstraint.CostAndScopeData;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.CostDataChart;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.ScopeDataChart;
import br.gov.es.openpmo.model.relations.Consumes;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.repository.ConsumesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GetCostAndScope implements IGetCostAndScope {

    private final ConsumesRepository consumesRepository;

    @Autowired
    public GetCostAndScope(final ConsumesRepository consumesRepository) {
        this.consumesRepository = consumesRepository;
    }

    @Override
    public CostAndScopeData get(
            final Long idBaseline,
            final YearMonth referenceDate,
            final Collection<? extends Step> steps
    ) {
        final CostDataChart costDataChart = new CostDataChart();

        final ScopeDataChart scopeDataChart = new ScopeDataChart();

        totalActualCost(
                referenceDate,
                costDataChart,
                scopeDataChart,
                steps
        );
        this.totalPlannedCost(
                idBaseline,
                costDataChart,
                scopeDataChart,
                steps
        );
        totalForeseenCost(
                costDataChart,
                scopeDataChart,
                steps
        );

        scopeDataChart.setCostDataChart(costDataChart);

        return new CostAndScopeData(
                costDataChart,
                scopeDataChart
        );
    }

    private static void totalActualCost(
            final YearMonth referenceDate,
            final CostDataChart costDataChart,
            final ScopeDataChart scopeDataChart,
            final Collection<? extends Step> steps
    ) {
        final List<Step> filteredMasterStep = steps.stream()
                .filter(step -> {
                    final YearMonth periodFromStartAsYearMonth = asYearMonth(step);
                    return periodFromStartAsYearMonth.equals(referenceDate) || periodFromStartAsYearMonth.isBefore(referenceDate);
                }).collect(Collectors.toList());

        scopeDataChart.sumActualValue(sumValuesOf(filteredMasterStep, Step::getActualWork));

        for (final Step step : filteredMasterStep) {
            costDataChart.sumActualValue(sumValuesOf(step.getConsumes(), Consumes::getActualCost));
        }
    }

    private void totalPlannedCost(
            final Long idBaseline,
            final CostDataChart costDataChart,
            final ScopeDataChart scopeDataChart,
            final Iterable<? extends Step> steps
    ) {
        for (final Step step : steps) {
            final Set<Consumes> consumesSnapshot = this.consumesRepository.findAllSnapshotConsumesOfStepMaster(
                    idBaseline,
                    step.getId()
            );

            costDataChart.sumPlannedValue(
                    sumValuesOf(consumesSnapshot, Consumes::getPlannedCost)
            );

            final List<Step> stepsSnapshot = consumesSnapshot.stream()
                    .map(Consumes::getStep)
                    .collect(Collectors.toList());
            scopeDataChart.sumPlannedValue(sumValuesOf(stepsSnapshot, Step::getPlannedWork));
        }
    }

    private static void totalForeseenCost(
            final CostDataChart costDataChart,
            final ScopeDataChart scopeDataChart,
            final Collection<? extends Step> steps
    ) {
        scopeDataChart.sumForeseenValue(sumValuesOf(steps, Step::getPlannedWork));
        for (final Step step : steps) {
            costDataChart.sumForeseenValue(
                    sumValuesOf(step.getConsumes(), Consumes::getPlannedCost)
            );
        }
    }

    private static YearMonth asYearMonth(final Step step) {
        return step.getYearMonth();
    }

    private static <T> BigDecimal sumValuesOf(
            final Collection<? extends T> itens,
            final Function<? super T, BigDecimal> valueToSum
    ) {
        if (itens.isEmpty()) return null;

        return itens.stream()
                .map(valueToSum)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
