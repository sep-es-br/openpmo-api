package br.gov.es.openpmo.service.dashboards.v2;

import org.springframework.transaction.annotation.Transactional;

@FunctionalInterface
public interface IAsyncDashboardService {

//  @Async
  @Transactional
  void calculate(Long worpackId, Boolean calculateInterval);

}
