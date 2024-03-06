package br.gov.es.openpmo.service.completed;

import br.gov.es.openpmo.dto.completed.CompleteWorkpackRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.completed.CompletedRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static br.gov.es.openpmo.utils.ApplicationMessage.*;

@Service
public class CompleteWorkpackService implements ICompleteWorkpackService {

  private final CompletedRepository repository;


  public CompleteWorkpackService(
    final CompletedRepository repository
  ) {
    this.repository = repository;
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
      this.testHierarchyAndSetCompleted(workpackId);
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
      this.setAllIncomplete(parentId);
    }
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
