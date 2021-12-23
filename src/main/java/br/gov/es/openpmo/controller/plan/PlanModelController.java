package br.gov.es.openpmo.controller.plan;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.planmodel.PlanModelDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelStoreDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelUpdateDto;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.service.office.plan.PlanModelService;
import br.gov.es.openpmo.service.office.plan.SharedPlanModelService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Api
@RestController
@CrossOrigin
@RequestMapping("/plan-models")
public class PlanModelController {

  private final PlanModelService planModelService;
  private final SharedPlanModelService sharedPlanModelService;

  @Autowired
  public PlanModelController(
    final PlanModelService planModelService,
    final SharedPlanModelService sharedPlanModelService
  ) {
    this.planModelService = planModelService;
    this.sharedPlanModelService = sharedPlanModelService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<PlanModelDto>>> indexBase(
    @RequestParam("id-office") final Long idOffice,
    @RequestParam(required = false) final Long idFilter
  ) {
    final List<PlanModelDto> planModels = new ArrayList<>();

    this.planModelService.findAllInOffice(idOffice, idFilter)
      .forEach(registro -> planModels.add(new PlanModelDto(registro)));

    return ResponseEntity.ok(ResponseBase.of(planModels));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseBase<PlanModelDto>> findById(@PathVariable final Long id) {
    final PlanModelDto planModelDto = new PlanModelDto(this.planModelService.findById(id));
    return ResponseEntity.ok(ResponseBase.of(planModelDto));
  }

  @GetMapping("/shareds")
  public ResponseEntity<ResponseBase<List<PlanModelDto>>> findAllSharedWithOffice(@RequestParam("id-office") final Long idOffice) {
    final List<PlanModelDto> sharedWithOffice = this.planModelService.findAllSharedWithOffice(idOffice);
    return ResponseEntity.ok(ResponseBase.of(sharedWithOffice));
  }

  @Transactional
  @PostMapping("/{idOffice}/create-from-shared/{idPlanModelShared}")
  public ResponseEntity<ResponseBase<EntityDto>> createFromShared(
    @PathVariable("idOffice") final Long idOffice,
    @PathVariable("idPlanModelShared") final Long idPlanModelShared
  ) {
    final EntityDto entityDto = this.sharedPlanModelService.createFromShared(idOffice, idPlanModelShared);
    return ResponseEntity.ok(ResponseBase.of(entityDto));
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> save(@RequestBody @Valid final PlanModelStoreDto planModelStoreDto) {
    final PlanModel planModel = this.planModelService.store(planModelStoreDto);
    return ResponseEntity.ok(ResponseBase.of(new EntityDto(planModel.getId())));
  }

  @PutMapping
  public ResponseEntity<ResponseBase<EntityDto>> update(@RequestBody @Valid final PlanModelUpdateDto planModelUpdateDto) {
    final PlanModel planModel = this.planModelService.update(planModelUpdateDto);
    return ResponseEntity.ok(ResponseBase.of(new EntityDto(planModel.getId())));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable final Long id) {
    final PlanModel planModel = this.planModelService.findById(id);
    this.planModelService.delete(planModel);
    return ResponseEntity.ok().build();
  }

}
