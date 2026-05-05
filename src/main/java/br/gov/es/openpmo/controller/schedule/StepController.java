package br.gov.es.openpmo.controller.schedule;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.ResponseBaseItens;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.permission.WorkpackPermissionResponse;
import br.gov.es.openpmo.dto.schedule.DeliveryStepsUpdateDto;
import br.gov.es.openpmo.dto.schedule.StepDeliveryUpdateDto;
import br.gov.es.openpmo.dto.schedule.StepDto;
import br.gov.es.openpmo.dto.schedule.StepStoreParamDto;
import br.gov.es.openpmo.dto.schedule.StepUpdateDto;
import br.gov.es.openpmo.dto.schedule.UpdateCostAccountByStepIdRequest;
import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.repository.StepRepository;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.service.schedule.BatchUpdateStep;
import br.gov.es.openpmo.service.schedule.StepService;
import br.gov.es.openpmo.service.schedule.UpdateStatusService;
import br.gov.es.openpmo.service.schedule.UpdateStep;
import br.gov.es.openpmo.service.workpack.GetWorkpackPermissions;
import br.gov.es.openpmo.service.workpack.UpdateCostAccountByStepId;
import br.gov.es.openpmo.service.workpack.WorkpackPermissionVerifier;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Api
@RestController
@CrossOrigin
@RequestMapping("/schedules/step")
public class StepController {

  private final StepRepository stepRepository;

  private final StepService stepService;

  private final UpdateStatusService status;

  private final WorkpackPermissionVerifier workpackPermissionVerifier;

  private final BatchUpdateStep batchUpdateStep;

  private final UpdateStep updateStep;

  private final ICanAccessService canAccessService;

  private final UpdateCostAccountByStepId updateCostAccountByStepId;

  private final TokenService tokenService;

  @Autowired
  public StepController(
    final StepService stepService,
    final UpdateStatusService status,
    final BatchUpdateStep batchUpdateStep,
    final UpdateStep updateStep,
    final ICanAccessService canAccessService,
    final UpdateCostAccountByStepId updateCostAccountByStepId,
    final WorkpackPermissionVerifier workpackPermissionVerifier,
    final TokenService tokenService, 
    final StepRepository stepRepository
  ) {
    this.stepService = stepService;
    this.status = status;
    this.batchUpdateStep = batchUpdateStep;
    this.updateStep = updateStep;
    this.canAccessService = canAccessService;
    this.updateCostAccountByStepId = updateCostAccountByStepId;
    this.workpackPermissionVerifier = workpackPermissionVerifier;
    this.tokenService = tokenService;
    this.stepRepository = stepRepository;
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

  // @Transactional
  // @PutMapping("/step/update/{idDelivery}")
  // public ResponseEntity<?> updateByDelivery(
  //     @PathVariable Long idDelivery,
  //     @RequestBody @Valid List<StepDeliveryUpdateDto> stepDeliveryUpdateDtos
  // ) {

  //   DeliveryStepsUpdateDto deliveryStepsUpdateDto = this.stepRepository.findStepsByWorkpack(idDelivery);

  //   this.batchUpdateStep.updateStepsByDelivery(idDelivery, stepDeliveryUpdateDtos);

  //   return ResponseEntity.ok().build();
  // }

  @Transactional
  @PutMapping("/batch/{idSchedule}")
  public ResponseEntity<ResponseBaseItens<Long>> batchUpdate(
    @PathVariable final Long idSchedule,
    @RequestParam final Long idPlan,
    @RequestParam final Long idWorkpack,
    @RequestBody final List<? extends @Valid StepUpdateDto> stepUpdates,
    @Authorization final String authorization
  ) {
    final List<Long> stepIds = stepUpdates.stream()
      .map(StepUpdateDto::getId)
      .collect(Collectors.toList());

    this.canAccessService.ensureCanUpdateResource(
      stepIds,
      authorization
    );

    final Long idUser = this.tokenService.getUserId(authorization);

    final List<PermissionDto> permissions = this.workpackPermissionVerifier.fetchPermissions(
      idUser,
      idPlan,
      idWorkpack
    );

    PermissionDto highestPermission = permissions.stream()
        .max(Comparator.comparingInt(p -> p.getLevel().getLevel()))
        .orElse(null);

    PermissionLevelEnum level = (highestPermission != null) ? highestPermission.getLevel() : null;

    final List<Long> ids;

    if (PermissionLevelEnum.UPDATE.equals(level)) {
        ids = this.batchUpdateStep.executeRestricted(stepUpdates, idSchedule);
    } else {
        ids = this.batchUpdateStep.execute(stepUpdates, idSchedule);
    }

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
    this.status.update(deliverables);
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
    this.status.update(deliverables);
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

  @GetMapping("/check-complete/{id}")
  public ResponseEntity<ResponseBase<Boolean>> checkIfComplete(
      @PathVariable final Long id,
      @Authorization final String authorization
  ) {

    this.canAccessService.ensureCanReadResource(id, authorization);

    boolean complete = !status.checkHasWorkToComplete(id);

    return ResponseEntity.ok(ResponseBase.of(complete));
  }

}
