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

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    return this.calculateForMonth(workpackId, baselineId, yearMonth, deliverablesId, false);
  }

  @Override
  @NonNull
  public List<TripleConstraintDataChart> calculate(@NonNull final Long workpackId, List<YearMonth> yearMonths) {
    if (yearMonths == null) {
      return Collections.emptyList();
    }
    final boolean isProject = this.workpackRepository.isProject(workpackId);
    return this.calculateForAllMonths(workpackId, yearMonths, isProject);
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
    final Iterable<Long> deliverablesId,
    final boolean isProject
  ) {
    final TripleConstraintDataChart tripleConstraint = new TripleConstraintDataChart();

    if (isProject) {
      tripleConstraint.setIdBaseline(baselineId);
    }
    tripleConstraint.setMesAno(yearMonth.atEndOfMonth());

    this.buildScheduleDataChart(baselineId, workpackId, tripleConstraint, yearMonth);

    for (final Long deliverableId : deliverablesId) {
      this.calculateForDeliverable(baselineId, tripleConstraint, deliverableId, yearMonth);
    }

    return tripleConstraint;
  }

  private List<TripleConstraintDataChart> calculateForAllMonths(
    final Long workpackId,
    final List<YearMonth> months,
    final boolean isProject
  ) {
    final List<TripleConstraintDataChart> charts = new ArrayList<>();
    final Set<Long> deliverablesId = this.getDeliverablesId(workpackId);

    List<YearMonth> yearMonths;
    if (isProject) {
      yearMonths = months;
    } else {
      yearMonths = months.stream()
        .filter(m -> !m.isAfter(YearMonth.now()))
        .collect(Collectors.toList());
      if (yearMonths.isEmpty()) {
        yearMonths = months.stream()
          .min(Comparator.naturalOrder())
          .map(Collections::singletonList)
          .orElseGet(Collections::emptyList);
      }
    }

    final List<Baseline> baselines = this.getBaselines(workpackId);
    if (baselines.isEmpty()) {
      for (final YearMonth month : yearMonths) {
        charts.add(this.calculateForMonth(workpackId, null, month, deliverablesId, isProject));
      }
    } else {
      for (final YearMonth month : yearMonths) {
        for (final Baseline baseline : baselines) {
          charts.add(this.calculateForMonth(workpackId, baseline.getId(), month, deliverablesId, isProject));
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
