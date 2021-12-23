package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.GetAllBaselinesResponse;
import br.gov.es.openpmo.dto.baselines.GetAllCCBMemberBaselineResponse;

import java.util.List;

public interface IGetAllBaselinesService {

  List<GetAllBaselinesResponse> getAllByWorkpackId(Long idWorkpack);

  List<GetAllCCBMemberBaselineResponse> getAllByPersonId(Long idPerson);

}
