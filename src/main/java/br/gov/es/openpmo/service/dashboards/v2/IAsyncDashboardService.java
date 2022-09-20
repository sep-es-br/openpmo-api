package br.gov.es.openpmo.service.dashboards.v2;

import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

public interface IAsyncDashboardService {

  @Async
  @Transactional
  void calculate(Long worpackId);

}
