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
import br.gov.es.openpmo.dto.domain.LocalityDetailDto;
import br.gov.es.openpmo.dto.domain.LocalityDto;
import br.gov.es.openpmo.dto.domain.LocalityPropertyDto;
import br.gov.es.openpmo.dto.domain.LocalityStoreDto;
import br.gov.es.openpmo.dto.domain.LocalityUpdateDto;
import br.gov.es.openpmo.model.Locality;
import br.gov.es.openpmo.service.LocalityService;
import io.swagger.annotations.Api;

@Api
@RestController
@CrossOrigin
@RequestMapping(value = "/localities")
public class LocalityController {

	private final LocalityService localityService;
	private final ModelMapper modelMapper;

	@Autowired
	public LocalityController(LocalityService localityService, ModelMapper modelMapper) {
		this.localityService = localityService;
		this.modelMapper = modelMapper;
	}

	@GetMapping
	public ResponseEntity<ResponseBase<List<LocalityDto>>> indexBase(@RequestParam("id-domain") Long idDomain) {
		List<LocalityDto> localitys = localityService.findAll(idDomain).stream()
				.map(o -> modelMapper.map(o, LocalityDto.class)).collect(Collectors.toList());
		if (localitys.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		ResponseBase<List<LocalityDto>> response = new ResponseBase<List<LocalityDto>>().setData(localitys)
				.setSuccess(true);
		return ResponseEntity.status(200).body(response);
	}

	@GetMapping("/firstLevel")
	public ResponseEntity<ResponseBase<List<LocalityDto>>> indexBaseFirstLevel(@RequestParam("id-domain") Long idDomain) {
		List<LocalityDto> localitys = localityService.findAllFirstLevel(idDomain).stream()
													 .map(o -> modelMapper.map(o, LocalityDto.class)).collect(Collectors.toList());
		if (localitys.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		ResponseBase<List<LocalityDto>> response = new ResponseBase<List<LocalityDto>>().setData(localitys)
																						.setSuccess(true);
		return ResponseEntity.status(200).body(response);
	}

	@GetMapping("/listProperty")
	public ResponseEntity<ResponseBase<List<LocalityPropertyDto>>> listProperty(
			@RequestParam("id-domain") Long idDomain) {
		List<LocalityPropertyDto> localitys = localityService.findAllByDomainProperties(idDomain).stream()
				.map(o -> modelMapper.map(o, LocalityPropertyDto.class)).collect(Collectors.toList());
		if (localitys.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		ResponseBase<List<LocalityPropertyDto>> response = new ResponseBase<List<LocalityPropertyDto>>()
				.setData(localitys).setSuccess(true);
		return ResponseEntity.status(200).body(response);
	}

	@GetMapping("{id}")
	public ResponseEntity<ResponseBase<LocalityDetailDto>> findById(@PathVariable Long id) {
		LocalityDetailDto localityDto = modelMapper.map(localityService.findById(id), LocalityDetailDto.class);
		ResponseBase<LocalityDetailDto> response = new ResponseBase<LocalityDetailDto>().setData(localityDto)
				.setSuccess(true);
		return ResponseEntity.status(200).body(response);
	}

	@PostMapping
	public ResponseEntity<ResponseBase<EntityDto>> save(@Valid @RequestBody LocalityStoreDto localityParamDto) {
		Locality locality = localityService.getLocality(localityParamDto);
		locality = localityService.save(locality);
		ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(new EntityDto(locality.getId()))
				.setSuccess(true);
		return ResponseEntity.status(200).body(entity);
	}

	@PutMapping
	public ResponseEntity<ResponseBase<EntityDto>> update(@RequestBody @Valid LocalityUpdateDto localityUpdateDto) {
		Locality locality = localityService.getLocality(localityUpdateDto);
		locality = localityService.update(locality);
		ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(new EntityDto(locality.getId()))
				.setSuccess(true);
		return ResponseEntity.status(200).body(entity);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		Locality locality = localityService.findById(id);
		localityService.delete(locality);
		return ResponseEntity.ok().build();
	}

}
