package br.gov.es.openpmo.controller.schedule;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.schedule.ScheduleDto;
import br.gov.es.openpmo.dto.schedule.ScheduleParamDto;
import br.gov.es.openpmo.dto.schedule.StepParamDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.service.schedule.ScheduleService;
import br.gov.es.openpmo.service.schedule.StepService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
@CrossOrigin
@RequestMapping("/schedules")
public class ScheduleController {

  private final ScheduleService scheduleService;
  private final StepService stepService;

  @Autowired
  public ScheduleController(final ScheduleService scheduleService, final StepService stepService) {
    this.scheduleService = scheduleService;
    this.stepService = stepService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<ScheduleDto>>> findAll(@RequestParam("id-workpack") final Long idWorkpack) {
    final List<ScheduleDto> schedules = this.scheduleService.findAll(idWorkpack);

    return schedules.isEmpty()
      ? ResponseEntity.noContent().build()
      : ResponseEntity.ok(ResponseBase.of(schedules));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseBase<ScheduleDto>> findById(@PathVariable final Long id) {
    final Schedule schedule = this.scheduleService.findById(id);
    final ScheduleDto scheduleDto = this.scheduleService.mapsToSheduleDto(schedule);
    return ResponseEntity.ok(ResponseBase.of(scheduleDto));
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> save(@Valid @RequestBody final ScheduleParamDto scheduleParamDto) {
    final Schedule schedule = this.scheduleService.save(scheduleParamDto);
    return ResponseEntity.ok(ResponseBase.of(getEntityDto(schedule)));
  }

  private static EntityDto getEntityDto(final Entity schedule) {
    return new EntityDto(getId(schedule));
  }

  public static Long getId(final Entity entity) {
    return entity.getId();
  }

  @PutMapping
  public ResponseEntity<ResponseBase<EntityDto>> update(@RequestBody @Valid final StepParamDto stepParamDto) {
    final Step step = this.stepService.update(stepParamDto);
    return ResponseEntity.ok(ResponseBase.of(getEntityDto(step)));
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(@PathVariable final Long id) {
    this.scheduleService.delete(id);
    return ResponseEntity.ok().build();
  }

}
