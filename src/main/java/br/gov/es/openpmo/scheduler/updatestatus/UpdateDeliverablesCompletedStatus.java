package br.gov.es.openpmo.scheduler.updatestatus;

import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.StepRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class UpdateDeliverablesCompletedStatus {

  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateDeliverablesCompletedStatus.class);
  private final WorkpackRepository workpackRepository;
  private final StepRepository stepRepository;

  @Autowired
  public UpdateDeliverablesCompletedStatus(
    final WorkpackRepository workpackRepository,
    final StepRepository stepRepository
  ) {
    this.workpackRepository = workpackRepository;
    this.stepRepository = stepRepository;
  }

  @Transactional
  public void update() {
    LOGGER.info("Initializing update of deliverables");

    final Collection<Deliverable> deliverables = this.findAllWorkpackDeliverable();

    LOGGER.info("Found {} workpacks deliverables", deliverables.size());

    final Collection<Deliverable> deliverablesCompleted = new ArrayList<>();

    for(final Deliverable deliverable : deliverables) {
      this.addDeliverableIfWasCompleted(deliverablesCompleted, deliverable);
    }

    LOGGER.info("Update status to completed of {} deliverables", deliverablesCompleted.size());

    if(!deliverablesCompleted.isEmpty()) {
      this.onlySaveNodes(deliverablesCompleted);
    }

    LOGGER.info("Finalizing update of deliverables");
  }

  private Collection<Deliverable> findAllWorkpackDeliverable() {
    return this.workpackRepository.findAllDeliverables();
  }

  private void addDeliverableIfWasCompleted(
    final Collection<? super Deliverable> deliverablesCompleted,
    final Deliverable deliverable
  ) {
    final boolean hasScheduleRelated = this.hasScheduleRelated(deliverable);

    if(!hasScheduleRelated) {
      LOGGER.info("Not found schedule for Deliverable {}", deliverable.getId());
      return;
    }

    final boolean hasWorkToComplete = this.hasWorkToComplete(deliverable.getId());

    LOGGER.info("Deliverable {} was completed: {}", deliverable.getId(), !hasWorkToComplete);

    if(hasWorkToComplete) return;

    deliverable.setCompleted(true);
    deliverablesCompleted.add(deliverable);
  }

  private boolean hasScheduleRelated(final Deliverable deliverable) {
    return this.workpackRepository.hasScheduleRelated(deliverable.getId());
  }

  private boolean hasWorkToComplete(final Long idDeliverable) {
    return this.hasBaselineActive(idDeliverable) ?
      this.hasWorkToCompleteComparingWithActiveBaseline(idDeliverable) :
      this.hasWorkToCompleteComparingWithMaster(idDeliverable);
  }

  private boolean hasWorkToCompleteComparingWithMaster(final Long idDeliverable) {
    return this.stepRepository.hasWorkToCompleteComparingWithMaster(idDeliverable);
  }

  private boolean hasBaselineActive(final Long idDeliverable) {
    return this.workpackRepository.hasActiveBaseline(idDeliverable);
  }

  private boolean hasWorkToCompleteComparingWithActiveBaseline(final Long idDeliverable) {
    return this.stepRepository.hasWorkToCompleteComparingWithActiveBaseline(idDeliverable);
  }

  private void onlySaveNodes(final Iterable<? extends Workpack> workpacks) {
    this.workpackRepository.save(workpacks, 0);
  }

}
