package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.SubmitBaselineRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.baselines.Status;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.service.dashboards.v2.IAsyncDashboardService;
import br.gov.es.openpmo.service.journals.JournalCreator;
import br.gov.es.openpmo.utils.ApplicationMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubmitBaselineService implements ISubmitBaselineService {

  private final JournalCreator journalCreator;

  private final BaselineRepository baselineRepository;

  private final IFirstTimeSubmitBaselineService firstTimeSubmitBaselineService;

  private final IAnotherTimeSubmitBaselineService anotherTimeSubmitBaselineService;

  private final IAsyncDashboardService dashboardService;

  @Autowired
  public SubmitBaselineService(
    final JournalCreator journalCreator,
    final BaselineRepository baselineRepository,
    final IFirstTimeSubmitBaselineService firstTimeSubmitBaselineService,
    final IAnotherTimeSubmitBaselineService anotherTimeSubmitBaselineService,
    final IAsyncDashboardService dashboardService
  ) {
    this.journalCreator = journalCreator;
    this.baselineRepository = baselineRepository;
    this.firstTimeSubmitBaselineService = firstTimeSubmitBaselineService;
    this.anotherTimeSubmitBaselineService = anotherTimeSubmitBaselineService;
    this.dashboardService = dashboardService;
  }

  @Override
  public void submit(
    final Long idBaseline,
    final SubmitBaselineRequest request,
    final Long idPerson
  ) {
    final Baseline baseline = this.getDraftBaselineById(idBaseline);
//    final Workpack workpack = this.getWorkpackByBaseline(baseline);
    // final Long workpackId = baseline.getBaselinedBy().getIdWorkpack();
    this.submit(request, baseline, baseline.getIdWorkpack(), null);

    baseline.setStatus(Status.PROPOSED);
    baseline.setProposalDate(LocalDateTime.now());
    this.baselineRepository.save(baseline, 0);

    // Added this line to avoid broken relationships
    baseline.getBaselinedBy().setId(null);
    baseline.getProposer().setId(null);
    this.baselineRepository.save(baseline);    

    this.journalCreator.baseline(baseline, idPerson);
    this.dashboardService.calculate(baseline.getBaselinedBy().getIdWorkpack(), true);
  }

  private Workpack getWorkpackByBaseline(final Baseline baseline) {
    return this.getWorkpackByIdBaseline(baseline.getId());
  }

  private Workpack getWorkpackByIdBaseline(final Long idBaseline) {
    return this.baselineRepository.findNotDeletedWorkpackWithPropertiesAndModelAndChildrenByBaselineId(idBaseline)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
  }

  private void submit(
    final SubmitBaselineRequest request,
    final Baseline baseline,
    final Long workpackId,
    final Optional<Long> parentId
  ) {
    //final Long workpackId = baseline.getBaselinedBy().getIdWorkpack();
    List<Long> childrenIds;
    if(this.isFirstTimeSubmittingBaseline(workpackId)) {
      childrenIds = this.firstTimeSubmitBaselineService.submit(baseline, workpackId, parentId);
      childrenIds.forEach(id -> {
        this.submit(request, baseline, id, Optional.of(workpackId));
      });
    }
    else {
      this.anotherTimeSubmitBaselineService.submit(baseline, workpackId, request.getUpdates());
    }
  }

  private boolean isFirstTimeSubmittingBaseline(final Long workpackId) {
    return !this.baselineRepository.workpackHasActiveBaseline(workpackId);
  }

  private Baseline getDraftBaselineById(final Long idBaseline) {
    return this.baselineRepository.findById(idBaseline)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.BASELINE_NOT_FOUND))
      .ifIsNotDraftThrowsException();
  }

}
