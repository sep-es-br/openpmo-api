package br.gov.es.openpmo.controller.workpack;

import br.gov.es.openpmo.dto.permission.WorkpackPermissionResponse;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.workpack.GetWorkpackPermissions;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@CrossOrigin
@RequestMapping("/workpack")
public class WorkpackPermissionController {

  private final GetWorkpackPermissions getWorkpackPermissions;
  private final TokenService tokenService;

  @Autowired
  public WorkpackPermissionController(
    final GetWorkpackPermissions getWorkpackPermissions,
    final TokenService tokenService
  ) {
    this.getWorkpackPermissions = getWorkpackPermissions;
    this.tokenService = tokenService;
  }

  @GetMapping("/{id-workpack}/permissions")
  public ResponseEntity<WorkpackPermissionResponse> getPermissions(
    @RequestHeader(name = "Authorization") final String authorization,
    @PathVariable(value = "id-workpack") final Long idWorkpack,
    @RequestParam(value = "id-plan", required = false) final Long idPlan
  ) {
    final Long idUser = this.tokenService.getUserId(authorization);

    final WorkpackPermissionResponse response = this.getWorkpackPermissions.execute(idUser, idWorkpack, idPlan);

    return ResponseEntity.ok(response);
  }

}
