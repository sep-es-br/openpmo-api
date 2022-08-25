package br.gov.es.openpmo.service.completed;

import br.gov.es.openpmo.dto.workpack.EndDeliverableManagementRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.completed.CompletedRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;

@Component
public class DeliverableEndManagementService implements IDeliverableEndManagementService {

  private final CompletedRepository repository;

  public DeliverableEndManagementService(final CompletedRepository repository) {
    this.repository = repository;
  }

  @Override
  public void apply(
    final Long workpackId,
    final EndDeliverableManagementRequest request
  ) {
    this.assertExistsWorkpack(workpackId);
    this.setFields(workpackId, request);
    if(request.getEndManagementDate() != null) {
      this.testHierarchySetEndDate(workpackId);
    }
    else {
      this.setAllEndDateNull(workpackId);
    }
  }

  private void setFields(
    final Long workapckId,
    final EndDeliverableManagementRequest request
  ) {
    this.repository.setEndManagementDate(workapckId, request.getEndManagementDate());
    this.repository.setReason(workapckId, request.getReason());
  }

  private void assertExistsWorkpack(final Long workpackId) {
    if(!this.repository.existsById(workpackId)) {
      throw new NegocioException(WORKPACK_NOT_FOUND);
    }
  }

  private void testHierarchySetEndDate(final Long workpackId) {
    final Long parentId = this.repository.getParentId(workpackId);
    if(parentId == null) {
      return;
    }
    if(this.allSonsHaveEndDate(parentId)) {
      final LocalDate latestDate = this.repository.getLatestDateFromSons(parentId);
      this.repository.setEndManagementDate(parentId, latestDate);
      this.testHierarchySetEndDate(parentId);
    }
  }

  private boolean allSonsHaveEndDate(final Long parentId) {
    final Set<Workpack> workpacks = this.repository.allSonsHaveEndDate(parentId);
    return workpacks.stream().allMatch(Workpack::hasEndManagementDate);
  }

  private void setAllEndDateNull(final Long workpackId) {
    final Long parentId = this.repository.getParentId(workpackId);
    if(parentId == null) {
      return;
    }
    this.repository.setEndManagementDate(parentId, null);
    this.setAllEndDateNull(parentId);
  }

}
