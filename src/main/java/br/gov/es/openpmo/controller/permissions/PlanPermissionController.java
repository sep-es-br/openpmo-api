package br.gov.es.openpmo.controller.permissions;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.planpermission.PlanPermissionDto;
import br.gov.es.openpmo.dto.planpermission.PlanPermissionParamDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.service.permissions.PlanPermissionService;
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

  private final PlanPermissionService service;
  private final ModelMapper modelMapper;

  @Autowired
  public PlanPermissionController(final PlanPermissionService service, final ModelMapper modelMapper) {
    this.service = service;
    this.modelMapper = modelMapper;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<PlanPermissionDto>>> indexBase(
    @RequestParam(name = "id-plan") final Long idPlan,
    @RequestParam(name = "email", required = false) final String email
  ) {
    final List<PlanPermissionDto> plans = this.service.findAllDto(idPlan, email).stream()
      .map(o -> this.modelMapper.map(o, PlanPermissionDto.class))
      .collect(Collectors.toList());
    if(plans.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    final ResponseBase<List<PlanPermissionDto>> response = new ResponseBase<List<PlanPermissionDto>>()
      .setData(plans).setSuccess(true);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<ResponseBase<Entity>> store(@RequestBody final PlanPermissionParamDto request) {
    this.service.store(request);
    return ResponseEntity.ok(ResponseBase.of());
  }

  @PutMapping
  public ResponseEntity<ResponseBase<Entity>> update(@RequestBody final PlanPermissionParamDto request) {
    this.service.update(request);
    return ResponseEntity.ok(ResponseBase.of());
  }

  @DeleteMapping
  public ResponseEntity<Void> delete(
    @RequestParam(name = "id-plan") final Long idPlan,
    @RequestParam(name = "email") final String email
  ) {
    this.service.delete(idPlan, email);
    return ResponseEntity.ok().build();
  }
}
