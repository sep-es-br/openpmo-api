package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpackmodel.EligibleProjectDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalContextDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalProgramOptionDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalProgramModelDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalProjectParticipationDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalSelectionOptionDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalLinkedModelDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalWorkpackDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalWorkpackParticipationDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.repository.TransversalRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static br.gov.es.openpmo.utils.ApplicationMessage.TRANSVERSAL_PROGRAM_CONTEXT_INVALID;
import static br.gov.es.openpmo.utils.ApplicationMessage.TRANSVERSAL_PROJECT_NOT_ELIGIBLE;
import static br.gov.es.openpmo.utils.ApplicationMessage.TRANSVERSAL_PROJECT_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.TRANSVERSAL_WORKPACK_NOT_ELIGIBLE;
import static br.gov.es.openpmo.utils.ApplicationMessage.TRANSVERSAL_WORKPACK_NOT_FOUND;
import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Service
public class TransversalProjectService {

  private final TransversalRepository repository;

  @Autowired
  public TransversalProjectService(final TransversalRepository repository) {
    this.repository = repository;
  }

  public List<TransversalProgramModelDto> findProgramModelsForView(
    final Long idTransversalView
  ) {
    return this.repository.findProgramModelsForView(idTransversalView);
  }

  public List<TransversalProgramOptionDto> findProgramsForView(
    final Long idTransversalView,
    final Long idPlan
  ) {
    return this.repository.findProgramsForView(idTransversalView, idPlan);
  }

  public List<EligibleProjectDto> findEligibleProjects(final Long idTransversalProgram) {
    this.requireProgramContext(idTransversalProgram);
    return this.repository.findEligibleProjects(idTransversalProgram);
  }

  public List<TransversalLinkedModelDto> findLinkedModels(final Long idTransversalProgram) {
    this.requireProgramContext(idTransversalProgram);
    final List<TransversalLinkedModelDto> models = new ArrayList<>(
      this.repository.findConfiguredModels(idTransversalProgram)
    );
    models.addAll(this.repository.findHistoricalModels(idTransversalProgram));
    return models;
  }

  public Page<TransversalWorkpackDto> findEligibleWorkpacks(
    final Long idTransversalProgram,
    final Long idWorkpackModel,
    final Pageable pageable
  ) {
    this.requireProgramContext(idTransversalProgram);
    return new PageImpl<>(
      this.repository.findEligibleWorkpacks(
        idTransversalProgram, idWorkpackModel, Math.toIntExact(pageable.getOffset()), pageable.getPageSize()
      ),
      pageable,
      this.repository.countEligibleWorkpacks(idTransversalProgram, idWorkpackModel)
    );
  }

  public Page<TransversalWorkpackDto> findIncludedWorkpacks(
    final Long idTransversalProgram,
    final Long idWorkpackModel,
    final Pageable pageable,
    final boolean showCanceled
  ) {
    this.requireProgramContext(idTransversalProgram);
    return new PageImpl<>(
      this.repository.findIncludedWorkpacks(
        idTransversalProgram, idWorkpackModel, showCanceled,
        Math.toIntExact(pageable.getOffset()), pageable.getPageSize()
      ),
      pageable,
      this.repository.countIncludedWorkpacks(idTransversalProgram, idWorkpackModel, showCanceled)
    );
  }

  @Transactional
  public TransversalWorkpackParticipationDto includeWorkpack(
    final Long idTransversalProgram,
    final Long idWorkpack
  ) {
    this.requireProgramContext(idTransversalProgram);
    if (!this.repository.existsWorkpack(idWorkpack)) {
      throw new NegocioException(TRANSVERSAL_WORKPACK_NOT_FOUND);
    }
    if (!this.repository.hasDirectParticipation(idTransversalProgram, idWorkpack)
      && !this.repository.isEligibleWorkpack(idTransversalProgram, idWorkpack)) {
      throw new NegocioException(TRANSVERSAL_WORKPACK_NOT_ELIGIBLE);
    }
    this.repository.includeWorkpack(idTransversalProgram, idWorkpack);
    return workpackParticipation(idTransversalProgram, idWorkpack, true);
  }

  @Transactional
  public TransversalWorkpackParticipationDto removeWorkpack(
    final Long idTransversalProgram,
    final Long idWorkpack
  ) {
    this.requireProgramContext(idTransversalProgram);
    if (!this.repository.existsWorkpack(idWorkpack)) {
      throw new NegocioException(TRANSVERSAL_WORKPACK_NOT_FOUND);
    }
    this.repository.removeWorkpack(idTransversalProgram, idWorkpack);
    return workpackParticipation(idTransversalProgram, idWorkpack, false);
  }

  private TransversalWorkpackParticipationDto workpackParticipation(
    final Long idTransversalProgram,
    final Long idWorkpack,
    final boolean included
  ) {
    final TransversalWorkpackParticipationDto response = new TransversalWorkpackParticipationDto();
    response.setIdTransversalProgram(idTransversalProgram);
    response.setIdWorkpack(idWorkpack);
    response.setIncluded(included);
    return response;
  }

  public List<EligibleProjectDto> findIncludedProjects(final Long idTransversalProgram) {
    this.requireProgramContext(idTransversalProgram);
    return this.repository.findIncludedProjects(idTransversalProgram);
  }

  public List<TransversalSelectionOptionDto> findSelectionOptions(
    final Long idRootTransversalViewModel,
    final Long idPlan,
    final Long idPlanModel
  ) {
    if (idPlan == null) {
      return this.repository.findSelectionOptionsByPlanModel(idRootTransversalViewModel);
    }
    return this.repository.findSelectionOptions(idRootTransversalViewModel, idPlan, idPlanModel);
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
    if (!this.repository.hasDirectParticipation(idTransversalProgram, idProject)
      && !this.repository.isEligibleWorkpack(idTransversalProgram, idProject)) {
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
