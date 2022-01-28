package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.RiskDataChart;

@FunctionalInterface
public interface IGetRiskDashboardData {

  RiskDataChart get(Long idWorkpack);

}
