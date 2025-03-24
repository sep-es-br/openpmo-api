package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.GetAllBaselinesResponse;
import br.gov.es.openpmo.enumerator.BaselineViewStatus;

import java.util.List;

public interface IGetAllBaselinesService {

  List<GetAllBaselinesResponse> getAllByWorkpackId(Long idWorkpack);

  List<GetAllBaselinesResponse> getAllByPersonIdAndStatus(
    Long idPerson,
    Long idFilter,
    String term,
    BaselineViewStatus status
  );

}
