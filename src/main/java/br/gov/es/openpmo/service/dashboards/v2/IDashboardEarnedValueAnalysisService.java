package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.DashboardEarnedValueAnalysis;

public interface IDashboardEarnedValueAnalysisService {

  DashboardEarnedValueAnalysis build(DashboardParameters parameters);

  DashboardEarnedValueAnalysis calculate(Long workpackId);

}
