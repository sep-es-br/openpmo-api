package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.tripleconstraint.CostAndScopeData;
import br.gov.es.openpmo.model.schedule.Step;

import java.time.YearMonth;
import java.util.Collection;

public interface IGetCostAndScope {

  CostAndScopeData get(
    Long idBaseline,
    YearMonth referenceDate,
    Collection<? extends Step> steps
  );

}
