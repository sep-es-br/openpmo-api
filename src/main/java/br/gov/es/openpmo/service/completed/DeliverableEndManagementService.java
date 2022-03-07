package br.gov.es.openpmo.service.completed;

import br.gov.es.openpmo.dto.workpack.EndDeliverableManagementRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.repository.completed.CompletedRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;

@Component
public class DeliverableEndManagementService implements IDeliverableEndManagementService {

    private final CompletedRepository repository;

    public DeliverableEndManagementService(final CompletedRepository repository) {
        this.repository = repository;
    }

    @Override
    public void apply(Long workpackId, EndDeliverableManagementRequest request) {
        assertExistsWorkpack(workpackId);
        setFields(workpackId, request);
        if (request.getEndManagementDate() != null) {
            testHierarchySetEndDate(workpackId);
        } else {
            setAllEndDateNull(workpackId);
        }
    }

    private void setFields(Long workapckId, EndDeliverableManagementRequest request) {
        this.repository.setEndManagementDate(workapckId, request.getEndManagementDate());
        this.repository.setReason(workapckId, request.getReason());
    }

    private void assertExistsWorkpack(Long workpackId) {
        if (!this.repository.existsById(workpackId)) {
            throw new NegocioException(WORKPACK_NOT_FOUND);
        }
    }

    private void testHierarchySetEndDate(Long workpackId) {
        final Long parentId = this.repository.getParentId(workpackId);
        if (parentId == null) {
            return;
        }
        if (this.repository.allSonsHaveEndDate(parentId)) {
            LocalDate latestDate = this.repository.getLatestDateFromSons(parentId);
            this.repository.setEndManagementDate(parentId, latestDate);
            testHierarchySetEndDate(parentId);
        }
    }

    private void setAllEndDateNull(Long workpackId) {
        Long parentId = this.repository.getParentId(workpackId);
        if (parentId == null) {
            return;
        }
        this.repository.setEndManagementDate(parentId, null);
        setAllEndDateNull(parentId);
    }

}
