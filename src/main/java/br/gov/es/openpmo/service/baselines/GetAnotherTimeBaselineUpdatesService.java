package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.UpdateResponse;
import br.gov.es.openpmo.enumerator.BaselineStatus;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import br.gov.es.openpmo.utils.MutableBoolean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class GetAnotherTimeBaselineUpdatesService implements IGetAnotherTimeBaselineUpdatesService {

  private final BaselineRepository baselineRepository;

  private final IBaselineChangesService baselineChangesService;

  private final IBaselineComposeService baselineComposeService;

  private final IBaselineStructuralChangesService baselineStructuralChangesService;

  @Autowired
  public GetAnotherTimeBaselineUpdatesService(
      final BaselineRepository baselineRepository,
      final IBaselineChangesService baselineChangesService,
      final IBaselineComposeService baselineComposeService,
      final IBaselineStructuralChangesService baselineStructuralChangesService
  ) {
    this.baselineRepository = baselineRepository;
    this.baselineChangesService = baselineChangesService;
    this.baselineComposeService = baselineComposeService;
    this.baselineStructuralChangesService = baselineStructuralChangesService;
  }

  @Override
  public List<UpdateResponse> getUpdates(
      final Baseline baseline,
      final Workpack workpack
  ) {
    final List<UpdateResponse> updates = new ArrayList<>();

    final MutableBoolean hasStructureChange = new MutableBoolean();
    this.addUpdatesRecursively(baseline, workpack.getChildren(), updates);

    final Workpack snapshot = this.findSnapshotByWorkpackIdAndBaselineId(baseline, workpack);
    this.addSnapshotsRecursively(baseline, snapshot, updates, hasStructureChange);

    return updates;
  }

  private void addSnapshotsRecursively(
      final Baseline baseline,
      final Workpack snapshot,
      final List<UpdateResponse> updates,
      final MutableBoolean hasStructureChange
  ) {
    if (snapshot.getChildren() == null) {
      return;
    }

    for (final Workpack child : snapshot.getChildren()) {
      this.addIfHasStructureChanges(baseline, child, updates, hasStructureChange);
      this.addSnapshotsRecursively(baseline, child, updates, hasStructureChange);
    }
  }

  private void addUpdates(
      final Baseline baseline,
      final Workpack workpack,
      final List<UpdateResponse> updates
  ) {
    this.addChanges(baseline, workpack, updates);
    this.addIfNotComposingBaseline(baseline, workpack, updates);
    this.addIfWorkpackIsDeleted(baseline, workpack, updates);

    if (!workpack.isDeleted()) {
      this.addUpdatesRecursively(baseline, workpack.getChildren(), updates);
    }
  }

  private void addIfWorkpackIsDeleted(Baseline baseline, Workpack workpack, List<UpdateResponse> updates) {
    if (this.isDeliverableOrMilestone(workpack) && this.isWorkpackDeletedAndHasSnapshot(workpack, baseline)) {
      updates.add(this.getDeletedResponse(workpack));
    }
  }

  private Workpack findSnapshotByWorkpackIdAndBaselineId(
      final Baseline baseline,
      final Workpack workpack
  ) {
    return this.baselineRepository.findSnapshotWithChildrenAndPropertiesByWorkpackIdAndBaselineId(workpack.getId(), baseline.getId())
        .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_HAS_NO_SNAPSHOT_INVALID_STATE_ERROR));
  }

  private void addUpdatesRecursively(
      final Baseline baseline,
      final Set<Workpack> workpacks,
      final List<UpdateResponse> updates
  ) {
    if (workpacks == null) {
      return;
    }

    for (final Workpack workpack : workpacks) {
      this.addUpdates(baseline, workpack, updates);
    }
  }

  private boolean isWorkpackDeletedAndHasSnapshot(final Workpack workpack, Baseline baseline) {
    return this.baselineRepository.isWorkpackDeletedAndHasSnapshot(workpack.getId(), baseline.getId());
  }

  private void addIfHasStructureChanges(
      final Baseline baseline,
      final Workpack workpack,
      final List<UpdateResponse> updates,
      final MutableBoolean hasStructureChange
  ) {
    if (this.isDeliverableOrMilestone(workpack)
        && !this.hasStructureChanges(baseline, workpack)
        && !hasStructureChange.isValue()) {
      updates.add(this.getStructureChangedResponse());
      hasStructureChange.setValue(true);
    }
  }

  private boolean hasChanges(
      final Baseline baseline,
      final Workpack workpack
  ) {
    return this.isDeliverableOrMilestone(workpack)
        && this.isSnapshotOfWorkpackComposingBaseline(baseline, workpack)
        && this.baselineChangesService.hasChanges(baseline, workpack, false);
  }

  private boolean hasStructureChanges(
      final Baseline baseline,
      final Workpack workpack
  ) {
    return this.baselineStructuralChangesService.hasStructureChanges(baseline, workpack);
  }

  private boolean isDeliverableOrMilestone(final Workpack workpack) {
    return workpack.isDeliverable() || workpack.isMilestone();
  }

  private void addChanges(
      final Baseline baseline,
      final Workpack workpack,
      final List<UpdateResponse> updates
  ) {
    if (!workpack.isDeleted() && this.hasChanges(baseline, workpack)) {
      updates.add(this.getChangedResponse(workpack));
    }
  }

  private boolean isSnapshotOfWorkpackComposingBaseline(
      final Baseline baseline,
      final Workpack workpack
  ) {
    return this.baselineComposeService.isSnapshotOfWorkpackComposingBaseline(baseline, workpack);
  }

  private UpdateResponse getChangedResponse(final Workpack workpack) {
    return new UpdateResponse(
        workpack.getId(),
        this.getIcon(workpack),
        this.getDescription(workpack),
        BaselineStatus.CHANGED,
        null
    );
  }

  private void addIfNotComposingBaseline(
      final Baseline baseline,
      final Workpack workpack,
      final List<UpdateResponse> updates
  ) {
    if (!workpack.isDeleted() && this.isDeliverableOrMilestone(workpack)
        && !this.isSnapshotOfWorkpackComposingBaseline(baseline, workpack)) {
      updates.add(this.getNewResponse(workpack));
    }
  }

  private UpdateResponse getNewResponse(final Workpack workpack) {
    return new UpdateResponse(
        workpack.getId(),
        this.getIcon(workpack),
        this.getDescription(workpack),
        BaselineStatus.NEW,
        null
    );
  }

  private UpdateResponse getStructureChangedResponse() {
    return new UpdateResponse(
        null,
        "plan",
        "structure",
        BaselineStatus.CHANGED,
        null
    );
  }

  private UpdateResponse getDeletedResponse(final Workpack workpack) {
    return new UpdateResponse(
        workpack.getId(),
        this.getIcon(workpack),
        this.getDescription(workpack),
        BaselineStatus.DELETED,
        null
    );
  }

  private String getIcon(final Workpack workpack) {
    final WorkpackModel workpackModel = workpack.getWorkpackModelInstance();
    return Optional.ofNullable(workpackModel).map(WorkpackModel::getFontIcon).orElse("");
  }

  private String getDescription(final Workpack workpack) {
    final WorkpackModel workpackModel = workpack.getWorkpackModelInstance();
    return Optional.ofNullable(workpackModel).map(WorkpackModel::getModelName).orElse("");
  }

}
