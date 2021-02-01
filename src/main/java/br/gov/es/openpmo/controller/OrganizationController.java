package br.gov.es.openpmo.controller;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.organization.OrganizationDto;
import br.gov.es.openpmo.dto.organization.OrganizationStoreDto;
import br.gov.es.openpmo.dto.organization.OrganizationUpdateDto;
import br.gov.es.openpmo.model.Organization;
import br.gov.es.openpmo.service.OrganizationService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import io.swagger.annotations.Api;

@Api
@RestController
@CrossOrigin
@RequestMapping(value = "/organizations")
public class OrganizationController {

    private static final String OPERATION_SUCCESS = ApplicationMessage.OPERATION_SUCCESS;

    private final OrganizationService organizationService;
    private final ModelMapper modelMapper;

    @Autowired
    public OrganizationController(OrganizationService organizationService, ModelMapper modelMapper) {
        this.organizationService = organizationService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<ResponseBase<List<OrganizationDto>>> indexBase(@RequestParam("id-office") Long idOffice) {
        List<OrganizationDto> organizations = new ArrayList<>();
        organizationService.findAll(idOffice).forEach(registro -> organizations.add(new OrganizationDto(registro)));
        ResponseBase<List<OrganizationDto>> response = new ResponseBase<List<OrganizationDto>>().setData(organizations)
                .setMessage(OPERATION_SUCCESS).setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<ResponseBase<OrganizationDto>> findById(@PathVariable Long id) {
        OrganizationDto officeDto = new OrganizationDto(organizationService.findById(id));
        ResponseBase<OrganizationDto> response = new ResponseBase<OrganizationDto>().setData(officeDto).setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ResponseBase<EntityDto>> save(@Valid @RequestBody OrganizationStoreDto organizationStoreDto) {
        Organization organization = organizationService.save(modelMapper.map(organizationStoreDto, Organization.class), organizationStoreDto.getIdOffice());
        ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setMessage(OPERATION_SUCCESS).setData(new EntityDto(organization.getId()))
                .setSuccess(true);
        return ResponseEntity.ok(entity);
    }

    @PutMapping
    public ResponseEntity<ResponseBase<EntityDto>> update(@Valid @RequestBody OrganizationUpdateDto organizationUpdateDto) {
        Organization organization = organizationService.save(organizationService.getOrganization(organizationUpdateDto));
        ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setMessage(OPERATION_SUCCESS).setData(new EntityDto(organization.getId()))
                .setSuccess(true);
        return ResponseEntity.ok(entity);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Organization organization = organizationService.findById(id);
        organizationService.delete(organization);
        return ResponseEntity.ok().build();
    }
}
