package br.gov.es.openpmo.service.ccbmembers;

import br.gov.es.openpmo.dto.ccbmembers.CCBMemberRequest;

@FunctionalInterface
public interface IUpdateCCBMemberRelationshipService {

  void updateRelationship(CCBMemberRequest request);

}
