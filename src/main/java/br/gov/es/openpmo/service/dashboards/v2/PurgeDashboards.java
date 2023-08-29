package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.model.dashboards.Dashboard;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.dashboards.DashboardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class PurgeDashboards {

  private final Logger logger = LoggerFactory.getLogger(PurgeDashboards.class);
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
    final List<Workpack> workpacks = new ArrayList<>();
    for (Dashboard dashboard : dashboards) {
      if (dashboard.getWorkpack() != null) {
        workpacks.add(dashboard.getWorkpack());
      }
    }
    this.dashboardRepository.deleteAll(dashboards);
    logger.info("Início do cálculo dos dashboards!");
    final int size = workpacks.size();
    for (Workpack workpack : workpacks) {
      logger.info("Calculando dashboard {} de {}.", workpacks.indexOf(workpack) + 1, size);
      this.asyncDashboardService.calculate(workpack.getId(), true);
    }
    logger.info("Fim do cálculo dos dashboards!");
  }

}
