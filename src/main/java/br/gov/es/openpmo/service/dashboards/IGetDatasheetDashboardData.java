package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;

@FunctionalInterface
public interface IGetDatasheetDashboardData {

  DatasheetResponse get(final DashboardParameters parameters);

}
