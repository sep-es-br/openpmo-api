package br.gov.es.openpmo.controller.actor;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.administrator.AdministratorDto;
import br.gov.es.openpmo.dto.administrator.AdministratorStatusRequest;
import br.gov.es.openpmo.service.actors.AdministratorService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/persons/administrator")
public class AdministratorController {

  private final AdministratorService administratorService;
  private final ICanAccessService canAccessService;


  @Autowired
  public AdministratorController(
    final AdministratorService administratorService,
    final ICanAccessService canAccessService
  ) {
    this.administratorService = administratorService;
    this.canAccessService = canAccessService;
  }

  @PatchMapping("/{personId}")
  public ResponseEntity<ResponseBase<AdministratorDto>> updateAdministratorStatus(
    @Valid @RequestBody final AdministratorStatusRequest request,
    @PathVariable final Long personId,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureIsAdministrator(authorization);
    final AdministratorDto dto = this.administratorService.updateAdministratorStatus(request, personId);
    return ResponseEntity.ok(ResponseBase.of(dto));
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<AdministratorDto>>> findAllAdministrators(
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureIsAdministrator(authorization);
    final List<AdministratorDto> response = this.administratorService.findAllAdministrators();
    return ResponseEntity.ok(ResponseBase.of(response));
  }

}
