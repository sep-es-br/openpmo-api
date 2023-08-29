package br.gov.es.openpmo.service.schedule;

import br.gov.es.openpmo.dto.completed.CompleteWorkpackRequest;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Program;
import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.StepRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.service.completed.ICompleteWorkpackService;
import br.gov.es.openpmo.service.dashboards.v2.IAsyncDashboardService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UpdateStatusService {

  private final WorkpackRepository workpackRepository;

  private final StepRepository stepRepository;

  private final IAsyncDashboardService dashboardService;

  private final ICompleteWorkpackService completeWorkpackService;

  public UpdateStatusService(
    final WorkpackRepository workpackRepository,
    final StepRepository stepRepository,
    final IAsyncDashboardService dashboardService,
    final ICompleteWorkpackService completeWorkpackService
  ) {
    this.workpackRepository = workpackRepository;
    this.stepRepository = stepRepository;
    this.dashboardService = dashboardService;
    this.completeWorkpackService = completeWorkpackService;
  }

  public List<Deliverable> getDeliverablesByStepId(final Long stepId) {
    return this.stepRepository.findAllDeliverablesByStepId(stepId);
  }

  public List<Deliverable> getDeliverablesByScheduleId(final Long scheduleId) {
    return this.stepRepository.findDeliverablesByScheduleId(scheduleId);
  }

  public void update(final Collection<Deliverable> deliverables, Boolean calculateInterval) {
    final Collection<Workpack> analyzedDeliverables = new ArrayList<>();

    for (final Deliverable deliverable : deliverables) {
      this.updateIfCompleted(deliverable, analyzedDeliverables);
    }

    for (Workpack workpack : analyzedDeliverables) {
      final CompleteWorkpackRequest request = new CompleteWorkpackRequest(workpack.getCompleted(), null);
      this.completeWorkpackService.apply(workpack.getId(), request);
    }

    this.updateDashboards(deliverables, calculateInterval);
  }

  private void updateIfCompleted(
    final Deliverable deliverable,
    final Collection<? super Workpack> analyzedDeliverables
  ) {
    final boolean hasScheduleRelated = this.hasScheduleRelated(deliverable);
    if (!hasScheduleRelated) {
      return;
    }
    final boolean hasWorkToComplete = this.hasWorkToComplete(deliverable.getId());
    if (!hasWorkToComplete) {
      deliverable.setCompleted(true);
      analyzedDeliverables.add(deliverable);
      return;
    }
    deliverable.setCompleted(false);
    analyzedDeliverables.add(deliverable);

    final Optional<Project> maybeProject = this.workpackRepository.findProject(deliverable.getId());
    if (maybeProject.isPresent()) {
      final Project project = maybeProject.get();
      project.setCompleted(false);
      analyzedDeliverables.add(project);
    }

    final Optional<Program> maybeProgram = this.workpackRepository.findProgram(deliverable.getId());
    if (maybeProgram.isPresent()) {
      final Program program = maybeProgram.get();
      program.setCompleted(false);
      analyzedDeliverables.add(program);
    }
  }


  private boolean hasScheduleRelated(final Deliverable deliverable) {
    return this.workpackRepository.hasScheduleRelated(deliverable.getId());
  }

  private boolean hasWorkToComplete(final Long idDeliverable) {
    return this.hasBaselineActive(idDeliverable)
      ? this.hasWorkToCompleteComparingWithActiveBaseline(idDeliverable)
      : this.hasWorkToCompleteComparingWithMaster(idDeliverable);
  }

  private boolean hasBaselineActive(final Long idDeliverable) {
    return this.workpackRepository.hasActiveBaseline(idDeliverable);
  }

  private boolean hasWorkToCompleteComparingWithActiveBaseline(final Long idDeliverable) {
    return this.stepRepository.hasWorkToCompleteComparingWithActiveBaseline(idDeliverable);
  }

  private boolean hasWorkToCompleteComparingWithMaster(final Long idDeliverable) {
    return this.stepRepository.hasWorkToCompleteComparingWithMaster(idDeliverable);
  }

  private void updateDashboards(final Collection<? extends Deliverable> deliverables, Boolean calculateInterval) {
    final List<Long> deliverablesId = deliverables.stream()
      .map(Deliverable::getId)
      .collect(Collectors.toList());

    this.stepRepository.findAllDeliverablesAndAscendents(deliverablesId)
      .stream()
      .map(Workpack::getId)
      .forEach(worpackId -> this.dashboardService.calculate(worpackId, calculateInterval));
  }

}
