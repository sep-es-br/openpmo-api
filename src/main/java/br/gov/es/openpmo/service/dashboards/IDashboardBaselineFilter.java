package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardBaselineResponse;

import java.util.List;

@FunctionalInterface
public interface IDashboardBaselineFilter {

  List<DashboardBaselineResponse> getBaselines(Long workpackId);

}
