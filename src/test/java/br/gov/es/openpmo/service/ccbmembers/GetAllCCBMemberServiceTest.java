package br.gov.es.openpmo.service.ccbmembers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.gov.es.openpmo.dto.ccbmembers.CCBMemberLevelResult;
import br.gov.es.openpmo.dto.ccbmembers.CCBMemberResponse;
import br.gov.es.openpmo.dto.ccbmembers.PersonResponse;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class GetAllCCBMemberServiceTest {

  private IsCCBMemberRepository repository;
  private GetAllCCBMemberService service;

  @Before
  public void setUp() {
    this.repository = mock(IsCCBMemberRepository.class);
    this.service = new GetAllCCBMemberService(this.repository);
  }

  @Test
  public void shouldReturnPlanMemberWithoutWorkpackMember() {
    final Long workpackId = 2572L;
    final CCBMemberLevelResult planMember = levelResult(10L, "PLAN", 20L, true);

    when(this.repository.findAllByWorkpackId(workpackId)).thenReturn(Collections.emptyList());
    when(this.repository.findAllPlanAndOfficeMembersByWorkpackId(workpackId))
      .thenReturn(Collections.singletonList(planMember));

    final List<CCBMemberResponse> result = this.service.getAll(workpackId);

    assertEquals(1, result.size());
    assertEquals(Long.valueOf(10L), result.get(0).getPersonId());
    assertEquals(workpackId, result.get(0).getIdWorkpack());
    assertEquals("PLAN", result.get(0).getMemberAs().get(0).getLevel());
    assertTrue(result.get(0).getActive());
  }

  @Test
  public void shouldCountDuplicateAuthorizationsInTheSamePlanOnlyOnce() {
    final Long workpackId = 2572L;
    final CCBMemberLevelResult inactive = levelResult(10L, "PLAN", 20L, false);
    final CCBMemberLevelResult active = levelResult(10L, "PLAN", 20L, true);

    when(this.repository.findAllByWorkpackId(workpackId)).thenReturn(Collections.emptyList());
    when(this.repository.findAllPlanAndOfficeMembersByWorkpackId(workpackId))
      .thenReturn(Arrays.asList(inactive, active));

    final List<CCBMemberResponse> result = this.service.getAll(workpackId);

    assertEquals(1, result.size());
    assertEquals(1, result.get(0).getMemberAs().size());
    assertTrue(result.get(0).getMemberAs().get(0).getActive());
  }

  private static CCBMemberLevelResult levelResult(
    final Long personId,
    final String level,
    final Long levelId,
    final Boolean active
  ) {
    final PersonResponse personResponse = new PersonResponse();
    personResponse.setId(personId);
    personResponse.setName("Person");

    final Person person = mock(Person.class);
    when(person.getPersonResponse()).thenReturn(personResponse);

    final CCBMemberLevelResult result = new CCBMemberLevelResult();
    result.setIdPerson(personId);
    result.setPerson(person);
    result.setLevel(level);
    result.setIdLevel(levelId);
    result.setLevelName("Plan");
    result.setRole("role");
    result.setActive(active);
    return result;
  }
}
