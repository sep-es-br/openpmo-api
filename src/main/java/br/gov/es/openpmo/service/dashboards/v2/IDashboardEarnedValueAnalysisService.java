package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.DashboardEarnedValueAnalysis;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.DateIntervalQuery;

import java.util.Optional;

public interface IDashboardEarnedValueAnalysisService {

  DashboardEarnedValueAnalysis build(DashboardParameters parameters, Optional<DateIntervalQuery> dateIntervalQuery);

  DashboardEarnedValueAnalysis calculate(Long workpackId, Optional<DateIntervalQuery> dateIntervalQuery);

}
