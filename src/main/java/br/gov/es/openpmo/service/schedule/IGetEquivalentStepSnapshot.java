package br.gov.es.openpmo.service.schedule;

import br.gov.es.openpmo.model.schedule.Step;

import java.util.Optional;

@FunctionalInterface
public interface IGetEquivalentStepSnapshot {

  Optional<Step> execute(Step master);

}
