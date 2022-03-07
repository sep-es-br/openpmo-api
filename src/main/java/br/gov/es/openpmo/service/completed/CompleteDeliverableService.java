package br.gov.es.openpmo.service.completed;

import br.gov.es.openpmo.dto.completed.CompleteDeliverableRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.repository.completed.CompletedRepository;
import org.springframework.stereotype.Service;

import static br.gov.es.openpmo.utils.ApplicationMessage.SCHEDULE_SESSION_ACTIVE_INVALID_STATE_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;

@Service
public class CompleteDeliverableService implements ICompleteDeliverableService {

    private final CompletedRepository repository;

    public CompleteDeliverableService(CompletedRepository repository) {
        this.repository = repository;
    }

    @Override
    public void apply(Long workpackId, CompleteDeliverableRequest request) {
        assertExistsWorkpack(workpackId);
        assertScheduleSessionIsNotActive(workpackId);
        setFileds(workpackId, request);
        if (request.getCompleted()) {
            testHierarchyAndSetCompleted(workpackId);
        } else {
            setAllIncompleted(workpackId);
        }
    }

    private void assertScheduleSessionIsNotActive(Long workpackId) {
        if (this.repository.isScheduleSessionActive(workpackId)) {
            throw new NegocioException(SCHEDULE_SESSION_ACTIVE_INVALID_STATE_ERROR);
        }
    }

    private void setFileds(Long workpackId, CompleteDeliverableRequest request) {
        this.repository.setCompleted(workpackId, request.getCompleted());
    }

    private void setAllIncompleted(Long workpackId) {
        Long parentId = this.repository.getParentId(workpackId);
        if (parentId == null) {
            return;
        }
        this.repository.setCompleted(parentId, false);
        setAllIncompleted(parentId);
    }

    private void assertExistsWorkpack(Long idDeliverable) {
        if (!this.repository.existsById(idDeliverable)) {
            throw new NegocioException(WORKPACK_NOT_FOUND);
        }
    }

    private void testHierarchyAndSetCompleted(Long workpackId) {
        final Long parentId = this.repository.getParentId(workpackId);
        if (parentId == null) {
            return;
        }
        if (this.repository.allSonsAreCompleted(parentId)) {
            this.repository.setCompleted(parentId, true);
            this.testHierarchyAndSetCompleted(parentId);
        }
    }

}
