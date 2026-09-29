package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpackmodel.EligibleProjectDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalContextDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalProjectParticipationDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.repository.TransversalRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static br.gov.es.openpmo.utils.ApplicationMessage.TRANSVERSAL_PROGRAM_CONTEXT_INVALID;
import static br.gov.es.openpmo.utils.ApplicationMessage.TRANSVERSAL_PROJECT_NOT_ELIGIBLE;
import static br.gov.es.openpmo.utils.ApplicationMessage.TRANSVERSAL_PROJECT_NOT_FOUND;

@Service
public class TransversalProjectService {

  private final TransversalRepository repository;

  @Autowired
  public TransversalProjectService(final TransversalRepository repository) {
    this.repository = repository;
  }

  public List<EligibleProjectDto> findEligibleProjects(final Long idTransversalProgram) {
    this.requireProgramContext(idTransversalProgram);
    return this.repository.findEligibleProjects(idTransversalProgram);
  }

  public List<EligibleProjectDto> findIncludedProjects(final Long idTransversalProgram) {
    this.requireProgramContext(idTransversalProgram);
    return this.repository.findIncludedProjects(idTransversalProgram);
  }

  @Transactional
  public TransversalProjectParticipationDto includeProject(
    final Long idTransversalProgram,
    final Long idProject
  ) {
    this.requireProgramContext(idTransversalProgram);
    if (!this.repository.existsProject(idProject)) {
      throw new NegocioException(TRANSVERSAL_PROJECT_NOT_FOUND);
    }
    if (!this.repository.isEligibleProject(idTransversalProgram, idProject)) {
      throw new NegocioException(TRANSVERSAL_PROJECT_NOT_ELIGIBLE);
    }

    this.repository.includeProject(idTransversalProgram, idProject);
    return this.participation(idTransversalProgram, idProject, true);
  }

  @Transactional
  public TransversalProjectParticipationDto removeProject(
    final Long idTransversalProgram,
    final Long idProject
  ) {
    this.requireProgramContext(idTransversalProgram);
    if (!this.repository.existsProject(idProject)) {
      throw new NegocioException(TRANSVERSAL_PROJECT_NOT_FOUND);
    }

    this.repository.removeProject(idTransversalProgram, idProject);
    return this.participation(idTransversalProgram, idProject, false);
  }

  private TransversalProjectParticipationDto participation(
    final Long idTransversalProgram,
    final Long idProject,
    final boolean included
  ) {
    final TransversalProjectParticipationDto response = new TransversalProjectParticipationDto();
    response.setIdTransversalProgram(idTransversalProgram);
    response.setIdProject(idProject);
    response.setIncluded(included);
    return response;
  }

  private TransversalContextDto requireProgramContext(final Long idTransversalProgram) {
    return this.repository.findTransversalProgramContext(idTransversalProgram)
      .orElseThrow(() -> new NegocioException(TRANSVERSAL_PROGRAM_CONTEXT_INVALID));
  }
}
