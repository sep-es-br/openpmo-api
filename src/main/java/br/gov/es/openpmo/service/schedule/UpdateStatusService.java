package br.gov.es.openpmo.service.schedule;

import br.gov.es.openpmo.dto.completed.CompleteWorkpackRequest;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Program;
import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.StepRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.service.completed.ICompleteWorkpackService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class UpdateStatusService {

  private final Logger logger;

  private final WorkpackRepository workpackRepository;

  private final StepRepository stepRepository;


  private final ICompleteWorkpackService completeWorkpackService;

  public UpdateStatusService(
    final WorkpackRepository workpackRepository,
    final StepRepository stepRepository,
    final ICompleteWorkpackService completeWorkpackService, Logger logger
  ) {
    this.workpackRepository = workpackRepository;
    this.stepRepository = stepRepository;
    this.completeWorkpackService = completeWorkpackService;
    this.logger = logger;
  }

  public List<Deliverable> getDeliverablesByStepId(final Long stepId) {
    return this.stepRepository.findAllDeliverablesByStepId(stepId);
  }

  public List<Deliverable> getDeliverablesByScheduleId(final Long scheduleId) {
    return this.stepRepository.findDeliverablesByScheduleId(scheduleId);
  }

  public void updateAllDeliverables() {
    final List<Deliverable> deliverables = this.workpackRepository.findAllDeliverables();
    this.update(deliverables);
    logger.info(">>> Finalizou update de todos os deliverables");
  }


  public List<Deliverable> getAllDeliverables() {
    return workpackRepository.findAllDeliverables();
  }

  public boolean checkHasWorkToComplete(final Long idProperty) {
    Long idDeliverable = this.workpackRepository.findWorkpackIdByPropertyId(idProperty);
    return this.hasWorkToComplete(idDeliverable);
  }

  public void update(final Collection<? extends Deliverable> deliverables) {
    final Collection<Workpack> analyzedDeliverables = new ArrayList<>();

    for (final Deliverable deliverable : deliverables) {
      this.updateIfCompleted(deliverable, analyzedDeliverables);
    }

    for (final Workpack workpack : analyzedDeliverables) {
      final CompleteWorkpackRequest request = new CompleteWorkpackRequest(workpack.getCompleted(), null);
      this.completeWorkpackService.apply(workpack.getId(), request);
    }

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
      this.workpackRepository.updateSituationValue(deliverable.getId(), "Concluída");
      return;
    }
    final boolean isSituationConcluded = this.workpackRepository.isSituationConcluded(deliverable.getId());
    if(isSituationConcluded){
      this.workpackRepository.updateSituationValue(deliverable.getId(), "Em execução");
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

  public boolean hasWorkToComplete(final Long idDeliverable) {
    return this.hasBaselineActive(idDeliverable)
      ? this.hasWorkToCompleteComparingWithActiveBaseline(idDeliverable)
      : this.hasWorkToCompleteComparingWithMaster(idDeliverable);
  }

  private boolean hasBaselineActive(final Long idDeliverable) {
    return this.workpackRepository.hasActiveBaselineForDeliverable(idDeliverable);
  }

  private boolean hasWorkToCompleteComparingWithActiveBaseline(final Long idDeliverable) {
    return this.stepRepository.hasWorkToCompleteComparingWithActiveBaseline(idDeliverable);
  }

  private boolean hasWorkToCompleteComparingWithMaster(final Long idDeliverable) {
    return this.stepRepository.hasWorkToCompleteComparingWithMaster(idDeliverable);
  }

  public boolean canSaveStep(final Long stepId) {
    return this.stepRepository.canSaveStep(stepId);
  }

}
