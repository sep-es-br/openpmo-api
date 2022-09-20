package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelCompletedUpdateRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.models.DeliverableModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static br.gov.es.openpmo.utils.ApplicationMessage.COMPLETED_STATUS_MUST_BE_NOT_NULL;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_MODEL_INVALID_TYPE;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;

@Component
public class WorkpackModelPatchCompletedStatus {

  private final WorkpackModelRepository repository;

  @Autowired
  public WorkpackModelPatchCompletedStatus(final WorkpackModelRepository repository) {
    this.repository = repository;
  }

  public void patch(
    final WorkpackModelCompletedUpdateRequest request,
    final Long idWorkpackModel
  ) {

    if(Objects.isNull(request)) throw new IllegalArgumentException(COMPLETED_STATUS_MUST_BE_NOT_NULL);

    final WorkpackModel workpackModel = this.findWorkpackModelById(idWorkpackModel);

    ifNotDeliverableModelThrowException(workpackModel);

    ((DeliverableModel) workpackModel).setShowCompletedManagement(request.getCompleted());

    this.saveOnlyWorkpackModel(workpackModel);

  }

  private static void ifNotDeliverableModelThrowException(final WorkpackModel workpackModel) {
    if(!(workpackModel instanceof DeliverableModel)) {
      throw new NegocioException(WORKPACK_MODEL_INVALID_TYPE);
    }
  }

  private void saveOnlyWorkpackModel(final WorkpackModel workpackModel) {
    this.repository.save(workpackModel, 0);
  }

  private WorkpackModel findWorkpackModelById(final Long idWorkpackModel) {
    return this.repository.findById(idWorkpackModel)
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
  }

}
