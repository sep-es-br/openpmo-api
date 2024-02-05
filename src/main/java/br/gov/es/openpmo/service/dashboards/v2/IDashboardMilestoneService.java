package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.MilestoneDto;

import java.util.List;

public interface IDashboardMilestoneService {

  List<MilestoneDto> build(final DashboardParameters parameters);


}
