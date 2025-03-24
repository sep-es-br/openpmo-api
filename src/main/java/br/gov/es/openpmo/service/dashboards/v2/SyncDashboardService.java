package br.gov.es.openpmo.service.dashboards.v2;

import org.springframework.stereotype.Service;

import br.gov.es.openpmo.utils.DashboardCacheUtil;

@Service
public class SyncDashboardService implements ISyncDashboardService {

  private final DashboardCacheUtil dashboardCacheUtil;

  public SyncDashboardService(
    final DashboardCacheUtil dashboardCacheUtil
  ) {
    this.dashboardCacheUtil = dashboardCacheUtil;
  }

  @Override
  public void calculate() {
    dashboardCacheUtil.loadAllCache();
  }

}
