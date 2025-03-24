package br.gov.es.openpmo.controller.process;

import br.gov.es.openpmo.apis.organograma.OrganogramaApi;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.process.ProcessCardDto;
import br.gov.es.openpmo.dto.process.ProcessCreateDto;
import br.gov.es.openpmo.dto.process.ProcessDetailDto;
import br.gov.es.openpmo.dto.process.ProcessFromEDocsDto;
import br.gov.es.openpmo.dto.process.ProcessUpdateDto;
import br.gov.es.openpmo.model.process.Process;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.service.process.ProcessService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
@RequestMapping("/processes")
public class ProcessController {

  private final ProcessService service;

  private final TokenService tokenService;

  private final ICanAccessService canAccessService;

  @Autowired
  public ProcessController(
      final ProcessService service,
      final TokenService tokenService,
      final ICanAccessService canAccessService) {
    this.service = service;
    this.tokenService = tokenService;
    this.canAccessService = canAccessService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<ProcessCardDto>>> findAll(
      @RequestParam("id-workpack") final Long idWorkpack,
      @RequestParam(value = "idFilter", required = false) final Long idFilter,
      @RequestParam(required = false) final String term,
      @RequestHeader("Authorization") final String authorization) {

    this.canAccessService.ensureCanReadResourceWorkpack(idWorkpack, authorization);
    final Long idPerson = this.tokenService.getUserId(authorization);
    final List<ProcessCardDto> processes = this.service.findAllAsCardDto(idWorkpack, idFilter, idPerson, term);
    final ResponseBase<List<ProcessCardDto>> response = ResponseBase.of(processes);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/edocs")
  public ResponseEntity<ResponseBase<ProcessFromEDocsDto>> findProcessByProtocol(
      @RequestParam(name = "process-number") final String protocol,
      @RequestHeader(name = "Authorization") final String authorization) {

    // this.canAccessService.ensureIsAdministrator(authorization);
    OrganogramaApi.clearCache();
    final Long idPerson = this.tokenService.getUserId(authorization);
    final ProcessFromEDocsDto processByProtocol = this.service.findProcessByProtocol(protocol, idPerson);
    final ResponseBase<ProcessFromEDocsDto> response = ResponseBase.of(processByProtocol);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseBase<ProcessDetailDto>> findById(
      @PathVariable final Long id,
      @RequestHeader(name = "Authorization") final String authorization) {

    // this.canAccessService.ensureCanReadResource(id, authorization);
    final Long idPerson = this.tokenService.getUserId(authorization);
    final ProcessDetailDto process = this.service.findById(id, idPerson);
    final ResponseBase<ProcessDetailDto> response = ResponseBase.of(process);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> create(@Valid @RequestBody final ProcessCreateDto request,
      @RequestHeader(name = "Authorization") final String authorization) {

    this.canAccessService.ensureCanEditResource(request.getIdWorkpack(), authorization);
    final Process process = this.service.create(request);
    final ResponseBase<EntityDto> response = ResponseBase.of(new EntityDto(process.getId()));
    return ResponseEntity.ok(response);
  }

  @PutMapping
  public ResponseEntity<ResponseBase<ProcessDetailDto>> update(
      @Valid @RequestBody final ProcessUpdateDto request,
      @RequestHeader(name = "Authorization") final String authorization) {

    // this.canAccessService.ensureCanEditResource(request.getId(), authorization);
    final Long idPerson = this.tokenService.getUserId(authorization);
    final ProcessDetailDto process = this.service.update(request, idPerson);
    final ResponseBase<ProcessDetailDto> response = ResponseBase.of(process);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteById(@PathVariable final Long id,
      @RequestHeader(name = "Authorization") final String authorization) {

    this.canAccessService.ensureCanEditResource(id, authorization);
    this.service.deleteById(id);
    return ResponseEntity.ok().build();
  }

}
