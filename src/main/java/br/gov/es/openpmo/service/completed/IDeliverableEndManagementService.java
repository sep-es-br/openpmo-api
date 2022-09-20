package br.gov.es.openpmo.service.completed;

import br.gov.es.openpmo.dto.workpack.EndDeliverableManagementRequest;

public interface IDeliverableEndManagementService {

  void apply(
    Long idDeliverable,
    EndDeliverableManagementRequest request
  );

}
