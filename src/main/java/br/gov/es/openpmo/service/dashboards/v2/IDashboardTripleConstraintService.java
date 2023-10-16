package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.TripleConstraintDataChart;

import java.time.YearMonth;
import java.util.List;

public interface IDashboardTripleConstraintService {

  TripleConstraintDataChart build(DashboardParameters parameters);

  List<TripleConstraintDataChart> calculate(Long workpackId, List<YearMonth> yearMonths);

}
