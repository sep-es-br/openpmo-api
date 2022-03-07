package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.RiskDataChart;

public interface IDashboardRiskService {

    RiskDataChart build(DashboardParameters parameters);

    RiskDataChart build(Long workpackId);

}
