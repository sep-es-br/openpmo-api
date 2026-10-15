package br.gov.es.openpmo.service.completed;

import br.gov.es.openpmo.dto.completed.CompleteWorkpackRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.completed.CompletedRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static br.gov.es.openpmo.utils.ApplicationMessage.DATE_IS_IN_FUTURE;
import static br.gov.es.openpmo.utils.ApplicationMessage.PROJECT_COMPLETION_REQUIREMENTS_NOT_MET;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;

@Service
@Transactional
public class CompleteWorkpackService implements ICompleteWorkpackService {

  private final CompletedRepository repository;
  private final WorkpackRepository workpackRepository;

  public CompleteWorkpackService(
    final CompletedRepository repository,
    final WorkpackRepository workpackRepository
  ) {
    this.repository = repository;
    this.workpackRepository = workpackRepository;
  }

  private static void assertDateIsValid(
    Workpack workpack,
    CompleteWorkpackRequest request
  ) {
    if (workpack instanceof Milestone && Boolean.TRUE.equals(request.getCompleted())
      && LocalDate.now().isBefore(request.getDate())) {
      throw new NegocioException(DATE_IS_IN_FUTURE);
    }
  }

  @Override
  public void apply(
    final Long workpackId,
    final CompleteWorkpackRequest request
  ) {
    final Workpack workpack = this.getWorkpack(workpackId);
    assertDateIsValid(workpack, request);

    if (workpack.isProject()) {
      final boolean completed = this.repository.allProjectDeliverablesAndMilestonesAreCompleted(workpackId);
      if (Boolean.TRUE.equals(request.getCompleted()) && !completed) {
        throw new NegocioException(PROJECT_COMPLETION_REQUIREMENTS_NOT_MET);
      }
      this.updateProjectCompletion(workpackId, completed);
    } else {
      this.repository.setCompleted(workpackId, request.getCompleted());
    }
    this.recalculateAncestors(workpackId);
  }

  private Workpack getWorkpack(final Long workpackId) {
    return this.repository.findById(workpackId)
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
  }

  private void updateProjectCompletion(final Long projectId, final boolean completed) {
    this.repository.setCompleted(projectId, completed);
    if (completed) {
      this.workpackRepository.updateSituationValue(projectId, "Concluído");
    } else if (Boolean.TRUE.equals(this.workpackRepository.isSituationCompleted(projectId))) {
      this.workpackRepository.resetSituationOrStatusToDefault(projectId);
    }
  }

  private void recalculateNode(final Long workpackId) {
    if (this.workpackRepository.isProject(workpackId)) {
      this.updateProjectCompletion(
        workpackId,
        this.repository.allProjectDeliverablesAndMilestonesAreCompleted(workpackId)
      );
    } else {
      this.repository.setCompleted(workpackId, this.repository.allSonsAreCompleted(workpackId));
    }
  }

  private void recalculateAncestors(final Long workpackId) {
    for (final Long ancestorId : this.repository.getAncestorIds(workpackId)) {
      this.recalculateNode(ancestorId);
    }
  }

  @Override
  public void onWorkpackCreated(final Workpack workpack) {
    if (!workpack.isDeliverable() && !workpack.isMilestone()) {
      this.recalculateNode(workpack.getId());
    }
    this.recalculateAncestors(workpack.getId());
  }

  @Override
  public void onWorkpackDeleted(final Workpack workpack) {
    this.recalculateAncestors(workpack.getId());
  }

  @Override
  public void onWorkpackCanceled(final Long workpackId) {
    if (this.workpackRepository.isProject(workpackId)) {
      this.repository.setCompleted(workpackId, false);
      this.workpackRepository.updateSituationValue(workpackId, "Cancelado");
    }
    this.recalculateAncestors(workpackId);
  }

  @Override
  public void recalculateCompletionStatus(final Long workpackId) {
    final Workpack workpack = this.getWorkpack(workpackId);
    if (!workpack.isDeliverable() && !workpack.isMilestone()) {
      this.recalculateNode(workpackId);
    }
    this.recalculateAncestors(workpackId);
  }
}
