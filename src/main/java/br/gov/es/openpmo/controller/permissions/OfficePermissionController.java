package br.gov.es.openpmo.controller.permissions;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.officepermission.OfficePermissionDto;
import br.gov.es.openpmo.dto.officepermission.OfficePermissionParamDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.service.permissions.OfficePermissionService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import io.swagger.annotations.Api;
import org.modelmapper.ModelMapper;
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
  private final ModelMapper modelMapper;

  @Autowired
  public OfficePermissionController(final OfficePermissionService service, final ModelMapper modelMapper) {
    this.service = service;
    this.modelMapper = modelMapper;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<OfficePermissionDto>>> indexBase(
    @RequestParam(name = "id-office") final Long idOffice,
    @RequestParam(required = false) final Long idFilter,
    @RequestParam(name = "email", required = false) final String email
  ) {
    final List<OfficePermissionDto> offices = this.service.findAllDto(idOffice, idFilter, email);
    if(offices.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    final ResponseBase<List<OfficePermissionDto>> response = new ResponseBase<List<OfficePermissionDto>>()
      .setData(offices).setSuccess(true).setMessage(ApplicationMessage.OPERATION_SUCCESS);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id-office}")
  public ResponseEntity<ResponseBase<OfficePermissionDto>> findPermissionsById(
    @PathVariable(name = "id-office") final Long idOffice,
    @RequestParam(name = "email", required = false) final String email
  ) {
    final OfficePermissionDto permissions = this.service.findOfficePermissionsByEmail(idOffice, email);
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
    @RequestParam(name = "email") final String email
  ) {
    this.service.delete(idOffice, email);
    return ResponseEntity.ok().build();
  }
}
