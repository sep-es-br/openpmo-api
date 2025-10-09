package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.MilestoneResultDto;
import br.gov.es.openpmo.dto.dashboards.DashboardMonthDto;
import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.DashboardStatusData;
import br.gov.es.openpmo.dto.dashboards.MilestoneDto;
import br.gov.es.openpmo.dto.dashboards.RiskDataChart;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStepDto;
import br.gov.es.openpmo.dto.dashboards.v2.DashboardResponse;
import br.gov.es.openpmo.model.dashboards.Dashboard;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.dashboards.DashboardRepository;
import br.gov.es.openpmo.service.workpack.WorkpackService;
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
  
  private final IDashboardBaselineService baselineSrv;
  
  private final WorkpackService wpSrv;

  private final DashboardCacheUtil dashboardCacheUtil;
  
  private final DashboardRepository dashboardRepository;
  
  public DashboardService(
    final IDashboardMilestoneService milestoneService,
    final IDashboardRiskService riskService,
    final IDashboardDatasheetService datasheetService,
    final IDashboardBaselineService baselineSrv,
    final WorkpackService wpSrv,
    final DashboardCacheUtil dashboardCacheUtil,
    final DashboardRepository dashboardRepository
  ) {
    this.milestoneService = milestoneService;
    this.riskService = riskService;
    this.datasheetService = datasheetService;
    this.baselineSrv = baselineSrv;
    this.wpSrv = wpSrv;
    this.dashboardCacheUtil = dashboardCacheUtil;
    this.dashboardRepository = dashboardRepository;
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
    
    DashboardStatusData dashDataStatus = dashboardRepository.getStatusAmmountData(parameters.getWorkpackId()).orElse(null);

    return new DashboardResponse(
      this.getRisk(parameters),
      dashboardMonthDto.getTripleConstraint(),
      this.getDatasheet(parameters),
      stepDtos,
      dashboardMonthDto.getPerformanceIndex(),
      milestoneResultDto,
      dashDataStatus
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

    DashboardMonthDto dashboardMonthDto = dashboardCacheUtil.getListDashboardWorkpackDetailById(workpackId, baselineId, date, parameters.getPlanId());
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

    return dashboardCacheUtil.getDashboardEarnedValueAnalysis(workpackId, baselineId, date, parameters.getPlanId());
  }

    @Override
    public boolean isItemBeingBuild(Long workpackId) {
        
        Workpack wp = this.wpSrv.findById(workpackId);
        
        if(!(wp instanceof Deliverable)) return false;
        
        return !this.baselineSrv.findActiveBaseline(workpackId, "Deliverable").isPresent();
        
    }
  
  

}
