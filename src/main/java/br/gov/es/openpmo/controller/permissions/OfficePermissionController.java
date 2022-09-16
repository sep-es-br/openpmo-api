package br.gov.es.openpmo.controller.permissions;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.officepermission.OfficePermissionDto;
import br.gov.es.openpmo.dto.officepermission.OfficePermissionParamDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.permissions.OfficePermissionService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
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
  private final ICanAccessService canAccessService;

  @Autowired
  public OfficePermissionController(
    final OfficePermissionService service,
    final TokenService tokenService,
    final ICanAccessService canAccessService
  ) {
    this.service = service;
    this.tokenService = tokenService;
    this.canAccessService = canAccessService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<OfficePermissionDto>>> indexBase(
    @RequestParam(name = "id-office") final Long idOffice,
    @RequestParam(required = false) final Long idFilter,
    @RequestParam(name = "key", required = false) final String key,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadManagementResource(idOffice, key, authorization);
    final Long idPerson = this.tokenService.getUserId(authorization);
    final List<OfficePermissionDto> offices = this.service.findAllDto(idOffice, idFilter, key, idPerson);
    if(offices.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(ResponseBase.of(offices));
  }

  @GetMapping("/{id-office}")
  public ResponseEntity<ResponseBase<OfficePermissionDto>> findPermissionsById(
    @PathVariable(name = "id-office") final Long idOffice,
    @RequestParam(name = "key", required = false) final String key,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadManagementResource(idOffice, key, authorization);
    final Long idPerson = this.tokenService.getUserId(authorization);
    final OfficePermissionDto permissions = this.service.findOfficePermissionsByKey(idOffice, key, idPerson);
    return ResponseEntity.ok(ResponseBase.of(permissions));
  }

  @PostMapping
  public ResponseEntity<ResponseBase<Entity>> store(
    @Valid @RequestBody final OfficePermissionParamDto request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanAccessManagementResource(request.getIdOffice(), authorization);
    this.service.store(request);
    return ResponseEntity.ok(ResponseBase.of());
  }

  @PutMapping
  public ResponseEntity<ResponseBase<Entity>> update(
    @Valid @RequestBody final OfficePermissionParamDto request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanAccessManagementResource(request.getIdOffice(), authorization);
    this.service.update(request);
    return ResponseEntity.ok(ResponseBase.of());
  }

  @DeleteMapping
  public ResponseEntity<Void> delete(
    @RequestParam(name = "id-office") final Long idOffice,
    @RequestParam(name = "key") final String key,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanAccessManagementResource(idOffice, authorization);
    this.service.delete(idOffice, key);
    return ResponseEntity.ok().build();
  }

}
