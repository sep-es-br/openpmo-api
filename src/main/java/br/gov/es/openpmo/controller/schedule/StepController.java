package br.gov.es.openpmo.controller.schedule;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.schedule.StepDto;
import br.gov.es.openpmo.dto.schedule.StepStoreParamDto;
import br.gov.es.openpmo.dto.schedule.StepUpdateDto;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.service.schedule.StepService;
import br.gov.es.openpmo.service.schedule.UpdateStatusService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
@CrossOrigin
@RequestMapping("/schedules/step")
public class StepController {

    private final StepService stepService;

    private final UpdateStatusService status;

    @Autowired
    public StepController(final StepService stepService, UpdateStatusService status) {
        this.stepService = stepService;
        this.status = status;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBase<StepDto>> findById(@PathVariable final Long id) {
        final Step step = this.stepService.findById(id);
        final StepDto stepDto = this.stepService.mapToStepDto(step);
        return ResponseEntity.ok(ResponseBase.of(stepDto));
    }

    @PutMapping
    public ResponseEntity<ResponseBase<EntityDto>> update(@RequestBody @Valid final StepUpdateDto stepUpdateDto) {
        final Step step = this.stepService.update(stepUpdateDto);
        final List<Deliverable> deliverables = this.status.getDeliverablesByStepId(step.getId());
        this.status.update(deliverables);
        final Long idSchedule = step.getId();
        final EntityDto entityDto = new EntityDto(idSchedule);
        return ResponseEntity.ok(ResponseBase.of(entityDto));
    }

    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody final StepStoreParamDto stepStoreParamDto) {
        final List<Deliverable> deliverables = this.status.getDeliverablesByScheduleId(stepStoreParamDto.getIdSchedule());
        this.stepService.save(stepStoreParamDto);
        this.status.update(deliverables);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        final List<Deliverable> deliverables = this.status.getDeliverablesByStepId(id);
        this.stepService.delete(id);
        this.status.update(deliverables);
        return ResponseEntity.ok().build();
    }

}
