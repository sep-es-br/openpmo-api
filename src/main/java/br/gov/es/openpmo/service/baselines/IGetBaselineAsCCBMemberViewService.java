package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.ccbmemberview.BaselineDetailCCBMemberResponse;

public interface IGetBaselineAsCCBMemberViewService {

  BaselineDetailCCBMemberResponse getById(
    Long idBaseline,
    Long idPerson
  );

}
