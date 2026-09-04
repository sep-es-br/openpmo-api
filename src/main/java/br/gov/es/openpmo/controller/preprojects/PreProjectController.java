package br.gov.es.openpmo.controller.preprojects;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.preprojects.CreatePreProjectRequest;
import br.gov.es.openpmo.dto.preprojects.PreProjectCriteriaTabValuesDto;
import br.gov.es.openpmo.dto.preprojects.PreProjectDto;
import br.gov.es.openpmo.dto.preprojects.SavePreProjectCriteriaTabValuesRequest;
import br.gov.es.openpmo.dto.preprojects.UpdatePreProjectRequest;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.service.preprojects.PreProjectService;
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
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@CrossOrigin
@RequestMapping("/pre-projects")
public class PreProjectController {

  private final PreProjectService preProjectService;

  private final ICanAccessService canAccessService;

  public PreProjectController(
    final PreProjectService preProjectService,
    final ICanAccessService canAccessService
  ) {
    this.preProjectService = preProjectService;
    this.canAccessService = canAccessService;
  }

  @PostMapping
  public ResponseEntity<ResponseBase<PreProjectDto>> create(
    @RequestBody @Valid final CreatePreProjectRequest request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(request.getIdOffice(), authorization);
    return ResponseEntity.ok(ResponseBase.of(this.preProjectService.create(request)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseBase<PreProjectDto>> findById(
    @PathVariable final Long id,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(id, authorization);
    return ResponseEntity.ok(ResponseBase.of(this.preProjectService.findById(id)));
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
