package br.gov.es.openpmo.service.dashboards.v2;

import org.springframework.stereotype.Component;

@Component
public class AsyncDashboardService implements IAsyncDashboardService {

  private final ISyncDashboardService syncDashboardService;

  public AsyncDashboardService(final SyncDashboardService syncDashboardService) { this.syncDashboardService = syncDashboardService; }

  @Override
  public void calculate() {
    this.syncDashboardService.calculate();
  }

}
