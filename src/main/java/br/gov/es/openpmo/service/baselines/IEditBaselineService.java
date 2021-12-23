package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.EditDraftBaselineRequest;

@FunctionalInterface
public interface IEditBaselineService {

  void edit(
    Long idBaseline,
    EditDraftBaselineRequest request
  );

}
