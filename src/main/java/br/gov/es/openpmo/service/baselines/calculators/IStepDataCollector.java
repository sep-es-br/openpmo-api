package br.gov.es.openpmo.service.baselines.calculators;

import br.gov.es.openpmo.dto.baselines.ccbmemberview.StepCollectedData;
import br.gov.es.openpmo.model.schedule.Step;

@FunctionalInterface
public interface IStepDataCollector {

  StepCollectedData collect(
    final Long idBaseline,
    final Long idBaselineReference,
    final Iterable<? extends Step> steps
  );

}
