package br.gov.es.openpmo.controller.actor;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.organization.OrganizationDto;
import br.gov.es.openpmo.dto.organization.OrganizationStoreDto;
import br.gov.es.openpmo.dto.organization.OrganizationUpdateDto;
import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.service.actors.OrganizationService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import io.swagger.annotations.Api;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Api
@RestController
@CrossOrigin
@RequestMapping("/organizations")
public class OrganizationController {

  private static final String OPERATION_SUCCESS = ApplicationMessage.OPERATION_SUCCESS;

  private final OrganizationService organizationService;
  private final ModelMapper modelMapper;
  private final ICanAccessService canAccessService;

  @Autowired
  public OrganizationController(
      final OrganizationService organizationService,
      final ModelMapper modelMapper,
      final ICanAccessService canAccessService) {
    this.organizationService = organizationService;
    this.modelMapper = modelMapper;
    this.canAccessService = canAccessService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<OrganizationDto>>> indexBase(
      @RequestParam("id-office") final Long idOffice,
      @RequestParam(required = false) final Long idFilter,
      @Authorization final String authorization) {

    this.canAccessService.ensureCanReadResource(idOffice, authorization);

    final List<OrganizationDto> organizations = new ArrayList<>();
    this.organizationService.findAll(idOffice, idFilter)
        .forEach(registro -> organizations.add(
            new OrganizationDto(registro)));
    final ResponseBase<List<OrganizationDto>> response = new ResponseBase<List<OrganizationDto>>()
        .setData(organizations)
        .setMessage(OPERATION_SUCCESS).setSuccess(true);
    return ResponseEntity.ok(response);
  }

  @GetMapping("{id}")
  public ResponseEntity<ResponseBase<OrganizationDto>> findById(@PathVariable final Long id,
  @Authorization final String authorization) {

    this.canAccessService.ensureCanReadResource(id, authorization);

    final OrganizationDto officeDto = new OrganizationDto(this.organizationService.findById(id));
    final ResponseBase<OrganizationDto> response = new ResponseBase<OrganizationDto>().setData(officeDto)
        .setSuccess(true);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> save(
      @Valid @RequestBody final OrganizationStoreDto organizationStoreDto,
      @Authorization final String authorization) {


    this.canAccessService.ensureCanEditResource(organizationStoreDto.getIdOffice(), authorization);

    final Organization organization = this.organizationService.save(
        this.modelMapper.map(organizationStoreDto, Organization.class),
        organizationStoreDto.getIdOffice());
    final ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setMessage(OPERATION_SUCCESS)
        .setData(new EntityDto(
            organization.getId()))
        .setSuccess(true);
    return ResponseEntity.ok(entity);
  }

  @PutMapping
  public ResponseEntity<ResponseBase<EntityDto>> update(
      @Valid @RequestBody final OrganizationUpdateDto organizationUpdateDto,
      @Authorization final String authorization) {

    this.canAccessService.ensureCanEditResource(organizationUpdateDto.getId(), authorization);

    final Organization organization = this.organizationService
        .save(this.organizationService.getOrganization(organizationUpdateDto));
    final ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setMessage(OPERATION_SUCCESS)
        .setData(new EntityDto(
            organization.getId()))
        .setSuccess(true);
    return ResponseEntity.ok(entity);
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(@PathVariable final Long id,
      @Authorization final String authorization) {

    this.canAccessService.ensureCanEditResource(id, authorization);

    final Organization organization = this.organizationService.findById(id);
    this.organizationService.delete(organization);
    return ResponseEntity.ok().build();
  }

}
