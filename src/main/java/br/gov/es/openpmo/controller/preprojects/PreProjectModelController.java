package br.gov.es.openpmo.controller.preprojects;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.preprojects.PreProjectModelDto;
import br.gov.es.openpmo.dto.preprojects.UpdatePreProjectModelRequest;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.CriteriaTabModelDto;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.service.preprojects.PreProjectModelService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@Api
@RestController
@CrossOrigin
@RequestMapping("/pre-project-models")
public class PreProjectModelController {

  private final PreProjectModelService preProjectModelService;

  private final ICanAccessService canAccessService;

  @Autowired
  public PreProjectModelController(
    final PreProjectModelService preProjectModelService,
    final ICanAccessService canAccessService
  ) {
    this.preProjectModelService = preProjectModelService;
    this.canAccessService = canAccessService;
  }

  @PutMapping("/office/{id-office}")
  public ResponseEntity<ResponseBase<PreProjectModelDto>> findOrCreateByOfficeId(
    @PathVariable("id-office") final Long idOffice,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(idOffice, authorization);
    final PreProjectModelDto preProjectModel = this.preProjectModelService.findOrCreateByOfficeId(idOffice);
    return ResponseEntity.ok(ResponseBase.of(preProjectModel));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ResponseBase<PreProjectModelDto>> update(
    @PathVariable final Long id,
    @RequestBody @Valid final UpdatePreProjectModelRequest request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(id, authorization);
    final PreProjectModelDto preProjectModel = this.preProjectModelService.update(id, request);
    return ResponseEntity.ok(ResponseBase.of(preProjectModel));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseBase<PreProjectModelDto>> findById(
    @PathVariable final Long id,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(id, authorization);
    return ResponseEntity.ok(ResponseBase.of(this.preProjectModelService.findById(id)));
  }

  @PostMapping("/{id}/criteria-tabs")
  public ResponseEntity<ResponseBase<CriteriaTabModelDto>> createCriteriaTab(
    @PathVariable final Long id,
    @RequestBody @Valid final CriteriaTabModelDto request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(id, authorization);
    final CriteriaTabModelDto criteriaTabModel = this.preProjectModelService.createCriteriaTab(id, request);
    return ResponseEntity.ok(ResponseBase.of(criteriaTabModel));
  }

  @GetMapping("/criteria-tabs/{id}")
  public ResponseEntity<ResponseBase<CriteriaTabModelDto>> findCriteriaTabById(
    @PathVariable final Long id,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(id, authorization);
    final CriteriaTabModelDto criteriaTabModel = this.preProjectModelService.findCriteriaTabById(id);
    return ResponseEntity.ok(ResponseBase.of(criteriaTabModel));
  }

  @PutMapping("/criteria-tabs/{id}")
  public ResponseEntity<ResponseBase<CriteriaTabModelDto>> updateCriteriaTab(
    @PathVariable final Long id,
    @RequestBody @Valid final CriteriaTabModelDto request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(id, authorization);
    final CriteriaTabModelDto criteriaTabModel = this.preProjectModelService.updateCriteriaTab(id, request);
    return ResponseEntity.ok(ResponseBase.of(criteriaTabModel));
  }

  @DeleteMapping("/criteria-tabs/{id}")
  public ResponseEntity<ResponseBase<Void>> deleteCriteriaTab(
    @PathVariable final Long id,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(id, authorization);
    this.preProjectModelService.deleteCriteriaTab(id);
    return ResponseEntity.ok(ResponseBase.success());
  }

}
