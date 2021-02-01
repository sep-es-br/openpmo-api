package br.gov.es.openpmo.controller;

import java.util.ArrayList;
import java.util.List;

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
import br.gov.es.openpmo.dto.unitmeasure.UnitMeasureDto;
import br.gov.es.openpmo.dto.unitmeasure.UnitMeasureStoreDto;
import br.gov.es.openpmo.dto.unitmeasure.UnitMeasureUpdateDto;
import br.gov.es.openpmo.model.Office;
import br.gov.es.openpmo.model.UnitMeasure;
import br.gov.es.openpmo.service.UnitMeasureService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import io.swagger.annotations.Api;

@Api
@RestController
@CrossOrigin
@RequestMapping(value = "/unitMeasures")
public class UnitMeasureController {

    private static final String OPERATION_SUCCESS = ApplicationMessage.OPERATION_SUCCESS;

    private final UnitMeasureService unitMeasureService;
    private final ModelMapper modelMapper;

    @Autowired
    public UnitMeasureController(UnitMeasureService unitMeasureService, ModelMapper modelMapper) {
        this.unitMeasureService = unitMeasureService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<ResponseBase<List<UnitMeasureDto>>> indexBase(@RequestParam Long idOffice) {
        List<UnitMeasureDto> unitMeasures = new ArrayList<>();
        unitMeasureService.findAll(idOffice).forEach(registro -> unitMeasures.add(new UnitMeasureDto(registro)));
        ResponseBase<List<UnitMeasureDto>> response = new ResponseBase<List<UnitMeasureDto>>().setData(unitMeasures)
                .setMessage(OPERATION_SUCCESS).setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<ResponseBase<UnitMeasureDto>> findById(@PathVariable Long id) {
        UnitMeasureDto officeDto = new UnitMeasureDto(unitMeasureService.findById(id));
        ResponseBase<UnitMeasureDto> response = new ResponseBase<UnitMeasureDto>().setData(officeDto).setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ResponseBase<EntityDto>> save(@RequestBody UnitMeasureStoreDto unitMeasureStoreDto) {
        UnitMeasure unitMeasure = modelMapper.map(unitMeasureStoreDto, UnitMeasure.class);
        unitMeasure.setOffice(new Office());
        unitMeasure.getOffice().setId(unitMeasureStoreDto.getIdOffice());
        unitMeasure = unitMeasureService.save(unitMeasure);
        ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setMessage(OPERATION_SUCCESS).setData(new EntityDto(unitMeasure.getId()))
                .setSuccess(true);
        return ResponseEntity.ok(entity);
    }

    @PutMapping
    public ResponseEntity<ResponseBase<EntityDto>> update(@RequestBody UnitMeasureUpdateDto unitMeasureUpdateDto) {
        UnitMeasure unitMeasure = unitMeasureService.save(unitMeasureService.getUnitMeasure(unitMeasureUpdateDto));
        ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setMessage(OPERATION_SUCCESS).setData(new EntityDto(unitMeasure.getId()))
                .setSuccess(true);
        return ResponseEntity.ok(entity);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        UnitMeasure unitMeasure = unitMeasureService.findById(id);
        unitMeasureService.delete(unitMeasure);
        return ResponseEntity.ok().build();
    }

}
