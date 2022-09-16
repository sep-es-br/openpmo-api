package br.gov.es.openpmo.controller.stakeholder;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.stakeholder.OrganizationStakeholderParamDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderOrganizationDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.service.stakeholder.StakeholderService;
import io.swagger.annotations.Api;
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

import javax.validation.Valid;

@Api
@RestController
@CrossOrigin
@RequestMapping("/stakeholders/organization")
public class StakeholderOrganizationController {

  private final StakeholderService stakeholderService;
  private final ICanAccessService canAccessService;

  @Autowired
  public StakeholderOrganizationController(
    final StakeholderService stakeholderService,
    final ICanAccessService canAccessService
  ) {
    this.stakeholderService = stakeholderService;
    this.canAccessService = canAccessService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<StakeholderOrganizationDto>> index(
    @RequestParam(name = "id-workpack") final Long idWorkpack,
    @RequestParam(name = "id-organization", required = false) final Long idOrganization,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(idWorkpack, authorization);
    final StakeholderOrganizationDto organizationDto = this.stakeholderService.findOrganization(
      idWorkpack,
      idOrganization
    );
    return ResponseEntity.ok(ResponseBase.of(organizationDto));
  }

  @PostMapping
  public ResponseEntity<ResponseBase<Entity>> storeOrganization(
    @RequestBody @Valid final OrganizationStakeholderParamDto request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(request.getIdWorkpack(), authorization);
    this.stakeholderService.storeStakeholderOrganization(request);
    return ResponseEntity.ok(ResponseBase.of());
  }

  @PutMapping
  public ResponseEntity<ResponseBase<Entity>> updateOrganization(
    @RequestBody @Valid final OrganizationStakeholderParamDto request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(request.getIdWorkpack(), authorization);
    this.stakeholderService.updateStakeholderOrganization(request);
    return ResponseEntity.ok(ResponseBase.of());
  }

  @DeleteMapping
  public ResponseEntity<Void> deleteOrganization(
    @RequestParam(name = "id-workpack") final Long idWorkpack,
    @RequestParam(name = "id-organization") final Long idOrganization,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(idWorkpack, authorization);
    this.stakeholderService.deleteOrganization(idWorkpack, idOrganization);
    return ResponseEntity.ok().build();
  }

}
