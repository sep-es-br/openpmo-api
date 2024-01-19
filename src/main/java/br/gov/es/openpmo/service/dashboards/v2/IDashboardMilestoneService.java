package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.MilestoneDataChart;
import br.gov.es.openpmo.dto.dashboards.MilestoneDto;

import java.time.YearMonth;
import java.util.List;

public interface IDashboardMilestoneService {

  List<MilestoneDto> build(final DashboardParameters parameters);

  MilestoneDataChart build(
    final Long worpackId,
    final YearMonth yearMonth
  );

}
