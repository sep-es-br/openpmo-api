package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.MilestoneResultDto;
import br.gov.es.openpmo.dto.dashboards.DashboardMonthDto;
import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.MilestoneDto;
import br.gov.es.openpmo.dto.dashboards.RiskDataChart;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStepDto;
import br.gov.es.openpmo.dto.dashboards.v2.DashboardResponse;
import br.gov.es.openpmo.utils.DashboardCacheUtil;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Service
public class DashboardService implements IDashboardService {


  private final IDashboardMilestoneService milestoneService;

  private final IDashboardRiskService riskService;

  private final IDashboardDatasheetService datasheetService;

  private final DashboardCacheUtil dashboardCacheUtil;

  public DashboardService(
    final IDashboardMilestoneService milestoneService,
    final IDashboardRiskService riskService,
    final IDashboardDatasheetService datasheetService,
    final DashboardCacheUtil dashboardCacheUtil
  ) {
    this.milestoneService = milestoneService;
    this.riskService = riskService;
    this.datasheetService = datasheetService;
    this.dashboardCacheUtil = dashboardCacheUtil;
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
    List<MilestoneDto> milestones = this.getMilestones(parameters);
    MilestoneResultDto milestoneResultDto = MilestoneResultDto.of(milestones);

    return new DashboardResponse(
      this.getRisk(parameters),
      dashboardMonthDto.getTripleConstraint(),
      this.getDatasheet(parameters),
      stepDtos,
      dashboardMonthDto.getPerformanceIndex(),
      milestoneResultDto
    );
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
    final LocalDate date = YearMonth.now().isBefore(yearMonthParam) || YearMonth.now().equals(yearMonthParam) ? LocalDate.now() :  yearMonthParam.atDay(1).with(TemporalAdjusters.lastDayOfMonth());

    DashboardMonthDto dashboardMonthDto = dashboardCacheUtil.getListDashboardWorkpackDetailById(workpackId, baselineId, date);
    if (dashboardMonthDto == null) {
      return null;
    }
    if (dashboardMonthDto.getTripleConstraint().getScheduleActualEndDate().isAfter(date)) {
      dashboardMonthDto.getTripleConstraint().setScheduleActualEndDate(date);
    }

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

}
