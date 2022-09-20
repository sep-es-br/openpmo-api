package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.tripleconstraint.CostAndScopeData;
import br.gov.es.openpmo.model.schedule.Step;

import java.time.YearMonth;
import java.util.Set;

public interface IDashboardCostScopeService {

  CostAndScopeData build(
    Long baselineId,
    YearMonth referenceDate,
    Set<? extends Step> steps,
    boolean canceled
  );

}
