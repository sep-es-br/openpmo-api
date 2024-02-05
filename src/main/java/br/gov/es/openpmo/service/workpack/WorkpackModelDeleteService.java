package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
public class WorkpackModelDeleteService {

  private final WorkpackModelRepository workpackModelRepository;

  @Autowired
  public WorkpackModelDeleteService(final WorkpackModelRepository workpackModelRepository) {
    this.workpackModelRepository = workpackModelRepository;
  }

  public void delete(
    final Long idWorkpackModel,
    final Long idParentModel
  ) {
    if (idParentModel != null) {
      this.workpackModelRepository
              .findAllByIdWithParents(idWorkpackModel, idParentModel)
              .ifPresent(workpackModel -> deleteWorkpackModel(workpackModel, idParentModel));
    } else {
      this.workpackModelRepository
              .findByIdWithChildren(idWorkpackModel)
              .ifPresent(workpackModel -> deleteWorkpackModel(workpackModel, idParentModel));
    }

  }

  private void deleteWorkpackModel(WorkpackModel workpackModel, Long idParentModel) {
    if (workpackModel.hasMoreThanOneParent()) {
      this.workpackModelRepository.deleteRelationshipBetween(workpackModel.getId(), idParentModel);
      final WorkpackModel parent = getParent(workpackModel, idParentModel);
      if (parent != null) {
        parent.getChildren().remove(workpackModel);
        workpackModel.getParent().remove(parent);
      }
      return;
    }
    final Set<WorkpackModel> children = workpackModel.getChildren();
    if (children != null) {
      for (WorkpackModel child : children) {
        deleteWorkpackModel(child, workpackModel.getId());
      }
    }
    this.workpackModelRepository.delete(workpackModel);
  }

  private static WorkpackModel getParent(WorkpackModel workpackModel, Long idParentModel) {
    final Set<WorkpackModel> parents = workpackModel.getParent();
    for (WorkpackModel parent : parents) {
      if (Objects.equals(parent.getId(), idParentModel)) {
        return parent;
      }
    }
    return null;
  }

}
