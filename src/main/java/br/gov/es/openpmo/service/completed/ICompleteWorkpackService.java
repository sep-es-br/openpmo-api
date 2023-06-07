package br.gov.es.openpmo.service.completed;

import br.gov.es.openpmo.dto.completed.CompleteWorkpackRequest;

public interface ICompleteWorkpackService {

  void apply(
    Long idWorkpack,
    CompleteWorkpackRequest request
  );

}
