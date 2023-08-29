package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.MilestoneDataChart;
import br.gov.es.openpmo.dto.dashboards.RiskDataChart;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.*;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.TripleConstraintDataChart;
import br.gov.es.openpmo.dto.dashboards.v2.DashboardResponse;
import br.gov.es.openpmo.dto.dashboards.v2.Interval;
import br.gov.es.openpmo.dto.dashboards.v2.SimpleDashboard;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.dashboards.Dashboard;
import br.gov.es.openpmo.model.dashboards.EarnedValueAnalysisData;
import br.gov.es.openpmo.model.dashboards.TripleConstraintData;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.dashboards.DashboardRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.BASELINE_NOT_FOUND;

@Service
public class DashboardService implements IDashboardService {

  private final IDashboardTripleConstraintService tripleConstraintService;

  private final IDashboardMilestoneService milestoneService;

  private final IDashboardRiskService riskService;

  private final IDashboardDatasheetService datasheetService;

  private final DashboardRepository dashboardRepository;

  private final BaselineRepository baselineRepository;

  private final WorkpackRepository workpackRepository;

  private final IDashboardIntervalService intervalService;

  public DashboardService(
    final IDashboardTripleConstraintService tripleConstraintService,
    final IDashboardMilestoneService milestoneService,
    final IDashboardRiskService riskService,
    final IDashboardDatasheetService datasheetService,
    final DashboardRepository dashboardRepository,
    final BaselineRepository baselineRepository,
    final WorkpackRepository workpackRepository,
    final IDashboardIntervalService intervalService
  ) {
    this.tripleConstraintService = tripleConstraintService;
    this.milestoneService = milestoneService;
    this.riskService = riskService;
    this.datasheetService = datasheetService;
    this.dashboardRepository = dashboardRepository;
    this.baselineRepository = baselineRepository;
    this.workpackRepository = workpackRepository;
    this.intervalService = intervalService;
  }

  private static YearMonth clampYearMonth(
    final YearMonth yearMonth,
    final Collection<LocalDate> dateList
  ) {
    final YearMonth valid = DashboardService.thisOrPreviousMonth(yearMonth);

    final YearMonth minDate = dateList.parallelStream()
      .min(LocalDate::compareTo)
      .map(YearMonth::from)
      .orElse(valid);

    final YearMonth maxDate = dateList.parallelStream()
      .max(LocalDate::compareTo)
      .map(YearMonth::from)
      .orElse(valid);

    return DashboardService.clampDate(valid, minDate, maxDate);
  }

  private static YearMonth clampDate(
    final YearMonth underTest,
    final YearMonth minDate,
    final YearMonth maxDate
  ) {
    if (underTest.isBefore(minDate)) {
      return minDate;
    }
    if (underTest.isAfter(maxDate)) {
      return maxDate;
    }
    return underTest;
  }

  private static YearMonth thisOrPreviousMonth(final YearMonth test) {
    return Optional.ofNullable(test)
      .orElseGet(() -> YearMonth.now().minusMonths(1));
  }

  private static boolean sameBaseline(
    final TripleConstraintDataChart dataChart,
    final Long baselineId,
    final Collection<? extends Baseline> baselines
  ) {
    if (baselines == null) {
      return true;
    }

    if (Objects.equals(dataChart.getIdBaseline(), baselineId)) {
      return true;
    }

    return baselines.parallelStream()
      .map(Baseline::getId)
      .anyMatch(id -> Objects.equals(dataChart.getIdBaseline(), id));
  }

  private static boolean samePeriod(
    final TripleConstraintDataChart chart,
    final YearMonth yearMonth
  ) {
    return Optional.of(chart)
      .map(TripleConstraintDataChart::getMesAno)
      .map(YearMonth::from)
      .map(mesAno -> mesAno.compareTo(yearMonth) == 0)
      .orElse(false);
  }

  @Override
  @Transactional
  public DashboardResponse build(final DashboardParameters parameters) {
    if (parameters == null) {
      return null;
    }

    return new DashboardResponse(
      this.getRisk(parameters),
      this.getMilestone(parameters),
      this.getTripleConstraintList(parameters),
      this.getDatasheet(parameters),
      this.getEarnedValueAnalysis(parameters)
    );
  }

  @Override
  @Transactional
  public SimpleDashboard buildSimple(final Long workpackId) {
    final Interval interval = this.intervalService.calculateFor(workpackId);
    final YearMonth date;

    if (interval.getStartDate() == null || interval.getEndDate() == null) {
      date = null;
    } else {
      final YearMonth previousMonth = YearMonth.now().minusMonths(1);
      final YearMonth startMonth = YearMonth.from(interval.getStartDate());
      final YearMonth endMonth = YearMonth.from(interval.getEndDate());
      date = DashboardService.clampDate(previousMonth, startMonth, endMonth);
    }

    // TODO: verificar se é necessário informar o 'planId'
    final DashboardParameters parameters =
      new DashboardParameters(false, workpackId, null, null, null, null, date, false, null);

    final Optional<PerformanceIndexesByStep> performanceIndexes = Optional.of(parameters)
      .map(this::getEarnedValueAnalysis)
      .map(DashboardEarnedValueAnalysis::getPerformanceIndexes)
      .flatMap(indexes -> indexes.stream().findFirst());

    return new SimpleDashboard(
      this.getRisk(parameters),
      this.getMilestone(parameters),
      date == null ? null : this.getTripleConstraint(parameters),
      date == null ? null : this.getCostPerformanceIndex(performanceIndexes),
      date == null ? null : this.getSchedulePerformanceIndex(performanceIndexes),
      date == null ? null : this.getEarnedValue(performanceIndexes)
    );
  }

  private TripleConstraintDataChart getTripleConstraint(final DashboardParameters parameters) {
    return this.getTripleConstraintList(parameters).parallelStream().findFirst().orElse(null);
  }

  private BigDecimal getEarnedValue(final Optional<PerformanceIndexesByStep> indexes) {
    return indexes.map(PerformanceIndexesByStep::getEarnedValue)
      .orElse(null);
  }

  private SchedulePerformanceIndex getSchedulePerformanceIndex(final Optional<PerformanceIndexesByStep> indexes) {
    return indexes.map(PerformanceIndexesByStep::getSchedulePerformanceIndex).orElse(null);
  }

  private CostPerformanceIndex getCostPerformanceIndex(final Optional<PerformanceIndexesByStep> indexes) {
    return indexes.map(PerformanceIndexesByStep::getCostPerformanceIndex).orElse(null);
  }

  private RiskDataChart getRisk(final DashboardParameters parameters) {
    return Optional.of(parameters)
      .map(this.riskService::build)
      .orElse(null);
  }

  private MilestoneDataChart getMilestone(final DashboardParameters parameters) {
    return Optional.of(parameters)
      .map(this.milestoneService::build)
      .orElse(null);
  }

  private List<TripleConstraintDataChart> getTripleConstraintList(final DashboardParameters parameters) {
    final YearMonth yearMonth = parameters.getYearMonth();

    if (yearMonth == null) {
      return new ArrayList<>();
    }

    final Long workpackId = parameters.getWorkpackId();
    final Long baselineId = parameters.getBaselineId();

    return Optional.of(parameters)
      .map(this::getTripleConstraintData)
      .map(data -> this.getTripleConstraintDataChart(workpackId, baselineId, yearMonth, data))
      .orElseGet(() -> Collections.singletonList(this.tripleConstraintService.build(parameters)));
  }

  private List<TripleConstraintDataChart> getTripleConstraintDataChart(
    final Long workpackId,
    final Long baselineId,
    final YearMonth yearMonth,
    final Collection<? extends TripleConstraintData> tripleConstraintData
  ) {
    final List<Baseline> filteredBaselines = this.getBaselines(workpackId, baselineId);

    final List<LocalDate> dateList = tripleConstraintData.parallelStream()
      .map(TripleConstraintData::getMesAno)
      .collect(Collectors.toList());

    final YearMonth finalYearMonth = DashboardService.clampYearMonth(yearMonth, dateList);

    return tripleConstraintData
      .parallelStream()
      .map(TripleConstraintData::getResponse)
      .filter(dataChart -> DashboardService.samePeriod(dataChart, finalYearMonth))
      .filter(dataChart -> DashboardService.sameBaseline(dataChart, baselineId, filteredBaselines))
      .collect(Collectors.toList());
  }

  private List<Baseline> getBaselines(
    final Long workpackId,
    final Long baselineId
  ) {
    final List<Baseline> baselines =
      this.baselineRepository.findApprovedOrProposedBaselinesByAnyWorkpackId(workpackId);

    if (baselineId != null) {
      return baselines.parallelStream()
        .filter(baseline -> Objects.equals(baseline.getId(), baselineId))
        .collect(Collectors.toList());
    }

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
      .orElse(null);
  }

  @Nullable
  private List<TripleConstraintData> getTripleConstraintData(final DashboardParameters parameters) {
    return this.dashboardRepository.findByWorkpackId(parameters.getWorkpackId())
      .map(Dashboard::getTripleConstraint)
      .map(tripleConstraints -> tripleConstraints.stream().map(TripleConstraintData::of).collect(Collectors.toList()))
      .orElse(null);
  }

  private DatasheetResponse getDatasheet(final DashboardParameters parameters) {
    return Optional.of(parameters)
      .map(this.datasheetService::build)
      .orElse(null);
  }

  private DashboardEarnedValueAnalysis getEarnedValueAnalysis(final DashboardParameters parameters) {
    final Long workpackId = parameters.getWorkpackId();

    final DashboardEarnedValueAnalysis earnedValueAnalysis = this.dashboardRepository.findByWorkpackId(workpackId)
      .map(EarnedValueAnalysisData::of)
      .map(EarnedValueAnalysisData::getResponse)
      .orElse(null);

    if (earnedValueAnalysis == null) {
      return null;
    }

    final List<PerformanceIndexesByStep> performanceIndexes = this.filterPerformanceIndexes(
      earnedValueAnalysis,
      parameters.getYearMonth()
    );
    final List<EarnedValueByStep> earnedValueBySteps = this.handleEarnedValueBySteps(
      earnedValueAnalysis,
      parameters.getYearMonth()
    );

    final boolean baselinesEmpty = this.isBaselinesEmpty(workpackId);

    if (performanceIndexes.isEmpty()) {
      performanceIndexes.add(new PerformanceIndexesByStep(
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        baselinesEmpty ? null : new CostPerformanceIndex(null, BigDecimal.ZERO),
        baselinesEmpty ? null : new SchedulePerformanceIndex(null, BigDecimal.ZERO),
        parameters.getYearMonth()
      ));
    }

    earnedValueAnalysis.setPerformanceIndexes(performanceIndexes);
    earnedValueAnalysis.setEarnedValueByStep(earnedValueBySteps);

    return earnedValueAnalysis;
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

  private List<PerformanceIndexesByStep> filterPerformanceIndexes(
    final DashboardEarnedValueAnalysis earnedValueAnalysis,
    final YearMonth yearMonth
  ) {
    final List<LocalDate> dateList = earnedValueAnalysis.getPerformanceIndexes()
      .parallelStream()
      .map(PerformanceIndexesByStep::getDate)
      .map(YearMonth::atEndOfMonth)
      .collect(Collectors.toList());

    final YearMonth clampYearMonth = DashboardService.clampYearMonth(yearMonth, dateList);

    return earnedValueAnalysis.getPerformanceIndexes()
      .stream()
      .filter(indexes -> indexes.getDate().compareTo(clampYearMonth) == 0)
      .collect(Collectors.toList());
  }

  private List<EarnedValueByStep> handleEarnedValueBySteps(
    final DashboardEarnedValueAnalysis earnedValueAnalysis,
    final YearMonth yearMonth
  ) {
    final List<LocalDate> dateList = earnedValueAnalysis.getEarnedValueByStep()
      .parallelStream()
      .map(EarnedValueByStep::getDate)
      .map(YearMonth::atEndOfMonth)
      .collect(Collectors.toList());

    final YearMonth clampYearMonth = DashboardService.clampYearMonth(yearMonth, dateList);

    final ArrayList<EarnedValueByStep> earnedValueBySteps = new ArrayList<>();

    earnedValueAnalysis.getEarnedValueByStep()
      .forEach(step -> earnedValueBySteps.add(step.copy(step.getDate().compareTo(clampYearMonth) <= 0)));

    return earnedValueBySteps;
  }

}
