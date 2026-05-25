package br.gov.es.openpmo.service.completed;

import br.gov.es.openpmo.dto.completed.CompleteWorkpackRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.completed.CompletedRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static br.gov.es.openpmo.utils.ApplicationMessage.*;
import java.util.Collections;

@Service
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
    assertDateIsValid(
      workpack,
      request
    );
    this.setFields(
      workpackId,
      request
    );
    if (request.getCompleted()) {
      this.testHierarchyAndSetCompleted(workpackId, false);
    } else {
      this.setAllIncomplete(workpackId);
    }
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
    final List<Long> parentIds = this.repository.getParentIds(workpackId);
    if (parentIds == null) {
      return;
    }
    for (Long parentId : parentIds) {
      this.repository.setCompleted(
        parentId,
        false
      );
      if (workpackRepository.isProject(parentId) 
          && workpackRepository.isSituationCompleted(parentId)) {

          this.workpackRepository.resetSituationOrStatusToDefault(parentId);
      }
      this.setAllIncomplete(parentId);
    }
  }

  private Workpack getWorkpack(final Long idDeliverable) {
    return this.repository.findById(idDeliverable)
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
  }

private void testHierarchyAndSetCompleted(final Long workpackId, boolean startFromSelf) {

  List<Long> parentIds = startFromSelf
    ? Collections.singletonList(workpackId)
    : repository.getParentIds(workpackId);

  if (parentIds == null || parentIds.isEmpty()) {
    return;
  }

  for (Long parentId : parentIds) {

    if (this.repository.allSonsAreCompleted(parentId)) {

      this.repository.setCompleted(parentId, true);

      if (workpackRepository.isProject(parentId)) {
        this.workpackRepository.updateSituationValue(parentId, "Concluído");
      }
      this.testHierarchyAndSetCompleted(parentId, false);
    }
  }
}

  public void onWorkpackCreated(Workpack workpack) {

    if (workpack.isDeliverable() || workpack.isMilestone() || workpack.isProject()) {
      this.setAllIncomplete(workpack.getId());
    } else {
      this.testHierarchyAndSetCompleted(workpack.getId(), true);
    }

  }

  public void onWorkpackDeleted(Workpack workpack) {
    Long parentId = repository.getParentId(workpack.getId());
    if (parentId == null) {
      return;
    }
    
    boolean allCompleted = repository.allSonsAreCompleted(parentId);

    if(allCompleted){
      this.testHierarchyAndSetCompleted(workpack.getId(), false);
    }else{
      this.setAllIncomplete(workpack.getId());
    }
  }

  public void recalculateCompletionStatus(Long workpackId){
    Workpack workpack = workpackRepository.findById(workpackId)
    .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));

    if(Boolean.TRUE.equals(workpack.getCompleted())){
      this.testHierarchyAndSetCompleted(workpackId, false);
    }else{
      this.setAllIncomplete(workpackId);
    }
  }

}
