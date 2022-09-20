package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardBaselineResponse;
import br.gov.es.openpmo.repository.dashboards.DashboardBaselineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardBaselineService implements IDashboardBaselineService {

  private final DashboardBaselineRepository baselineRepository;

  @Autowired
  public DashboardBaselineService(final DashboardBaselineRepository baselineRepository) {
    this.baselineRepository = baselineRepository;
  }

  @Override
  public List<DashboardBaselineResponse> getBaselines(final Long workpackId) {
    return this.baselineRepository.findAllByWorkpackId(workpackId);
  }

}
