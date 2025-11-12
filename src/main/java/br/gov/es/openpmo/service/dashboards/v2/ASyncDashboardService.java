/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.MilestoneResultDto;
import br.gov.es.openpmo.dto.dashboards.DashboardDataByMonth;
import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.DashboardStatusData;
import br.gov.es.openpmo.dto.dashboards.MilestoneDto;
import br.gov.es.openpmo.dto.dashboards.RiskDataChart;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;
import br.gov.es.openpmo.repository.dashboards.DashboardRepository;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author gean.carneiro
 */
@Service
public class ASyncDashboardService {
    
  private final DashboardRepository dashboardRepository;
  private final IDashboardMilestoneService milestoneService;
  private final IDashboardDatasheetService datasheetService;
  private final IDashboardRiskService riskService;

    public ASyncDashboardService(
        DashboardRepository dashboardRepository, 
        IDashboardMilestoneService milestoneService,
        IDashboardDatasheetService datasheetService,
        IDashboardRiskService riskService
    ) {
        this.dashboardRepository = dashboardRepository;
        this.milestoneService = milestoneService;
        this.datasheetService = datasheetService; 
        this.riskService = riskService;
    }

  
    
  @Transactional
  @Async
  public CompletableFuture<DashboardDataByMonth> buildDataByMonth(final DashboardParameters parameters) {
    

    YearMonth yearMonth = Optional.ofNullable(parameters.getYearMonth()).orElse(YearMonth.now().minusMonths(1));
    String yearMonthAsStr = String.format("%d%02d", yearMonth.getYear(), yearMonth.getMonthValue());
    
    Long scopeId = Optional.ofNullable(parameters.getWorkpackId()).orElse(parameters.getPlanId());
    Long baselineId = parameters.getBaselineId();
    
    DashboardDataByMonth dataByMonth = dashboardRepository.getDataByMonth(scopeId, baselineId, Integer.valueOf(yearMonthAsStr));
    

    return CompletableFuture.completedFuture(dataByMonth);
  }
  
  

  @Transactional
  @Async
  public CompletableFuture<MilestoneResultDto> buildMilestones(final DashboardParameters parameters) {
    
    List<MilestoneDto> milestones = this.getMilestones(parameters);
    MilestoneResultDto milestoneResultDto = MilestoneResultDto.of(milestones);
    
    return CompletableFuture.completedFuture(milestoneResultDto);
  }
  
  

  @Transactional
  @Async
  public CompletableFuture<DashboardStatusData> buildStatusData(final DashboardParameters parameters) {
    
    DashboardStatusData dashDataStatus = dashboardRepository.getStatusAmmountData(parameters.getWorkpackId(), parameters.getBaselineId()).orElse(null);

    return CompletableFuture.completedFuture(dashDataStatus);
  }
  
    @Transactional
    @Async
    public CompletableFuture<DatasheetResponse> buildDatasheet(final DashboardParameters parameters) {
      return CompletableFuture.completedFuture(Optional.of(parameters)
            .map(this.datasheetService::build)
            .orElse(null));
    }
  
   @Transactional
   @Async
  public CompletableFuture<RiskDataChart> buildRisk(final DashboardParameters parameters) {
    return CompletableFuture.completedFuture(Optional.of(parameters)
      .map(this.riskService::build)
      .orElse(null));
  }
  

  private List<MilestoneDto> getMilestones(final DashboardParameters parameters) {
    return Optional.of(parameters)
      .map(this.milestoneService::build)
      .orElse(null);
  }
}
