package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.models.DeliverableModel;
import br.gov.es.openpmo.model.workpacks.models.MilestoneModel;
import br.gov.es.openpmo.model.workpacks.models.OrganizerModel;
import br.gov.es.openpmo.model.workpacks.models.PortfolioModel;
import br.gov.es.openpmo.model.workpacks.models.ProgramModel;
import br.gov.es.openpmo.model.workpacks.models.ProjectModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import br.gov.es.openpmo.service.properties.PropertyModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_MODEL_DELETE_RELATIONSHIP_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_MODEL_INVALID_STATE_DELETE_RELATIONSHIP_ERROR;

@Service
public class WorkpackModelDeleteService {

  private static final String TYPE_NAME_MODEL_PORTFOLIO = "br.gov.es.openpmo.model.workpacks.models.PortfolioModel";
  private static final String TYPE_NAME_MODEL_PROGRAM = "br.gov.es.openpmo.model.workpacks.models.ProgramModel";
  private static final String TYPE_NAME_MODEL_ORGANIZER = "br.gov.es.openpmo.model.workpacks.models.OrganizerModel";
  private static final String TYPE_NAME_MODEL_DELIVERABLE = "br.gov.es.openpmo.model.workpacks.models.DeliverableModel";
  private static final String TYPE_NAME_MODEL_PROJECT = "br.gov.es.openpmo.model.workpacks.models.ProjectModel";
  private static final String TYPE_NAME_MODEL_MILESTONE = "br.gov.es.openpmo.model.workpacks.models.MilestoneModel";
  private final PropertyModelService propertyModelService;
  private final WorkpackModelRepository workpackModelRepository;

  @Autowired
  public WorkpackModelDeleteService(
    final PropertyModelService propertyModelService,
    final WorkpackModelRepository workpackModelRepository
  ) {
    this.propertyModelService = propertyModelService;
    this.workpackModelRepository = workpackModelRepository;
  }

  @Transactional
  public void delete(final WorkpackModel workpackModel, final WorkpackModel parent) {
    if(isInvalidStateToDelete(workpackModel, parent)) {
      throw new NegocioException(WORKPACK_MODEL_INVALID_STATE_DELETE_RELATIONSHIP_ERROR);
    }

    if(!hasWorkpackInstanceRelationship(workpackModel)) {
      throw new NegocioException(WORKPACK_MODEL_DELETE_RELATIONSHIP_ERROR);
    }

    if(workpackModel.hasMoreThanOneParent()) {
      this.workpackModelRepository.deleteRelationshipBetween(workpackModel.getId(), parent.getId());
      return;
    }

    this.workpackModelRepository.deleteCascadeAllNodesRelated(workpackModel.getId());
  }

  private static boolean isInvalidStateToDelete(final WorkpackModel children, final WorkpackModel parent) {
    return isChildrenInvalidState(children, parent)
           || isParentInvalidState(parent, children);
  }

  private static boolean isParentInvalidState(
    final WorkpackModel parent,
    final WorkpackModel children
  ) {
    return parent != null
           && parent.getChildren() == null
           && children.containsParent(parent);
  }

  private static boolean isChildrenInvalidState(final WorkpackModel children, final WorkpackModel parent) {
    return (children.getParent() == null && parent != null) || (children.getParent() != null && parent == null);
  }

  private static boolean hasWorkpackInstanceRelationship(final WorkpackModel workpackModel) {
    switch(workpackModel.getClass().getTypeName()) {
      case TYPE_NAME_MODEL_PORTFOLIO:
        final PortfolioModel portfolioModel = (PortfolioModel) workpackModel;
        return portfolioModel.getInstances() == null || portfolioModel.getInstances().isEmpty();
      case TYPE_NAME_MODEL_PROGRAM:
        final ProgramModel programModel = (ProgramModel) workpackModel;
        return programModel.getInstances() == null || programModel.getInstances().isEmpty();
      case TYPE_NAME_MODEL_ORGANIZER:
        final OrganizerModel organizerModel = (OrganizerModel) workpackModel;
        return organizerModel.getInstances() == null || organizerModel.getInstances().isEmpty();
      case TYPE_NAME_MODEL_DELIVERABLE:
        final DeliverableModel deliverableModel = (DeliverableModel) workpackModel;
        return deliverableModel.getInstances() == null || deliverableModel.getInstances().isEmpty();
      case TYPE_NAME_MODEL_PROJECT:
        final ProjectModel projectModel = (ProjectModel) workpackModel;
        return projectModel.getInstances() == null || projectModel.getInstances().isEmpty();
      case TYPE_NAME_MODEL_MILESTONE:
        final MilestoneModel milestoneModel = (MilestoneModel) workpackModel;
        return milestoneModel.getInstances() == null || milestoneModel.getInstances().isEmpty();
    }
    return true;
  }
}
