package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardBaselineResponse;

import java.util.List;

@FunctionalInterface
public interface IDashboardBaselineService {

    List<DashboardBaselineResponse> getBaselines(Long workpackId);

}
