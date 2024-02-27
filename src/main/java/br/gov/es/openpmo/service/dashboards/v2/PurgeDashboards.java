package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.model.workpacks.Organizer;
import br.gov.es.openpmo.model.workpacks.Portfolio;
import br.gov.es.openpmo.model.workpacks.Program;
import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.dashboards.DashboardRepository;
import br.gov.es.openpmo.service.schedule.UpdateStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PurgeDashboards {

  private static final Logger log = LoggerFactory.getLogger(PurgeDashboards.class);

  private final WorkpackRepository workpackRepository;

  private final ISyncDashboardService syncDashboardService;

  private final UpdateStatusService updateStatusService;

  private final DashboardRepository dashboardRepository;

  public PurgeDashboards(
    final WorkpackRepository workpackRepository,
    final ISyncDashboardService syncDashboardService,
    final UpdateStatusService updateStatusService,
    final DashboardRepository dashboardRepository
  ) {
    this.workpackRepository = workpackRepository;
    this.syncDashboardService = syncDashboardService;
    this.updateStatusService = updateStatusService;
    this.dashboardRepository = dashboardRepository;
  }

  @Async
  public void execute() {
    final Instant start = Instant.now();
    log.info("Apagando todos os Dashboards existentes.");
    this.dashboardRepository.purgeAllDashboards();
    log.info("Dashboards apagados com sucesso");
    log.info("Início do cálculo dos dashboards!");
    log.info("Buscando workpacks...");
    final List<Workpack> workpacks = this.workpackRepository.findAll();
    this.removeMilestones(workpacks);
    log.info("{} workpacks encontrados.", workpacks.size());
    this.calculateForClass(workpacks, Deliverable.class);
    this.calculateForClass(workpacks, Project.class);
    this.calculateForClass(workpacks, Organizer.class);
    this.calculateForClass(workpacks, Program.class);
    this.calculateForClass(workpacks, Portfolio.class);
    log.info("Fim do cálculo dos dashboards!");
    final Instant end = Instant.now();
    this.logTime(start, end);
  }

  private void removeMilestones(final Collection<Workpack> workpacks) {
    log.info("Ignorando milestones...");
    final List<Workpack> milestones = getWorkpacksByClass(workpacks, Milestone.class);
    workpacks.removeAll(milestones);
  }

  private void calculateForClass(final Collection<Workpack> workpacks, final Class<? extends Workpack> clazz) {
    final String simpleName = clazz.getSimpleName();
    final List<Workpack> workpackByType = getWorkpacksByClass(workpacks, clazz);
    final int size = workpackByType.size();
    if (Deliverable.class.equals(clazz)) {
      log.info(
        "{}: Calculando completed para {} entregas.",
        simpleName,
        size
      );
      this.updateStatusService.updateOnlyCompletedFlag(
        workpackByType.stream()
          .map(Deliverable.class::cast)
          .collect(Collectors.toList())
      );
    }
    log.info("Calculando dashboard para {}. {} encontrados.", simpleName, size);
    for (final Workpack workpack : workpackByType) {
      log.info(
        "{}: Calculando dashboard {} de {} | ID do Workpack = {}.",
        simpleName,
        workpackByType.indexOf(workpack) + 1,
        size,
        workpack.getId()
      );
      this.syncDashboardService.calculate();
    }
    workpacks.removeAll(workpackByType);
  }

  private void logTime(final Instant start, final Instant end) {
    final Duration between = Duration.between(start, end);
    long seconds = between.getSeconds();
    final long hours = (seconds - (seconds % 3600)) / 3600;
    seconds = seconds - (hours * 3600);
    final long minutes = (seconds - (seconds % 60)) / 60;
    seconds = seconds - (minutes * 60);
    log.info("Tempo decorrido: {}h{}min{}s.", hours, minutes, seconds);
  }

  private static List<Workpack> getWorkpacksByClass(final Iterable<Workpack> workpacks, final Class<? extends Workpack> clazz) {
    final List<Workpack> list = new ArrayList<>();
    for (final Workpack workpack : workpacks) {
      if (clazz.isInstance(workpack)) {
        final Workpack cast = clazz.cast(workpack);
        list.add(cast);
      }
    }
    return list;
  }

}
