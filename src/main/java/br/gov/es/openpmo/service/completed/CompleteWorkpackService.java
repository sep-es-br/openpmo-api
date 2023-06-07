package br.gov.es.openpmo.service.completed;

import br.gov.es.openpmo.dto.completed.CompleteWorkpackRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.completed.CompletedRepository;
import br.gov.es.openpmo.service.workpack.HasScheduleSessionActive;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static br.gov.es.openpmo.utils.ApplicationMessage.*;

@Service
public class CompleteWorkpackService implements ICompleteWorkpackService {

  private final CompletedRepository repository;

  private final WorkpackRepository workpackRepository;

  private final HasScheduleSessionActive hasScheduleSessionActive;

  public CompleteWorkpackService(
    final CompletedRepository repository,
    final WorkpackRepository workpackRepository,
    final HasScheduleSessionActive hasScheduleSessionActive
  ) {
    this.repository = repository;
    this.workpackRepository = workpackRepository;
    this.hasScheduleSessionActive = hasScheduleSessionActive;
  }

  private static void assertDateIsValid(
    Workpack workpack,
    CompleteWorkpackRequest request
  ) {
    if (workpack instanceof Milestone && request.getCompleted() && LocalDate.now().isBefore(request.getDate())) {
      throw new NegocioException(DATE_IS_IN_FUTURE);
    }
  }

  @Override
  public void apply(
    final Long workpackId,
    final CompleteWorkpackRequest request
  ) {
    final Workpack workpack = this.getWorkpack(workpackId);
//    assertScheduleSessionIsNotActive(workpackId);
    assertDateIsValid(
      workpack,
      request
    );
    this.setFields(
      workpackId,
      request
    );
    if (request.getCompleted()) {
      this.testHierarchyAndSetCompleted(workpackId);
    } else {
      this.setAllIncomplete(workpackId);
    }
  }

  private void assertScheduleSessionIsNotActive(final Long workpackId) {
    if (!this.hasScheduleSessionActive.execute(workpackId)) return;
    final boolean hasSchedule = hasScheduleRelated(workpackId);
    if (!hasSchedule) {
      throw new NegocioException(SCHEDULE_SESSION_ACTIVE_INVALID_STATE_ERROR);
    }
  }

  private boolean hasScheduleRelated(Long idWorkpack) {
    return this.workpackRepository.hasScheduleRelated(idWorkpack);
  }

  private void setFields(
    final Long workpackId,
    final CompleteWorkpackRequest request
  ) {
    this.repository.setCompleted(
      workpackId,
      request.getCompleted()
    );
  }

  private void setAllIncomplete(final Long workpackId) {
    final Long parentId = this.repository.getParentId(workpackId);
    if (parentId == null) {
      return;
    }
    this.repository.setCompleted(
      parentId,
      false
    );
    this.setAllIncomplete(parentId);
  }

  private Workpack getWorkpack(final Long idDeliverable) {
    return this.repository.findById(idDeliverable)
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
  }

  private void testHierarchyAndSetCompleted(final Long workpackId) {
    final Long parentId = this.repository.getParentId(workpackId);
    if (parentId == null) {
      return;
    }
    if (this.repository.allSonsAreCompleted(parentId)) {
      this.repository.setCompleted(
        parentId,
        true
      );
      this.testHierarchyAndSetCompleted(parentId);
    }
  }

}
