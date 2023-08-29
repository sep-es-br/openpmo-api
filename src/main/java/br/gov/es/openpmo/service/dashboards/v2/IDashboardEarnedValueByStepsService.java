package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStep;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.DateIntervalQuery;

import java.util.List;
import java.util.Optional;

public interface IDashboardEarnedValueByStepsService {

  List<EarnedValueByStep> build(DashboardParameters parameters, Optional<DateIntervalQuery> dateIntervalQuery);

  List<EarnedValueByStep> calculate(final Long workpackId, Optional<DateIntervalQuery> dateIntervalQuery);

}
