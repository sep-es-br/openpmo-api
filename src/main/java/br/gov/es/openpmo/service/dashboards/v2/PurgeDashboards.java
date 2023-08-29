package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.model.dashboards.Dashboard;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.dashboards.DashboardRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class PurgeDashboards {

  private final DashboardRepository dashboardRepository;
  private final IAsyncDashboardService asyncDashboardService;

  public PurgeDashboards(
    DashboardRepository dashboardRepository,
    IAsyncDashboardService asyncDashboardService
  ) {
    this.dashboardRepository = dashboardRepository;
    this.asyncDashboardService = asyncDashboardService;
  }

  @Transactional
  public void execute() {
    final Iterable<Dashboard> dashboards = this.dashboardRepository.findAll();
    final Collection<Workpack> workpacks = new ArrayList<>();
    for (Dashboard dashboard : dashboards) {
      if (dashboard.getWorkpack() != null) {
        workpacks.add(dashboard.getWorkpack());
      }
    }
    this.dashboardRepository.deleteAll(dashboards);
    for (Workpack workpack : workpacks) {
      this.asyncDashboardService.calculate(workpack.getId(), true);
    }
  }

}
