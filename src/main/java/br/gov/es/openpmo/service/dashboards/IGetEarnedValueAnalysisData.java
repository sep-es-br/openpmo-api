package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardDataParameters;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.DashboardEarnedValueAnalysis;

@FunctionalInterface
public interface IGetEarnedValueAnalysisData {

  DashboardEarnedValueAnalysis get(DashboardDataParameters parameters);

}
