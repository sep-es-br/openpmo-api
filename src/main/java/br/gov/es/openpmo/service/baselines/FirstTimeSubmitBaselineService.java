package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.UpdateRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.baselines.Status;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.relations.IsCostAccountSnapshotOf;
import br.gov.es.openpmo.model.relations.IsPropertySnapshotOf;
import br.gov.es.openpmo.model.relations.IsScheduleSnapshotOf;
import br.gov.es.openpmo.model.relations.IsStepSnapshotOf;
import br.gov.es.openpmo.model.relations.IsWorkpackSnapshotOf;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.*;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class FirstTimeSubmitBaselineService implements IFirstTimeSubmitBaselineService {

  private final BaselineRepository baselineRepository;

  private final WorkpackRepository workpackRepository;

  private final IsWorkpackSnapshotOfRepository workpackSnapshotOfRepository;

  private final ScheduleRepository scheduleRepository;

  private final IsScheduleSnapshotOfRepository scheduleSnapshotOfRepository;

  private final StepRepository stepRepository;

  private final IsStepSnapshotOfRepository stepSnapshotOfRepository;

  private final CostAccountRepository costAccountRepository;

  private final IsCostAccountSnapshotOfRepository costAccountSnapshotOfRepository;

  private final ConsumesRepository consumesRepository;

  private final PropertyRepository propertyRepository;

  private final IsPropertySnapshotOfRepository propertySnapshotOfRepository;

  private final BaselineHelper baselineHelper;

  @Autowired
  public FirstTimeSubmitBaselineService(
    final BaselineRepository baselineRepository,
    final WorkpackRepository workpackRepository,
    final IsWorkpackSnapshotOfRepository workpackSnapshotOfRepository,
    final ScheduleRepository scheduleRepository,
    final IsScheduleSnapshotOfRepository scheduleSnapshotOfRepository,
    final StepRepository stepRepository,
    final IsStepSnapshotOfRepository stepSnapshotOfRepository,
    final CostAccountRepository costAccountRepository,
    final IsCostAccountSnapshotOfRepository costAccountSnapshotOfRepository,
    final ConsumesRepository consumesRepository,
    final PropertyRepository propertyRepository,
    final IsPropertySnapshotOfRepository propertySnapshotOfRepository,
    final BaselineHelper baselineHelper
  ) {
    this.baselineRepository = baselineRepository;
    this.workpackSnapshotOfRepository = workpackSnapshotOfRepository;
    this.workpackRepository = workpackRepository;
    this.scheduleRepository = scheduleRepository;
    this.scheduleSnapshotOfRepository = scheduleSnapshotOfRepository;
    this.stepRepository = stepRepository;
    this.stepSnapshotOfRepository = stepSnapshotOfRepository;
    this.costAccountRepository = costAccountRepository;
    this.costAccountSnapshotOfRepository = costAccountSnapshotOfRepository;
    this.consumesRepository = consumesRepository;
    this.propertyRepository = propertyRepository;
    this.propertySnapshotOfRepository = propertySnapshotOfRepository;
    this.baselineHelper = baselineHelper;
  }

  private static void ifWorkpackIsNotSnapshotThrowException(final Workpack workpack) {
    if(!workpack.isSnapshot()) {
      throw new NegocioException(ApplicationMessage.WORKPACK_IS_NOT_SNAPSHOT_INVALID_STATE_ERROR);
    }
  }

  private static boolean canSnapshotWorkpack(
    final Workpack workpack,
    final Collection<? extends UpdateRequest> updates
  ) {
    return isNotDeleted(workpack) &&
           isAllowedForSnapshotting(workpack, updates);
  }

  private static boolean isNotDeleted(final Workpack workpack) {
    return !workpack.isDeleted();
  }

  private static boolean isAllowedForSnapshotting(
    final Workpack workpack,
    final Collection<? extends UpdateRequest> updates
  ) {
    return updates == null ||
           !isMilestoneOrDeliverable(workpack) ||
           includesWorkpack(updates, workpack);
  }

  private static boolean isMilestoneOrDeliverable(final Workpack workpack) {
    return workpack.isMilestone() || workpack.isDeliverable();
  }

  private static boolean includesWorkpack(
    final Collection<? extends UpdateRequest> updates,
    final Workpack workpack
  ) {
//    return updates.stream()
//      .filter(update -> update.getIdWorkpack().equals(workpack.getId()))
//      .anyMatch(UpdateRequest::isIncluded);

    final UpdateRequest[] updatesArr = updates.toArray(new UpdateRequest[0]);
    for (int i=0; i<updatesArr.length; i++) {
      if (updatesArr[i].getIdWorkpack().equals(workpack.getId()) && updatesArr[i].isIncluded()) {
        return true;
      }
    }
    return false;
  }

  private static Set<Workpack> getChildrenOrEmpty(final Workpack parent) {
    return Optional.ofNullable(parent.getChildren()).orElse(Collections.emptySet());
  }

  void createConsumesRelationship(
    final Step step,
    final CostAccount costAccount,
    final Step stepSnapshot,
    final CostAccount costAccountSnapshot
  ) {
    this.baselineHelper.createCostAccountConsumesRelationship(
      step.getId(),
      costAccount.getId(),
      stepSnapshot.getId(),
      costAccountSnapshot.getId()
    );
  }


  private void snapshotChildren(
    final Baseline baseline,
    final Workpack parent,
    final Workpack parentSnapshot,
    final List<UpdateRequest> updates
  ) {
    //getChildrenOrEmpty(parent).stream()
    //  .filter(child -> canSnapshotWorkpack(child, updates))
    //  .forEach(child -> this.snapshot(baseline, child, parentSnapshot, updates));
    final Workpack[] children = getChildrenOrEmpty(parent).toArray(new Workpack[0]);
    for (int i=0; i < children.length ; i++) {
      if (isNotDeleted(children[i]) && isAllowedForSnapshotting(children[i], updates)) {
        this.snapshot(baseline, children[i], parentSnapshot, updates);
      }
    }
  }

  /* 
  @Override
  public void submit(
    final Baseline baseline,
    final Workpack workpack,
    final List<UpdateRequest> updates
  ) {
    final Workpack workpackSnapshot = this.createSnapshot(baseline, workpack);
    this.snapshotChildren(baseline, workpack, workpackSnapshot, updates);
    this.changeStatusToProposed(baseline);

    // Added this line to repair the broken relationships
    //baseline.getBaselinedBy().setId(null);
    //baseline.getProposer().setId(null);
    //this.baselineRepository.save(baseline);
  }
  */

  @Override
  public List<Long> submit(
    final Baseline baseline,
    final Long workpackId,
    final Optional<Long> parentId)
  {

    final Workpack workpack = this.baselineRepository.findNotDeletedWorkpackWithPropertiesAndModelAndChildrenByWorkpackId(workpackId)
                                                              .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));

    if (!isNotDeleted(workpack)) {
      return new ArrayList<Long>();
    }

    final Workpack workpackSnapshot = this.baselineHelper.createSnapshot(workpack, this.workpackRepository);
    this.baselineHelper.createBaselineSnapshotRelationship(baseline, workpackSnapshot, this.workpackRepository);
    this.createMasterSnapshotRelationship(workpack, workpackSnapshot);
    this.snapshotProperties(workpack, workpackSnapshot, baseline);
    this.createScheduleWorkpackRelationship(baseline, workpack, workpackSnapshot);

    if (parentId != null) {
      this.linkChildAndParentSnapshots(
        workpackSnapshot, 
        this.baselineRepository.findSnapshotByMasterIdAndBaselineId(parentId.get(), baseline.getId()).get()
      );
    }

    List<Long> childrenToReturn = new ArrayList<Long>();;
    getChildrenOrEmpty(workpack).forEach(w -> {
      childrenToReturn.add(w.getId());
    });

    return childrenToReturn;

  }

  public Workpack createSnapshot(
    final Baseline baseline,
    final Workpack workpack
  ) {
    final Workpack workpackSnapshot = this.baselineHelper.createSnapshotWithBaselineRelationship(workpack, this.workpackRepository, baseline);
    //this.baselineHelper.createBaselineSnapshotRelationship(baseline, workpackSnapshot, this.workpackRepository);
    this.snapshotProperties(workpack, workpackSnapshot, baseline);
    this.createMasterSnapshotRelationship(workpack, workpackSnapshot);
    this.createScheduleWorkpackRelationship(baseline, workpack, workpackSnapshot);
    return workpackSnapshot;
  }

  private void snapshotProperties(
    final Workpack workpack,
    final Workpack workpackSnapshot,
    final Baseline baseline
  ) {
    for(final Property property : workpack.getProperties()) {
      this.snapshotProperty(property, workpackSnapshot, baseline);
    }
  }

  private void snapshotProperty(
    final Property property,
    final Workpack workpackSnapshot,
    final Baseline baseline
  ) {
    final Property snapshot = this.baselineHelper.createSnapshotWithBaselineRelationship(property, this.propertyRepository, baseline);
    //this.baselineHelper.createBaselineSnapshotRelationship(baseline, snapshot, this.propertyRepository);
    this.createMasterSnapshotRelationship(property, snapshot);
    this.createFeatureRelationship(workpackSnapshot, snapshot);
  }

  private void createMasterSnapshotRelationship(
    final Property property,
    final Property snapshot
  ) {
    this.baselineHelper.createMasterSnapshotRelationship(
      property,
      snapshot,
      this.propertySnapshotOfRepository,
      IsPropertySnapshotOf::new
    );
  }

  private void createFeatureRelationship(
    final Workpack workpackSnapshot,
    final Property propertySnapshot
  ) {
    propertySnapshot.setWorkpack(workpackSnapshot);
    this.workpackRepository.createFeaturesRelationship(workpackSnapshot.getId(), propertySnapshot.getId());
  }

  private void createMasterSnapshotRelationship(
    final Workpack workpack,
    final Workpack snapshot
  ) {
    this.baselineHelper.createMasterSnapshotRelationship(
      workpack,
      snapshot,
      this.workpackSnapshotOfRepository,
      IsWorkpackSnapshotOf::new
    );
  }

  private void createScheduleWorkpackRelationship(
    final Baseline baseline,
    final Workpack workpack,
    final Workpack workpackSnapshot
  ) {
    this.getScheduleByWorkpackId(workpack).ifPresent(schedule -> {
      final Schedule scheduleSnapshot = this.baselineHelper.createSnapshot(schedule, this.scheduleRepository);
      this.baselineHelper.createBaselineSnapshotRelationship(baseline, scheduleSnapshot);
      this.baselineHelper.createFeatureRelationship(workpackSnapshot, scheduleSnapshot);
      this.createMasterSnapshotRelationship(schedule, scheduleSnapshot);
      this.createStepScheduleRelationship(baseline, schedule, scheduleSnapshot, workpackSnapshot);
    });
  }

  private Optional<Schedule> getScheduleByWorkpackId(final Workpack workpack) {
    return this.scheduleRepository.findScheduleByWorkpackId(workpack.getId());
  }

  private void createStepScheduleRelationship(
    final Baseline baseline,
    final Schedule schedule,
    final Schedule scheduleSnapshot,
    final Workpack workpackSnapshot
  ) {
    final List<Step> steps = this.stepRepository.findAllByScheduleId(schedule.getId());
    for(final Step step : steps) {
      final Step stepSnapshot = this.baselineHelper.createSnapshot(step, this.stepRepository);
      this.baselineHelper.createBaselineSnapshotRelationship(baseline, stepSnapshot, this.stepRepository);
      this.baselineHelper.createComposesRelationship(scheduleSnapshot, stepSnapshot);
      this.createMasterSnapshotRelationship(step, stepSnapshot);
      this.createCostAccountStepRelationship(baseline, step, stepSnapshot, workpackSnapshot);
    }
  }

  private void createCostAccountStepRelationship(
    final Baseline baseline,
    final Step step,
    final Step stepSnapshot,
    final Workpack workpackSnapshot
  ) {
    final List<CostAccount> costAccounts = this.costAccountRepository.findAllByStepId(step.getId());
    for(final CostAccount costAccount : costAccounts) {
      final Optional<CostAccount> snapshot = this.getSnapshot(baseline, costAccount);

      if(snapshot.isPresent()) {
        this.createConsumesRelationship(step, costAccount, stepSnapshot, snapshot.get());
        continue;
      }

      final CostAccount costAccountSnapshot = this.baselineHelper.createSnapshotWithBaselineRelationship(costAccount, this.costAccountRepository, baseline);
      //this.baselineHelper.createBaselineSnapshotRelationship(baseline, costAccountSnapshot);
      this.baselineHelper.createAppliesToRelationship(workpackSnapshot, costAccountSnapshot);
      this.createConsumesRelationship(step, costAccount, stepSnapshot, costAccountSnapshot);
      this.createMasterSnapshotRelationship(costAccount, costAccountSnapshot);
    }
  }

  private Optional<CostAccount> getSnapshot(
    final Baseline baseline,
    final CostAccount costAccount
  ) {
    return this.costAccountRepository.findSnapshotByMasterIdAndBaselineId(costAccount.getId(), baseline.getId());
  }

  private void createMasterSnapshotRelationship(
    final CostAccount costAccount,
    final CostAccount costAccountSnapshot
  ) {
    this.baselineHelper.createMasterSnapshotRelationship(
      costAccount,
      costAccountSnapshot,
      this.costAccountSnapshotOfRepository,
      IsCostAccountSnapshotOf::new
    );
  }

  private void createMasterSnapshotRelationship(
    final Step step,
    final Step stepSnapshot
  ) {
    this.baselineHelper.createMasterSnapshotRelationship(
      step,
      stepSnapshot,
      this.stepSnapshotOfRepository,
      IsStepSnapshotOf::new
    );
  }

  private void createMasterSnapshotRelationship(
    final Schedule schedule,
    final Schedule scheduleSnapshot
  ) {
    this.baselineHelper.createMasterSnapshotRelationship(
      schedule,
      scheduleSnapshot,
      this.scheduleSnapshotOfRepository,
      IsScheduleSnapshotOf::new
    );
  }

  private void changeStatusToProposed(final Baseline baseline) {
    baseline.getBaselinedBy().setId(null);
    baseline.getProposer().setId(null);
    baseline.setStatus(Status.PROPOSED);
    baseline.setProposalDate(LocalDateTime.now());
    this.baselineRepository.save(baseline, 0);
  }

  private void snapshot(
    final Baseline baseline,
    final Workpack child,
    final Workpack parentSnapshot,
    final List<UpdateRequest> updates
  ) {
    final Workpack childSnapshot = this.baselineHelper.createSnapshotWithBaselineRelationship(child, this.workpackRepository, baseline);
//    this.baselineHelper.createBaselineSnapshotRelationship(baseline, childSnapshot, this.workpackRepository);
    this.createMasterSnapshotRelationship(child, childSnapshot);
    this.linkChildAndParentSnapshots(childSnapshot, parentSnapshot);
    this.snapshotProperties(child, childSnapshot, baseline);
    this.createScheduleWorkpackRelationship(baseline, child, childSnapshot);
    this.snapshotChildren(baseline, child, childSnapshot, updates);
  }

  private void linkChildAndParentSnapshots(
    final Workpack childSnapshot,
    final Workpack parentSnapshot
  ) {
    ifWorkpackIsNotSnapshotThrowException(childSnapshot);
    ifWorkpackIsNotSnapshotThrowException(parentSnapshot);
    this.baselineHelper.createIsInRelationship(childSnapshot, parentSnapshot);
  }

}
