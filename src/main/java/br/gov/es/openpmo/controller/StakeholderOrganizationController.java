package br.gov.es.openpmo.controller;

import javax.validation.Valid;

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
import br.gov.es.openpmo.dto.stakeholder.OrganizationStakeholderParamDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderOrganizationDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.service.StakeholderService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import io.swagger.annotations.Api;

@Api
@RestController
@CrossOrigin
@RequestMapping(value = "/stakeholders/organization")
public class StakeholderOrganizationController {

	@Autowired
	private StakeholderService stakeholderService;

	@GetMapping
	public ResponseEntity<ResponseBase<StakeholderOrganizationDto>> index(
			@RequestParam(name = "id-workpack") Long idWorkpack,
			@RequestParam(name = "id-organization", required = false) Long idOrganization) {
		StakeholderOrganizationDto organizationDto = stakeholderService.findOrganization(idWorkpack, idOrganization);

		ResponseBase<StakeholderOrganizationDto> entity = new ResponseBase<StakeholderOrganizationDto>().setData(organizationDto)
                                                                                            .setSuccess(true).setMessage(ApplicationMessage.OPERATION_SUCCESS);
		return ResponseEntity.status(200).body(entity);
	}

	@PostMapping
	public ResponseEntity<ResponseBase<Entity>> storeOrganization(
			@RequestBody @Valid OrganizationStakeholderParamDto request) {
		stakeholderService.storeStakeholderOrganization(request);
		ResponseBase<Entity> entity = new ResponseBase<Entity>().setSuccess(true).setMessage(ApplicationMessage.OPERATION_SUCCESS);
		return ResponseEntity.ok(entity);
	}

	@PutMapping
	public ResponseEntity<ResponseBase<Entity>> updateOrganization(
			@RequestBody @Valid OrganizationStakeholderParamDto request) {
		stakeholderService.updateStakeholderOrganization(request);
		ResponseBase<Entity> entity = new ResponseBase<Entity>().setSuccess(true).setMessage(ApplicationMessage.OPERATION_SUCCESS);
		return ResponseEntity.ok(entity);
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteOrganization(@RequestParam(name = "id-workpack", required = true) Long idWorkpack,
			@RequestParam(name = "id-organization", required = true) Long idOrganization) {
		stakeholderService.deleteOrganization(idWorkpack, idOrganization);
		return ResponseEntity.ok().build();
	}

}
