package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.BaselineOfficeMember;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.baselines.Decision;
import br.gov.es.openpmo.model.relations.IsEvaluatedBy;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import br.gov.es.openpmo.repository.IsEvaluatedByRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class BaselineApprovalMembersService {

  private final IsCCBMemberRepository memberRepository;
  private final IsEvaluatedByRepository evaluationRepository;

  public BaselineApprovalMembersService(
    final IsCCBMemberRepository memberRepository,
    final IsEvaluatedByRepository evaluationRepository
  ) {
    this.memberRepository = memberRepository;
    this.evaluationRepository = evaluationRepository;
  }

  public Set<Person> findActiveMembers(final Long idBaseline) {
    return this.memberRepository.findAllActiveMembersOfBaseline(idBaseline);
  }

  public boolean hasAllRequiredApprovals(final Long idBaseline) {
    final Set<Long> memberIds = this.findActiveMembers(idBaseline).stream()
      .map(Person::getId)
      .collect(Collectors.toSet());
    if (memberIds.isEmpty()) {
      return false;
    }

    final Set<Long> approvedIds = this.evaluationRepository.findAllEvaluations(idBaseline).stream()
      .filter(evaluation -> evaluation.getDecision() == Decision.APPROVED)
      .map(IsEvaluatedBy::getIdPerson)
      .collect(Collectors.toSet());
    if (!approvedIds.containsAll(memberIds)) {
      return false;
    }

    final List<Long> requiredOffices = this.memberRepository.findRequiredOfficeIdsOfBaseline(idBaseline);
    if (requiredOffices.isEmpty()) {
      return false;
    }
    if (requiredOffices.size() == 1) {
      return true;
    }

    // A vote is recorded per person. A member associated with two offices can represent both.
    final Set<Long> officesWithApproval = this.memberRepository.findOfficeMembersOfBaseline(idBaseline).stream()
      .filter(member -> memberIds.contains(member.getIdPerson()) && approvedIds.contains(member.getIdPerson()))
      .map(BaselineOfficeMember::getIdOffice)
      .collect(Collectors.toSet());
    return officesWithApproval.containsAll(requiredOffices);
  }
}
