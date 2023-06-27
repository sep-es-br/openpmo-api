package br.gov.es.openpmo.controller.office;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.domain.LocalityDetailDto;
import br.gov.es.openpmo.dto.domain.LocalityDto;
import br.gov.es.openpmo.dto.domain.LocalityPropertyDto;
import br.gov.es.openpmo.dto.domain.LocalityStoreDto;
import br.gov.es.openpmo.dto.domain.LocalityUpdateDto;
import br.gov.es.openpmo.model.office.Locality;
import br.gov.es.openpmo.service.office.LocalityService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
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
@RequestMapping("/localities")
public class LocalityController {

  private final LocalityService localityService;
  private final ModelMapper modelMapper;
  private final ICanAccessService canAccessService;

  @Autowired
  public LocalityController(
      final LocalityService localityService,
      final ModelMapper modelMapper,
      final ICanAccessService canAccessService) {
    this.localityService = localityService;
    this.modelMapper = modelMapper;
    this.canAccessService = canAccessService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<LocalityDto>>> indexBase(@RequestParam("id-domain") final Long idDomain,
      @Authorization final String authorization) {

//    this.canAccessService.ensureCanReadResource(idDomain, authorization);
    final List<LocalityDto> localities = this.localityService.findAll(idDomain).stream()
        .map(o -> this.modelMapper.map(o, LocalityDto.class))
        .collect(Collectors.toList());
    if (localities.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(ResponseBase.of(localities));
  }

  @GetMapping("/firstLevel")
  public ResponseEntity<ResponseBase<List<LocalityDto>>> indexBaseFirstLevel(
      @RequestParam("id-domain") final Long idDomain,
      @RequestParam(required = false) final Long idFilter,
      @RequestParam(required = false) final String term,
      @Authorization final String authorization) {

//    this.canAccessService.ensureCanReadResource(idDomain, authorization);
    final List<LocalityDto> localities = this.localityService.findAllFirstLevel(idDomain, idFilter, term)
        .stream()
        .map(o -> this.modelMapper.map(o, LocalityDto.class))
        .collect(Collectors.toList());
    if (localities.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(ResponseBase.of(localities));
  }

  @GetMapping("/listProperty")
  public ResponseEntity<ResponseBase<List<LocalityPropertyDto>>> listProperty(
      @RequestParam("id-domain") final Long idDomain,
      @Authorization final String authorization) {

//    this.canAccessService.ensureCanReadResource(idDomain, authorization);
    final List<LocalityPropertyDto> localities = this.localityService.findAllByDomainProperties(idDomain).stream()
        .map(locality -> this.modelMapper.map(locality, LocalityPropertyDto.class))
        .collect(Collectors.toList());
    if (localities.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(ResponseBase.of(localities));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseBase<LocalityDetailDto>> findById(
      @PathVariable final Long id,
      @RequestParam(required = false) final Long idFilter,
      @RequestParam(required = false) final String term,
      @Authorization final String authorization) {

//    this.canAccessService.ensureCanReadResource(id, authorization);
    final LocalityDetailDto localityDto = this.modelMapper.map(
        this.localityService.findById(id, idFilter, term),
        LocalityDetailDto.class);

    final ResponseBase<LocalityDetailDto> response = new ResponseBase<LocalityDetailDto>()
        .setData(localityDto)
        .setSuccess(true);

    return ResponseEntity.status(200).body(response);
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> save(@Valid @RequestBody final LocalityStoreDto localityParamDto,
      @Authorization final String authorization) {

    this.canAccessService.ensureCanEditResource(localityParamDto.getIdParent(), authorization);

    Locality locality = this.localityService.getLocality(localityParamDto);
    locality = this.localityService.save(locality);
    final ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(new EntityDto(locality.getId()))
        .setSuccess(true);
    return ResponseEntity.status(200).body(entity);
  }

  @PutMapping
  public ResponseEntity<ResponseBase<EntityDto>> update(@RequestBody @Valid final LocalityUpdateDto localityUpdateDto,
      @Authorization final String authorization) {

    this.canAccessService.ensureCanEditResource(localityUpdateDto.getId(), authorization);

    Locality locality = this.localityService.getLocality(localityUpdateDto);
    locality = this.localityService.update(locality);
    final ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(new EntityDto(locality.getId()))
        .setSuccess(true);
    return ResponseEntity.status(200).body(entity);
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(@PathVariable final Long id,
      @Authorization final String authorization) {

    this.canAccessService.ensureCanEditResource(id, authorization);

    final Locality locality = this.localityService.findById(id);
    this.localityService.delete(locality);
    return ResponseEntity.ok().build();
  }

}
