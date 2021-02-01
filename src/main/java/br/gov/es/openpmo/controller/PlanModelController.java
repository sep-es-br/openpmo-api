package br.gov.es.openpmo.controller;

import java.util.ArrayList;
import java.util.List;
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
import br.gov.es.openpmo.dto.planmodel.PlanModelDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelStoreDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelUpdateDto;
import br.gov.es.openpmo.model.PlanModel;
import br.gov.es.openpmo.service.OfficeService;
import br.gov.es.openpmo.service.PlanModelService;
import io.swagger.annotations.Api;

@Api
@RestController
@CrossOrigin
@RequestMapping(value = "/plan-models")
public class PlanModelController {

    private PlanModelService planModelService;
    private OfficeService officeService;
    private ModelMapper modelMapper;

    @Autowired
    public PlanModelController(PlanModelService planModelService, ModelMapper modelMapper,
                               OfficeService officeService) {
        this.planModelService = planModelService;
        this.modelMapper = modelMapper;
        this.officeService = officeService;
    }

    @GetMapping
    public ResponseEntity<ResponseBase<List<PlanModelDto>>> indexBase(
            @RequestParam("id-office") Long idOffice
    ) {
        List<PlanModelDto> planModels = new ArrayList<>();
        planModelService.findAllInOffice(idOffice).forEach(registro -> planModels.add(new PlanModelDto(registro)));
        ResponseBase<List<PlanModelDto>> response =
                new ResponseBase<List<PlanModelDto>>().setData(planModels).setSuccess(true);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBase<PlanModelDto>> findById(@PathVariable Long id) {
        PlanModelDto planModelDto = new PlanModelDto(planModelService.findById(id));
        ResponseBase<PlanModelDto> response = new ResponseBase<PlanModelDto>().setData(planModelDto).setSuccess(true);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping
    public ResponseEntity<ResponseBase<EntityDto>> save(@RequestBody @Valid PlanModelStoreDto planModelStoreDto) {
        PlanModel planModel = modelMapper.map(planModelStoreDto, PlanModel.class);
        planModel.setOffice(officeService.findById(planModelStoreDto.getIdOffice()));
        planModel = planModelService.save(planModel);
        ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(new EntityDto(planModel.getId())).setSuccess(true);
        return ResponseEntity.status(200).body(entity);
    }

    @PutMapping
    public ResponseEntity<ResponseBase<EntityDto>> update(@RequestBody @Valid PlanModelUpdateDto planModelUpdateDto) {
        PlanModel planModel = planModelService.getPlanModel(planModelUpdateDto);
        planModel = planModelService.save(planModel);
        ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(new EntityDto(planModel.getId())).setSuccess(true);
        return ResponseEntity.status(200).body(entity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        PlanModel planModel = planModelService.findById(id);
        planModelService.delete(planModel);
        return ResponseEntity.ok().build();
    }

}
