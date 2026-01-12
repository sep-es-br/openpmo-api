package br.gov.es.openpmo.service.completed;

import br.gov.es.openpmo.dto.completed.CompleteWorkpackRequest;
import br.gov.es.openpmo.model.workpacks.Workpack;

public interface ICompleteWorkpackService {

  void apply(
    Long idWorkpack,
    CompleteWorkpackRequest request
  );

  void onWorkpackCreated(Workpack workpack);

  void onWorkpackDeleted(Workpack workpack);

  void onWorkpackRestore(Long workpackId);

}
