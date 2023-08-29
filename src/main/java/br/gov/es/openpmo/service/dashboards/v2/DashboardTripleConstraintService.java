package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.CostAndScopeData;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.DateIntervalQuery;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.ScheduleDataChart;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.TripleConstraintDataChart;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.ScheduleRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.BASELINE_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.INTERVAL_DATE_IN_BASELINE_NOT_FOUND;

@Service
public class DashboardTripleConstraintService implements IDashboardTripleConstraintService {

  private final WorkpackRepository workpackRepository;
  private final BaselineRepository baselineRepository;
  private final ScheduleRepository scheduleRepository;
  private final IDashboardCostScopeService costScopeService;
  private final FindWorkpackInterval findWorkpackInterval;
  private final FindWorkpackBaselineInterval findWorkpackBaselineInterval;

  public DashboardTripleConstraintService(
    final WorkpackRepository workpackRepository,
    final BaselineRepository baselineRepository,
    final ScheduleRepository scheduleRepository,
    final IDashboardCostScopeService costScopeService,
    final FindWorkpackInterval findWorkpackInterval,
    final FindWorkpackBaselineInterval findWorkpackBaselineInterval
  ) {
    this.workpackRepository = workpackRepository;
    this.baselineRepository = baselineRepository;
    this.scheduleRepository = scheduleRepository;
    this.costScopeService = costScopeService;
    this.findWorkpackInterval = findWorkpackInterval;
    this.findWorkpackBaselineInterval = findWorkpackBaselineInterval;
  }

  @Override
  public TripleConstraintDataChart build(final DashboardParameters parameters) {
    final YearMonth yearMonth = parameters.getYearMonth();

    if (yearMonth == null) {
      return null;
    }

    final Long workpackId = parameters.getWorkpackId();
    final Long baselineId = parameters.getBaselineId();

    final Set<Long> deliverablesId = this.getDeliverablesId(workpackId);
    return this.calculateForMonth(workpackId, baselineId, yearMonth, deliverablesId);
  }

  @Override
  @NonNull
  public List<TripleConstraintDataChart> calculate(@NonNull final Long workpackId) {
    Optional<DateIntervalQuery> dateIntervalQuery;
    final List<Long> baselineIds = this.getActiveBaselineIds(workpackId);
    if (baselineIds.isEmpty()) {
      dateIntervalQuery = this.findWorkpackInterval.execute(workpackId);
    } else {
      dateIntervalQuery = this.findWorkpackBaselineInterval.execute(workpackId, baselineIds);
    }
    return dateIntervalQuery
      .filter(DateIntervalQuery::isValid)
      .map(DateIntervalQuery::toYearMonths)
      .map(months -> this.calculateForAllMonths(workpackId, months))
      .orElseGet(ArrayList::new);
  }

  private Set<Long> getDeliverablesId(final Long workpackId) {
    final Set<Long> deliverablesId = this.workpackRepository.getDeliverablesId(workpackId);

    if (this.workpackRepository.isDeliverable(workpackId)) {
      deliverablesId.add(workpackId);
    }

    return deliverablesId;
  }

  private TripleConstraintDataChart calculateForMonth(
    final Long workpackId,
    final Long baselineId,
    final YearMonth yearMonth,
    final Iterable<Long> deliverablesId
  ) {
    final TripleConstraintDataChart tripleConstraint = new TripleConstraintDataChart();

    tripleConstraint.setIdBaseline(baselineId);
    tripleConstraint.setMesAno(this.getMesAno(yearMonth));

    this.buildScheduleDataChart(baselineId, workpackId, tripleConstraint, yearMonth);

    for (final Long deliverableId : deliverablesId) {
      this.calculateForDeliverable(baselineId, tripleConstraint, deliverableId, yearMonth);
    }

    return tripleConstraint;
  }

  public LocalDate getMesAno(final YearMonth yearMonth) {
    final LocalDate now = LocalDate.now();
    if (yearMonth == null) {
      return now;
    }
    final LocalDate endOfMonth = yearMonth.atEndOfMonth();
    if (now.isBefore(endOfMonth)) {
      return now;
    }
    return endOfMonth;
  }

  private List<TripleConstraintDataChart> calculateForAllMonths(
    @NonNull final Long workpackId,
    @NonNull final Iterable<YearMonth> months
  ) {
    final List<TripleConstraintDataChart> charts = new ArrayList<>();
    final Set<Long> deliverablesId = this.getDeliverablesId(workpackId);

    final List<Baseline> baselines = this.getBaselines(workpackId);
    if (baselines.isEmpty()) {
      for (final YearMonth month : months) {
        charts.add(this.calculateForMonth(workpackId, null, month, deliverablesId));
      }
    } else {
      for (final YearMonth month : months) {
        for (final Baseline baseline : baselines) {
          charts.add(this.calculateForMonth(workpackId, baseline.getId(), month, deliverablesId));
        }
      }
    }
    return charts;
  }

  private List<Baseline> getBaselines(final Long workpackId) {
    final List<Baseline> baselines =
      this.baselineRepository.findApprovedOrProposedBaselinesByAnyWorkpackId(workpackId);

    if (this.workpackRepository.isProject(workpackId)) {
      return baselines;
    }

    for (final Baseline baseline : baselines) {
      if (baseline.isActive()) {
        return Collections.singletonList(baseline);
      }
    }

    return baselines.stream()
      .max(Comparator.comparing(Baseline::getProposalDate))
      .map(Collections::singletonList)
      .orElseGet(ArrayList::new);
  }

  private void calculateForBaseline(
    final Long workpackId,
    final Long baselineId,
    final Iterable<YearMonth> months,
    final Iterable<Long> deliverablesId,
    final Collection<? super TripleConstraintDataChart> charts
  ) {
    for (final YearMonth month : months) {
      charts.add(this.calculateForMonth(workpackId, baselineId, month, deliverablesId));
    }
  }

  private void buildScheduleDataChart(
    final Long baselineId,
    final Long workpackId,
    final TripleConstraintDataChart tripleConstraint,
    final YearMonth yearMonth
  ) {
    final List<Long> baselineIds = Optional.ofNullable(baselineId)
      .map(Collections::singletonList)
      .orElseGet(() -> this.getActiveBaselineIds(workpackId));

    final DateIntervalQuery plannedInterval = this.findIntervalInSnapshots(workpackId, baselineIds);
    final DateIntervalQuery foreseenInterval = this.findIntervalInWorkpack(workpackId);
    final ScheduleDataChart schedule = ScheduleDataChart.ofIntervals(plannedInterval, foreseenInterval, yearMonth);
    tripleConstraint.setSchedule(schedule);
  }

  private void calculateForDeliverable(
    final Long baselineId,
    final TripleConstraintDataChart tripleConstraint,
    final Long deliverableId,
    final YearMonth yearMonth
  ) {
    final boolean canceled = this.workpackRepository.isCanceled(deliverableId);
    this.scheduleRepository.findScheduleByWorkpackId(deliverableId)
      .ifPresent(schedule ->
        this.sumCostAndWorkOfSteps(
          baselineId,
          tripleConstraint,
          schedule.getSteps(),
          schedule.getId(),
          yearMonth,
          canceled
        ));
  }

  private List<Long> getActiveBaselineIds(final Long workpackId) {
    final List<Baseline> baselines = this.hasActiveBaseline(workpackId)
      ? this.findActiveBaseline(workpackId)
      : this.findAllActiveBaselines(workpackId);

    return baselines.stream()
      .map(Baseline::getId)
      .collect(Collectors.toList());
  }

  private DateIntervalQuery findIntervalInSnapshots(
    final Long workpackId,
    final List<Long> baselineIds
  ) {
    return this.findWorkpackBaselineInterval.execute(workpackId, baselineIds)
      .orElseThrow(() -> new NegocioException(INTERVAL_DATE_IN_BASELINE_NOT_FOUND));
  }

  private void sumCostAndWorkOfSteps(
    final Long baselineId,
    final TripleConstraintDataChart tripleConstraint,
    final Set<? extends Step> steps,
    final Long idSchedule,
    final YearMonth yearMonth,
    final boolean canceled
  ) {
    final CostAndScopeData costAndScopeData = this.costScopeService.build(baselineId, idSchedule, yearMonth, steps, canceled);
    tripleConstraint.sumCostData(costAndScopeData.getCostDataChart());
    tripleConstraint.sumScopeData(costAndScopeData.getScopeDataChart());
  }

  private DateIntervalQuery findIntervalInWorkpack(final Long workpackId) {
    return this.findWorkpackInterval.execute(workpackId)
      .orElseThrow(() -> new NegocioException(INTERVAL_DATE_IN_BASELINE_NOT_FOUND));
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

}
