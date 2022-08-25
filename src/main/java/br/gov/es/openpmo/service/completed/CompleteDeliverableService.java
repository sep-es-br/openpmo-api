package br.gov.es.openpmo.service.completed;

import br.gov.es.openpmo.dto.completed.CompleteDeliverableRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.repository.completed.CompletedRepository;
import br.gov.es.openpmo.service.workpack.HasScheduleSessionActive;
import org.springframework.stereotype.Service;

import static br.gov.es.openpmo.utils.ApplicationMessage.SCHEDULE_SESSION_ACTIVE_INVALID_STATE_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;

@Service
public class CompleteDeliverableService implements ICompleteDeliverableService {

  private final CompletedRepository repository;
  private final HasScheduleSessionActive hasScheduleSessionActive;

  public CompleteDeliverableService(
    final CompletedRepository repository,
    final HasScheduleSessionActive hasScheduleSessionActive
  ) {
    this.repository = repository;
    this.hasScheduleSessionActive = hasScheduleSessionActive;
  }

  @Override
  public void apply(
    final Long workpackId,
    final CompleteDeliverableRequest request
  ) {
    this.assertExistsWorkpack(workpackId);
    this.assertScheduleSessionIsNotActive(workpackId);
    this.setFileds(workpackId, request);
    if(request.getCompleted()) {
      this.testHierarchyAndSetCompleted(workpackId);
    }
    else {
      this.setAllIncompleted(workpackId);
    }
  }

  private void assertScheduleSessionIsNotActive(final Long workpackId) {
    if(this.hasScheduleSessionActive.execute(workpackId)) {
      throw new NegocioException(SCHEDULE_SESSION_ACTIVE_INVALID_STATE_ERROR);
    }
  }

  private void setFileds(
    final Long workpackId,
    final CompleteDeliverableRequest request
  ) {
    this.repository.setCompleted(workpackId, request.getCompleted());
  }

  private void setAllIncompleted(final Long workpackId) {
    final Long parentId = this.repository.getParentId(workpackId);
    if(parentId == null) {
      return;
    }
    this.repository.setCompleted(parentId, false);
    this.setAllIncompleted(parentId);
  }

  private void assertExistsWorkpack(final Long idDeliverable) {
    if(!this.repository.existsById(idDeliverable)) {
      throw new NegocioException(WORKPACK_NOT_FOUND);
    }
  }

  private void testHierarchyAndSetCompleted(final Long workpackId) {
    final Long parentId = this.repository.getParentId(workpackId);
    if(parentId == null) {
      return;
    }
    if(this.repository.allSonsAreCompleted(parentId)) {
      this.repository.setCompleted(parentId, true);
      this.testHierarchyAndSetCompleted(parentId);
    }
  }

}
