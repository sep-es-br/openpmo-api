package br.gov.es.openpmo.controller.schedule;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.schedule.StepDto;
import br.gov.es.openpmo.dto.schedule.StepParamDto;
import br.gov.es.openpmo.dto.schedule.StepStoreParamDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.service.schedule.StepService;
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

@Api
@RestController
@CrossOrigin
@RequestMapping("/schedules/step")
public class StepController {

  private final StepService stepService;

  @Autowired
  public StepController(final StepService stepService) {
    this.stepService = stepService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseBase<StepDto>> findById(@PathVariable final Long id) {
    final Step step = this.findByIdImpl(id);
    final StepDto stepDto = this.mapToStepDto(step);
    return ResponseEntity.ok(ResponseBase.of(stepDto));
  }

  private Step findByIdImpl(final Long id) {
    return this.stepService.findById(id);
  }

  private StepDto mapToStepDto(final Step step) {
    return this.stepService.mapToStepDto(step);
  }

  @PutMapping
  public ResponseEntity<ResponseBase<EntityDto>> update(@RequestBody @Valid final StepParamDto stepParamDto) {
    final Step step = this.updateImpl(stepParamDto);
    final EntityDto entityDto = getEntityDto(step);
    return ResponseEntity.ok(ResponseBase.of(entityDto));
  }

  private Step updateImpl(final StepParamDto stepParamDto) {
    return this.stepService.update(stepParamDto);
  }

  private static EntityDto getEntityDto(final Entity schedule) {
    final Long idSchedule = getId(schedule);
    return new EntityDto(idSchedule);
  }

  public static Long getId(final Entity entity) {
    return entity.getId();
  }

  @PostMapping
  public ResponseEntity<Void> save(@Valid @RequestBody final StepStoreParamDto stepStoreParamDto) {
    this.saveImpl(stepStoreParamDto);
    return ResponseEntity.ok().build();
  }

  private void saveImpl(final StepStoreParamDto stepStoreParamDto) {
    this.stepService.save(stepStoreParamDto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable final Long id) {
    this.deleteImpl(id);
    return ResponseEntity.ok().build();
  }

  private void deleteImpl(final Long id) {
    this.stepService.delete(id);
  }

}
