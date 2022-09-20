package br.gov.es.openpmo.service.completed;

import br.gov.es.openpmo.dto.completed.CompleteDeliverableRequest;

public interface ICompleteDeliverableService {

  void apply(
    Long idWorkpack,
    CompleteDeliverableRequest request
  );

}
