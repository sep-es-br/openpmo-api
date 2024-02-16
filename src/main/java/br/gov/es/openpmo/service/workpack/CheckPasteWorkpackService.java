package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpack.WorkpackPasteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckPasteWorkpackService {

  @Autowired
  public CheckPasteWorkpackService() {
  }

  public WorkpackPasteResponse checksIfCanPasteWorkpack(
    final Long idWorkpackModelTo,
    final Long idWorkpackModelFrom
  ) {
    WorkpackPasteResponse response = new WorkpackPasteResponse(false,true);

    if(idWorkpackModelFrom.equals(idWorkpackModelTo)) {
      response.setCanPaste(true);
    }

    return response;
  }


}
