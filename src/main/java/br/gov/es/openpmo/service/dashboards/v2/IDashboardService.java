package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.v2.DashboardResponse;
import br.gov.es.openpmo.dto.dashboards.v2.SimpleDashboard;

public interface IDashboardService {

  DashboardResponse build(DashboardParameters parameters);

  SimpleDashboard buildSimple(Long workpackId);

}
