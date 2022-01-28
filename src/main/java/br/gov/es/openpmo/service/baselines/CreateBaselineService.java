package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.IncludeBaselineRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.relations.IsBaselinedBy;
import br.gov.es.openpmo.model.relations.IsProposedBy;
import br.gov.es.openpmo.model.relations.IsStakeholderIn;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.IsBaselinedByRepository;
import br.gov.es.openpmo.repository.IsProposedByRepository;
import br.gov.es.openpmo.repository.PersonRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.service.journals.JournalCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static br.gov.es.openpmo.utils.ApplicationMessage.PERSON_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_HAS_CANCELATION_PROPOSAL_INVALID_STATE_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_HAS_PENDING_BASELINES_INVALID_STATE_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;

@Service
public class CreateBaselineService implements ICreateBaselineService {

  private final BaselineRepository baselineRepository;

  private final IsBaselinedByRepository isBaselinedByRepository;

  private final PersonRepository personRepository;

  private final WorkpackRepository workpackRepository;

  private final IsProposedByRepository isProposedByRepository;

  private final JournalCreator journalCreator;

  @Autowired
  public CreateBaselineService(
      final BaselineRepository baselineRepository,
      final IsBaselinedByRepository isBaselinedByRepository,
      final PersonRepository personRepository,
      final WorkpackRepository workpackRepository,
      final IsProposedByRepository isProposedByRepository,
      final JournalCreator journalCreator
  ) {
    this.baselineRepository = baselineRepository;
    this.isBaselinedByRepository = isBaselinedByRepository;
    this.personRepository = personRepository;
    this.workpackRepository = workpackRepository;
    this.isProposedByRepository = isProposedByRepository;
    this.journalCreator = journalCreator;
  }

  @Override
  public Long create(
      final IncludeBaselineRequest request,
      final Long idPerson
  ) {
    final Workpack workpack = this.getWorkpackProjectById(request.getIdWorkpack());

    this.ifWorkpackHasPendingBaselinesThrowsException(workpack);
    this.ifWorkpackHasCancelationProposalThrowsException(workpack);

    final Baseline baseline = this.createBaseline(request);

    this.createNewIsProposedBy(request.getIdWorkpack(), idPerson, baseline);
    this.createNewIsBaselinedBy(baseline, workpack);

    this.journalCreator.baseline(baseline, idPerson);

    return baseline.getId();
  }

  private void createNewIsProposedBy(final Long idWorkpack, final Long idPerson, final Baseline baseline) {
    final Optional<IsStakeholderIn> maybeProposer = this.findProposerById(idWorkpack, idPerson);
    final IsProposedBy isProposedBy = new IsProposedBy();

    if (!maybeProposer.isPresent()) {
      final Person person = this.findPersonById(idPerson);
      isProposedBy.fillProposerData(person);
    } else {
      final IsStakeholderIn proposer = maybeProposer.get();
      isProposedBy.fillProposerData(proposer);
    }

    isProposedBy.setBaseline(baseline);

    this.isProposedByRepository.save(isProposedBy, 0);
  }

  private Person findPersonById(final Long idPerson) {
    return this.personRepository.findById(idPerson)
        .orElseThrow(() -> new NegocioException(PERSON_NOT_FOUND));
  }

  private Optional<IsStakeholderIn> findProposerById(final Long idWorkpack, final Long idPerson) {
    return this.personRepository.findProposerById(idPerson, idWorkpack);
  }

  private Workpack getWorkpackProjectById(final Long idWorkpack) {
    return this.workpackRepository.findById(idWorkpack)
        .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND))
        .ifIsNotProjectThrowsException();
  }

  private void ifWorkpackHasPendingBaselinesThrowsException(final Workpack workpack) {
    if (this.hasPendingBaselines(workpack)) {
      throw new NegocioException(WORKPACK_HAS_PENDING_BASELINES_INVALID_STATE_ERROR);
    }
  }

  private boolean hasPendingBaselines(final Workpack workpack) {
    return this.baselineRepository.workpackHasPendingBaselines(workpack.getId());
  }

  private void ifWorkpackHasCancelationProposalThrowsException(final Workpack workpack) {
    if (this.hasCancelationProposal(workpack)) {
      throw new NegocioException(WORKPACK_HAS_CANCELATION_PROPOSAL_INVALID_STATE_ERROR);
    }
  }

  private boolean hasCancelationProposal(final Workpack workpack) {
    return this.baselineRepository.workpackHasCancelationProposal(workpack.getId());
  }

  private Baseline createBaseline(final IncludeBaselineRequest request) {
    return this.baselineRepository.save(new Baseline(request), 0);
  }

  private void createNewIsBaselinedBy(
      final Baseline baseline,
      final Workpack workpack
  ) {
    final IsBaselinedBy baselinedBy = new IsBaselinedBy(baseline, workpack);
    this.isBaselinedByRepository.save(baselinedBy, 0);
  }

}
