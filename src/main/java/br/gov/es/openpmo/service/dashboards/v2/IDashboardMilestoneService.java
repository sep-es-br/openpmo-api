package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.MilestoneDataChart;

import java.time.YearMonth;

public interface IDashboardMilestoneService {

    MilestoneDataChart build(final DashboardParameters parameters);

    MilestoneDataChart build(final Long worpackId, final YearMonth yearMonth);

}
