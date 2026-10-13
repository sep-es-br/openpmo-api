package br.gov.es.openpmo.controller.preprojects;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.preprojects.CreatePreProjectRequest;
import br.gov.es.openpmo.dto.preprojects.CreateProjectFromPreProjectRequest;
import br.gov.es.openpmo.dto.preprojects.PreProjectCriteriaTabValuesDto;
import br.gov.es.openpmo.dto.preprojects.PreProjectDto;
import br.gov.es.openpmo.dto.preprojects.PreProjectListDto;
import br.gov.es.openpmo.dto.preprojects.PreProjectEvaluationDto;
import br.gov.es.openpmo.dto.preprojects.SavePreProjectCriteriaTabValuesRequest;
import br.gov.es.openpmo.dto.preprojects.UpdatePreProjectRequest;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.service.preprojects.CreateProjectFromPreProjectService;
import br.gov.es.openpmo.service.preprojects.PreProjectService;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.completed.ICompleteWorkpackService;
import br.gov.es.openpmo.service.journals.JournalCreator;
import br.gov.es.openpmo.model.journals.JournalAction;
import br.gov.es.openpmo.model.workpacks.Workpack;
import io.swagger.annotations.Api;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Api
@RestController
@CrossOrigin
@RequestMapping("/pre-projects")
public class PreProjectController {

  private final PreProjectService preProjectService;

  private final CreateProjectFromPreProjectService createProjectFromPreProjectService;

  private final ICanAccessService canAccessService;

  private final ICompleteWorkpackService completeDeliverableService;

  private final TokenService tokenService;

  private final JournalCreator journalCreator;

  public PreProjectController(
    final PreProjectService preProjectService,
    final CreateProjectFromPreProjectService createProjectFromPreProjectService,
    final ICanAccessService canAccessService,
    final ICompleteWorkpackService completeDeliverableService,
    final TokenService tokenService,
    final JournalCreator journalCreator
  ) {
    this.preProjectService = preProjectService;
    this.createProjectFromPreProjectService = createProjectFromPreProjectService;
    this.canAccessService = canAccessService;
    this.completeDeliverableService = completeDeliverableService;
    this.tokenService = tokenService;
    this.journalCreator = journalCreator;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<PreProjectListDto>>> findAllByOfficeId(
    @RequestParam("id-office") final Long idOffice,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(idOffice, authorization);
    return ResponseEntity.ok(ResponseBase.of(this.preProjectService.findAllByOfficeId(idOffice)));
  }

  @PostMapping
  public ResponseEntity<ResponseBase<PreProjectDto>> create(
    @RequestBody @Valid final CreatePreProjectRequest request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(request.getIdOffice(), authorization);
    return ResponseEntity.ok(ResponseBase.of(this.preProjectService.create(request)));
  }

  @PostMapping("/{id}/projects")
  public ResponseEntity<ResponseBase<EntityDto>> createProject(
    @PathVariable final Long id,
    @RequestBody @Valid final CreateProjectFromPreProjectRequest request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(
      java.util.Arrays.asList(id, request.getIdPlan(), request.getIdParent()),
      authorization
    );
    final Workpack project = this.createProjectFromPreProjectService.create(id, request);
    this.completeDeliverableService.onWorkpackCreated(project);
    this.journalCreator.edition(
      project,
      JournalAction.CREATED,
      request.getObservations(),
      this.tokenService.getUserId(authorization)
    );
    return ResponseEntity.ok(ResponseBase.of(EntityDto.of(project)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseBase<PreProjectDto>> findById(
    @PathVariable final Long id,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(id, authorization);
    return ResponseEntity.ok(ResponseBase.of(this.preProjectService.findById(id)));
  }

  @GetMapping("/{id}/evaluation")
  public ResponseEntity<ResponseBase<PreProjectEvaluationDto>> findEvaluation(
    @PathVariable final Long id,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(id, authorization);
    return ResponseEntity.ok(ResponseBase.of(this.preProjectService.findEvaluation(id)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ResponseBase<PreProjectDto>> update(
    @PathVariable final Long id,
    @RequestBody @Valid final UpdatePreProjectRequest request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(id, authorization);
    return ResponseEntity.ok(ResponseBase.of(this.preProjectService.update(id, request)));
  }

  @GetMapping("/{id}/criteria-tabs/{idCriteriaTabModel}/values")
  public ResponseEntity<ResponseBase<PreProjectCriteriaTabValuesDto>> findCriteriaTabValues(
    @PathVariable final Long id,
    @PathVariable final Long idCriteriaTabModel,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(id, authorization);
    return ResponseEntity.ok(ResponseBase.of(
      this.preProjectService.findCriteriaTabValues(id, idCriteriaTabModel)
    ));
  }

  @PutMapping("/{id}/criteria-tabs/{idCriteriaTabModel}/values")
  public ResponseEntity<ResponseBase<PreProjectCriteriaTabValuesDto>> saveCriteriaTabValues(
    @PathVariable final Long id,
    @PathVariable final Long idCriteriaTabModel,
    @RequestBody @Valid final SavePreProjectCriteriaTabValuesRequest request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(id, authorization);
    return ResponseEntity.ok(ResponseBase.of(
      this.preProjectService.saveCriteriaTabValues(id, idCriteriaTabModel, request)
    ));
  }

}
