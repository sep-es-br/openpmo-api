package br.gov.es.openpmo.service.ccbmembers;

import br.gov.es.openpmo.dto.ccbmembers.CCBMemberRequest;

@FunctionalInterface
public interface ICreateCCBMemberRelationshipService {

  void createRelationship(CCBMemberRequest request);

}
