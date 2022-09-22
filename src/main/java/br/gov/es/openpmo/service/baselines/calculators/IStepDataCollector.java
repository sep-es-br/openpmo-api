package br.gov.es.openpmo.service.baselines.calculators;

import br.gov.es.openpmo.dto.baselines.ccbmemberview.StepCollectedData;

@FunctionalInterface
public interface IStepDataCollector {

  StepCollectedData collect(
    final Long idBaseline,
    final Long idBaselineReference,
    final Long idSchedule
  );

}
