package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.BaselineEvaluationRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.baselines.Status;
import br.gov.es.openpmo.model.relations.IsEvaluatedBy;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import br.gov.es.openpmo.repository.IsEvaluatedByRepository;
import br.gov.es.openpmo.service.journals.JournalCreator;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static br.gov.es.openpmo.model.baselines.Decision.APPROVED;
import static br.gov.es.openpmo.model.baselines.Decision.REJECTED;
import static br.gov.es.openpmo.service.baselines.EvaluateBaselineServiceTest.BaselineApprove.*;
import static br.gov.es.openpmo.utils.ApplicationMessage.NOT_VALID_CCB_MEMBER;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class EvaluateBaselineServiceTest {

  @Mock Person member1;
  @Mock Person member2;
  @Mock IsCCBMemberRepository ccbMemberRepository;
  @Mock BaselineRepository repository;
  @Mock IsEvaluatedByRepository evaluatedByRepository;
  @Mock Baseline baseline;

  @Mock
  Baseline activeBaseline;

  @Mock
  JournalCreator journalCreator;

  private EvaluateBaselineService service;

  @BeforeEach
  void setUp() {
    this.service = new EvaluateBaselineService(
        this.repository,
        this.ccbMemberRepository,
        this.evaluatedByRepository,
        this.journalCreator
    );
  }

  @Test
  void shouldThrowExceptionIfBaselineIsNotProposed() {
    this.givenBaselineIsDraft();

    assertThatThrownBy(() -> this.service.evaluate(1L, 2L, null))
      .isInstanceOf(NegocioException.class)
      .hasMessage(ApplicationMessage.BASELINE_IS_NOT_PROPOSED_INVALID_STATE_ERROR);

    verify(this.repository, times(1)).findById(2L);
    verify(this.ccbMemberRepository, never()).findAllActiveMembersOfBaseline(2L);
    verify(this.evaluatedByRepository, never()).save(isA(IsEvaluatedBy.class));
    verify(this.evaluatedByRepository, never()).findEvaluation(2L, 1L);

  }

  private void givenBaselineIsDraft() {
    when(this.repository.findById(2L)).thenReturn(Optional.of(this.baseline));
    when(this.baseline.getStatus()).thenReturn(Status.DRAFT);
  }

  @Test
  void shouldNotEvaluateWhenNonCCBMember() {
    final BaselineEvaluationRequest request = new BaselineEvaluationRequest("", APPROVED);

    this.givenUserIsNotCCBMember();

    assertThatThrownBy(() -> this.service.evaluate(1L, 2L, request))
      .isInstanceOf(NegocioException.class)
      .hasMessage(NOT_VALID_CCB_MEMBER);

    verify(this.repository, times(1)).findById(2L);
    verify(this.ccbMemberRepository, times(1)).findAllActiveMembersOfBaseline(2L);
    verify(this.evaluatedByRepository, never()).save(isA(IsEvaluatedBy.class));
    verify(this.evaluatedByRepository, never()).findEvaluation(2L, 1L);
  }

  private void givenUserIsNotCCBMember() {
    when(this.baseline.getStatus()).thenReturn(Status.PROPOSED);
    when(this.member1.getId()).thenReturn(3L);
    when(this.repository.findById(2L)).thenReturn(Optional.of(this.baseline));
    when(this.ccbMemberRepository.findAllActiveMembersOfBaseline(2L)).thenReturn(singletonList(
      this.member1));
  }

  @Nested class BaselineApprove {

    static final long ID_MEMBER = 1L;
    static final long ID_BASELINE = 2L;
    static final long ID_WORKPACK = 3L;


    @Test
    void shouldEvaluateBaselineWithPendingEvaluations() {

      final BaselineEvaluationRequest request = new BaselineEvaluationRequest("", APPROVED);

      this.givenUserIsCBBMember();

      EvaluateBaselineServiceTest.this.service.evaluate(1L, 2L, request);

      verify(EvaluateBaselineServiceTest.this.repository, times(1)).findById(2L);
      verify(EvaluateBaselineServiceTest.this.evaluatedByRepository, times(1)).findEvaluation(2L, 1L);
      verify(EvaluateBaselineServiceTest.this.ccbMemberRepository, times(1)).findAllActiveMembersOfBaseline(2L);
      verify(EvaluateBaselineServiceTest.this.evaluatedByRepository, times(1)).save(isA(IsEvaluatedBy.class), anyInt());
    }

    private void givenUserIsCBBMember() {
      when(EvaluateBaselineServiceTest.this.baseline.getStatus()).thenReturn(Status.PROPOSED);
      when(EvaluateBaselineServiceTest.this.member1.getId()).thenReturn(1L);
      when(EvaluateBaselineServiceTest.this.evaluatedByRepository.findEvaluation(2L, 1L)).thenReturn(Optional.empty());
      when(EvaluateBaselineServiceTest.this.repository.findById(2L)).thenReturn(Optional.of(EvaluateBaselineServiceTest.this.baseline));
      when(EvaluateBaselineServiceTest.this.ccbMemberRepository.findAllActiveMembersOfBaseline(2L)).thenReturn(asList(
        EvaluateBaselineServiceTest.this.member1,
        EvaluateBaselineServiceTest.this.member2
      ));
    }

    @Test
    void shouldOnlyUpdateStatusWhenNotHasPreviousBaseline() {
      this.givenValidBaselineAndCCBMember();
      this.givenBaselineWasEvaluatedByAllMembers();
      this.givenIsFirstBaselineActive();

      final BaselineEvaluationRequest request = new BaselineEvaluationRequest("", APPROVED);

      EvaluateBaselineServiceTest.this.service.evaluate(ID_MEMBER, ID_BASELINE, request);

      verify(EvaluateBaselineServiceTest.this.repository, times(1)).findById(2L);
      verify(EvaluateBaselineServiceTest.this.evaluatedByRepository, times(1)).findEvaluation(ID_BASELINE, ID_MEMBER);
      verify(EvaluateBaselineServiceTest.this.ccbMemberRepository, times(1)).findAllActiveMembersOfBaseline(ID_BASELINE);
      verify(EvaluateBaselineServiceTest.this.evaluatedByRepository, times(1)).save(isA(IsEvaluatedBy.class), anyInt());
      verify(EvaluateBaselineServiceTest.this.evaluatedByRepository, times(1)).wasEvaluatedByAllMembers(ID_BASELINE);
      verify(EvaluateBaselineServiceTest.this.baseline, times(1)).approve();
      verify(EvaluateBaselineServiceTest.this.repository, times(1)).findActiveBaselineByWorkpackId(ID_WORKPACK);
      verify(EvaluateBaselineServiceTest.this.baseline, times(1)).getIdWorkpack();
      verify(EvaluateBaselineServiceTest.this.repository, times(1)).save(isA(Baseline.class), anyInt());
    }

    private void givenIsFirstBaselineActive() {
      when(EvaluateBaselineServiceTest.this.baseline.getIdWorkpack()).thenReturn(ID_WORKPACK);
      when(EvaluateBaselineServiceTest.this.repository.findActiveBaselineByWorkpackId(ID_WORKPACK)).thenReturn(Optional.empty());
      when(EvaluateBaselineServiceTest.this.repository.save(isA(Baseline.class), anyInt())).thenReturn(null);
    }

    private void givenValidBaselineAndCCBMember() {
      when(EvaluateBaselineServiceTest.this.member1.getId()).thenReturn(ID_MEMBER);
      when(EvaluateBaselineServiceTest.this.baseline.getStatus()).thenReturn(Status.PROPOSED);
      when(EvaluateBaselineServiceTest.this.evaluatedByRepository.findEvaluation(
        ID_BASELINE,
        ID_MEMBER
      )).thenReturn(Optional.empty());
      when(EvaluateBaselineServiceTest.this.repository.findById(ID_BASELINE)).thenReturn(Optional.of(EvaluateBaselineServiceTest.this.baseline));
      when(EvaluateBaselineServiceTest.this.ccbMemberRepository.findAllActiveMembersOfBaseline(ID_BASELINE)).thenReturn(asList(
        EvaluateBaselineServiceTest.this.member1,
        EvaluateBaselineServiceTest.this.member2
      ));
    }

    private void givenBaselineWasEvaluatedByAllMembers() {
      when(EvaluateBaselineServiceTest.this.evaluatedByRepository.wasEvaluatedByAllMembers(ID_BASELINE)).thenReturn(true);
      doNothing().when(EvaluateBaselineServiceTest.this.baseline).approve();
    }

    @Test
    void shouldNotUpdateStatusWhenRemainEvaluations() {
      this.givenValidBaselineAndCCBMember();
      this.givenBaselineHasRemainEvaluation();

      final BaselineEvaluationRequest request = new BaselineEvaluationRequest("", APPROVED);

      EvaluateBaselineServiceTest.this.service.evaluate(ID_MEMBER, ID_BASELINE, request);

      verify(EvaluateBaselineServiceTest.this.repository, times(1)).findById(2L);
      verify(EvaluateBaselineServiceTest.this.evaluatedByRepository, times(1)).findEvaluation(ID_BASELINE, ID_MEMBER);
      verify(EvaluateBaselineServiceTest.this.ccbMemberRepository, times(1)).findAllActiveMembersOfBaseline(ID_BASELINE);
      verify(EvaluateBaselineServiceTest.this.evaluatedByRepository, times(1)).save(isA(IsEvaluatedBy.class), anyInt());
      verify(EvaluateBaselineServiceTest.this.evaluatedByRepository, times(1)).wasEvaluatedByAllMembers(ID_BASELINE);
      verify(EvaluateBaselineServiceTest.this.baseline, never()).approve();
      verify(EvaluateBaselineServiceTest.this.repository, never()).findActiveBaselineByWorkpackId(ID_WORKPACK);
      verify(EvaluateBaselineServiceTest.this.repository, never()).save(isA(Baseline.class), anyInt());
    }

    private void givenBaselineHasRemainEvaluation() {
      when(EvaluateBaselineServiceTest.this.evaluatedByRepository.wasEvaluatedByAllMembers(ID_BASELINE)).thenReturn(
        false);
    }

    @Test
    void shouldUpdateBaselineStatusIfIsLastEvaluation() {

      this.givenValidBaselineAndCCBMember();
      this.givenBaselineWasEvaluatedByAllMembers();
      this.givenPreviousBaselineActive();

      final BaselineEvaluationRequest request = new BaselineEvaluationRequest("", APPROVED);

      EvaluateBaselineServiceTest.this.service.evaluate(ID_MEMBER, ID_BASELINE, request);

      verify(EvaluateBaselineServiceTest.this.repository, times(1)).findById(2L);
      verify(EvaluateBaselineServiceTest.this.evaluatedByRepository, times(1)).findEvaluation(ID_BASELINE, ID_MEMBER);
      verify(EvaluateBaselineServiceTest.this.ccbMemberRepository, times(1)).findAllActiveMembersOfBaseline(ID_BASELINE);
      verify(EvaluateBaselineServiceTest.this.evaluatedByRepository, times(1)).save(isA(IsEvaluatedBy.class), anyInt());
      verify(EvaluateBaselineServiceTest.this.evaluatedByRepository, times(1)).wasEvaluatedByAllMembers(ID_BASELINE);
      verify(EvaluateBaselineServiceTest.this.baseline, times(1)).approve();
      verify(EvaluateBaselineServiceTest.this.repository, times(1)).findActiveBaselineByWorkpackId(ID_WORKPACK);
      verify(EvaluateBaselineServiceTest.this.repository, times(2)).save(isA(Baseline.class), anyInt());
    }

    private void givenPreviousBaselineActive() {
      when(EvaluateBaselineServiceTest.this.repository.findActiveBaselineByWorkpackId(ID_WORKPACK)).thenReturn(Optional.of(
        EvaluateBaselineServiceTest.this.activeBaseline));
      when(EvaluateBaselineServiceTest.this.baseline.getIdWorkpack()).thenReturn(ID_WORKPACK);
      doNothing().when(EvaluateBaselineServiceTest.this.activeBaseline).setActive(false);
      when(EvaluateBaselineServiceTest.this.repository.save(isA(Baseline.class), anyInt())).thenReturn(null);
    }


  }

  @Nested class BaselineReject {

    @Test
    void shouldRejectBaseline() {
      final BaselineEvaluationRequest request = new BaselineEvaluationRequest("", REJECTED);

      this.givenValidBaselineAndCCBMember();

      EvaluateBaselineServiceTest.this.service.evaluate(1L, 2L, request);

      verify(EvaluateBaselineServiceTest.this.repository, times(1)).findById(2L);
      verify(EvaluateBaselineServiceTest.this.evaluatedByRepository, times(1)).findEvaluation(ID_BASELINE, ID_MEMBER);
      verify(EvaluateBaselineServiceTest.this.ccbMemberRepository, times(1)).findAllActiveMembersOfBaseline(ID_BASELINE);
      verify(EvaluateBaselineServiceTest.this.evaluatedByRepository, times(1)).save(isA(IsEvaluatedBy.class), anyInt());
      verify(EvaluateBaselineServiceTest.this.repository, times(1)).save(isA(Baseline.class), anyInt());
      verify(EvaluateBaselineServiceTest.this.evaluatedByRepository, never()).wasEvaluatedByAllMembers(ID_BASELINE);
      verify(EvaluateBaselineServiceTest.this.baseline, never()).approve();
      verify(EvaluateBaselineServiceTest.this.repository, never()).findActiveBaselineByWorkpackId(ID_WORKPACK);
    }

    private void givenValidBaselineAndCCBMember() {
      when(EvaluateBaselineServiceTest.this.member1.getId()).thenReturn(ID_MEMBER);
      when(EvaluateBaselineServiceTest.this.baseline.getStatus()).thenReturn(Status.PROPOSED);
      when(EvaluateBaselineServiceTest.this.evaluatedByRepository.findEvaluation(
        ID_BASELINE,
        ID_MEMBER
      )).thenReturn(Optional.empty());
      when(EvaluateBaselineServiceTest.this.repository.findById(ID_BASELINE)).thenReturn(Optional.of(EvaluateBaselineServiceTest.this.baseline));
      when(EvaluateBaselineServiceTest.this.ccbMemberRepository.findAllActiveMembersOfBaseline(ID_BASELINE)).thenReturn(asList(
        EvaluateBaselineServiceTest.this.member1,
        EvaluateBaselineServiceTest.this.member2
      ));
    }

  }


}
