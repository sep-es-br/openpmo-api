package br.gov.es.openpmo.service.schedule;

import br.gov.es.openpmo.dto.schedule.StepUpdateDto;
import br.gov.es.openpmo.model.schedule.Step;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Component
@Validated
public class BatchUpdateStep {

  private final UpdateStep updateStep;

  public BatchUpdateStep(final UpdateStep updateStep) {this.updateStep = updateStep;}

  public List<Long> execute(final Iterable<? extends StepUpdateDto> steps) {
    final List<Long> updatedSteps = new ArrayList<>();

    for (final StepUpdateDto step : steps) {
      final Step updatedStep = this.updateStep(step);
      updatedSteps.add(updatedStep.getId());
    }

    return updatedSteps;
  }

  private Step updateStep(final StepUpdateDto step) {
    return this.updateStep.execute(step);
  }

}
