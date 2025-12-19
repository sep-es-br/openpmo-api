package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.BaselineEvaluationRequest;
import br.gov.es.openpmo.dto.person.ApprovedPersonDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.baselines.Decision;
import br.gov.es.openpmo.model.baselines.Status;
import static br.gov.es.openpmo.model.baselines.Status.PROPOSED;
import br.gov.es.openpmo.model.journals.JournalAction;
import br.gov.es.openpmo.model.relations.IsEvaluatedBy;
import static br.gov.es.openpmo.model.relations.IsEvaluatedBy.fromMemberEvaluation;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import br.gov.es.openpmo.repository.IsEvaluatedByRepository;
import br.gov.es.openpmo.repository.JournalRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.service.journals.JournalCreator;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import static br.gov.es.openpmo.utils.ApplicationMessage.BASELINE_IS_NOT_PROPOSED_INVALID_STATE_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.BASELINE_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.CCB_MEMBER_ALREADY_EVALUATED;
import static br.gov.es.openpmo.utils.ApplicationMessage.NOT_VALID_CCB_MEMBER;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EvaluateBaselineService implements IEvaluateBaselineService {

    private final JournalRepository journalRepository;

  private final BaselineRepository repository;

  private final WorkpackService workpackService;

  private final WorkpackRepository workpackRepository;

  private final IsCCBMemberRepository ccbMemberRepository;

  private final IsEvaluatedByRepository evaluatedByRepository;

  private final JournalCreator journalCreator;


  @Autowired
  public EvaluateBaselineService(
    final BaselineRepository repository,
    final WorkpackService workpackService,
    final WorkpackRepository workpackRepository,
    final IsCCBMemberRepository ccbMemberRepository,
    final IsEvaluatedByRepository evaluatedByRepository,
    final JournalCreator journalCreator,
    final JournalRepository journalRepository
    
  ) {
    this.repository = repository;
    this.workpackService = workpackService;
    this.workpackRepository = workpackRepository;
    this.ccbMemberRepository = ccbMemberRepository;
    this.evaluatedByRepository = evaluatedByRepository;
    this.journalCreator = journalCreator;
    this.journalRepository = journalRepository;
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

    this.updateBaselineStatus(baseline);
    this.journalCreator.baselineForAllApprovedPersons(baseline);

    if (baseline.getStatus() != Status.APPROVED || baseline.isCancelation()) {
      return;
    }

  }

  private boolean isAlreadyRejected(final Long idBaseline) {
    final Set<IsEvaluatedBy> evaluations = this.evaluatedByRepository.findAllEvaluations(idBaseline);
    return evaluations.stream()
      .anyMatch(evaluation -> Decision.REJECTED.equals(evaluation.getDecision()));
  }

  private void rejectBaseline(final Baseline baseline) {
    if(baseline.getStatus() == Status.REJECTED) return;
    baseline.reject();
    if(baseline.isCancelation()){
      workpackRepository.resetSituationOrStatusToDefault(baseline.getBaselinedBy().getIdWorkpack());
    }

    this.repository.setStatusBaseline(baseline.getId(), baseline.getStatus().name());
  }

  private void saveBaseline(final Baseline baseline) {
    this.repository.save(baseline, 0);
  }


  private void updateBaselineStatus(
    final Baseline baseline
  ) {
    this.inactivateBaselineIfHasPrevious(baseline);
    this.approveBaseline(baseline);

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
    final Baseline baseline
  ) {
    baseline.approve();
    this.saveBaseline(baseline);
    workpackRepository.resetSituationOrStatusToDefault(baseline.getIdWorkpack());
    this.cancelWorkpacksFromSnapshots(baseline);
    if(baseline.isCancelation()) {
      this.cancelWorkpackByBaseline(baseline);
    }
  }

  private void cancelWorkpacksFromSnapshots(final Baseline baseline) {
    List<Workpack> canceledSnapshots = this.repository.findSnapshotsByBaselineIdAndCanceledTrue(baseline.getId());

    List<Long> masterIds = canceledSnapshots.stream()
    .map(snap -> this.repository.findMasterBySnapshotId(snap.getId()))
    .filter(Optional::isPresent)
    .map(opt -> opt.get().getId())
    .collect(Collectors.toList());

    if (!masterIds.isEmpty()) {
      workpackRepository.setWorkpacksCanceled(masterIds, true);

      masterIds.forEach(id -> {
          Optional<Workpack> masterOpt = workpackRepository.findById(id);
          workpackRepository.updateSituationValue(id, "Cancelada");
          masterOpt.ifPresent(master -> {

            List<ApprovedPersonDto> approvedPersons = journalRepository.getApprovedPersons(master.getId(), baseline.getId());

            for (ApprovedPersonDto approvedPerson : approvedPersons) {
              journalCreator.edition(
                  master,
                  JournalAction.CANCELLED,
                  approvedPerson.getPersonId()
              );
          }
        });
    });
  }}

  private void cancelWorkpackByBaseline(
    final Baseline baseline
  ) {
    final Workpack workpack = this.repository.findWorkpackByBaselineId(baseline.getId())
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
    this.workpackService.cancel(workpack.getId());
    workpackRepository.updateSituationValue(workpack.getId(), "Cancelado");
    List<ApprovedPersonDto> approvedPersons =
    journalRepository.getApprovedPersons(workpack.getId(), baseline.getId());

    for (ApprovedPersonDto approvedPerson : approvedPersons) {
        journalCreator.edition(
            workpack,
            JournalAction.CANCELLED,
            approvedPerson.getPersonId()
        );
    }
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

  public void handlePostMemberDeletion(Long idWorkpack) {
    final Long idBaseline = this.repository.findProposedBaselineByWorkpack(idWorkpack)
            .orElse(null);

    if (idBaseline == null) {
        return; 
    }

    final Baseline baseline = this.findBaselineById(idBaseline);
    throwExceptionIfBaselineIsNotProposedOrReject(baseline);

    final boolean hasEvaluationsRemain = !this.evaluatedByRepository.wasEvaluatedByAllMembers(idBaseline);

    if (!hasEvaluationsRemain) {
        final boolean alreadyRejected = this.isAlreadyRejected(idBaseline);
        if (!alreadyRejected) {
          
            this.updateBaselineStatus(baseline);
            this.journalCreator.baselineForAllApprovedPersons(baseline);

        }
    }
  }

}
