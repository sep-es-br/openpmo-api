package br.gov.es.openpmo.controller.schedule;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.ResponseBaseItens;
import br.gov.es.openpmo.dto.schedule.StepDto;
import br.gov.es.openpmo.dto.schedule.StepStoreParamDto;
import br.gov.es.openpmo.dto.schedule.StepUpdateDto;
import br.gov.es.openpmo.dto.schedule.UpdateCostAccountByStepIdRequest;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.service.schedule.BatchUpdateStep;
import br.gov.es.openpmo.service.schedule.StepService;
import br.gov.es.openpmo.service.schedule.UpdateStatusService;
import br.gov.es.openpmo.service.schedule.UpdateStep;
import br.gov.es.openpmo.service.workpack.UpdateCostAccountByStepId;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Api
@RestController
@CrossOrigin
@RequestMapping("/schedules/step")
public class StepController {

  private final StepService stepService;

  private final UpdateStatusService status;

  private final BatchUpdateStep batchUpdateStep;

  private final UpdateStep updateStep;

  private final ICanAccessService canAccessService;

  private final UpdateCostAccountByStepId updateCostAccountByStepId;

  @Autowired
  public StepController(
    final StepService stepService,
    final UpdateStatusService status,
    final BatchUpdateStep batchUpdateStep,
    final UpdateStep updateStep,
    final ICanAccessService canAccessService,
    final UpdateCostAccountByStepId updateCostAccountByStepId
  ) {
    this.stepService = stepService;
    this.status = status;
    this.batchUpdateStep = batchUpdateStep;
    this.updateStep = updateStep;
    this.canAccessService = canAccessService;
    this.updateCostAccountByStepId = updateCostAccountByStepId;
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseBase<StepDto>> findById(
    @PathVariable final Long id,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(
      id,
      authorization
    );
    final Step step = this.stepService.findById(id);
    final StepDto stepDto = this.stepService.mapToStepDto(step);
    return ResponseEntity.ok(ResponseBase.of(stepDto));
  }

  @PutMapping
  @Transactional
  public ResponseEntity<ResponseBase<EntityDto>> update(
    @RequestBody @Valid final StepUpdateDto stepUpdateDto,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(stepUpdateDto.getId(), authorization);

    final Step step = this.updateStep.execute(stepUpdateDto, false);

    return ResponseEntity.ok(ResponseBase.of(new EntityDto(step.getId())));
  }

  @Transactional
  @PutMapping("/batch")
  public ResponseEntity<ResponseBaseItens<Long>> batchUpdate(
    @RequestBody final List<? extends @Valid StepUpdateDto> stepUpdates,
    @Authorization final String authorization
  ) {
    final List<Long> stepIds = stepUpdates.stream()
      .map(StepUpdateDto::getId)
      .collect(Collectors.toList());

    this.canAccessService.ensureCanEditResource(
      stepIds,
      authorization
    );

    final List<Long> ids = this.batchUpdateStep.execute(stepUpdates);

    return ResponseEntity.ok(ResponseBaseItens.of(ids));
  }

  @PostMapping
  public ResponseEntity<Void> save(
    @Valid @RequestBody final StepStoreParamDto stepStoreParamDto,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(
      stepStoreParamDto.getIdSchedule(),
      authorization
    );
    final List<Deliverable> deliverables = this.status.getDeliverablesByScheduleId(stepStoreParamDto.getIdSchedule());
    this.stepService.save(stepStoreParamDto);
    this.status.update(deliverables, true);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
    @PathVariable final Long id,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(
      id,
      authorization
    );
    final List<Deliverable> deliverables = this.status.getDeliverablesByStepId(id);
    this.stepService.delete(id);
    this.status.update(deliverables, true);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{idStep}/cost-account/{idCostAccount}")
  public ResponseEntity<ResponseBase<Void>> updateCostAccountByStepId(
    @PathVariable Long idStep,
    @PathVariable Long idCostAccount,
    @Valid @RequestBody UpdateCostAccountByStepIdRequest request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(
      idStep,
      authorization
    );
    this.updateCostAccountByStepId.execute(
      idStep,
      idCostAccount,
      request
    );
    return ResponseEntity.ok(ResponseBase.success());
  }

}
