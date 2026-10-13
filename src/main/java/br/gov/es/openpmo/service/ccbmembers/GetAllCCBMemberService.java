package br.gov.es.openpmo.service.ccbmembers;

import br.gov.es.openpmo.dto.ccbmembers.CCBMemberLevelResult;
import br.gov.es.openpmo.dto.ccbmembers.CCBMemberResponse;
import br.gov.es.openpmo.dto.ccbmembers.MemberAs;
import br.gov.es.openpmo.model.relations.IsCCBMemberFor;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    final List<CCBMemberResponse> workpackMembers = this.findAllCCBMmembersByWorkpackId(workpackId)
      .stream()
      .map(this::getWorkpackMemberResponse)
      .filter(distinctByKeyAndWorkpack(CCBMemberResponse::getPersonId, CCBMemberResponse::getIdWorkpack))
      .collect(Collectors.toList());

    final List<CCBMemberResponse> planAndOfficeMembers = this.getPlanAndOfficeMemberResponses(
      workpackId,
      planAndOfficeResults
    );

    return Stream.concat(workpackMembers.stream(), planAndOfficeMembers.stream())
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

  private CCBMemberResponse getWorkpackMemberResponse(final IsCCBMemberFor ccbMember) {
    final List<MemberAs> memberAs = this.findByPersonIdAndWorkpackId(ccbMember)
      .stream()
      .map(IsCCBMemberFor::getMemberAs)
      .collect(Collectors.toList());

    return new CCBMemberResponse(
      ccbMember.getWorkpackId(),
      ccbMember.getPersonResponse(),
      memberAs,
      memberAs.stream().anyMatch(member -> Boolean.TRUE.equals(member.getActive()))
    );
  }

  private List<CCBMemberResponse> getPlanAndOfficeMemberResponses(
    final Long workpackId,
    final List<CCBMemberLevelResult> planAndOfficeResults
  ) {
    final Map<Long, List<CCBMemberLevelResult>> resultsByPerson = planAndOfficeResults.stream()
      .filter(result -> result.getIdPerson() != null && result.getPerson() != null)
      .collect(Collectors.groupingBy(
        CCBMemberLevelResult::getIdPerson,
        LinkedHashMap::new,
        Collectors.toList()
      ));

    return resultsByPerson.values().stream()
      .map(results -> {
        final List<MemberAs> memberAs = results.stream()
          .sorted((first, second) -> Boolean.compare(
            Boolean.TRUE.equals(second.getActive()),
            Boolean.TRUE.equals(first.getActive())
          ))
          .filter(distinctByKey(result -> Arrays.asList(result.getLevel(), result.getIdLevel())))
          .map(result -> new MemberAs(
            result.getRole(),
            result.getWorkLocation(),
            result.getActive(),
            result.getLevel(),
            result.getLevelName(),
            result.getIdLevel()
          ))
          .collect(Collectors.toList());

        return new CCBMemberResponse(
          workpackId,
          results.get(0).getPerson().getPersonResponse(),
          memberAs,
          memberAs.stream().anyMatch(member -> Boolean.TRUE.equals(member.getActive()))
        );
      })
      .collect(Collectors.toList());
  }

  private List<IsCCBMemberFor> findByPersonIdAndWorkpackId(final IsCCBMemberFor ccbMember) {
    return this.ccbMemberRepository.findByPersonIdAndWorkpackId(
      ccbMember.getIdPerson(),
      ccbMember.getWorkpackId()
    );
  }

}
