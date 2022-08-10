package br.gov.es.openpmo.controller.permissions;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.officepermission.OfficePermissionDto;
import br.gov.es.openpmo.dto.officepermission.OfficePermissionParamDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.permissions.OfficePermissionService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
@CrossOrigin
@RequestMapping("/office-permissions")
public class OfficePermissionController {

  private final OfficePermissionService service;
  private final TokenService tokenService;

  @Autowired
  public OfficePermissionController(
    OfficePermissionService service,
    TokenService tokenService
  ) {
    this.service = service;
    this.tokenService = tokenService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<OfficePermissionDto>>> indexBase(
    @RequestParam(name = "id-office") final Long idOffice,
    @RequestParam(required = false) final Long idFilter,
    @RequestParam(name = "key", required = false) final String key,
    @RequestHeader(name = "Authorization") final String authorization
  ) {
    final Long idPerson = this.tokenService.getUserId(authorization);
    final List<OfficePermissionDto> offices = this.service.findAllDto(idOffice, idFilter, key, idPerson);
    if (offices.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    final ResponseBase<List<OfficePermissionDto>> response = new ResponseBase<List<OfficePermissionDto>>()
      .setData(offices).setSuccess(true).setMessage(ApplicationMessage.OPERATION_SUCCESS);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id-office}")
  public ResponseEntity<ResponseBase<OfficePermissionDto>> findPermissionsById(
    @PathVariable(name = "id-office") final Long idOffice,
    @RequestParam(name = "key", required = false) final String key,
    @RequestHeader(name = "Authorization") final String authorization
  ) {
    Long idPerson = this.tokenService.getUserId(authorization);
    OfficePermissionDto permissions = this.service.findOfficePermissionsByKey(idOffice, key, idPerson);
    return ResponseEntity.ok(ResponseBase.of(permissions));
  }

  @PostMapping
  public ResponseEntity<ResponseBase<Entity>> store(@Valid @RequestBody final OfficePermissionParamDto request) {
    this.service.store(request);
    final ResponseBase<Entity> response = new ResponseBase<Entity>()
      .setSuccess(true)
      .setMessage(ApplicationMessage.OPERATION_SUCCESS);
    return ResponseEntity.ok(response);
  }

  @PutMapping
  public ResponseEntity<ResponseBase<Entity>> update(@Valid @RequestBody final OfficePermissionParamDto request) {
    this.service.update(request);
    final ResponseBase<Entity> entity = new ResponseBase<Entity>().setSuccess(true).setMessage(ApplicationMessage.OPERATION_SUCCESS);
    return ResponseEntity.ok(entity);
  }

  @DeleteMapping
  public ResponseEntity<Void> delete(
    @RequestParam(name = "id-office") final Long idOffice,
    @RequestParam(name = "key") final String key
  ) {
    this.service.delete(idOffice, key);
    return ResponseEntity.ok().build();
  }
}
