package br.gov.es.openpmo.service.ccbmembers;

import br.gov.es.openpmo.dto.ccbmembers.CCBMemberResponse;
import br.gov.es.openpmo.dto.ccbmembers.MemberAs;
import br.gov.es.openpmo.model.relations.IsCCBMemberFor;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class GetAllCCBMemberService implements IGetAllCCBMemberService {

  private final IsCCBMemberRepository ccbMemberRepository;

  @Autowired
  public GetAllCCBMemberService(final IsCCBMemberRepository ccbMemberRepository) {
    this.ccbMemberRepository = ccbMemberRepository;
  }

  @Override
  public List<CCBMemberResponse> getAll(final Long workpackId) {
    return this.findAllCCBMmembersByWorkpackId(workpackId)
        .stream()
        .map(this::getCCBMemberResponse)
        .filter(distinctByKey(CCBMemberResponse::getPersonId))
        .collect(Collectors.toList());
  }

  private static <T> Predicate<T> distinctByKey(final Function<? super T, ?> keyExtractor) {
    final Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }

  private List<IsCCBMemberFor> findAllCCBMmembersByWorkpackId(final Long workpackId) {
    return this.ccbMemberRepository.findAllByWorkpackId(workpackId);
  }

  private CCBMemberResponse getCCBMemberResponse(final IsCCBMemberFor ccbMember) {
    final List<MemberAs> memberAs = this.getMemberAs(ccbMember);

    return new CCBMemberResponse(
        ccbMember.getPersonResponse(),
        memberAs,
        memberAs.stream().anyMatch(MemberAs::getActive)
    );
  }

  private List<MemberAs> getMemberAs(final IsCCBMemberFor ccbMember) {
    return this.findByPersonIdAndWorkpackId(ccbMember)
        .stream()
        .map(IsCCBMemberFor::getMemberAs)
        .collect(Collectors.toList());
  }

  private List<IsCCBMemberFor> findByPersonIdAndWorkpackId(final IsCCBMemberFor ccbMember) {
    return this.ccbMemberRepository.findByPersonIdAndWorkpackId(
        ccbMember.getIdPerson(),
        ccbMember.getWorkpackId()
    );
  }

}
