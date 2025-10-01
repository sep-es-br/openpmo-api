package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardBaselineResponse;
import br.gov.es.openpmo.model.baselines.Baseline;

import java.util.List;
import java.util.Optional;

public interface IDashboardBaselineService {

  List<DashboardBaselineResponse> getBaselines(Long workpackId);
   
    Optional<Baseline> findActiveBaseline(Long workpackId, String workpackType);  

}
