package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpack.EndDeliverableManagementRequest;

public interface IDeliverableEndManagement {
  void execute(Long idDeliverable, EndDeliverableManagementRequest request);
}
