package br.gov.es.openpmo.scheduler.updatestatus;

import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.StepRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class UpdateDeliverablesCompletedStatus {

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
    final Collection<Deliverable> deliverables = this.findAllWorkpackDeliverable();
    final Collection<Deliverable> deliverablesCompleted = new ArrayList<>();
    for(final Deliverable deliverable : deliverables) {
      this.addDeliverableIfWasCompleted(deliverablesCompleted, deliverable);
    }
    if(!deliverablesCompleted.isEmpty()) {
      this.onlySaveNodes(deliverablesCompleted);
    }
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
      return;
    }
    final boolean hasWorkToComplete = this.hasWorkToComplete(deliverable.getId());
    if(hasWorkToComplete) return;
    deliverable.setCompleted(true);
    deliverablesCompleted.add(deliverable);
  }

  private void onlySaveNodes(final Iterable<? extends Workpack> workpacks) {
    this.workpackRepository.save(workpacks, 0);
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

}
