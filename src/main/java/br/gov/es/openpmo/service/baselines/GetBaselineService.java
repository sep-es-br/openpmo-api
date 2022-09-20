package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.BaselineDetailResponse;
import br.gov.es.openpmo.dto.baselines.EvaluationItem;
import br.gov.es.openpmo.dto.baselines.UpdateResponse;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static br.gov.es.openpmo.dto.baselines.BaselineDetailResponse.of;
import static br.gov.es.openpmo.utils.ApplicationMessage.BASELINE_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;

@Service
public class GetBaselineService implements IGetBaselineService {

  private final IGetBaselineUpdatesService updatesService;

  private final IGetBaselineUpdatesFromAnotherBaselineService updatesFromAnotherBaselineService;

  private final IGetFirstTimeBaselineUpdatesService getFirstTimeBaselineUpdatesService;

  private final BaselineRepository baselineRepository;

  private final WorkpackRepository workpackRepository;

  private final IGetAllBaselineEvaluations getAllBaselineEvaluations;

  private final BaselineRepository repository;

  @Autowired
  public GetBaselineService(
    final IGetBaselineUpdatesService updatesService,
    final IGetBaselineUpdatesFromAnotherBaselineService updatesFromAnotherBaselineService,
    final IGetFirstTimeBaselineUpdatesService getFirstTimeBaselineUpdatesService,
    final BaselineRepository baselineRepository,
    final WorkpackRepository workpackRepository,
    final IGetAllBaselineEvaluations getAllBaselineEvaluations,
    final BaselineRepository repository
  ) {
    this.updatesService = updatesService;
    this.updatesFromAnotherBaselineService = updatesFromAnotherBaselineService;
    this.getFirstTimeBaselineUpdatesService = getFirstTimeBaselineUpdatesService;
    this.baselineRepository = baselineRepository;
    this.workpackRepository = workpackRepository;
    this.getAllBaselineEvaluations = getAllBaselineEvaluations;
    this.repository = repository;
  }

  @Override
  public BaselineDetailResponse getById(final Long idBaseline) {
    final BaselineDetailResponse result;

    final Baseline baseline = this.getBaselineById(idBaseline);
    final BaselineDetailResponse response = of(baseline);

    this.addEvaluatedBy(response, baseline.getId());

    if(isCancelation(baseline)) {
      result = response;
    }
    else if(isDraft(baseline)) {
      this.addUpdates(response, baseline);
      result = response;
    }
    else {
      this.addUpdatesFromPreviousBaselineOrFromBaseline(response, baseline);
      result = response;
    }

    return result;
  }

  private Baseline getBaselineById(final Long idBaseline) {
    return this.repository.findBaselineDetailById(idBaseline)
      .orElseThrow(() -> new NegocioException(BASELINE_NOT_FOUND));
  }

  private static boolean isCancelation(final Baseline baseline) {
    return baseline.isCancelation();
  }

  private static boolean isDraft(final Baseline baseline) {
    return baseline.isDraft();
  }

  private static Set<Workpack> getChildren(final Workpack snapshot) {
    return snapshot.getChildren();
  }

  private void addUpdates(
    final BaselineDetailResponse response,
    final Baseline baseline
  ) {
    response.setUpdates(this.getUpdatesFromWorkpack(this.getWorkpackByBaseline(baseline)));
  }

  private void addUpdatesFromPreviousBaselineOrFromBaseline(
    final BaselineDetailResponse response,
    final Baseline baseline
  ) {
    response.setUpdates(this.getUpdates(baseline));
  }

  private void addEvaluatedBy(
    final BaselineDetailResponse response,
    final Long idBaseline
  ) {
    final List<EvaluationItem> items = this.getEvaluationItems(idBaseline);
    response.setEvaluations(items);
  }

  private List<EvaluationItem> getEvaluationItems(final Long idBaseline) {
    return this.getAllBaselineEvaluations.getEvaluations(idBaseline);
  }

  private List<UpdateResponse> getUpdatesFromWorkpack(final Workpack workpack) {
    return this.updatesService.getUpdates(workpack.getId());
  }

  private List<UpdateResponse> getUpdates(final Baseline baseline) {
    return this.getPreviousBaseline(baseline)
      .map(previousBaseline -> this.getUpdatesFromPreviousBaseline(baseline, previousBaseline))
      .orElseGet(() -> this.getUpdatesFromBaseline(baseline));
  }

  private Workpack getWorkpackByBaseline(final Baseline baseline) {
    return this.getWorkpackById(baseline.getIdWorkpack());
  }

  private Workpack getWorkpackById(final Long idWorkpack) {
    return this.workpackRepository.findWithPropertiesAndModelAndChildrenById(idWorkpack)
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
  }

  private Optional<Baseline> getPreviousBaseline(final Baseline baseline) {
    return this.repository.findPreviousBaseline(baseline.getId(), baseline.getIdWorkpack());
  }

  private List<UpdateResponse> getUpdatesFromBaseline(final Baseline baseline) {
    final Workpack snapshot = this.getSnapshotFromBaseline(baseline);
    return this.getFirstTimeBaselineUpdatesService.getUpdates(getChildren(snapshot), true);
  }

  private Workpack getSnapshotFromBaseline(final Baseline baseline) {
    return this.baselineRepository.findWorkpackProjectSnapshotFromBaseline(baseline.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.SNAPSHOT_NOT_FOUND));
  }

  private List<UpdateResponse> getUpdatesFromPreviousBaseline(
    final Baseline baseline,
    final Baseline previousBaseline
  ) {
    return this.updatesFromAnotherBaselineService.getUpdates(previousBaseline, baseline);
  }

}
