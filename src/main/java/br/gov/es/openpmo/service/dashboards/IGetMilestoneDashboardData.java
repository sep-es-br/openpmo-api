package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardDataParameters;
import br.gov.es.openpmo.dto.dashboards.MilestoneDataChart;

@FunctionalInterface
public interface IGetMilestoneDashboardData {

  MilestoneDataChart get(final DashboardDataParameters parameters);

}
