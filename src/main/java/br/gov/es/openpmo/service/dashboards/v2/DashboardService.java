package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.MilestoneResultDto;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.ScheduleInterval;
import br.gov.es.openpmo.dto.dashboards.DashboardMonthDto;
import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.MilestoneDto;
import br.gov.es.openpmo.dto.dashboards.RiskDataChart;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.DashboardEarnedValueAnalysis;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStep;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStepDto;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.PerformanceIndexesByStep;
import br.gov.es.openpmo.dto.dashboards.v2.DashboardResponse;
import br.gov.es.openpmo.dto.dashboards.v2.SimpleDashboard;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.utils.DashboardCacheUtil;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.BASELINE_NOT_FOUND;

@Service
public class DashboardService implements IDashboardService {


  private final IDashboardMilestoneService milestoneService;

  private final IDashboardRiskService riskService;

  private final IDashboardDatasheetService datasheetService;


  private final BaselineRepository baselineRepository;

  private final WorkpackRepository workpackRepository;


  private final DashboardCacheUtil dashboardCacheUtil;

  public DashboardService(
    final IDashboardMilestoneService milestoneService,
    final IDashboardRiskService riskService,
    final IDashboardDatasheetService datasheetService,
    final BaselineRepository baselineRepository,
    final WorkpackRepository workpackRepository,
    final DashboardCacheUtil dashboardCacheUtil
  ) {
    this.milestoneService = milestoneService;
    this.riskService = riskService;
    this.datasheetService = datasheetService;
    this.baselineRepository = baselineRepository;
    this.workpackRepository = workpackRepository;
    this.dashboardCacheUtil = dashboardCacheUtil;
  }

  private static YearMonth clampYearMonth(
    final YearMonth yearMonth,
    final Collection<LocalDate> dateList
  ) {
    final YearMonth valid = DashboardService.thisOrPreviousMonth(yearMonth);

    final YearMonth minDate = dateList.stream()
      .min(LocalDate::compareTo)
      .map(YearMonth::from)
      .orElse(valid);

    final YearMonth maxDate = dateList.stream()
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

  @Override
  @Transactional
  public DashboardResponse build(final DashboardParameters parameters) {
    if (parameters == null) {
      return null;
    }

    final DashboardMonthDto dashboardMonthDto = getDashboardMonthDto(parameters);
    if (dashboardMonthDto == null) {
      return null;
    }
    List<EarnedValueByStepDto> stepDtos = this.getEarnedValueAnalysis(parameters);
    final LocalDate start = stepDtos.stream().map(EarnedValueByStepDto::getDate).min(LocalDate::compareTo).orElse(null);
    final LocalDate end = stepDtos.stream().map(EarnedValueByStepDto::getDate).max(LocalDate::compareTo).orElse(null);
    final ScheduleInterval scheduleInterval = new ScheduleInterval(start, end);

    List<MilestoneDto> milestones = this.getMilestones(parameters);
    MilestoneResultDto milestoneResultDto = MilestoneResultDto.of(milestones);

    return new DashboardResponse(
      this.getRisk(parameters),
      dashboardMonthDto.getTripleConstraint(),
      this.getDatasheet(parameters),
      stepDtos,
      dashboardMonthDto.getPerformanceIndex(),
      milestoneResultDto,
      scheduleInterval
    );
  }

  @Override
  @Transactional
  public SimpleDashboard buildSimple(final Long workpackId) {

    return null;
  }



  private RiskDataChart getRisk(final DashboardParameters parameters) {
    return Optional.of(parameters)
      .map(this.riskService::build)
      .orElse(null);
  }

  private List<MilestoneDto> getMilestones(final DashboardParameters parameters) {
    return Optional.of(parameters)
      .map(this.milestoneService::build)
      .orElse(null);
  }

  private DashboardMonthDto getDashboardMonthDto(final DashboardParameters parameters) {
    final YearMonth yearMonthParam =
        parameters.getYearMonth() == null ? YearMonth.now().minusMonths(1) : parameters.getYearMonth();

    final Long workpackId = parameters.getWorkpackId();
    final Long baselineId = parameters.getBaselineId();
    final LocalDate date = YearMonth.now().isBefore(yearMonthParam) ? YearMonth.now().atDay(1) :  yearMonthParam.atDay(1);

    DashboardMonthDto dashboardMonthDto = dashboardCacheUtil.getListDashboardWorkpackDetailById(workpackId, baselineId, date);
    if (dashboardMonthDto == null) {
      return null;
    }
    dashboardMonthDto.getTripleConstraint().setScheduleActualEndDate(date);

    if (date.isBefore(dashboardMonthDto.getTripleConstraint().getScheduleActualStartDate())) {
      dashboardMonthDto.getTripleConstraint().setScheduleActualEndDate(dashboardMonthDto.getTripleConstraint().getScheduleActualStartDate().with(TemporalAdjusters.lastDayOfMonth()));
    }
    return dashboardMonthDto;
  }



  private DatasheetResponse getDatasheet(final DashboardParameters parameters) {
    return Optional.of(parameters)
      .map(this.datasheetService::build)
      .orElse(null);
  }

  private List<EarnedValueByStepDto> getEarnedValueAnalysis(final DashboardParameters parameters) {
    final YearMonth yearMonthParam =
        parameters.getYearMonth() == null ? YearMonth.now().minusMonths(1) : parameters.getYearMonth();

    final Long baselineId = parameters.getBaselineId();
    final Long workpackId = parameters.getWorkpackId();
    final LocalDate date = YearMonth.now().isBefore(yearMonthParam) ? YearMonth.now().atDay(1) :  yearMonthParam.atDay(1);

    return dashboardCacheUtil.getDashboardEarnedValueAnalysis(workpackId, baselineId, date);
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

    return new ArrayList<>(0);
  }

  private List<EarnedValueByStep> handleEarnedValueBySteps(
    final DashboardEarnedValueAnalysis earnedValueAnalysis,
    final YearMonth yearMonth
  ) {
    final List<LocalDate> dateList = earnedValueAnalysis.getEarnedValueByStep()
      .stream()
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
