package br.gov.es.openpmo.controller.workpack;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.ResponseBasePaginated;
import br.gov.es.openpmo.dto.workpackmodel.EligibleProjectDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalProgramOptionDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalProgramModelDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalProjectParticipationDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalSelectionOptionDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalLinkedModelDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalWorkpackDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalWorkpackParticipationDto;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessData;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.service.workpack.TransversalProjectService;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@CrossOrigin
@RequestMapping("/transversal")
public class TransversalController {

  private final TransversalProjectService transversalProjectService;
  private final ICanAccessService canAccessService;
  private final ICanAccessData canAccessData;

  @Autowired
  public TransversalController(
    final TransversalProjectService transversalProjectService,
    final ICanAccessService canAccessService,
    final ICanAccessData canAccessData
  ) {
    this.transversalProjectService = transversalProjectService;
    this.canAccessService = canAccessService;
    this.canAccessData = canAccessData;
  }

  @GetMapping("/selection-options")
  public ResponseEntity<ResponseBase<List<TransversalSelectionOptionDto>>> getSelectionOptions(
    @RequestParam("id-root-transversal-view-model") final Long idRootTransversalViewModel,
    @RequestParam(value = "id-plan", required = false) final Long idPlan,
    @RequestParam(value = "id-plan-model", required = false) final Long idPlanModel,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(idRootTransversalViewModel, authorization);
    final List<TransversalSelectionOptionDto> options = this.transversalProjectService.findSelectionOptions(
      idRootTransversalViewModel,
      idPlan,
      idPlanModel
    );
    return ResponseEntity.ok(ResponseBase.of(options));
  }

  @GetMapping("/views/{idTransversalView}/program-models")
  public ResponseEntity<ResponseBase<List<TransversalProgramModelDto>>> getProgramModels(
    @PathVariable final Long idTransversalView,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(idTransversalView, authorization);
    return ResponseEntity.ok(ResponseBase.of(
      this.transversalProjectService.findProgramModelsForView(idTransversalView).stream()
        .filter(model -> this.canAccessData.execute(model.getId(), authorization).canReadResource())
        .collect(Collectors.toList())
    ));
  }

  @GetMapping("/views/{idTransversalView}/programs")
  public ResponseEntity<ResponseBase<List<TransversalProgramOptionDto>>> getPrograms(
    @PathVariable final Long idTransversalView,
    @RequestParam("id-plan") final Long idPlan,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(idTransversalView, authorization);
    return ResponseEntity.ok(ResponseBase.of(
      this.transversalProjectService.findProgramsForView(idTransversalView, idPlan).stream()
        .filter(program -> this.canAccessData.execute(program.getIdTransversalProgram(), authorization).canReadResource())
        .collect(Collectors.toList())
    ));
  }

  @GetMapping("/programs/{idTransversalProgram}/eligible-projects")
  public ResponseEntity<ResponseBase<List<EligibleProjectDto>>> getEligibleProjects(
    @PathVariable final Long idTransversalProgram,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanReadResourceWorkpack(idTransversalProgram, authorization);
    return ResponseEntity.ok(ResponseBase.of(
      this.transversalProjectService.findEligibleProjects(idTransversalProgram)
    ));
  }

  @GetMapping("/programs/{idTransversalProgram}/linked-models")
  public ResponseEntity<ResponseBase<List<TransversalLinkedModelDto>>> getLinkedModels(
    @PathVariable final Long idTransversalProgram,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanReadResourceWorkpack(idTransversalProgram, authorization);
    return ResponseEntity.ok(ResponseBase.of(
      this.transversalProjectService.findLinkedModels(idTransversalProgram)
    ));
  }

  @GetMapping("/programs/{idTransversalProgram}/eligible-workpacks")
  public ResponseEntity<ResponseBasePaginated<TransversalWorkpackDto>> getEligibleWorkpacks(
    @PathVariable final Long idTransversalProgram,
    @RequestParam("id-workpack-model") final Long idWorkpackModel,
    @RequestParam(defaultValue = "0") final int page,
    @RequestParam(defaultValue = "20") final int pageSize,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanReadResourceWorkpack(idTransversalProgram, authorization);
    final Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, pageSize), 50));
    return ResponseEntity.ok(ResponseBasePaginated.of(
      this.transversalProjectService.findEligibleWorkpacks(idTransversalProgram, idWorkpackModel, pageable)
    ));
  }

  @GetMapping("/programs/{idTransversalProgram}/workpacks")
  public ResponseEntity<ResponseBasePaginated<TransversalWorkpackDto>> getIncludedWorkpacks(
    @PathVariable final Long idTransversalProgram,
    @RequestParam("id-workpack-model") final Long idWorkpackModel,
    @RequestParam(defaultValue = "0") final int page,
    @RequestParam(defaultValue = "20") final int pageSize,
    @RequestParam(value = "show-canceled", defaultValue = "false") final boolean showCanceled,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanReadResourceWorkpack(idTransversalProgram, authorization);
    final Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, pageSize), 50));
    return ResponseEntity.ok(ResponseBasePaginated.of(
      this.transversalProjectService.findIncludedWorkpacks(idTransversalProgram, idWorkpackModel, pageable, showCanceled)
    ));
  }

  @PostMapping("/programs/{idTransversalProgram}/workpacks/{idWorkpack}")
  public ResponseEntity<ResponseBase<TransversalWorkpackParticipationDto>> includeWorkpack(
    @PathVariable final Long idTransversalProgram,
    @PathVariable final Long idWorkpack,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(idTransversalProgram, authorization);
    return ResponseEntity.ok(ResponseBase.of(
      this.transversalProjectService.includeWorkpack(idTransversalProgram, idWorkpack)
    ));
  }

  @DeleteMapping("/programs/{idTransversalProgram}/workpacks/{idWorkpack}")
  public ResponseEntity<ResponseBase<TransversalWorkpackParticipationDto>> removeWorkpack(
    @PathVariable final Long idTransversalProgram,
    @PathVariable final Long idWorkpack,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(idTransversalProgram, authorization);
    return ResponseEntity.ok(ResponseBase.of(
      this.transversalProjectService.removeWorkpack(idTransversalProgram, idWorkpack)
    ));
  }

  @GetMapping("/programs/{idTransversalProgram}/projects")
  public ResponseEntity<ResponseBase<List<EligibleProjectDto>>> getIncludedProjects(
    @PathVariable final Long idTransversalProgram,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanReadResourceWorkpack(idTransversalProgram, authorization);
    return ResponseEntity.ok(ResponseBase.of(
      this.transversalProjectService.findIncludedProjects(idTransversalProgram)
    ));
  }

  @PostMapping("/programs/{idTransversalProgram}/projects/{idProject}")
  public ResponseEntity<ResponseBase<TransversalProjectParticipationDto>> includeProject(
    @PathVariable final Long idTransversalProgram,
    @PathVariable final Long idProject,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(idTransversalProgram, authorization);
    return ResponseEntity.ok(ResponseBase.of(
      this.transversalProjectService.includeProject(idTransversalProgram, idProject)
    ));
  }

  @DeleteMapping("/programs/{idTransversalProgram}/projects/{idProject}")
  public ResponseEntity<ResponseBase<TransversalProjectParticipationDto>> removeProject(
    @PathVariable final Long idTransversalProgram,
    @PathVariable final Long idProject,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(idTransversalProgram, authorization);
    return ResponseEntity.ok(ResponseBase.of(
      this.transversalProjectService.removeProject(idTransversalProgram, idProject)
    ));
  }
}
