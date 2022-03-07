package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.MilestoneDataChart;

@FunctionalInterface
public interface IGetMilestoneDashboardData {

  MilestoneDataChart get(final DashboardParameters parameters);

}
