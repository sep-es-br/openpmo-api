package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardDataParameters;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.TripleConstraintDataChart;

@FunctionalInterface
public interface IGetTripleConstraintData {

  TripleConstraintDataChart get(DashboardDataParameters parameters);

}
