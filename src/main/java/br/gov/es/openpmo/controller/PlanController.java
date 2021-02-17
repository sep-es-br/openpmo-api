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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.plan.PlanDto;
import br.gov.es.openpmo.dto.plan.PlanStoreDto;
import br.gov.es.openpmo.dto.plan.PlanUpdateDto;
import br.gov.es.openpmo.model.Plan;
import br.gov.es.openpmo.model.domain.TokenType;
import br.gov.es.openpmo.service.OfficeService;
import br.gov.es.openpmo.service.PlanModelService;
import br.gov.es.openpmo.service.PlanService;
import br.gov.es.openpmo.service.TokenService;
import io.swagger.annotations.Api;

@Api
@RestController
@CrossOrigin
@RequestMapping(value = "/plans")
public class PlanController {

    private final PlanService planService;
    private final OfficeService officeService;
    private final PlanModelService planModelService;
    private final ModelMapper modelMapper;
    private final TokenService tokenService;

    @Autowired
    public PlanController(PlanService planService, OfficeService officeService,
                          PlanModelService planModelService, ModelMapper modelMapper, TokenService tokenService) {
        this.planService = planService;
        this.officeService = officeService;
        this.planModelService = planModelService;
        this.modelMapper = modelMapper;
        this.tokenService = tokenService;
    }

    @GetMapping
    public ResponseEntity<ResponseBase<List<PlanDto>>> indexBase(
            @RequestParam("id-office") Long idOffice,
            @RequestHeader(name="Authorization") String autorization
    ) {
        String token = autorization.substring(7);
        Long idUser = tokenService.getPersonId(token, TokenType.AUTHENTICATION);
        List<PlanDto> plans = planService.findAllInOffice(idOffice).stream().map(PlanDto::new).collect(
            Collectors.toList());
        plans = planService.chekPermission(plans, idUser, idOffice);
        ResponseBase<List<PlanDto>> response =
                new ResponseBase<List<PlanDto>>().setData(plans).setSuccess(true);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBase<PlanDto>> findById(@PathVariable Long id) {
        PlanDto planDto = new PlanDto(planService.findById(id));
        ResponseBase<PlanDto> response = new ResponseBase<PlanDto>().setData(planDto).setSuccess(true);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping
    public ResponseEntity<ResponseBase<EntityDto>> save(@RequestBody @Valid PlanStoreDto planStoreDto) {
        Plan plan = modelMapper.map(planStoreDto, Plan.class);
        plan.setOffice(officeService.findById(planStoreDto.getIdOffice()));
        plan.setPlanModel(planModelService.findById(planStoreDto.getIdPlanModel()));
        plan = planService.save(plan);
        ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(new EntityDto(plan.getId())).setSuccess(true);
        return ResponseEntity.status(200).body(entity);
    }

    @PutMapping
    public ResponseEntity<ResponseBase<EntityDto>> update(@RequestBody @Valid PlanUpdateDto planParamDto) {
        Plan plan = planService.save(planService.getPlan(planParamDto));
        ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(new EntityDto(plan.getId())).setSuccess(true);
        return ResponseEntity.status(200).body(entity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Plan plan = planService.findById(id);
        planService.delete(plan);
        return ResponseEntity.ok().build();
    }

}
