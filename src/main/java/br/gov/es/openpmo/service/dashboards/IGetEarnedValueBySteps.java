package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStep;

import java.util.List;

@FunctionalInterface
public interface IGetEarnedValueBySteps {

    List<EarnedValueByStep> get(DashboardParameters parameters);

}
