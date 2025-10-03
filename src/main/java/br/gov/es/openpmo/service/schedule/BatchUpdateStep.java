package br.gov.es.openpmo.service.schedule;

import br.gov.es.openpmo.dto.schedule.StepUpdateDto;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Component
@Validated
public class BatchUpdateStep {

  private final UpdateStep updateStep;
  private final UpdateStatusService updateStatusService;

  public BatchUpdateStep(final UpdateStep updateStep, UpdateStatusService updateStatusService) {this.updateStep = updateStep;
    this.updateStatusService = updateStatusService;
  }

  public List<Long> execute(final Iterable<? extends StepUpdateDto> steps, final Long idSchedule) {
    final List<Long> updatedSteps = new ArrayList<>();

    for (final StepUpdateDto step : steps) {
      final Step updatedStep = this.updateStep(step);
      updatedSteps.add(updatedStep.getId());
  }

    final List<Deliverable> deliverables = this.updateStatusService.getDeliverablesByScheduleId(idSchedule);
    this.updateStatusService.update(deliverables);

    return updatedSteps;
  }

  private Step updateStep(final StepUpdateDto step) {
    return this.updateStep.execute(step, false);
  }

  public List<Long> executeRestricted(final Iterable<? extends StepUpdateDto> steps, final Long idSchedule) {
    List<Long> updatedSteps = new ArrayList<>();

    for (StepUpdateDto stepDto : steps) {
        Step updatedStep = this.updateStepRestricted(stepDto);
        updatedSteps.add(updatedStep.getId());
    }

    List<Deliverable> deliverables = this.updateStatusService.getDeliverablesByScheduleId(idSchedule);
    this.updateStatusService.update(deliverables);

    return updatedSteps;
  }

  private Step updateStepRestricted(final StepUpdateDto step) {
    return this.updateStep.executeRestricted(step, false);
  }

}
