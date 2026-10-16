package br.gov.es.openpmo.service.preprojects;

import static br.gov.es.openpmo.utils.ApplicationMessage.PRE_PROJECT_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_DELETE_RELATIONSHIP_ERROR;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.PreProjectRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.service.completed.ICompleteWorkpackService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeletePreProjectService {

  private final PreProjectRepository preProjectRepository;
  private final WorkpackRepository workpackRepository;
  private final WorkpackService workpackService;
  private final ICompleteWorkpackService completeWorkpackService;
  private final ICanAccessService canAccessService;

  public DeletePreProjectService(
    final PreProjectRepository preProjectRepository,
    final WorkpackRepository workpackRepository,
    final WorkpackService workpackService,
    final ICompleteWorkpackService completeWorkpackService,
    final ICanAccessService canAccessService
  ) {
    this.preProjectRepository = preProjectRepository;
    this.workpackRepository = workpackRepository;
    this.workpackService = workpackService;
    this.completeWorkpackService = completeWorkpackService;
    this.canAccessService = canAccessService;
  }

  @Transactional
  public void delete(final Long idPreProject, final Long idProject, final String authorization) {
    if (!this.preProjectRepository.existsById(idPreProject)) {
      throw new RegistroNaoEncontradoException(PRE_PROJECT_NOT_FOUND);
    }

    if (idProject != null) {
      if (!this.preProjectRepository.findActiveProjectId(idPreProject, idProject).isPresent()
        || !this.preProjectRepository.findStructuringProjectId(idPreProject, idProject).isPresent()) {
        throw new NegocioException(WORKPACK_DELETE_RELATIONSHIP_ERROR);
      }
      this.canAccessService.ensureCanEditResource(idProject, authorization);
      if (this.workpackRepository.hasActiveBaseline(idProject)
        || this.workpackRepository.hasProposedBaseline(idProject)
        || this.workpackRepository.hasCancelPropose(idProject)) {
        throw new NegocioException(WORKPACK_DELETE_RELATIONSHIP_ERROR);
      }
      final Workpack project = this.workpackService.findById(idProject);
      this.workpackService.delete(project);
      this.completeWorkpackService.onWorkpackDeleted(project);
      return;
    }

    if (this.preProjectRepository.findActiveProjectId(idPreProject).isPresent()) {
      throw new NegocioException(WORKPACK_DELETE_RELATIONSHIP_ERROR);
    }

    this.preProjectRepository.deleteOwnedProperties(idPreProject);
    this.preProjectRepository.deletePreProject(idPreProject);
  }
}
