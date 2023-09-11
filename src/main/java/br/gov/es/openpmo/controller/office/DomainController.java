package br.gov.es.openpmo.controller.office;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.domain.DomainDto;
import br.gov.es.openpmo.dto.domain.DomainStoreDto;
import br.gov.es.openpmo.dto.domain.DomainUpdateDto;
import br.gov.es.openpmo.model.office.Domain;
import br.gov.es.openpmo.service.office.DomainService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import io.swagger.annotations.Api;
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
  private final ICanAccessService canAccessService;

  @Autowired
  public DomainController(
      final DomainService domainService,
      final ModelMapper modelMapper,
      final ICanAccessService canAccessService
  ) {
    this.domainService = domainService;
    this.modelMapper = modelMapper;
    this.canAccessService = canAccessService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<DomainDto>>> indexBase(
      @RequestParam(value = "id-office", required = false) final Long idOffice,
      @RequestParam(value = "idFilter", required = false) final Long idFilter,
      @RequestParam(required = false) final String term,
      @Authorization final String authorization) {

    this.canAccessService.ensureCanReadResource(idOffice, authorization);
    final List<DomainDto> domains = this.domainService.findAll(idOffice, idFilter, term)
        .stream()
        .map(o -> this.modelMapper.map(o, DomainDto.class))
        .collect(Collectors.toList());
    if (domains.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    final ResponseBase<List<DomainDto>> response = new ResponseBase<List<DomainDto>>().setData(domains)
        .setSuccess(true);
    return ResponseEntity.ok(response);
  }

  @GetMapping("{id}")
  public ResponseEntity<ResponseBase<DomainDto>> findById(@PathVariable final Long id,
      @Authorization final String authorization) {
    this.canAccessService.ensureCanReadResource(id, authorization);
    final DomainDto domainDto = this.modelMapper.map(this.domainService.findById(id), DomainDto.class);
    final ResponseBase<DomainDto> response = new ResponseBase<DomainDto>()
        .setData(domainDto)
        .setSuccess(true);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> save(@Valid @RequestBody final DomainStoreDto domainStoreDto,
      @Authorization final String authorization) {

    this.canAccessService.ensureCanEditResource(domainStoreDto.getIdOffice(), authorization);

    final Domain domain = this.domainService.save(domainStoreDto);

    final ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>()
        .setData(new EntityDto(domain.getId()))
        .setSuccess(true);

    return ResponseEntity.ok(entity);
  }

  @PutMapping
  public ResponseEntity<ResponseBase<EntityDto>> update(@RequestBody @Valid final DomainUpdateDto domainUpdateDto,
      @Authorization final String authorization) {

    this.canAccessService.ensureCanEditResource(domainUpdateDto.getId(), authorization);

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
  public ResponseEntity<Void> delete(@PathVariable final Long id,
      @Authorization final String authorization) {

    this.canAccessService.ensureCanEditResource(id, authorization);

    final Domain domain = this.domainService.findById(id);
    this.domainService.delete(domain);
    return ResponseEntity.ok().build();
  }

}
