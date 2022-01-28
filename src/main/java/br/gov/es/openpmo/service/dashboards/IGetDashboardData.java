package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardDataParameters;
import br.gov.es.openpmo.dto.dashboards.DashboardDataResponse;

public interface IGetDashboardData {

  DashboardDataResponse get(DashboardDataParameters parameters);

}
