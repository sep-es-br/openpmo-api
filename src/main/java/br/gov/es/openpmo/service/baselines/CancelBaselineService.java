package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.baselines.SubmitCancellingRequest;
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
import org.springframework.stereotype.Service;

import java.util.Optional;

import static br.gov.es.openpmo.utils.ApplicationMessage.PERSON_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;

@Service
public class CancelBaselineService implements ICancelBaselineService {

  private final WorkpackRepository workpackRepository;

  private final BaselineRepository baselineRepository;

  private final PersonRepository personRepository;

  private final IsProposedByRepository isProposedByRepository;

  private final IsBaselinedByRepository isBaselinedByRepository;

  public CancelBaselineService(
    final WorkpackRepository workpackRepository,
    final BaselineRepository baselineRepository,
    final PersonRepository personRepository,
    final IsProposedByRepository isProposedByRepository,
    final IsBaselinedByRepository isBaselinedByRepository
  ) {
    this.workpackRepository = workpackRepository;
    this.baselineRepository = baselineRepository;
    this.personRepository = personRepository;
    this.isProposedByRepository = isProposedByRepository;
    this.isBaselinedByRepository = isBaselinedByRepository;
  }

  @Override
  public EntityDto submit(final SubmitCancellingRequest request, final Long personId) {
    final Baseline baselineCancelled = Baseline.of(request);

    this.createBaselinedByRelationship(
      request,
      baselineCancelled
    );

    this.createProposerRelationship(
      baselineCancelled,
      personId,
      request.getIdWorkpack()
    );

    this.baselineRepository.save(baselineCancelled, 0);

    return EntityDto.of(baselineCancelled);
  }

  private void createBaselinedByRelationship(final SubmitCancellingRequest request, final Baseline baselineCancelled) {
    final Workpack workpack = this.findWorkpackById(request);
    final IsBaselinedBy isBaselinedBy = new IsBaselinedBy();
    isBaselinedBy.setWorkpack(workpack);
    isBaselinedBy.setBaseline(baselineCancelled);
    this.isBaselinedByRepository.save(isBaselinedBy, 0);
  }

  private Workpack findWorkpackById(final SubmitCancellingRequest request) {
    return this.workpackRepository.findByIdWorkpack(request.getIdWorkpack())
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
  }

  private void createProposerRelationship(final Baseline baselineCancelled, final Long idPerson, final Long idWorkpack) {
    final Optional<IsStakeholderIn> maybeProposer = this.findProposerById(idPerson, idWorkpack);
    final IsProposedBy isProposedBy = new IsProposedBy();
    if(!maybeProposer.isPresent()) {
      final Person person = this.findPersonById(idPerson);
      isProposedBy.fillProposerData(person);
    }
    else {
      final IsStakeholderIn proposer = maybeProposer.get();
      isProposedBy.fillProposerData(proposer);
    }
    isProposedBy.setBaseline(baselineCancelled);
    this.isProposedByRepository.save(isProposedBy, 0);
  }

  private Person findPersonById(final Long idPerson) {
    return this.personRepository.findById(idPerson)
      .orElseThrow(() -> new NegocioException(PERSON_NOT_FOUND));
  }

  private Optional<IsStakeholderIn> findProposerById(final Long personId, final Long idWorkpack) {
    return this.personRepository.findProposerById(personId, idWorkpack);
  }

}
