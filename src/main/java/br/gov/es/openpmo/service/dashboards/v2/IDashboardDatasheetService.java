package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;

@FunctionalInterface
public interface IDashboardDatasheetService {

    DatasheetResponse build(final DashboardParameters parameters);

}
