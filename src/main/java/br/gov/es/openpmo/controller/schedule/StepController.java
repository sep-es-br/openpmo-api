package br.gov.es.openpmo.controller.schedule;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.schedule.StepDto;
import br.gov.es.openpmo.dto.schedule.StepStoreParamDto;
import br.gov.es.openpmo.dto.schedule.StepUpdateDto;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.service.schedule.StepService;
import br.gov.es.openpmo.service.schedule.UpdateStatusService;
import io.swagger.annotations.Api;
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
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
@CrossOrigin
@RequestMapping("/schedules/step")
public class StepController {

  private final StepService stepService;

  private final UpdateStatusService status;

  private final ICanAccessService canAccessService;

  @Autowired
  public StepController(
      final StepService stepService,
      final UpdateStatusService status,
      final ICanAccessService canAccessService) {
    this.stepService = stepService;
    this.status = status;
    this.canAccessService = canAccessService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseBase<StepDto>> findById(@PathVariable final Long id,
  @Authorization final String authorization) {

    this.canAccessService.ensureCanReadResource(id, authorization);
    final Step step = this.stepService.findById(id);
    final StepDto stepDto = this.stepService.mapToStepDto(step);
    return ResponseEntity.ok(ResponseBase.of(stepDto));
  }

  @PutMapping
  public ResponseEntity<ResponseBase<EntityDto>> update(@RequestBody @Valid final StepUpdateDto stepUpdateDto,
  @Authorization final String authorization) {

    this.canAccessService.ensureCanEditResource(stepUpdateDto.getId(), authorization);
    final Step step = this.stepService.update(stepUpdateDto);
    final List<Deliverable> deliverables = this.status.getDeliverablesByStepId(step.getId());
    this.status.update(deliverables);
    final Long idSchedule = step.getId();
    final EntityDto entityDto = new EntityDto(idSchedule);
    return ResponseEntity.ok(ResponseBase.of(entityDto));
  }

  @PostMapping
  public ResponseEntity<Void> save(@Valid @RequestBody final StepStoreParamDto stepStoreParamDto,
  @Authorization final String authorization) {

    this.canAccessService.ensureCanEditResource(stepStoreParamDto.getIdSchedule(), authorization);
    final List<Deliverable> deliverables = this.status.getDeliverablesByScheduleId(stepStoreParamDto.getIdSchedule());
    this.stepService.save(stepStoreParamDto);
    this.status.update(deliverables);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable final Long id, @Authorization final String authorization) {

    this.canAccessService.ensureCanEditResource(id, authorization);
    final List<Deliverable> deliverables = this.status.getDeliverablesByStepId(id);
    this.stepService.delete(id);
    this.status.update(deliverables);
    return ResponseEntity.ok().build();
  }

}
