package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStep;

import java.util.List;

public interface IDashboardEarnedValueByStepsService {

  List<EarnedValueByStep> build(DashboardParameters parameters);

  List<EarnedValueByStep> calculate(final Long workpackId);

}
