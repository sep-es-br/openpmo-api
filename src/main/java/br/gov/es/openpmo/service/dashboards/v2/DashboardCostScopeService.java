package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.tripleconstraint.CostAndScopeData;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.CostDataChart;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.ScopeDataChart;
import br.gov.es.openpmo.model.relations.Consumes;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DashboardCostScopeService implements IDashboardCostScopeService {


  private final ScheduleRepository scheduleRepository;

  @Autowired
  public DashboardCostScopeService(final ScheduleRepository scheduleRepository) {
    this.scheduleRepository = scheduleRepository;
  }

  private static void totalActualCost(
    final YearMonth referenceDate,
    final CostDataChart costDataChart,
    final ScopeDataChart scopeDataChart,
    final Set<? extends Step> steps,
    final boolean canceled
  ) {
    if(canceled) {
      return;
    }

    final Set<Step> filteredSteps = steps.stream()
      .filter(step -> isBeforeOrEquals(step, referenceDate))
      .collect(Collectors.toSet());

    final BigDecimal sumOfActualWork = sumValuesOf(filteredSteps, Step::getActualWork);
    scopeDataChart.sumActualValue(sumOfActualWork);

    filteredSteps.stream()
      .map(step -> sumValuesOf(step.getConsumes(), Consumes::getActualCost))
      .forEach(costDataChart::sumActualValue);
  }

  private static void totalForeseenCost(
    final CostDataChart costDataChart,
    final ScopeDataChart scopeDataChart,
    final Set<? extends Step> steps,
    final boolean canceled
  ) {
    if(canceled) {
      return;
    }

    final BigDecimal sumOfPlannedWork = sumValuesOf(steps, Step::getPlannedWork);
    scopeDataChart.sumForeseenValue(sumOfPlannedWork);

    for(final Step step : steps) {
      final BigDecimal bigDecimal = sumValuesOf(step.getConsumes(), Consumes::getPlannedCost);
      costDataChart.sumForeseenValue(bigDecimal);
    }
  }

  private static boolean isBeforeOrEquals(
    final Step step,
    final YearMonth referenceDate
  ) {
    final LocalDate mesAno = getMesAno(referenceDate);
    return !asYearMonth(step).isAfter(YearMonth.from(mesAno));
  }

  private static LocalDate getMesAno(final YearMonth yearMonth) {
    final LocalDate now = LocalDate.now();
    if(yearMonth == null) {
      return now;
    }
    final LocalDate endOfMonth = yearMonth.atEndOfMonth();
    if(now.isBefore(endOfMonth)) {
      return now;
    }
    return endOfMonth;
  }

  private static <T> BigDecimal sumValuesOf(
    final Set<? extends T> itens,
    final Function<? super T, BigDecimal> mapper
  ) {
    if(itens == null || itens.isEmpty()) {
      return BigDecimal.ZERO;
    }

    return itens.stream()
      .map(mapper)
      .filter(Objects::nonNull)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private static YearMonth asYearMonth(final Step step) {
    return step.getYearMonth();
  }

  @Override
  public CostAndScopeData build(
    final Long baselineId,
    final Long idSchedule,
    final YearMonth referenceDate,
    final Set<? extends Step> steps,
    final boolean canceled
  ) {
    final CostDataChart costDataChart = new CostDataChart();
    final ScopeDataChart scopeDataChart = new ScopeDataChart();

    totalActualCost(referenceDate, costDataChart, scopeDataChart, steps, canceled);
    this.totalPlannedCost(baselineId, idSchedule, costDataChart, scopeDataChart);
    totalForeseenCost(costDataChart, scopeDataChart, steps, canceled);

    scopeDataChart.setCostDataChart(costDataChart);
    return new CostAndScopeData(costDataChart, scopeDataChart);
  }

  private void totalPlannedCost(
    final Long baselineId,
    final Long idSchedule,
    final CostDataChart costDataChart,
    final ScopeDataChart scopeDataChart
  ) {
    final Long baselineIdToWorkWith = this.getBaselineIdOrNull(baselineId, idSchedule);

    if(baselineIdToWorkWith == null) return;

    final Optional<Schedule> maybeScheduleSnapshot = this.maybeGetScheduleSnapshot(
      idSchedule,
      baselineIdToWorkWith
    );

    if(!maybeScheduleSnapshot.isPresent()) return;

    final Set<Consumes> consumesSnapshot = maybeScheduleSnapshot.map(Schedule::getSteps)
      .map(Set::stream)
      .map(s -> s.flatMap(s2 -> s2.getConsumes().stream()))
      .map(c -> c.collect(Collectors.toSet()))
      .orElse(new HashSet<>());
    final Set<Step> snapshotSteps = maybeScheduleSnapshot.get().getSteps();

    final BigDecimal sumOfPlannedCost = sumValuesOf(consumesSnapshot, Consumes::getPlannedCost);
    final BigDecimal sumOfPlannedWork = sumValuesOf(snapshotSteps, Step::getPlannedWork);

    costDataChart.sumPlannedValue(sumOfPlannedCost);
    scopeDataChart.sumPlannedValue(sumOfPlannedWork);
  }

  private Optional<Schedule> maybeGetScheduleSnapshot(
    final Long idSchedule,
    final Long baselineIdToWorkWith
  ) {
    return this.scheduleRepository.findSnapshotByMasterIdAndBaselineId(idSchedule, baselineIdToWorkWith);
  }

  private Long getBaselineIdOrNull(
    final Long baselineId,
    final Long scheduleId
  ) {
    return Optional.ofNullable(baselineId)
      .orElse(this.getActiveBaselineIdOrNull(scheduleId));
  }

  private Long getActiveBaselineIdOrNull(final Long scheduleId) {
    return this.scheduleRepository.findActiveBaseline(scheduleId);
  }

}
