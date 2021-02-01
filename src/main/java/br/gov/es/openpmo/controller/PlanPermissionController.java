package br.gov.es.openpmo.controller;

import java.util.List;
import java.util.stream.Collectors;

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

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.planpermission.PlanPermissionDto;
import br.gov.es.openpmo.dto.planpermission.PlanPermissionParamDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.service.PlanPermissionService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import io.swagger.annotations.Api;

@Api
@RestController
@CrossOrigin
@RequestMapping(value = "/plan-permissions")
public class PlanPermissionController {

    private PlanPermissionService service;
    private ModelMapper modelMapper;

    @Autowired
    public PlanPermissionController(PlanPermissionService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<ResponseBase<List<PlanPermissionDto>>> indexBase(
            @RequestParam(name = "id-plan") Long idPlan,
            @RequestParam(name = "email", required = false) String email) {
        List<PlanPermissionDto> plans = service.findAllDto(idPlan, email).stream()
                .map(o -> modelMapper.map(o, PlanPermissionDto.class)).collect(Collectors.toList());
        if (plans.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        ResponseBase<List<PlanPermissionDto>> response = new ResponseBase<List<PlanPermissionDto>>()
                .setData(plans).setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ResponseBase<Entity>> store(@RequestBody PlanPermissionParamDto request) {
        service.store(request);
        ResponseBase<Entity> entity = new ResponseBase<Entity>().setSuccess(true).setMessage(ApplicationMessage.OPERATION_SUCCESS);
        return ResponseEntity.ok(entity);
    }

    @PutMapping
    public ResponseEntity<ResponseBase<Entity>> update(@RequestBody PlanPermissionParamDto request) {
        service.update(request);
        ResponseBase<Entity> entity = new ResponseBase<Entity>().setSuccess(true).setMessage(ApplicationMessage.OPERATION_SUCCESS);
        return ResponseEntity.ok(entity);
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam(name = "id-plan") Long idPlan,
            @RequestParam(name = "email") String email) {
        service.delete(idPlan, email);
        return ResponseEntity.ok().build();
    }
}