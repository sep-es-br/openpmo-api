package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.tripleconstraint.DateIntervalQuery;
import br.gov.es.openpmo.dto.dashboards.v2.Interval;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.dashboards.Dashboard;
import br.gov.es.openpmo.repository.dashboards.DashboardRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DashboardIntervalService implements IDashboardIntervalService {

  private final DashboardRepository dashboardRepository;

  public DashboardIntervalService(final DashboardRepository dashboardRepository) {
    this.dashboardRepository = dashboardRepository;
  }

  @Override
  public Interval calculateFor(final Long workpackId) {
    if (workpackId == null) {
      throw new NegocioException(ApplicationMessage.WORKPACK_IS_NULL);
    }
    final Optional<Dashboard> maybeDashboard = this.dashboardRepository.findByWorkpackIdForInterval(workpackId);
    if (!maybeDashboard.isPresent()) {
      return Interval.empty();
    }
    final Dashboard dashboard = maybeDashboard.get();
    return new Interval(DateIntervalQuery.of(dashboard.getMonths()));
  }

}
