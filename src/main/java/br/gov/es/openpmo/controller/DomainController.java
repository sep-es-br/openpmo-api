package br.gov.es.openpmo.controller;

import java.util.List;
import java.util.stream.Collectors;
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
import br.gov.es.openpmo.dto.domain.DomainDto;
import br.gov.es.openpmo.dto.domain.DomainStoreDto;
import br.gov.es.openpmo.dto.domain.DomainUpdateDto;
import br.gov.es.openpmo.model.Domain;
import br.gov.es.openpmo.service.DomainService;
import br.gov.es.openpmo.service.OfficeService;
import io.swagger.annotations.Api;

@Api
@RestController
@CrossOrigin
@RequestMapping(value = "/domains")
public class DomainController {

	private final DomainService domainService;
	private final ModelMapper modelMapper;
	private final OfficeService officeService;

	@Autowired
	public DomainController(DomainService domainService, ModelMapper modelMapper,
							OfficeService officeService) {
		this.domainService = domainService;
		this.modelMapper = modelMapper;
		this.officeService = officeService;
	}

	@GetMapping
	public ResponseEntity<ResponseBase<List<DomainDto>>> indexBase(@RequestParam(value = "id-office", required = false) Long idOffice) {
		List<DomainDto> domains = domainService.findAll(idOffice).stream().map(o -> modelMapper.map(o, DomainDto.class)).collect(
			Collectors.toList());
		if (domains.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		ResponseBase<List<DomainDto>> response = new ResponseBase<List<DomainDto>>().setData(domains).setSuccess(true);
		return ResponseEntity.status(200).body(response);
	}

	@GetMapping("{id}")
	public ResponseEntity<ResponseBase<DomainDto>> findById(@PathVariable Long id) {
		DomainDto domainDto = modelMapper.map(domainService.findById(id), DomainDto.class);
		ResponseBase<DomainDto> response = new ResponseBase<DomainDto>().setData(domainDto).setSuccess(true);
		return ResponseEntity.status(200).body(response);
	}

	@PostMapping
	public ResponseEntity<ResponseBase<EntityDto>> save(@Valid @RequestBody DomainStoreDto domainParamDto) {
		Domain domain = getDomain(domainParamDto);
		domain.setOffice(officeService.findById(domainParamDto.getIdOffice()));
		domain =  domainService.save(domain);
		ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(new EntityDto(domain.getId())).setSuccess(true);
		return ResponseEntity.status(200).body(entity);
	}

	@PutMapping
	public ResponseEntity<ResponseBase<EntityDto>> update(@RequestBody @Valid DomainUpdateDto domainUpdateDto) {
		Domain domain =  domainService.update(getDomain(domainUpdateDto));
		ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(new EntityDto(domain.getId())).setSuccess(true);
		return ResponseEntity.status(200).body(entity);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		Domain domain = domainService.findById(id);
		domainService.delete(domain);
		return ResponseEntity.ok().build();
	}

	private Domain getDomain(Object object) {
		return modelMapper.map(object, Domain.class);
	}

}
