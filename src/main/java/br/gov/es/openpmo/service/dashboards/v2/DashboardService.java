package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.MilestoneResultDto;
import br.gov.es.openpmo.dto.dashboards.DashboardDataByMonth;
import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.DashboardStatusData;
import br.gov.es.openpmo.dto.dashboards.MilestoneDto;
import br.gov.es.openpmo.dto.dashboards.RiskDataChart;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;
import br.gov.es.openpmo.dto.dashboards.v2.DashboardResponse;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.dashboards.DashboardRepository;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import br.gov.es.openpmo.utils.DashboardCacheUtil;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

     YearMonth yearMonth = Optional.ofNullable(parameters.getYearMonth()).orElse(YearMonth.now().minusMonths(1));
    String yearMonthAsStr = String.format("%d%02d", yearMonth.getYear(), yearMonth.getMonthValue());
    
    Long scopeId = Optional.ofNullable(parameters.getWorkpackId()).orElse(parameters.getPlanId());
    Long baselineId = parameters.getBaselineId();
    
    DashboardDataByMonth dataByMonth = dashboardRepository.getDataByMonth(scopeId, baselineId, Integer.valueOf(yearMonthAsStr));
    
    List<MilestoneDto> milestones = this.getMilestones(parameters);
    MilestoneResultDto milestoneResultDto = MilestoneResultDto.of(milestones);
    
    DashboardStatusData dashDataStatus = dashboardRepository.getStatusAmmountData(parameters.getWorkpackId(), parameters.getBaselineId()).orElse(null);

    return new DashboardResponse(
      this.getRisk(parameters),
      dataByMonth.getDashboardMonthDto().getTripleConstraint(),
      this.getDatasheet(parameters),
      dataByMonth.getEarnedValueByStepDto(),
      dataByMonth.getDashboardMonthDto().getPerformanceIndex(),
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


  private DatasheetResponse getDatasheet(final DashboardParameters parameters) {
    return Optional.of(parameters)
      .map(this.datasheetService::build)
      .orElse(null);
  }

    @Override
    public boolean isItemBeingBuild(Long workpackId) {
        
        Workpack wp = this.wpSrv.findById(workpackId);
        
        if(!(wp instanceof Deliverable)) return false;
        
        return !this.baselineSrv.findActiveBaseline(workpackId, "Deliverable").isPresent();
        
    }
  
  

}
