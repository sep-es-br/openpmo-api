package br.gov.es.openpmo.service.ccbmembers;

import br.gov.es.openpmo.dto.ccbmembers.CCBMemberResponse;

@FunctionalInterface
public interface IGetByIdCCBMemberService {

  CCBMemberResponse getById(
    Long idPerson,
    Long idWorkpack,
    Long idPlan
  );

}
