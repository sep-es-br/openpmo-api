package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.MilestoneResultDto;
import br.gov.es.openpmo.dto.dashboards.DashboardDataByMonth;
import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.DashboardStatusData;
import br.gov.es.openpmo.dto.dashboards.RiskDataChart;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;
import br.gov.es.openpmo.dto.dashboards.v2.DashboardResponse;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

  private final IDashboardMilestoneService milestoneService;

  private final IDashboardBaselineService baselineSrv;
  
  private final ASyncDashboardService aSyncDashboardService;
  
  private final WorkpackService wpSrv;

  
  public DashboardService(
    final IDashboardMilestoneService milestoneService,
    final IDashboardBaselineService baselineSrv,
    final ASyncDashboardService aSyncDashboardService,
    final WorkpackService wpSrv
  ) {
    this.milestoneService = milestoneService;
    this.baselineSrv = baselineSrv;
    this.aSyncDashboardService = aSyncDashboardService;
    this.wpSrv = wpSrv;
  }

  @Transactional
  public DashboardResponse build(final DashboardParameters parameters) {
    if (parameters == null) {
      return null;
    }
      
    final Long agora = System.currentTimeMillis();
    
    CompletableFuture<DashboardDataByMonth> dataByMonthFuture = aSyncDashboardService.buildDataByMonth(parameters, agora);
    CompletableFuture<MilestoneResultDto> milestonesFuture = aSyncDashboardService.buildMilestones(parameters, agora);
    CompletableFuture<DashboardStatusData> statusDataFuture = aSyncDashboardService.buildStatusData(parameters, agora);
    CompletableFuture<DatasheetResponse> datasheetFuture = aSyncDashboardService.buildDatasheet(parameters, agora);
    CompletableFuture<RiskDataChart> riskFuture = aSyncDashboardService.buildRisk(parameters, agora);
      
      CompletableFuture.allOf(dataByMonthFuture, milestonesFuture, statusDataFuture, datasheetFuture, riskFuture).join();
      
    
    return new DashboardResponse(
      riskFuture.join(),
      dataByMonthFuture.join().getDashboardMonthDto().getTripleConstraint(),
      datasheetFuture.join(),
      dataByMonthFuture.join().getEarnedValueByStepDto(),
      dataByMonthFuture.join().getDashboardMonthDto().getPerformanceIndex(),
      milestonesFuture.join(),
      statusDataFuture.join()
    );
  }


    public boolean isItemBeingBuild(Long workpackId) {
        
        Workpack wp = this.wpSrv.findById(workpackId);
        
        if(!(wp instanceof Deliverable)) return false;
        
        return !this.baselineSrv.findActiveBaseline(workpackId, "Deliverable").isPresent();
        
    }
  
  

}
