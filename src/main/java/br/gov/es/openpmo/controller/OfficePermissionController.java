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
import br.gov.es.openpmo.dto.officepermission.OfficePermissionDto;
import br.gov.es.openpmo.dto.officepermission.OfficePermissionParamDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.service.OfficePermissionService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import io.swagger.annotations.Api;

@Api
@RestController
@CrossOrigin
@RequestMapping(value = "/office-permissions")
public class OfficePermissionController {

    private OfficePermissionService service;
    private ModelMapper modelMapper;

    @Autowired
    public OfficePermissionController(OfficePermissionService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<ResponseBase<List<OfficePermissionDto>>> indexBase(
            @RequestParam(name = "id-office", required = true) Long idOffice,
            @RequestParam(name = "email", required = false) String email) {
        List<OfficePermissionDto> offices = service.findAllDto(idOffice, email).stream()
                .map(o -> modelMapper.map(o, OfficePermissionDto.class)).collect(Collectors.toList());
        if (offices.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        ResponseBase<List<OfficePermissionDto>> response = new ResponseBase<List<OfficePermissionDto>>()
            .setData(offices).setSuccess(true).setMessage(ApplicationMessage.OPERATION_SUCCESS);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ResponseBase<Entity>> store(@RequestBody OfficePermissionParamDto request) {
        service.store(request);
        ResponseBase<Entity> entity = new ResponseBase<Entity>().setSuccess(true).setMessage(ApplicationMessage.OPERATION_SUCCESS);
        return ResponseEntity.ok(entity);
    }

    @PutMapping
    public ResponseEntity<ResponseBase<Entity>> update(@RequestBody OfficePermissionParamDto request) {
        service.update(request);
        ResponseBase<Entity> entity = new ResponseBase<Entity>().setSuccess(true).setMessage(ApplicationMessage.OPERATION_SUCCESS);
        return ResponseEntity.ok(entity);
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam(name = "id-office") Long idOffice,
            @RequestParam(name = "email") String email) {
        service.delete(idOffice, email);
        return ResponseEntity.ok().build();
    }
}