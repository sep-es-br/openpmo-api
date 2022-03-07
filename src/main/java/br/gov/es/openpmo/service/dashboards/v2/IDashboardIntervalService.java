package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.v2.Interval;

public interface IDashboardIntervalService {

    Interval calculateFor(Long workpackId);

}
