package br.gov.es.openpmo.controller.permissions;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.planpermission.PlanPermissionDto;
import br.gov.es.openpmo.dto.planpermission.PlanPermissionParamDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.permissions.PlanPermissionService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import io.swagger.annotations.Api;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Api
@RestController
@CrossOrigin
@RequestMapping("/plan-permissions")
public class PlanPermissionController {

  private final TokenService tokenService;
  private final PlanPermissionService service;
  private final ModelMapper modelMapper;
  private final ICanAccessService canAccessService;

  @Autowired
  public PlanPermissionController(
    final TokenService tokenService,
    final PlanPermissionService service,
    final ModelMapper modelMapper,
    final ICanAccessService canAccessService
  ) {
    this.tokenService = tokenService;
    this.service = service;
    this.modelMapper = modelMapper;
    this.canAccessService = canAccessService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<PlanPermissionDto>>> indexBase(
    @RequestParam(name = "id-plan") final Long idPlan,
    @RequestParam(name = "key", required = false) final String key,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadManagementResource(idPlan, key, authorization);
    final Long idPerson = this.tokenService.getUserId(authorization);
    final List<PlanPermissionDto> plans = this.service.findAllDto(idPlan, key, idPerson).stream()
      .map(o -> this.modelMapper.map(o, PlanPermissionDto.class))
      .collect(Collectors.toList());
    return plans.isEmpty() ?
      ResponseEntity.noContent().build() :
      ResponseEntity.ok(ResponseBase.of(plans));
  }

  @PostMapping
  public ResponseEntity<ResponseBase<Entity>> store(
    @RequestBody final PlanPermissionParamDto request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanAccessManagementResource(request.getIdPlan(), authorization);
    this.service.store(request);
    return ResponseEntity.ok(ResponseBase.of());
  }

  @PutMapping
  public ResponseEntity<ResponseBase<Entity>> update(
    @RequestBody final PlanPermissionParamDto request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanAccessManagementResource(request.getIdPlan(), authorization);
    this.service.update(request);
    return ResponseEntity.ok(ResponseBase.of());
  }

  @DeleteMapping
  public ResponseEntity<Void> delete(
    @RequestParam(name = "id-plan") final Long idPlan,
    @RequestParam(name = "key") final String key,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanAccessManagementResource(idPlan, authorization);
    this.service.delete(idPlan, key);
    return ResponseEntity.ok().build();
  }

}
