package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardDataResponse;
import br.gov.es.openpmo.dto.dashboards.DashboardParameters;

public interface IGetDashboardData {

  DashboardDataResponse get(DashboardParameters parameters);

}
