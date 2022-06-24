package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.BaselineEvaluationRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.baselines.Baseline;
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

import static br.gov.es.openpmo.model.baselines.Decision.REJECTED;
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

  private static void throwExceptionIfBaselineIsNotProposed(final Baseline baseline) {
    if (baseline.getStatus() != PROPOSED) {
      throw new NegocioException(BASELINE_IS_NOT_PROPOSED_INVALID_STATE_ERROR);
    }
  }

  private static Person findCCBMember(final Long idPerson, final Collection<? extends Person> members) {
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
    throwExceptionIfBaselineIsNotProposed(baseline);

    this.saveEvaluation(
      idPerson,
      idBaseline,
      request,
      baseline
    );

    if (request.getDecision() == REJECTED) {
      this.rejectBaseline(baseline);
      this.journalCreator.baseline(baseline, idPerson);
      return;
    }

    final boolean hasEvaluationsRemain = !this.evaluatedByRepository.wasEvaluatedByAllMembers(idBaseline);

    if (hasEvaluationsRemain) {
      return;
    }

    this.updateBaselineStatus(baseline, idPerson);
    this.journalCreator.baseline(baseline, idPerson);

    this.repository.findWorkpacksIdByBaselineId(baseline.getId())
      .forEach(this.dashboardService::calculate);
  }

  private void rejectBaseline(final Baseline baseline) {
    baseline.reject();
    this.saveBaseline(baseline);
  }

  private void saveBaseline(final Baseline baseline) {
    this.repository.save(baseline, 0);
  }

  private void updateBaselineStatus(final Baseline baseline, Long idPerson) {
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

  private void approveBaseline(Baseline baseline, Long idPerson) {
    baseline.approve();
    this.saveBaseline(baseline);
    if (baseline.isCancelation()) {
      cancelWorkpackByBaseline(baseline, idPerson);
    }
  }

  private void cancelWorkpackByBaseline(Baseline baseline, Long idPerson) {
    Workpack workpack = this.repository.findWorkpackByBaselineId(baseline.getId())
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
    this.workpackService.cancel(workpack.getId());
    this.journalCreator.edition(workpack, JournalAction.CANCELLED, idPerson);
  }

  private void verifyAlreadyEvaluationOfMember(final Long idPerson, final Long idBaseline) {
    final Optional<IsEvaluatedBy> maybeEvaluation = this.evaluatedByRepository.findEvaluation(idBaseline, idPerson);
    if (maybeEvaluation.isPresent()) {
      throw new NegocioException(CCB_MEMBER_ALREADY_EVALUATED);
    }
  }

  private void saveEvaluation(final IsEvaluatedBy evaluation) {
    this.evaluatedByRepository.save(evaluation, 0);
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
    if (maybeActiveBaseline.isPresent()) {
      final Baseline activeBaseline = maybeActiveBaseline.get();
      activeBaseline.setActive(false);
      this.saveBaseline(activeBaseline);
    }
  }

}
