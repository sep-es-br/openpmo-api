package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.UpdateResponse;
import br.gov.es.openpmo.dto.workpack.WorkpackName;
import br.gov.es.openpmo.enumerator.BaselineStatus;
import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.baselines.Snapshotable;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import br.gov.es.openpmo.utils.MutableBoolean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class GetBaselineUpdatesFromBaselineService implements IGetBaselineUpdatesFromAnotherBaselineService {

  private final BaselineRepository baselineRepository;

  private final WorkpackRepository workpackRepository;

  private final IBaselineChangesService baselineChangesService;

  private final IBaselineComposeService baselineComposeService;

  private final IBaselineStructuralChangesService baselineStructuralChangesService;

  @Autowired
  public GetBaselineUpdatesFromBaselineService(
    final BaselineRepository baselineRepository,
    final WorkpackRepository workpackRepository,
    final IBaselineChangesService baselineChangesService,
    final IBaselineComposeService baselineComposeService,
    final IBaselineStructuralChangesService baselineStructuralChangesService
  ) {
    this.baselineRepository = baselineRepository;
    this.workpackRepository = workpackRepository;
    this.baselineChangesService = baselineChangesService;
    this.baselineComposeService = baselineComposeService;
    this.baselineStructuralChangesService = baselineStructuralChangesService;
  }

  private static Set<Workpack> getChildrenOrEmpty(final Workpack workpack) {
    return Optional.ofNullable(workpack.getChildren()).orElse(Collections.emptySet());
  }

  private static UpdateResponse getStructureChangedResponse() {
    return new UpdateResponse(
      null,
      "plan",
      "structure",
      BaselineStatus.CHANGED,
      null
    );
  }

  private static boolean isDeliverableOrMilestone(final Workpack workpack) {
    return workpack.isDeliverable() || workpack.isMilestone();
  }

  private static boolean notIncluded(final Workpack workpack, final Collection<? extends UpdateResponse> updates) {
    return updates.stream()
      .filter(update -> update.getClassification() == BaselineStatus.DELETED)
      .noneMatch(update -> update.getIdWorkpack().equals(workpack.getWorkpackMasterId()));
  }

  private static CategoryEnum getCategoryOrMaster(final Snapshotable<Workpack> workpack) {
    return Optional.ofNullable(workpack.getCategory()).orElse(CategoryEnum.MASTER);
  }

  private String getDescription(final Workpack workpack) {
    return this.getWorkpackMaster(workpack)
      .map(this::getMasterName)
      .orElse(null);
  }

  private void addIfWorkpackIsDeleted(final Workpack snapshot, final Collection<UpdateResponse> updates) {
    if(notIncluded(snapshot, updates)
       && isDeliverableOrMilestone(snapshot)
       && this.isMasterDeleted(snapshot)) {
      updates.add(this.getDeletedResponse(snapshot.getWorkpackMaster()));
    }
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

  private Optional<Workpack> getWorkpackMaster(final Workpack workpack) {
    final CategoryEnum category = getCategoryOrMaster(workpack);

    return category == CategoryEnum.MASTER
      ? Optional.of(workpack)
      : this.baselineRepository.findMasterBySnapshotId(workpack.getId());
  }

  private String getMasterName(final Workpack workpack) {
    return this.workpackRepository.findWorkpackNameAndFullname(workpack.getId())
      .map(WorkpackName::getName)
      .orElse(null);
  }

  private Optional<WorkpackModel> getWorkpackModel(final Workpack workpack) {
    final CategoryEnum category = getCategoryOrMaster(workpack);

    return category == CategoryEnum.MASTER
      ? this.workpackRepository.findWorkpackModelByWorkpackId(workpack.getId())
      : this.workpackRepository.findWorkpackModelBySnapshotId(workpack.getId());
  }

  private String getIcon(final Workpack workpack) {
    return this.getWorkpackModel(workpack)
      .map(WorkpackModel::getFontIcon)
      .orElse(null);
  }

  private void addIfMasterNotComposingBaseline(
    final Baseline baseline,
    final Workpack snapshot,
    final Collection<? super UpdateResponse> updates
  ) {
    if(isDeliverableOrMilestone(snapshot) &&
       this.workpackHasMaster(snapshot) &&
       !this.isSnapshotOfMasterComposingBaseline(baseline, snapshot)) {
      updates.add(this.getNewResponse(snapshot.getWorkpackMaster()));
    }
  }

  private boolean workpackHasMaster(final Workpack workpack) {
    return this.baselineRepository.workpackHasMaster(workpack.getId());
  }

  @Override
  public List<UpdateResponse> getUpdates(
    final Baseline baseline,
    final Baseline anotherBaseline
  ) {
    final List<UpdateResponse> updates = new ArrayList<>();

    final Workpack snapshot = this.getSnapshotFromBaseline(anotherBaseline);
    this.addUpdatesRecursively(baseline, snapshot, updates, new MutableBoolean());
    this.addDeletedRecursively(this.getSnapshotFromBaseline(baseline), updates);

    return updates;
  }

  private void addDeletedRecursively(final Workpack workpack, final List<UpdateResponse> updates) {
    for(final Workpack child : getChildrenOrEmpty(workpack)) {
      this.addIfWorkpackIsDeleted(child, updates);
      this.addDeletedRecursively(child, updates);
    }
  }

  private void addUpdatesRecursively(
    final Baseline baseline,
    final Workpack snapshot,
    final List<UpdateResponse> updates,
    final MutableBoolean hasStructureChange
  ) {
    final Set<Workpack> children = getChildrenOrEmpty(snapshot);

    for(final Workpack child : children) {
      this.addUpdates(baseline, child, updates, hasStructureChange);
    }
  }

  private void addUpdates(
    final Baseline baseline,
    final Workpack snapshot,
    final List<UpdateResponse> updates,
    final MutableBoolean hasStructureChange
  ) {
    this.addChanges(baseline, snapshot, updates);
    this.addIfMasterNotComposingBaseline(baseline, snapshot, updates);
    this.addIfWorkpackIsDeleted(snapshot, updates);
    this.addIfHasStructureChanges(baseline, snapshot, updates, hasStructureChange);
    this.addUpdatesRecursively(baseline, snapshot, updates, hasStructureChange);
  }

  private void addIfHasStructureChanges(
    final Baseline baseline,
    final Workpack snapshot,
    final List<? super UpdateResponse> updates,
    final MutableBoolean hasStructureChange
  ) {
    if(isDeliverableOrMilestone(snapshot)
       && this.hasStructureChanges(baseline, snapshot)
       && !hasStructureChange.isValue()) {
      updates.add(getStructureChangedResponse());
      Collections.reverse(updates);
      hasStructureChange.setValue(true);
    }
  }

  private boolean hasStructureChanges(
    final Baseline baseline,
    final Workpack workpack
  ) {
    return this.baselineStructuralChangesService.hasBaselineStructureChanges(baseline, workpack);
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

  private boolean isMasterDeleted(final Workpack workpack) {
    return this.baselineRepository.isMasterDeleted(workpack.getId());
  }

  private void addChanges(
    final Baseline baseline,
    final Workpack snapshot,
    final Collection<? super UpdateResponse> updates
  ) {
    if(this.hasChanges(baseline, snapshot)) {
      updates.add(this.getChangedResponse(snapshot));
    }
  }

  private boolean isSnapshotOfMasterComposingBaseline(
    final Baseline baseline,
    final Workpack workpack
  ) {
    return this.baselineComposeService.isSnapshotOfMasterComposingBaseline(baseline, workpack);
  }

  private UpdateResponse getChangedResponse(final Workpack snapshot) {
    return new UpdateResponse(
      snapshot.getId(),
      this.getIcon(snapshot),
      this.getDescription(snapshot),
      BaselineStatus.CHANGED,
      null
    );
  }

  private boolean hasChanges(
    final Baseline baseline,
    final Workpack workpack
  ) {
    return isDeliverableOrMilestone(workpack)
           && this.baselineChangesService.hasChanges(baseline, workpack, true);
  }

  private Workpack getSnapshotFromBaseline(final Baseline baseline) {
    return this.baselineRepository.findWorkpackProjectSnapshotFromBaseline(baseline.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.SNAPSHOT_NOT_FOUND));
  }

}
