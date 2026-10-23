package br.gov.es.openpmo.service.baselines;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.gov.es.openpmo.dto.baselines.BaselineOfficeMember;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.baselines.Decision;
import br.gov.es.openpmo.model.relations.IsEvaluatedBy;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import br.gov.es.openpmo.repository.IsEvaluatedByRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class BaselineApprovalMembersServiceTest {

  private final Long baselineId = 42L;
  private IsCCBMemberRepository members;
  private IsEvaluatedByRepository evaluations;
  private BaselineApprovalMembersService service;

  @Before
  public void setUp() {
    this.members = mock(IsCCBMemberRepository.class);
    this.evaluations = mock(IsEvaluatedByRepository.class);
    this.service = new BaselineApprovalMembersService(this.members, this.evaluations);
  }

  @Test
  public void requiresApprovalFromOriginAndEveryEditConsumer() {
    activeMembers(1L, 2L, 3L);
    requiredOffices(10L, 20L, 30L);
    officeMembers(member(1L, 10L), member(2L, 20L), member(3L, 30L));
    approvedEvaluations(1L, 2L);

    assertFalse(this.service.hasAllRequiredApprovals(this.baselineId));

    approvedEvaluations(1L, 2L, 3L);
    assertTrue(this.service.hasAllRequiredApprovals(this.baselineId));
  }

  @Test
  public void doesNotApproveWhenAnOfficeHasNoRepresentative() {
    activeMembers(1L);
    requiredOffices(10L, 20L);
    officeMembers(member(1L, 10L));
    approvedEvaluations(1L);

    assertFalse(this.service.hasAllRequiredApprovals(this.baselineId));
  }

  @Test
  public void doesNotApproveWithoutOriginRepresentative() {
    activeMembers(2L);
    requiredOffices(10L, 20L);
    officeMembers(member(2L, 20L));
    approvedEvaluations(2L);

    assertFalse(this.service.hasAllRequiredApprovals(this.baselineId));
  }

  @Test
  public void onePersonCanRepresentBothOffices() {
    activeMembers(1L);
    requiredOffices(10L, 20L);
    officeMembers(member(1L, 10L), member(1L, 20L));
    approvedEvaluations(1L);

    assertTrue(this.service.hasAllRequiredApprovals(this.baselineId));
  }

  @Test
  public void keepsExistingRuleForProjectWithoutEditConsumer() {
    activeMembers(1L);
    requiredOffices(10L);
    approvedEvaluations(1L);

    assertTrue(this.service.hasAllRequiredApprovals(this.baselineId));
  }

  @Test
  public void rejectedVoteCannotSatisfyApproval() {
    activeMembers(1L, 2L);
    requiredOffices(10L, 20L);
    officeMembers(member(1L, 10L), member(2L, 20L));
    final IsEvaluatedBy rejected = evaluation(2L, Decision.REJECTED);
    final IsEvaluatedBy approved = evaluation(1L, Decision.APPROVED);
    when(this.evaluations.findAllEvaluations(this.baselineId))
      .thenReturn(new HashSet<>(Arrays.asList(approved, rejected)));

    assertFalse(this.service.hasAllRequiredApprovals(this.baselineId));
  }

  private void activeMembers(final Long... ids) {
    final Set<Person> people = new HashSet<>();
    for (final Long id : ids) {
      final Person person = mock(Person.class);
      when(person.getId()).thenReturn(id);
      people.add(person);
    }
    when(this.members.findAllActiveMembersOfBaseline(this.baselineId)).thenReturn(people);
  }

  private void requiredOffices(final Long... ids) {
    when(this.members.findRequiredOfficeIdsOfBaseline(this.baselineId)).thenReturn(Arrays.asList(ids));
  }

  private void officeMembers(final BaselineOfficeMember... people) {
    when(this.members.findOfficeMembersOfBaseline(this.baselineId)).thenReturn(Arrays.asList(people));
  }

  private void approvedEvaluations(final Long... ids) {
    final Set<IsEvaluatedBy> votes = new HashSet<>();
    for (final Long id : ids) {
      votes.add(evaluation(id, Decision.APPROVED));
    }
    when(this.evaluations.findAllEvaluations(this.baselineId)).thenReturn(votes);
  }

  private static BaselineOfficeMember member(final Long personId, final Long officeId) {
    final BaselineOfficeMember member = new BaselineOfficeMember();
    member.setIdPerson(personId);
    member.setIdOffice(officeId);
    return member;
  }

  private static IsEvaluatedBy evaluation(final Long personId, final Decision decision) {
    final IsEvaluatedBy evaluation = mock(IsEvaluatedBy.class);
    when(evaluation.getIdPerson()).thenReturn(personId);
    when(evaluation.getDecision()).thenReturn(decision);
    return evaluation;
  }
}
