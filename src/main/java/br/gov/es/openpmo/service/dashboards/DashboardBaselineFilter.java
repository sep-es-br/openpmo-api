package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardBaselineResponse;
import br.gov.es.openpmo.repository.dashboards.DashboardBaselineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardBaselineFilter implements IDashboardBaselineFilter {

  private final DashboardBaselineRepository baselineRepository;

  @Autowired
  public DashboardBaselineFilter(final DashboardBaselineRepository baselineRepository) {
    this.baselineRepository = baselineRepository;
  }

  @Override
  public List<DashboardBaselineResponse> getBaselines(final Long workpackId) {
    return this.baselineRepository.findAllByWorkpackId(workpackId);
  }

}
