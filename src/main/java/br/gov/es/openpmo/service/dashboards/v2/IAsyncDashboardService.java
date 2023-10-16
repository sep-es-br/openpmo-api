package br.gov.es.openpmo.service.dashboards.v2;

@FunctionalInterface
public interface IAsyncDashboardService {

//  @Async
//  @Transactional
  void calculate(Long workpackId, Boolean calculateInterval);

}
