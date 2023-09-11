package br.gov.es.openpmo.controller.office;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.office.OfficeDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.office.OfficeUpdateDto;
import br.gov.es.openpmo.dto.treeview.OfficeTreeViewDto;
import br.gov.es.openpmo.enumerator.TokenType;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.office.OfficeService;
import br.gov.es.openpmo.service.office.OfficeTreeViewService;
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
@RequestMapping("/offices")
public class OfficeController {

  private final OfficeService officeService;
  private final OfficeTreeViewService officeTreeViewService;
  private final ModelMapper modelMapper;
  private final TokenService tokenService;
  private final ICanAccessService canAccessService;

  @Autowired
  public OfficeController(
      final OfficeService officeService,
      final OfficeTreeViewService officeTreeViewService,
      final ModelMapper modelMapper,
      final TokenService tokenService,
      final ICanAccessService canAccessService) {
    this.officeService = officeService;
    this.officeTreeViewService = officeTreeViewService;
    this.modelMapper = modelMapper;
    this.tokenService = tokenService;
    this.canAccessService = canAccessService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<OfficeDto>>> indexBase(
      @RequestHeader(name = "Authorization") final String authorization,
      @RequestParam(required = false) final Long idFilter,
      @RequestParam(required = false) final String term) {

    final String token = authorization.substring(7);
    final Long idUser = this.tokenService.getPersonId(token, TokenType.AUTHENTICATION);
    List<OfficeDto> offices = this.officeService.findAll(idFilter, term)
        .stream()
        .map(o -> this.modelMapper.map(o, OfficeDto.class))
        .collect(Collectors.toList());
    if (offices.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    offices = this.officeService.checkPermission(offices, idUser);
    final ResponseBase<List<OfficeDto>> response = new ResponseBase<List<OfficeDto>>().setData(offices)
        .setSuccess(true);
    return ResponseEntity.status(200).body(response);
  }

  @GetMapping("{id}")
  public ResponseEntity<ResponseBase<OfficeDto>> findById(@PathVariable final Long id,
      @RequestHeader(name = "Authorization") final String authorization) {

    this.canAccessService.ensureCanReadResource(id, authorization);

    final OfficeDto officeDto = this.officeService.maybeFindById(id).map(o -> this.modelMapper.map(o, OfficeDto.class)).orElse(null);
    final ResponseBase<OfficeDto> response = new ResponseBase<OfficeDto>().setData(officeDto).setSuccess(true);
    return ResponseEntity.status(200).body(response);
  }

  @GetMapping("/{id-office}/tree-view")
  public ResponseEntity<ResponseBase<OfficeTreeViewDto>> findTreeViewById(
      @PathVariable("id-office") final Long idOffice,
      @RequestHeader(name = "Authorization") final String authorization) {

    this.canAccessService.ensureCanReadResource(idOffice, authorization);

    final OfficeTreeViewDto officeTreeViewDto = this.officeTreeViewService.findOfficeTreeViewById(idOffice);

    final ResponseBase<OfficeTreeViewDto> response = new ResponseBase<OfficeTreeViewDto>()
        .setData(officeTreeViewDto)
        .setSuccess(true);

    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> save(@Valid @RequestBody final OfficeStoreDto officeStoreDto,
      @RequestHeader(name = "Authorization") final String authorization) {

    this.canAccessService.ensureIsAdministrator(authorization);

    final Office office = this.officeService.save(this.getOffice(officeStoreDto));
    final ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(new EntityDto(office.getId()))
        .setSuccess(
            true);
    return ResponseEntity.status(200).body(entity);
  }

  @PutMapping
  public ResponseEntity<ResponseBase<EntityDto>> update(@RequestBody @Valid final OfficeUpdateDto officeUpdateDto,
      @RequestHeader(name = "Authorization") final String authorization) {

    this.canAccessService.ensureCanEditResource(officeUpdateDto.getId(), authorization);

    final Office office = this.officeService.save(this.getOffice(officeUpdateDto));
    final ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(new EntityDto(office.getId()))
        .setSuccess(
            true);
    return ResponseEntity.status(200).body(entity);
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(@PathVariable final Long id,
      @RequestHeader(name = "Authorization") final String authorization) {

    this.canAccessService.ensureIsAdministrator(authorization);

    final Office office = this.officeService.findById(id);
    this.officeService.delete(office);
    return ResponseEntity.ok().build();
  }

  private Office getOffice(final Object object) {
    return this.modelMapper.map(object, Office.class);
  }

}
