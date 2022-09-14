package br.gov.es.openpmo.controller.workpack;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.workpackshared.WorkpackSharedDto;
import br.gov.es.openpmo.dto.workpackshared.WorkpackSharedParamDto;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.service.workpack.WorkpackSharedService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
@CrossOrigin
@RequestMapping("/workpack/{idWorkpack}/shared")
public class WorkpackSharedController {

  private final WorkpackSharedService service;
  private final ICanAccessService canAccessService;

  @Autowired
  public WorkpackSharedController(
    final WorkpackSharedService service,
    final ICanAccessService canAccessService
  ) {
    this.service = service;
    this.canAccessService = canAccessService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<WorkpackSharedDto>>> getAll(
    @PathVariable("idWorkpack") final Long idWorkpack,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(idWorkpack, authorization);
    final List<WorkpackSharedDto> response = this.service.getAll(idWorkpack);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseBase<WorkpackSharedDto>> getById(
    @PathVariable("idWorkpack") final Long ignored,
    @PathVariable("id") final Long id,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(id, authorization);
    final WorkpackSharedDto dto = this.service.getById(id);
    return ResponseEntity.ok(ResponseBase.of(dto));
  }

  @PostMapping
  public ResponseEntity<ResponseBase<List<WorkpackSharedDto>>> store(
    @PathVariable("idWorkpack") final Long idWorkpack,
    @Valid @RequestBody final List<WorkpackSharedParamDto> request,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(idWorkpack, authorization);
    this.service.store(idWorkpack, request);
    final List<WorkpackSharedDto> response = this.service.getAll(idWorkpack);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @DeleteMapping
  public ResponseEntity<Void> delete(
    @PathVariable("idWorkpack") final Long idWorkpack,
    @RequestParam(value = "id-shared-with", required = false) final Long idSharedWith,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(idWorkpack, authorization);
    this.service.revokeShare(idSharedWith, idWorkpack);
    return ResponseEntity.ok().build();
  }

}
