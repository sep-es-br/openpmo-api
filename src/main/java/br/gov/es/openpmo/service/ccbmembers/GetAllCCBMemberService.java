package br.gov.es.openpmo.service.ccbmembers;

import br.gov.es.openpmo.dto.ccbmembers.CCBMemberLevelResult;
import br.gov.es.openpmo.dto.ccbmembers.CCBMemberResponse;
import br.gov.es.openpmo.dto.ccbmembers.MemberAs;
import br.gov.es.openpmo.model.relations.IsCCBMemberFor;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetAllCCBMemberService implements IGetAllCCBMemberService {

  private final IsCCBMemberRepository ccbMemberRepository;

  @Autowired
  public GetAllCCBMemberService(final IsCCBMemberRepository ccbMemberRepository) {
    this.ccbMemberRepository = ccbMemberRepository;
  }

  @Override
  public List<CCBMemberResponse> getAll(final Long workpackId) {
    final List<CCBMemberLevelResult> planAndOfficeResults =
      this.ccbMemberRepository.findAllPlanAndOfficeMembersByWorkpackId(workpackId);

    return this.findAllCCBMmembersByWorkpackId(workpackId)
      .stream()
      .map(ccbMember -> this.getCCBMemberResponse(ccbMember, planAndOfficeResults))
      .filter(distinctByKeyAndWorkpack(CCBMemberResponse::getPersonId, CCBMemberResponse::getIdWorkpack))
      .collect(Collectors.toList());
  }

  private static <T> Predicate<T> distinctByKeyAndWorkpack(final Function<? super T, ?> keyExtractor, final Function<? super T, ?> workpackExtractor) {

      final Set<Object> seen = ConcurrentHashMap.newKeySet();

      return t -> {

          Object key = keyExtractor.apply(t);
          Object workpack = workpackExtractor.apply(t);

          Object compositeKey = Arrays.asList(key, workpack);

      return seen.add(compositeKey);
    };
  }

  private static <T> Predicate<T> distinctByKey(final Function<? super T, ?> keyExtractor) {
    final Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }

  private List<IsCCBMemberFor> findAllCCBMmembersByWorkpackId(final Long workpackId) {
    return this.ccbMemberRepository.findAllByWorkpackId(workpackId);
  }

  private CCBMemberResponse getCCBMemberResponse(
    final IsCCBMemberFor ccbMember,
    final List<CCBMemberLevelResult> planAndOfficeResults
  ) {
    final List<MemberAs> memberAs = this.getMemberAs(ccbMember, planAndOfficeResults);

    return new CCBMemberResponse(
      ccbMember.getWorkpackId(),
      ccbMember.getPersonResponse(),
      memberAs,
      memberAs.stream().anyMatch(MemberAs::getActive)
    );
  }

  private List<MemberAs> getMemberAs(
    final IsCCBMemberFor ccbMember,
    final List<CCBMemberLevelResult> planAndOfficeResults
  ) {
    final List<MemberAs> workpackLevel = this.findByPersonIdAndWorkpackId(ccbMember)
      .stream()
      .map(IsCCBMemberFor::getMemberAs)
      .collect(Collectors.toList());

    final List<MemberAs> planAndOfficeLevel = planAndOfficeResults.stream()
      .filter(r -> r.getIdPerson() != null && r.getIdPerson().equals(ccbMember.getIdPerson()))
      .map(r -> new MemberAs(r.getRole(), r.getWorkLocation(), r.getActive(), r.getLevel(), r.getLevelName()))
      .collect(Collectors.toList());

    return Stream.concat(workpackLevel.stream(), planAndOfficeLevel.stream())
      .collect(Collectors.toList());
  }

  private List<IsCCBMemberFor> findByPersonIdAndWorkpackId(final IsCCBMemberFor ccbMember) {
    return this.ccbMemberRepository.findByPersonIdAndWorkpackId(
      ccbMember.getIdPerson(),
      ccbMember.getWorkpackId()
    );
  }

}