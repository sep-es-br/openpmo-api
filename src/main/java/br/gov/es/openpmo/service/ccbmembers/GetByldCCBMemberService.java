package br.gov.es.openpmo.service.ccbmembers;

import br.gov.es.openpmo.dto.ccbmembers.CCBMemberResponse;
import br.gov.es.openpmo.dto.ccbmembers.MemberAs;
import br.gov.es.openpmo.dto.person.RoleResource;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;
import br.gov.es.openpmo.model.relations.IsCCBMemberFor;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import br.gov.es.openpmo.service.permissions.IRemoteRolesFetcher;
import br.gov.es.openpmo.service.permissions.RoleService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GetByldCCBMemberService implements IGetByIdCCBMemberService {

  private final IsCCBMemberRepository ccbMemberRepository;
  private final IRemoteRolesFetcher remoteRolesFetcher;
  private final RoleService roleService;

  @Autowired
  public GetByldCCBMemberService(
    final IsCCBMemberRepository ccbMemberRepository,
    final IRemoteRolesFetcher remoteRolesFetcher,
    final RoleService roleService
  ) {
    this.ccbMemberRepository = ccbMemberRepository;
    this.remoteRolesFetcher = remoteRolesFetcher;
    this.roleService = roleService;
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

  private List<IsCCBMemberFor> findByPersonIdAndWorkpackIdAndPlanId(
    final Long idPerson,
    final Long idWorkpack,
    final Long idPlan
  ) {
    return this.ccbMemberRepository.findByPersonIdAndWorkpackIdAndPlanId(idPerson, idWorkpack, idPlan);
  }

  private CCBMemberResponse getCCBMemberResponse(final IsCCBMemberFor ccbMember) {
    final List<MemberAs> memberAs = this.getMemberAs(ccbMember);

    final CCBMemberResponse ccbMemberResponse = new CCBMemberResponse(
      ccbMember.getPersonResponse(),
      memberAs,
      memberAs.stream().anyMatch(MemberAs::getActive)
    );

    ccbMemberResponse.addAllRoles(this.remoteRolesFetcher.fetch(ccbMember.getIdPerson()));

    return ccbMemberResponse;
  }

  private List<MemberAs> getMemberAs(final IsCCBMemberFor ccbMember) {
    return this.findAllCCBMembersByPersonId(ccbMember.getIdPerson(), ccbMember.getWorkpackId())
      .stream()
      .map(IsCCBMemberFor::getMemberAs)
      .collect(Collectors.toList());
  }

  private List<IsCCBMemberFor> findAllCCBMembersByPersonId(
    final Long personId,
    final Long workpackId
  ) {
    List<IsCCBMemberFor> ccbMembers = this.ccbMemberRepository.findAllByPersonIdAndWorkpackId(personId, workpackId);
    String sub = null;

    if (!ccbMembers.isEmpty()) {
        Person person = ccbMembers.get(0).getPerson();

        if (person.getAuthentications() != null && !person.getAuthentications().isEmpty()) {
            sub = person.getAuthentications()
                        .stream()
                        .findFirst()
                        .map(IsAuthenticatedBy::getKey)
                        .orElse(null);
        }
    }

    final List<RoleResource> roles = this.roleService.getRolesBySub(personId, sub);

  List<IsCCBMemberFor> result = new ArrayList<>(ccbMembers);

  for (RoleResource role : roles) {

      boolean exists = ccbMembers.stream().anyMatch(m ->
          m.getRole().equals(role.getRole()) &&
          Objects.equals(m.getWorkLocation(), role.getWorkLocation())
      );

      if (!exists) {
          IsCCBMemberFor newMember = new IsCCBMemberFor();
          newMember.setRole(role.getRole());
          newMember.setWorkLocation(role.getWorkLocation());
          newMember.setActive(false);

          result.add(newMember);
      }
  }

  return result;
  }

}
