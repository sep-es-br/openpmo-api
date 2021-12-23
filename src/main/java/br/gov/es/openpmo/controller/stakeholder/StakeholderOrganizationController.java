package br.gov.es.openpmo.controller.stakeholder;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.stakeholder.OrganizationStakeholderParamDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderOrganizationDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.service.stakeholder.StakeholderService;
import br.gov.es.openpmo.utils.ApplicationMessage;
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

  @Autowired
  public StakeholderOrganizationController(final StakeholderService stakeholderService) {
    this.stakeholderService = stakeholderService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<StakeholderOrganizationDto>> index(
    @RequestParam(name = "id-workpack") final Long idWorkpack,
    @RequestParam(name = "id-organization", required = false) final Long idOrganization
  ) {
    final StakeholderOrganizationDto organizationDto = this.stakeholderService.findOrganization(
      idWorkpack,
      idOrganization
    );

    final ResponseBase<StakeholderOrganizationDto> entity = new ResponseBase<StakeholderOrganizationDto>().setData(
        organizationDto)
      .setSuccess(true).setMessage(ApplicationMessage.OPERATION_SUCCESS);
    return ResponseEntity.status(200).body(entity);
  }

  @PostMapping
  public ResponseEntity<ResponseBase<Entity>> storeOrganization(
    @RequestBody @Valid final OrganizationStakeholderParamDto request
  ) {
    this.stakeholderService.storeStakeholderOrganization(request);
    final ResponseBase<Entity> entity = new ResponseBase<Entity>().setSuccess(true).setMessage(ApplicationMessage.OPERATION_SUCCESS);
    return ResponseEntity.ok(entity);
  }

  @PutMapping
  public ResponseEntity<ResponseBase<Entity>> updateOrganization(
    @RequestBody @Valid final OrganizationStakeholderParamDto request
  ) {
    this.stakeholderService.updateStakeholderOrganization(request);
    final ResponseBase<Entity> entity = new ResponseBase<Entity>().setSuccess(true).setMessage(ApplicationMessage.OPERATION_SUCCESS);
    return ResponseEntity.ok(entity);
  }

  @DeleteMapping
  public ResponseEntity<Void> deleteOrganization(
    @RequestParam(name = "id-workpack", required = true) final Long idWorkpack,
    @RequestParam(name = "id-organization", required = true) final Long idOrganization
  ) {
    this.stakeholderService.deleteOrganization(idWorkpack, idOrganization);
    return ResponseEntity.ok().build();
  }

}
