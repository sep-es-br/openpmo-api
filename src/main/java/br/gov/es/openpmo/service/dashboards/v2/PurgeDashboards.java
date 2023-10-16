package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.model.workpacks.Organizer;
import br.gov.es.openpmo.model.workpacks.Portfolio;
import br.gov.es.openpmo.model.workpacks.Program;
import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class PurgeDashboards {

  private final Logger logger = LoggerFactory.getLogger(PurgeDashboards.class);

  private final WorkpackRepository workpackRepository;
  private final IAsyncDashboardService asyncDashboardService;

  public PurgeDashboards(
    WorkpackRepository workpackRepository,
    IAsyncDashboardService asyncDashboardService
  ) {
    this.workpackRepository = workpackRepository;
    this.asyncDashboardService = asyncDashboardService;
  }

  @Async
  public void execute() {
    final Instant start = Instant.now();
    logger.info("Início do cálculo dos dashboards!");
    logger.info("Buscando workpacks...");
    final List<Workpack> workpacks = this.workpackRepository.findAll();
    removeMilestones(workpacks);
    logger.info("{} workpacks encontrados.", workpacks.size());
    calculateForClass(workpacks, Deliverable.class);
    calculateForClass(workpacks, Project.class);
    calculateForClass(workpacks, Organizer.class);
    calculateForClass(workpacks, Program.class);
    calculateForClass(workpacks, Portfolio.class);
    logger.info("Fim do cálculo dos dashboards!");
    final Instant end = Instant.now();
    logTime(start, end);
  }

  private void removeMilestones(Collection<Workpack> workpacks) {
    logger.info("Ignorando milestones...");
    final List<Workpack> milestones = getWorkpacksByClass(workpacks, Milestone.class);
    workpacks.removeAll(milestones);
  }

  private void calculateForClass(Collection<Workpack> workpacks, Class<? extends Workpack> clazz) {
    final String simpleName = clazz.getSimpleName();
    final List<Workpack> workpackByType = getWorkpacksByClass(workpacks, clazz);
    final int size = workpackByType.size();
    logger.info("Calculando dashboard para {}. {} encontrados.", simpleName, size);
    for (Workpack workpack : workpackByType) {
      logger.info(
        "{}: Calculando dashboard {} de {} | ID do Workpack = {}.",
        simpleName,
        workpackByType.indexOf(workpack) + 1,
        size,
        workpack.getId()
      );
      this.asyncDashboardService.calculate(workpack.getId(), true);
    }
    workpacks.removeAll(workpackByType);
  }

  private static List<Workpack> getWorkpacksByClass(Iterable<Workpack> workpacks, Class<? extends Workpack> clazz) {
    List<Workpack> list = new ArrayList<>();
    for (Workpack workpack : workpacks) {
      if (clazz.isInstance(workpack)) {
        Workpack cast = clazz.cast(workpack);
        list.add(cast);
      }
    }
    return list;
  }

  private void logTime(Instant start, Instant end) {
    final Duration between = Duration.between(start, end);
    long seconds = between.getSeconds();
    final long hours = (seconds - (seconds % 3600)) / 3600;
    seconds = seconds - (hours * 3600);
    final long minutes = (seconds - (seconds % 60)) / 60;
    seconds = seconds - (minutes * 60);
    logger.info("Tempo decorrido: {}h{}min{}s.", hours, minutes, seconds);
  }

}
