package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.IncludeBaselineRequest;

@FunctionalInterface
public interface ICreateBaselineService {

  Long create(
    IncludeBaselineRequest request,
    Long idPerson
  );

}
