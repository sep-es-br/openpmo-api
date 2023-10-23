package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.DashboardEarnedValueAnalysis;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.DateIntervalQuery;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.TripleConstraintDataChart;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.dashboards.Dashboard;
import br.gov.es.openpmo.model.dashboards.DashboardMonth;
import br.gov.es.openpmo.model.dashboards.EarnedValue;
import br.gov.es.openpmo.model.dashboards.EarnedValueAnalysisData;
import br.gov.es.openpmo.model.dashboards.PerformanceIndexes;
import br.gov.es.openpmo.model.dashboards.TripleConstraint;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.dashboards.DashboardMonthRepository;
import br.gov.es.openpmo.repository.dashboards.DashboardRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SyncDashboardService implements ISyncDashboardService {

  private final DashboardRepository dashboardRepository;
  private final DashboardMonthRepository dashboardMonthRepository;
  private final DashboardTripleConstraintService tripleConstraintService;
  private final DashboardEarnedValueAnalysisService earnedValueAnalysisService;
  private final WorkpackRepository workpackRepository;
  private final FindWorkpackBaselineInterval findWorkpackBaselineInterval;
  private final FindWorkpackInterval findWorkpackInterval;
  private final BaselineRepository baselineRepository;

  public SyncDashboardService(
    final DashboardRepository dashboardRepository,
    final DashboardMonthRepository dashboardMonthRepository,
    final DashboardTripleConstraintService tripleConstraintService,
    final DashboardEarnedValueAnalysisService earnedValueAnalysisService,
    final WorkpackRepository workpackRepository,
    final FindWorkpackBaselineInterval findWorkpackBaselineInterval,
    final FindWorkpackInterval findWorkpackInterval,
    final BaselineRepository baselineRepository
  ) {
    this.dashboardRepository = dashboardRepository;
    this.dashboardMonthRepository = dashboardMonthRepository;
    this.tripleConstraintService = tripleConstraintService;
    this.earnedValueAnalysisService = earnedValueAnalysisService;
    this.workpackRepository = workpackRepository;
    this.findWorkpackBaselineInterval = findWorkpackBaselineInterval;
    this.findWorkpackInterval = findWorkpackInterval;
    this.baselineRepository = baselineRepository;
  }

  @Override
  public void calculate(@NonNull final Long worpackId, final Boolean calculateInterval) {
    Optional<DateIntervalQuery> baselineInterval;
    final List<Long> activeBaselineIds = getActiveBaselineIds(worpackId);
    if (activeBaselineIds.isEmpty()) {
      baselineInterval = Optional.empty();
    } else {
      baselineInterval = this.findWorkpackBaselineInterval.execute(worpackId, activeBaselineIds);
    }

    final Dashboard dashboard = this.getDashboard(worpackId);
    if (Boolean.TRUE.equals(calculateInterval) || !dashboard.hasMonths()) {
      final Optional<DateIntervalQuery> dateIntervalQuery;
      if (activeBaselineIds.isEmpty()) {
        dateIntervalQuery = this.findWorkpackInterval.execute(worpackId);
      } else {
        dateIntervalQuery = baselineInterval;
      }
      final List<DashboardMonth> dashboardMonths = dateIntervalQuery
        .map(DateIntervalQuery::toDashboardMonths)
        .orElse(null);
      final List<DashboardMonth> months = dashboard.getMonths();
      if (months != null && !months.isEmpty()) {
        final List<Long> monthsId = months.stream().map(Entity::getId).collect(Collectors.toList());
        this.dashboardMonthRepository.deleteWithNodes(monthsId);
        dashboard.setMonths(new ArrayList<>());
      }
      dashboard.addMonths(dashboardMonths);
    }

    Optional.of(worpackId)
      .map(id -> getTripleConstraint(id, dashboard.getYearMonths()))
      .ifPresent(dashboard::addTripleConstraints);

    final Optional<EarnedValueAnalysisData> maybeEarnedValueAnalysisData = Optional.of(worpackId)
      .map(id -> getEarnedValueAnalysis(id, baselineInterval));

    if (maybeEarnedValueAnalysisData.isPresent()) {
      final EarnedValueAnalysisData earnedValueAnalysisData = maybeEarnedValueAnalysisData.get();

      final List<EarnedValue> earnedValues = earnedValueAnalysisData.getEarnedValueByStep().stream()
        .map(EarnedValue::of)
        .collect(Collectors.toList());

      final List<PerformanceIndexes> performanceIndexes = earnedValueAnalysisData.getPerformanceIndexes().stream()
        .map(PerformanceIndexes::of)
        .collect(Collectors.toList());

      dashboard.addEarnedValues(earnedValues);
      dashboard.addPerformanceIndexes(performanceIndexes);
    }

    this.dashboardRepository.save(dashboard);
  }

  private Dashboard getDashboard(@NonNull final Long worpackId) {
    return this.dashboardRepository
      .findByWorkpackId(worpackId)
      .orElseGet(() -> this.createDashboard(worpackId));
  }

  private Dashboard createDashboard(@NonNull final Long worpackId) {
    final Dashboard dashboard = new Dashboard();

    Optional.of(worpackId)
      .map(this::getWorkpack)
      .ifPresent(dashboard::setWorkpack);

    return dashboard;
  }

  private Workpack getWorkpack(@NonNull final Long worpackId) {
    return this.workpackRepository.findById(worpackId)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
  }

  private EarnedValueAnalysisData getEarnedValueAnalysis(@NonNull final Long worpackId, Optional<DateIntervalQuery> dateIntervalQuery) {
    return Optional.of(worpackId)
      .map(id -> calculateEarnedValueAnalysis(id, dateIntervalQuery))
      .map(EarnedValueAnalysisData::of)
      .orElse(null);
  }

  private DashboardEarnedValueAnalysis calculateEarnedValueAnalysis(@NonNull final Long worpackId, Optional<DateIntervalQuery> dateIntervalQuery) {
    return this.earnedValueAnalysisService.calculate(worpackId, dateIntervalQuery);
  }

  private List<TripleConstraint> getTripleConstraint(@NonNull final Long worpackId, List<YearMonth> yearMonths) {
    return Optional.of(worpackId)
      .map(id -> calculateTripleConstraintDataChart(id, yearMonths))
      .map(this::convertToTripleConstraintData)
      .orElse(null);
  }

  private List<TripleConstraint> convertToTripleConstraintData(final Collection<TripleConstraintDataChart> charts) {
    return charts.stream()
      .map(TripleConstraint::of)
      .collect(Collectors.toList());
  }

  private List<TripleConstraintDataChart> calculateTripleConstraintDataChart(final Long worpackId, List<YearMonth> yearMonths) {
    return this.tripleConstraintService.calculate(worpackId, yearMonths);
  }

  private List<Long> getActiveBaselineIds(final Long workpackId) {
    return this.getBaselines(workpackId).stream()
      .map(Baseline::getId)
      .collect(Collectors.toList());
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
      .orElse(Collections.emptyList());
  }

}
