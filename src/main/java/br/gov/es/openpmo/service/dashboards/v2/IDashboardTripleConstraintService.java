package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.TripleConstraintDataChart;

import java.util.List;
import java.util.Optional;

public interface IDashboardTripleConstraintService {

  TripleConstraintDataChart build(DashboardParameters parameters);

  Optional<List<TripleConstraintDataChart>> calculate(Long workpackId);

}
