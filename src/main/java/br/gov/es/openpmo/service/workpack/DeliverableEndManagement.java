package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpack.EndDeliverableManagementRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_IS_NOT_DELIVERABLE_INVALID_STATE_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;

@Component
public class DeliverableEndManagement implements IDeliverableEndManagement {

  private final WorkpackRepository repository;

  public DeliverableEndManagement(final WorkpackRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional
  public void execute(final Long idDeliverable, @Valid @NotNull final EndDeliverableManagementRequest request) {
    final Workpack workpack = this.findDeliverableById(idDeliverable);

    ifNotDeliverableThrowException(workpack);

    ((Deliverable) workpack).setEndManagementDate(request.getEndManagementDate());

    this.onlySaveWorkpack(workpack);

    final Project project = this.findProjectParentOf(workpack);

    final boolean hasRemainDeliveriesToManage = this.repository.hasRemainDeliveriesToManage(project.getId());

    if(hasRemainDeliveriesToManage) return;

    this.endManagementOfProject(project);
  }

  private void endManagementOfProject(final Project project) {
    project.setEndManagementDate(LocalDate.now());
    this.onlySaveWorkpack(project);
  }

  private Project findProjectParentOf(final Workpack workpack) {
    return this.repository.findProjectParentOf(workpack.getId())
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
  }

  private static void ifNotDeliverableThrowException(final Workpack workpack) {
    if(!(workpack instanceof Deliverable)) {
      throw new NegocioException(WORKPACK_IS_NOT_DELIVERABLE_INVALID_STATE_ERROR);
    }
  }

  private void onlySaveWorkpack(final Workpack workpack) {
    this.repository.save(workpack, 1);
  }

  private Workpack findDeliverableById(final Long idDeliverable) {
    return this.repository.findById(idDeliverable)
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
  }


}
