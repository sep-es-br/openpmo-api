package br.gov.es.openpmo.service.ccbmembers;

import br.gov.es.openpmo.dto.ccbmembers.CCBMemberResponse;
import br.gov.es.openpmo.dto.ccbmembers.MemberAs;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.relations.IsCCBMember;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import br.gov.es.openpmo.service.permissions.IRemoteRolesFetcher;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetByldCCBMemberService implements IGetByIdCCBMemberService {

  private final IsCCBMemberRepository ccbMemberRepository;
  private final IRemoteRolesFetcher remoteRolesFetcher;

  @Autowired
  public GetByldCCBMemberService(
    final IsCCBMemberRepository ccbMemberRepository,
    final IRemoteRolesFetcher remoteRolesFetcher
  ) {
    this.ccbMemberRepository = ccbMemberRepository;
    this.remoteRolesFetcher = remoteRolesFetcher;
  }

  @Override
  public CCBMemberResponse getById(
    final Long idPerson,
    final Long idWorkpack,
    final Long idPlan
  ) {
    return this.findByPersonIdAndWorkpackIdAndPlanId(idPerson, idWorkpack, idPlan)
      .stream()
      .findAny()
      .map(this::getCCBMemberResponse)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.CCB_MEMBER_NOT_FOUND));
  }

  private List<IsCCBMember> findByPersonIdAndWorkpackIdAndPlanId(
    final Long idPerson,
    final Long idWorkpack,
    final Long idPlan
  ) {
    return this.ccbMemberRepository.findByPersonIdAndWorkpackIdAndPlanId(idPerson, idWorkpack, idPlan);
  }

  private CCBMemberResponse getCCBMemberResponse(final IsCCBMember ccbMember) {
    final List<MemberAs> memberAs = this.getMemberAs(ccbMember);

    final CCBMemberResponse ccbMemberResponse = new CCBMemberResponse(
      ccbMember.getPersonResponse(),
      memberAs,
      memberAs.stream().anyMatch(MemberAs::getActive)
    );

    ccbMemberResponse.addAllRoles(this.remoteRolesFetcher.fetch(ccbMember.getIdPerson()));

    return ccbMemberResponse;
  }

  private List<MemberAs> getMemberAs(final IsCCBMember ccbMember) {
    return this.findAllCCBMembersByPersonId(ccbMember.getIdPerson(), ccbMember.getWorkpackId())
      .stream()
      .map(IsCCBMember::getMemberAs)
      .collect(Collectors.toList());
  }

  private List<IsCCBMember> findAllCCBMembersByPersonId(final Long personId, final Long workpackId) {
    return this.ccbMemberRepository.findAllByPersonIdAndWorkpackId(personId, workpackId);
  }

}
