package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.BaselineEvaluationRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.baselines.Decision;
import br.gov.es.openpmo.model.baselines.Status;
import br.gov.es.openpmo.model.journals.JournalAction;
import br.gov.es.openpmo.model.relations.IsEvaluatedBy;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import br.gov.es.openpmo.repository.IsEvaluatedByRepository;
import br.gov.es.openpmo.service.dashboards.v2.IAsyncDashboardService;
import br.gov.es.openpmo.service.journals.JournalCreator;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static br.gov.es.openpmo.model.baselines.Status.PROPOSED;
import static br.gov.es.openpmo.model.relations.IsEvaluatedBy.fromMemberEvaluation;
import static br.gov.es.openpmo.utils.ApplicationMessage.BASELINE_IS_NOT_PROPOSED_INVALID_STATE_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.BASELINE_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.CCB_MEMBER_ALREADY_EVALUATED;
import static br.gov.es.openpmo.utils.ApplicationMessage.NOT_VALID_CCB_MEMBER;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;

@Service
public class EvaluateBaselineService implements IEvaluateBaselineService {

  private final BaselineRepository repository;

  private final WorkpackService workpackService;

  private final IsCCBMemberRepository ccbMemberRepository;

  private final IsEvaluatedByRepository evaluatedByRepository;

  private final IAsyncDashboardService dashboardService;

  private final JournalCreator journalCreator;

  @Autowired
  public EvaluateBaselineService(
    final BaselineRepository repository,
    final WorkpackService workpackService,
    final IsCCBMemberRepository ccbMemberRepository,
    final IsEvaluatedByRepository evaluatedByRepository,
    final IAsyncDashboardService dashboardService,
    final JournalCreator journalCreator
  ) {
    this.repository = repository;
    this.workpackService = workpackService;
    this.ccbMemberRepository = ccbMemberRepository;
    this.evaluatedByRepository = evaluatedByRepository;
    this.dashboardService = dashboardService;
    this.journalCreator = journalCreator;
  }

  private static void throwExceptionIfBaselineIsNotProposedOrReject(final Baseline baseline) {
    if(baseline.getStatus() != PROPOSED && baseline.getStatus() != Status.REJECTED) {
      throw new NegocioException(BASELINE_IS_NOT_PROPOSED_INVALID_STATE_ERROR);
    }
  }

  private static Person findCCBMember(
    final Long idPerson,
    final Collection<? extends Person> members
  ) {
    return members.stream()
      .filter(person -> person.getId().equals(idPerson))
      .findFirst()
      .orElseThrow(() -> new NegocioException(NOT_VALID_CCB_MEMBER));
  }

  @Override
  public void evaluate(
    final Long idPerson,
    final Long idBaseline,
    final BaselineEvaluationRequest request
  ) {
    final Baseline baseline = this.findBaselineById(idBaseline);
    throwExceptionIfBaselineIsNotProposedOrReject(baseline);

    this.saveEvaluation(
      idPerson,
      idBaseline,
      request,
      baseline
    );

    if(request.getDecision() == Decision.REJECTED) {
      this.rejectBaseline(baseline);
      this.journalCreator.baseline(baseline, idPerson);
      return;
    }

    final boolean hasEvaluationsRemain = !this.evaluatedByRepository.wasEvaluatedByAllMembers(idBaseline);

    if(hasEvaluationsRemain) {
      return;
    }

    final boolean alreadyRejected = this.isAlreadyRejected(idBaseline);
    if(alreadyRejected) return;

    this.updateBaselineStatus(baseline, idPerson);
    this.journalCreator.baseline(baseline, idPerson);

    if (baseline.getStatus() != Status.APPROVED || baseline.isCancelation()) {
      return;
    }
    this.dashboardService.calculate();

  }

  private boolean isAlreadyRejected(final Long idBaseline) {
    final Set<IsEvaluatedBy> evaluations = this.evaluatedByRepository.findAllEvaluations(idBaseline);
    return evaluations.stream()
      .anyMatch(evaluation -> Decision.REJECTED.equals(evaluation.getDecision()));
  }

  private void rejectBaseline(final Baseline baseline) {
    if(baseline.getStatus() == Status.REJECTED) return;
    baseline.reject();
    this.repository.setStatusBaseline(baseline.getId(), baseline.getStatus().name());
  }

  private void saveBaseline(final Baseline baseline) {
    this.repository.save(baseline, 0);
  }


  private void updateBaselineStatus(
    final Baseline baseline,
    final Long idPerson
  ) {
    this.inactivateBaselineIfHasPrevious(baseline);
    this.approveBaseline(baseline, idPerson);
  }

  private void saveEvaluation(
    final Long idPerson,
    final Long idBaseline,
    final BaselineEvaluationRequest request,
    final Baseline baseline
  ) {
    final Set<Person> members = this.findAllActiveMembersOfBaseline(idBaseline);
    final Person member = findCCBMember(idPerson, members);
    this.verifyAlreadyEvaluationOfMember(idPerson, idBaseline);
    final IsEvaluatedBy evaluation = fromMemberEvaluation(member, baseline, request);
    this.saveEvaluation(evaluation);
  }

  private void approveBaseline(
    final Baseline baseline,
    final Long idPerson
  ) {
    baseline.approve();
    this.saveBaseline(baseline);
    if(baseline.isCancelation()) {
      this.cancelWorkpackByBaseline(baseline, idPerson);
    }
  }

  private void cancelWorkpackByBaseline(
    final Baseline baseline,
    final Long idPerson
  ) {
    final Workpack workpack = this.repository.findWorkpackByBaselineId(baseline.getId())
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
    this.workpackService.cancel(workpack.getId());
    this.journalCreator.edition(workpack, JournalAction.CANCELLED, idPerson);
  }

  private void verifyAlreadyEvaluationOfMember(
    final Long idPerson,
    final Long idBaseline
  ) {
    final Optional<IsEvaluatedBy> maybeEvaluation = this.evaluatedByRepository.findEvaluation(idBaseline, idPerson);
    if(maybeEvaluation.isPresent()) {
      throw new NegocioException(CCB_MEMBER_ALREADY_EVALUATED);
    }
  }

  private void saveEvaluation(final IsEvaluatedBy evaluation) {
    this.evaluatedByRepository.createIsEvaluatedBy(evaluation.getIdPerson(),
            evaluation.getBaseline().getId(),
            evaluation.getDecision().name(),
            evaluation.getInRoleWorkLocation(),
            evaluation.getWhen(),
            evaluation.getComment());
  }

  private Baseline findBaselineById(final Long idBaseline) {
    return this.repository.findById(idBaseline)
      .orElseThrow(() -> new NegocioException(BASELINE_NOT_FOUND));
  }

  private Set<Person> findAllActiveMembersOfBaseline(final Long idBaseline) {
    return this.ccbMemberRepository.findAllActiveMembersOfBaseline(idBaseline);
  }

  private void inactivateBaselineIfHasPrevious(final Baseline baseline) {
    final Optional<Baseline> maybeActiveBaseline = this.repository.findActiveBaseline(baseline.getIdWorkpack());
    if(maybeActiveBaseline.isPresent()) {
      final Baseline activeBaseline = maybeActiveBaseline.get();
      activeBaseline.setActive(false);
      this.saveBaseline(activeBaseline);
    }
  }

}
