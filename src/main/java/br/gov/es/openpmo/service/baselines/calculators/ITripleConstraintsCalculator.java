package br.gov.es.openpmo.service.baselines.calculators;

import br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintOutput;

@FunctionalInterface
public interface ITripleConstraintsCalculator {

  TripleConstraintOutput calculate(Long idBaseline);


}
