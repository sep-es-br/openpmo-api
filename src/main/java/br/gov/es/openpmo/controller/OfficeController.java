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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.office.OfficeDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.office.OfficeUpdateDto;
import br.gov.es.openpmo.model.Office;
import br.gov.es.openpmo.model.domain.TokenType;
import br.gov.es.openpmo.service.OfficeService;
import br.gov.es.openpmo.service.TokenService;
import io.swagger.annotations.Api;

@Api
@RestController
@CrossOrigin
@RequestMapping(value = "/offices")
public class OfficeController {

	private final OfficeService officeService;
	private final ModelMapper modelMapper;
	private final TokenService tokenService;

	@Autowired
	public OfficeController(OfficeService officeService, ModelMapper modelMapper, TokenService tokenService) {
		this.officeService = officeService;
		this.modelMapper = modelMapper;
		this.tokenService = tokenService;
	}

	@GetMapping
	public ResponseEntity<ResponseBase<List<OfficeDto>>> indexBase(@RequestHeader(name="Authorization") String autorization) {
		String token = autorization.substring(7);
		Long idUser = tokenService.getPersonId(token, TokenType.AUTHENTICATION);
		List<OfficeDto> offices = officeService.findAll().stream().map(o -> modelMapper.map(o, OfficeDto.class)).collect(
			Collectors.toList());
		if (offices.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		offices = officeService.chekPermission(offices, idUser);
		ResponseBase<List<OfficeDto>> response = new ResponseBase<List<OfficeDto>>().setData(offices).setSuccess(true);
		return ResponseEntity.status(200).body(response);
	}

	@GetMapping("{id}")
	public ResponseEntity<ResponseBase<OfficeDto>> findById(@PathVariable Long id) {
		OfficeDto officeDto = modelMapper.map(officeService.findById(id), OfficeDto.class);
		ResponseBase<OfficeDto> response = new ResponseBase<OfficeDto>().setData(officeDto).setSuccess(true);
		return ResponseEntity.status(200).body(response);
	}

	@PostMapping
	public ResponseEntity<ResponseBase<EntityDto>> save(@Valid @RequestBody OfficeStoreDto officeStoreDto) {
		Office office = officeService.save(getOffice(officeStoreDto));
		ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(new EntityDto(office.getId())).setSuccess(true);
		return ResponseEntity.status(200).body(entity);
	}

	@PutMapping
	public ResponseEntity<ResponseBase<EntityDto>> update(@RequestBody @Valid OfficeUpdateDto officeUpdateDto) {
		Office office = officeService.save(getOffice(officeUpdateDto));
		ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(new EntityDto(office.getId())).setSuccess(true);
		return ResponseEntity.status(200).body(entity);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		Office office = officeService.findById(id);
		officeService.delete(office);
		return ResponseEntity.ok().build();
	}

	private Office getOffice(Object object) {
		return modelMapper.map(object, Office.class);
	}

}
