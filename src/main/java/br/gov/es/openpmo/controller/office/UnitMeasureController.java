package br.gov.es.openpmo.controller.office;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.unitmeasure.UnitMeasureDto;
import br.gov.es.openpmo.dto.unitmeasure.UnitMeasureStoreDto;
import br.gov.es.openpmo.dto.unitmeasure.UnitMeasureUpdateDto;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.UnitMeasure;
import br.gov.es.openpmo.service.office.UnitMeasureService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import io.swagger.annotations.Api;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api
@RestController
@CrossOrigin
@RequestMapping("/unitMeasures")
public class UnitMeasureController {

  private static final String OPERATION_SUCCESS = ApplicationMessage.OPERATION_SUCCESS;

  private final UnitMeasureService unitMeasureService;
  private final ModelMapper modelMapper;
  private final ICanAccessService canAccessService;

  @Autowired
  public UnitMeasureController(
      final UnitMeasureService unitMeasureService,
      final ModelMapper modelMapper,
      final ICanAccessService canAccessService) {
    this.unitMeasureService = unitMeasureService;
    this.modelMapper = modelMapper;
    this.canAccessService = canAccessService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<UnitMeasureDto>>> indexBase(
      @RequestParam final Long idOffice,
      @RequestParam(required = false) final Long idFilter) {
    final List<UnitMeasureDto> unitMeasures = new ArrayList<>();
    this.unitMeasureService.findAll(idOffice, idFilter)
        .forEach(registro -> unitMeasures.add(new UnitMeasureDto(registro)));
    final ResponseBase<List<UnitMeasureDto>> response = new ResponseBase<List<UnitMeasureDto>>().setData(unitMeasures)
        .setMessage(OPERATION_SUCCESS).setSuccess(true);
    return ResponseEntity.ok(response);
  }

  @GetMapping("{id}")
  public ResponseEntity<ResponseBase<UnitMeasureDto>> findById(@PathVariable final Long id) {
    final UnitMeasureDto officeDto = new UnitMeasureDto(this.unitMeasureService.findById(id));
    final ResponseBase<UnitMeasureDto> response = new ResponseBase<UnitMeasureDto>().setData(officeDto)
        .setSuccess(true);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> save(@RequestBody final UnitMeasureStoreDto unitMeasureStoreDto,
      @Authorization final String authorization) {

    this.canAccessService.ensureIsAdministrator(authorization);

    UnitMeasure unitMeasure = this.modelMapper.map(unitMeasureStoreDto, UnitMeasure.class);
    unitMeasure.setOffice(new Office());
    unitMeasure.getOffice().setId(unitMeasureStoreDto.getIdOffice());
    unitMeasure = this.unitMeasureService.save(unitMeasure);
    final ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setMessage(OPERATION_SUCCESS)
        .setData(new EntityDto(
            unitMeasure.getId()))
        .setSuccess(true);
    return ResponseEntity.ok(entity);
  }

  @PutMapping
  public ResponseEntity<ResponseBase<EntityDto>> update(@RequestBody final UnitMeasureUpdateDto unitMeasureUpdateDto,
      @Authorization final String authorization) {

    this.canAccessService.ensureIsAdministrator(authorization);

    final UnitMeasure unitMeasure = this.unitMeasureService
        .save(this.unitMeasureService.getUnitMeasure(unitMeasureUpdateDto));
    final ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setMessage(OPERATION_SUCCESS)
        .setData(new EntityDto(
            unitMeasure.getId()))
        .setSuccess(true);
    return ResponseEntity.ok(entity);
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(@PathVariable final Long id,
      @Authorization final String authorization) {

    this.canAccessService.ensureIsAdministrator(authorization);

    final UnitMeasure unitMeasure = this.unitMeasureService.findById(id);
    this.unitMeasureService.delete(unitMeasure);
    return ResponseEntity.ok().build();
  }

}
