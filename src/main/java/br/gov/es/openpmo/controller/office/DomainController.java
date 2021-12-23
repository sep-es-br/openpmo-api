package br.gov.es.openpmo.controller.office;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.domain.DomainDto;
import br.gov.es.openpmo.dto.domain.DomainStoreDto;
import br.gov.es.openpmo.dto.domain.DomainUpdateDto;
import br.gov.es.openpmo.model.office.Domain;
import br.gov.es.openpmo.service.office.DomainService;
import io.swagger.annotations.Api;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Api
@RestController
@CrossOrigin
@RequestMapping("/domains")
public class DomainController {

  private final DomainService domainService;
  private final ModelMapper modelMapper;

  @Autowired
  public DomainController(final DomainService domainService, final ModelMapper modelMapper) {
    this.domainService = domainService;
    this.modelMapper = modelMapper;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<DomainDto>>> indexBase(
    @RequestParam(value = "id-office", required = false) final Long idOffice,
    @RequestParam(value = "idFilter", required = false) final Long idFilter
  ) {
    final List<DomainDto> domains = this.domainService.findAll(idOffice, idFilter)
      .stream()
      .map(o -> this.modelMapper.map(o, DomainDto.class))
      .collect(Collectors.toList());
    if(domains.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    final ResponseBase<List<DomainDto>> response = new ResponseBase<List<DomainDto>>().setData(domains).setSuccess(true);
    return ResponseEntity.ok(response);
  }

  @GetMapping("{id}")
  public ResponseEntity<ResponseBase<DomainDto>> findById(@PathVariable final Long id) {
    final DomainDto domainDto = this.modelMapper.map(this.domainService.findById(id), DomainDto.class);
    final ResponseBase<DomainDto> response = new ResponseBase<DomainDto>()
      .setData(domainDto)
      .setSuccess(true);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> save(@Valid @RequestBody final DomainStoreDto domainStoreDto) {

    final Domain domain = this.domainService.save(domainStoreDto);

    final ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>()
      .setData(new EntityDto(domain.getId()))
      .setSuccess(true);

    return ResponseEntity.ok(entity);
  }

  @PutMapping
  public ResponseEntity<ResponseBase<EntityDto>> update(@RequestBody @Valid final DomainUpdateDto domainUpdateDto) {
    final Domain domain = this.domainService.update(this.getDomain(domainUpdateDto));

    final ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>()
      .setData(new EntityDto(domain.getId()))
      .setSuccess(true);

    return ResponseEntity.ok(entity);
  }

  private Domain getDomain(final Object object) {
    return this.modelMapper.map(object, Domain.class);
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(@PathVariable final Long id) {
    final Domain domain = this.domainService.findById(id);
    this.domainService.delete(domain);
    return ResponseEntity.ok().build();
  }

}
