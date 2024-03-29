package br.gov.es.openpmo.controller.plan;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.plan.PlanDto;
import br.gov.es.openpmo.dto.plan.PlanStoreDto;
import br.gov.es.openpmo.dto.plan.PlanUpdateDto;
import br.gov.es.openpmo.enumerator.TokenType;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.office.OfficeService;
import br.gov.es.openpmo.service.office.plan.PlanModelService;
import br.gov.es.openpmo.service.office.plan.PlanService;
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
@RequestMapping("/plans")
public class PlanController {

  private final PlanService planService;
  private final OfficeService officeService;
  private final PlanModelService planModelService;
  private final ModelMapper modelMapper;
  private final TokenService tokenService;
  private final ICanAccessService canAccessService;

  @Autowired
  public PlanController(
    final PlanService planService,
    final OfficeService officeService,
    final PlanModelService planModelService,
    final ModelMapper modelMapper,
    final TokenService tokenService,
    final ICanAccessService canAccessService
  ) {
    this.planService = planService;
    this.officeService = officeService;
    this.planModelService = planModelService;
    this.modelMapper = modelMapper;
    this.tokenService = tokenService;
    this.canAccessService = canAccessService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<PlanDto>>> indexBase(
    @RequestParam("id-office") final Long idOffice,
    @RequestParam(required = false) final Long idFilter,
    @RequestParam(required = false) final String term,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(idOffice, authorization);
    final String token = authorization.substring(7);
    final Long idUser = this.tokenService.getPersonId(token, TokenType.AUTHENTICATION);
    List<PlanDto> plans = this.planService.findAllInOffice(idOffice, idFilter, term)
      .stream()
      .map(PlanDto::of)
      .collect(Collectors.toList());
    plans = this.planService.checkPermission(plans, idUser, idOffice);
    final ResponseBase<List<PlanDto>> response = new ResponseBase<List<PlanDto>>().setData(plans).setSuccess(true);
    return ResponseEntity.status(200).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseBase<PlanDto>> findById(
    @PathVariable final Long id,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(id, authorization);

    final PlanDto response = this.planService.maybeFindById(id)
      .map(PlanDto::of)
      .orElse(null);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> save(
    @RequestBody @Valid final PlanStoreDto planStoreDto,
    @Authorization final String authorization
  ) {

    this.canAccessService.ensureCanEditResource(planStoreDto.getIdOffice(), authorization);
    Plan plan = this.modelMapper.map(planStoreDto, Plan.class);
    plan.setOffice(this.officeService.findById(planStoreDto.getIdOffice()));
    plan.setPlanModel(this.planModelService.findById(planStoreDto.getIdPlanModel()));
    plan = this.planService.save(plan);
    final ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(new EntityDto(plan.getId()))
      .setSuccess(true);
    return ResponseEntity.status(200).body(entity);
  }

  @PutMapping
  public ResponseEntity<ResponseBase<EntityDto>> update(
    @RequestBody @Valid final PlanUpdateDto planParamDto,
    @Authorization final String authorization
  ) {

    this.canAccessService.ensureCanEditResource(planParamDto.getId(), authorization);
    final Plan plan = this.planService.save(this.planService.getPlan(planParamDto));
    final ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(new EntityDto(plan.getId()))
      .setSuccess(true);
    return ResponseEntity.status(200).body(entity);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
    @PathVariable final Long id,
    @Authorization final String authorization
  ) {

    this.canAccessService.ensureCanEditResource(id, authorization);
    final Plan plan = this.planService.findById(id);
    this.planService.delete(plan);
    return ResponseEntity.ok().build();
  }

}
