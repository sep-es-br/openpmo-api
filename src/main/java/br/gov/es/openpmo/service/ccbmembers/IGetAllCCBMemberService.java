package br.gov.es.openpmo.service.ccbmembers;

import br.gov.es.openpmo.dto.ccbmembers.CCBMemberResponse;

import java.util.List;

@FunctionalInterface
public interface IGetAllCCBMemberService {

  List<CCBMemberResponse> getAll(Long workpackId);

}
