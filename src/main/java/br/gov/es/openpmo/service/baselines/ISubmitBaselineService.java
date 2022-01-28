package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.SubmitBaselineRequest;

@FunctionalInterface
public interface ISubmitBaselineService {

  void submit(
      Long idBaseline,
      SubmitBaselineRequest request,
      Long idPerson
  );

}
