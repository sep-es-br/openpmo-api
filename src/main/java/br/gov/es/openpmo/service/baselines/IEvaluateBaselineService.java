package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.BaselineEvaluationRequest;

@FunctionalInterface
public interface IEvaluateBaselineService {

  void evaluate(
    Long idPerson,
    Long idBaseline,
    BaselineEvaluationRequest request
  );

}
