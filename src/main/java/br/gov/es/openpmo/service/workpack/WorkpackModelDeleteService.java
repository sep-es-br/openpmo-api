package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_MODEL_DELETE_RELATIONSHIP_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_MODEL_INVALID_STATE_DELETE_RELATIONSHIP_ERROR;

@Service
public class WorkpackModelDeleteService {

  private final WorkpackModelRepository workpackModelRepository;

  @Autowired
  public WorkpackModelDeleteService(final WorkpackModelRepository workpackModelRepository) {
    this.workpackModelRepository = workpackModelRepository;
  }

  private static boolean isInvalidStateToDelete(
    final WorkpackModel children,
    final WorkpackModel parent
  ) {
    return isChildrenInvalidState(children, parent)
      || isParentInvalidState(parent, children);
  }

  private static boolean isChildrenInvalidState(
    final WorkpackModel children,
    final WorkpackModel parent
  ) {
    return (children.getParent() == null && parent != null) || (children.getParent() != null && parent == null);
  }

  private static boolean isParentInvalidState(
    final WorkpackModel parent,
    final WorkpackModel children
  ) {
    return parent != null
      && parent.getChildren() == null
      && children.containsParent(parent);
  }

  @Transactional
  public void delete(
    final WorkpackModel workpackModel,
    final WorkpackModel parent
  ) {
    if (isInvalidStateToDelete(workpackModel, parent)) {
      throw new NegocioException(WORKPACK_MODEL_INVALID_STATE_DELETE_RELATIONSHIP_ERROR);
    }

    if (workpackModel.hasMoreThanOneParent()) {
      this.workpackModelRepository.deleteRelationshipBetween(workpackModel.getId(), parent.getId());
      return;
    }

    if (hasWorkpackInstanceRelationship(workpackModel)) {
      throw new NegocioException(WORKPACK_MODEL_DELETE_RELATIONSHIP_ERROR);
    }

    this.workpackModelRepository.deleteCascadeAllNodesRelated(workpackModel.getId());
  }

  private static boolean hasWorkpackInstanceRelationship(final WorkpackModel workpackModel) {
    final Set<? extends Workpack> instances = workpackModel.getInstances();
    if (instances != null && !instances.isEmpty()) {
      return true;
    }
    final Set<WorkpackModel> children = workpackModel.getChildren();
    if (children == null) {
      return false;
    }
    return children.stream()
      .anyMatch(WorkpackModelDeleteService::hasWorkpackInstanceRelationship);
  }

}
