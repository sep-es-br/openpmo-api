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
import br.gov.es.openpmo.dto.workpackmodel.TransversalStructuralLinkDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalProjectScopeCountDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalProjectScopeItemDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.repository.TransversalRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static br.gov.es.openpmo.utils.ApplicationMessage.TRANSVERSAL_PROGRAM_CONTEXT_INVALID;
import static br.gov.es.openpmo.utils.ApplicationMessage.TRANSVERSAL_PROJECT_NOT_ELIGIBLE;
import static br.gov.es.openpmo.utils.ApplicationMessage.TRANSVERSAL_PROJECT_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.TRANSVERSAL_WORKPACK_NOT_ELIGIBLE;
import static br.gov.es.openpmo.utils.ApplicationMessage.TRANSVERSAL_WORKPACK_NOT_FOUND;
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

  public List<TransversalStructuralLinkDto> findStructuralLinks(final Long idTransversalProgram) {
    this.requireProgramContext(idTransversalProgram);
    return this.repository.findStructuralLinks(idTransversalProgram, null, null, false, true);
  }

  public List<TransversalStructuralLinkDto> findStructuralLinksForProgramTree(final Long idTransversalProgram) {
    return this.findStructuralLinksForProgramTree(idTransversalProgram, true);
  }

  public List<TransversalStructuralLinkDto> findStructuralLinksForProgramTree(
    final Long idTransversalProgram,
    final boolean includeStructuralDescendants
  ) {
    this.requireProgramContext(idTransversalProgram);
    return this.repository.findStructuralLinks(
      idTransversalProgram, null, null, true, includeStructuralDescendants
    );
  }

  public List<TransversalStructuralLinkDto> findStructuralLinksForView(final Long idTransversalView, final Long idPlan) {
    return this.repository.findStructuralLinks(null, idTransversalView, idPlan, false, true);
  }

  public List<TransversalProjectScopeItemDto> findProjectScopeItems(final Long idTransversalProgram) {
    this.requireProgramContext(idTransversalProgram);
    return this.repository.findProjectScopeItems(idTransversalProgram);
  }

  public void attachProjectScopeCounts(
    final List<TransversalStructuralLinkDto> links,
    final List<TransversalProjectScopeItemDto> scopeItems,
    final Set<Long> readableProjectIds
  ) {
    final Map<Long, Map<Long, TransversalProjectScopeCountDto>> countsByProject = new HashMap<>();
    scopeItems.stream()
      .filter(item -> readableProjectIds.contains(item.getIdProject()))
      .forEach(item -> {
        final Map<Long, TransversalProjectScopeCountDto> countsByModel = countsByProject
          .computeIfAbsent(item.getIdProject(), key -> new HashMap<>());
        final TransversalProjectScopeCountDto count = countsByModel.computeIfAbsent(
          item.getIdWorkpackModel(), key -> {
            final TransversalProjectScopeCountDto newCount = new TransversalProjectScopeCountDto();
            newCount.setIdProject(item.getIdProject());
            newCount.setIdWorkpackModel(item.getIdWorkpackModel());
            newCount.setModelNameInPlural(item.getModelNameInPlural());
            newCount.setFontIcon(item.getFontIcon());
            return newCount;
          }
        );
        count.setTotalCount(count.getTotalCount() + 1);
        if (item.isLinked()) {
          count.setLinkedCount(count.getLinkedCount() + 1);
        }
      });
    links.forEach(link -> {
      final Map<Long, TransversalProjectScopeCountDto> countsByModel = countsByProject.get(link.getIdWorkpack());
      if (countsByModel != null) {
        final List<TransversalProjectScopeCountDto> counts = countsByModel.values().stream()
          .filter(count -> count.getLinkedCount() > 0)
          .sorted(Comparator.comparing(TransversalProjectScopeCountDto::getModelNameInPlural,
            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
          .collect(Collectors.toList());
        if (!counts.isEmpty()) {
          link.setScopeCounts(counts);
        }
      }
    });
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
    if (idRootTransversalViewModel == null && idPlanModel != null) {
      return this.repository.findSelectionOptionsByPlanModel(idPlanModel);
    }
    if (idPlan == null) {
      return this.repository.findSelectionOptionsForRootModel(idRootTransversalViewModel);
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
