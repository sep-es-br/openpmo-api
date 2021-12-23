package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.UpdateRequest;
import br.gov.es.openpmo.enumerator.BaselineStatus;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.baselines.Status;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.relations.Consumes;
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
import java.util.stream.Collectors;

@Service
public class AnotherTimeSubmitBaselineService implements IAnotherTimeSubmitBaselineService {

  private final BaselineRepository baselineRepository;

  private final FirstTimeSubmitBaselineService firstTimeSubmitBaselineService;

  private final WorkpackRepository workpackRepository;

  private final IsWorkpackSnapshotOfRepository workpackSnapshotOfRepository;

  private final PropertyRepository propertyRepository;

  private final IsPropertySnapshotOfRepository propertySnapshotOfRepository;

  private final ScheduleRepository scheduleRepository;

  private final IsScheduleSnapshotOfRepository scheduleSnapshotOfRepository;

  private final StepRepository stepRepository;

  private final CostAccountRepository costAccountRepository;

  private final IsCostAccountSnapshotOfRepository costAccountSnapshotOfRepository;

  private final ConsumesRepository consumesRepository;

  private final IsStepSnapshotOfRepository stepSnapshotOfRepository;

  private final BaselineHelper baselineHelper;

  @Autowired
  public AnotherTimeSubmitBaselineService(
    final BaselineRepository baselineRepository,
    final FirstTimeSubmitBaselineService firstTimeSubmitBaselineService,
    final WorkpackRepository workpackRepository,
    final IsWorkpackSnapshotOfRepository workpackSnapshotOfRepository,
    final PropertyRepository propertyRepository,
    final IsPropertySnapshotOfRepository propertySnapshotOfRepository,
    final ScheduleRepository scheduleRepository,
    final IsScheduleSnapshotOfRepository scheduleSnapshotOfRepository,
    final StepRepository stepRepository,
    final CostAccountRepository costAccountRepository,
    final IsCostAccountSnapshotOfRepository costAccountSnapshotOfRepository,
    final ConsumesRepository consumesRepository,
    final IsStepSnapshotOfRepository stepSnapshotOfRepository,
    final BaselineHelper baselineHelper
  ) {
    this.baselineRepository = baselineRepository;
    this.firstTimeSubmitBaselineService = firstTimeSubmitBaselineService;
    this.workpackRepository = workpackRepository;
    this.workpackSnapshotOfRepository = workpackSnapshotOfRepository;
    this.propertyRepository = propertyRepository;
    this.propertySnapshotOfRepository = propertySnapshotOfRepository;
    this.scheduleRepository = scheduleRepository;
    this.scheduleSnapshotOfRepository = scheduleSnapshotOfRepository;
    this.stepRepository = stepRepository;
    this.costAccountRepository = costAccountRepository;
    this.costAccountSnapshotOfRepository = costAccountSnapshotOfRepository;
    this.consumesRepository = consumesRepository;
    this.stepSnapshotOfRepository = stepSnapshotOfRepository;
    this.baselineHelper = baselineHelper;
  }

  private static boolean hasChanges(final UpdateRequest update) {
    return update.getClassification() == BaselineStatus.CHANGED && !"structure".equals(update.getDescription());
  }

  private static boolean isClassificationDeleted(final UpdateRequest updateRequest) {
    return updateRequest.getClassification() == BaselineStatus.DELETED;
  }

  private static boolean isIncluded(final UpdateRequest updateRequest) {
    return Boolean.TRUE.equals(updateRequest.getIncluded());
  }

  private static boolean hasStructureChanges(final UpdateRequest update) {
    return update.getClassification() == BaselineStatus.CHANGED && "structure".equals(update.getDescription());
  }

  private static boolean isClassificationNew(final UpdateRequest updateRequest) {
    return updateRequest.getClassification() == BaselineStatus.NEW;
  }

  private static Set<Property> getProperties(final Workpack workpack) {
    return Optional.ofNullable(workpack.getProperties()).orElse(Collections.emptySet());
  }

  @Override
  public void submit(
    final Baseline baseline,
    final Workpack workpack,
    final List<UpdateRequest> updates
  ) {
    this.checksStructureChanges(baseline, workpack, updates);
    this.checksNewChanges(baseline, updates);
    this.checksDeletedChanges(baseline, updates);
    this.checksChanges(baseline, workpack, updates);
    this.changeStatusToProposed(baseline);
  }

  private void checksChanges(
    final Baseline baseline,
    final Workpack workpack,
    final Collection<? extends UpdateRequest> updates
  ) {
    final List<UpdateRequest> updateRequests = updates.stream()
      .filter(AnotherTimeSubmitBaselineService::hasChanges)
      .collect(Collectors.toList());

    for(final UpdateRequest updateRequest : updateRequests) {
      final Workpack snapshot = this.getSnapshotWithProperties(baseline, updateRequest);

      if(isIncluded(updateRequest)) {
        this.updateSnapshotProperties(snapshot, null);
        this.updateSnapshotSchedule(snapshot, null);
      }
      else {
        final Baseline activeBaseline = this.getActiveBaseline(workpack);
        this.updateSnapshotProperties(snapshot, activeBaseline);
        this.updateSnapshotSchedule(snapshot, activeBaseline);
      }
    }
  }

  private void checksDeletedChanges(
    final Baseline baseline,
    final Collection<? extends UpdateRequest> updates
  ) {
    final List<UpdateRequest> updateRequests = updates.stream()
      .filter(AnotherTimeSubmitBaselineService::isClassificationDeleted)
      .collect(Collectors.toList());

    for(final UpdateRequest updateRequest : updateRequests) {
      if(isIncluded(updateRequest)) {
        this.getSnapshot(baseline, updateRequest).ifPresent(this.workpackRepository::delete);
      }
      else {
        this.snapshotWorpackIfItHasNoSnapshot(baseline, updateRequest);
      }
    }
  }

  private void checksStructureChanges(
    final Baseline baseline,
    final Workpack workpack,
    final Collection<UpdateRequest> updates
  ) {
    final Optional<UpdateRequest> updateRequest = updates.stream()
      .filter(AnotherTimeSubmitBaselineService::hasStructureChanges)
      .findFirst();

    if(updateRequest.isPresent() && !isIncluded(updateRequest.get())) {
      this.snapshotViaActiveBaseline(baseline, workpack);
    }
    else {
      this.firstTimeSubmitBaselineService.submit(baseline, workpack, null);
    }
  }

  private void updateSnapshotSchedule(
    final Workpack snapshot,
    final Baseline activeBaseline
  ) {
    this.getSchedule(snapshot).ifPresent(snapshotSchedule -> {
      final Schedule masterSchedule = this.getScheduleMaster(snapshotSchedule);
      final Schedule schedule = this.getScheduleToCopy(activeBaseline, masterSchedule);

      snapshotSchedule.setStart(schedule.getStart());
      snapshotSchedule.setEnd(schedule.getEnd());

      this.scheduleRepository.save(snapshotSchedule);
      this.updateScheduleSteps(snapshotSchedule, activeBaseline);
    });
  }

  private Schedule getScheduleToCopy(
    final Baseline activeBaseline,
    final Schedule masterSchedule
  ) {
    return Optional.ofNullable(activeBaseline)
      .map(baseline -> this.getSnapshotOfActiveBaseline(masterSchedule, baseline))
      .orElse(masterSchedule);
  }

  private Schedule getSnapshotOfActiveBaseline(
    final Schedule masterSchedule,
    final Baseline baseline
  ) {
    return this.scheduleRepository.findSnapshotByMasterIdAndBaselineId(masterSchedule.getId(), baseline.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.SNAPSHOT_NOT_FOUND));
  }

  private void checksNewChanges(
    final Baseline baseline,
    final Collection<? extends UpdateRequest> updates
  ) {
    final List<UpdateRequest> updateRequests = updates.stream()
      .filter(AnotherTimeSubmitBaselineService::isClassificationNew)
      .collect(Collectors.toList());

    for(final UpdateRequest updateRequest : updateRequests) {
      if(isIncluded(updateRequest)) {
        this.snapshotWorpackIfItHasNoSnapshot(baseline, updateRequest);
      }
      else {
        this.getSnapshot(baseline, updateRequest).ifPresent(this.workpackRepository::delete);
      }
    }
  }

  private Step getStepToCopy(
    final Baseline activeBaseline,
    final Step masterStep
  ) {
    return Optional.ofNullable(activeBaseline)
      .map(baseline -> this.getSnapshotOfActiveBaseline(masterStep, baseline))
      .orElse(masterStep);
  }

  private Step getSnapshotOfActiveBaseline(
    final Step masterStep,
    final Baseline baseline
  ) {
    return this.stepRepository.findSnapshotByMasterIdAndBaselineId(masterStep.getId(), baseline.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.SNAPSHOT_NOT_FOUND));
  }

  private CostAccount getCostAccountToCopy(
    final Baseline activeBaseline,
    final CostAccount masterCostAccount
  ) {
    return Optional.ofNullable(activeBaseline)
      .map(baseline -> this.getSnapshotOfActiveBaseline(masterCostAccount, baseline))
      .orElse(masterCostAccount);
  }

  private CostAccount getSnapshotOfActiveBaseline(
    final CostAccount costAccount,
    final Baseline baseline
  ) {
    return this.costAccountRepository.findSnapshotByMasterIdAndBaselineId(costAccount.getId(), baseline.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.SNAPSHOT_NOT_FOUND));
  }

  private Consumes getConsumes(
    final Step masterStep,
    final CostAccount masterCostAccount
  ) {
    return this.consumesRepository.findByStepIdAndCostAccountId(masterStep.getId(), masterCostAccount.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.STEP_DOES_NOT_CONSUME_COST_ACCOUNT_INVALID_STATE_ERROR));
  }

  private CostAccount getCostAccountMaster(final CostAccount costAccount) {
    return this.costAccountRepository.findMasterBySnapshotId(costAccount.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.COST_ACCOUNT_NOT_FOUND));
  }

  private List<CostAccount> getCostAccounts(final Step step) {
    return this.costAccountRepository.findAllByStepId(step.getId());
  }

  private Step getStepMaster(final Step step) {
    return this.stepRepository.findMasterBySnapshotId(step.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.STEP_NOT_FOUND));
  }

  private List<Step> getSteps(final Schedule schedule) {
    return this.stepRepository.findAllByScheduleId(schedule.getId());
  }

  private Optional<Schedule> getSchedule(final Workpack snapshot) {
    return this.scheduleRepository.findScheduleByWorkpackId(snapshot.getId());
  }

  private void updateSnapshotProperties(
    final Workpack snapshot,
    final Baseline activeBaseline
  ) {
    for(final Property snapshotProperty : snapshot.getProperties()) {
      this.updateSnapshotProperty(snapshotProperty, activeBaseline);
    }
    this.propertyRepository.saveAll(snapshot.getProperties());
  }

  private void updateSnapshotProperty(
    final Property snapshotProperty,
    final Baseline activeBaseline
  ) {
    final Property masterProperty = this.getPropertyMaster(snapshotProperty);
    final Property property = this.getPropertyToCopy(activeBaseline, masterProperty);
    snapshotProperty.setValue(property.getValue());
    this.propertyRepository.save(snapshotProperty);
  }

  private Property getPropertyToCopy(
    final Baseline activeBaseline,
    final Property masterProperty
  ) {
    return Optional.ofNullable(activeBaseline)
      .map(baseline -> this.getSnapshotOfActiveBaseline(baseline, masterProperty))
      .orElse(masterProperty);
  }

  private Property getSnapshotOfActiveBaseline(
    final Baseline activeBaseline,
    final Property masterProperty
  ) {
    return this.propertyRepository.findSnapshotByMasterIdAndBaselineId(masterProperty.getId(), activeBaseline.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.SNAPSHOT_NOT_FOUND));
  }

  private Property getPropertyMaster(final Property snapshotProperty) {
    return this.propertyRepository.findMasterBySnapshotId(snapshotProperty.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PROPERTY_NOT_FOUND));
  }

  private void updateScheduleSteps(
    final Schedule schedule,
    final Baseline activeBaseline
  ) {
    final Collection<Step> steps = new ArrayList<>();

    for(final Step snapshotStep : this.getSteps(schedule)) {
      final Step masterStep = this.getStepMaster(snapshotStep);
      final Step step = this.getStepToCopy(activeBaseline, masterStep);

      snapshotStep.setActualWork(step.getActualWork());
      snapshotStep.setPlannedWork(step.getPlannedWork());
      snapshotStep.setPeriodFromStart(step.getPeriodFromStart());

      steps.add(snapshotStep);
      this.updateStepCostAccounts(snapshotStep, masterStep, activeBaseline);
    }

    this.stepRepository.saveAll(steps);
  }

  private void snapshotWorpackIfItHasNoSnapshot(
    final Baseline baseline,
    final UpdateRequest updateRequest
  ) {
    if(this.workpackHasSnapshot(baseline, updateRequest)) {
      return;
    }

    final Workpack workpack = this.findWorkpackById(updateRequest.getIdWorkpack());
    final Workpack workpackSnapshot = this.firstTimeSubmitBaselineService.createSnapshot(baseline, workpack);

    this.getParentSnapshot(baseline, updateRequest).ifPresent(parentSnapshot ->
                                                                this.linkChildAndParentSnapshots(workpackSnapshot, parentSnapshot));
  }

  private void changeStatusToProposed(final Baseline baseline) {
    baseline.setStatus(Status.PROPOSED);
    baseline.setProposalDate(LocalDateTime.now());
    this.baselineRepository.save(baseline);
  }

  private void updateStepCostAccounts(
    final Step snapshotStep,
    final Step masterStep,
    final Baseline activeBaseline
  ) {
    final Collection<Consumes> consumes = new ArrayList<>();

    for(final CostAccount snapshotCostAccount : this.getCostAccounts(snapshotStep)) {
      final CostAccount masterCostAccount = this.getCostAccountMaster(snapshotCostAccount);

      final Step step = this.getStepToCopy(activeBaseline, masterStep);
      final CostAccount costAccount = this.getCostAccountToCopy(activeBaseline, masterCostAccount);

      final Consumes masterConsumes = this.getConsumes(step, costAccount);
      final Consumes snapshotConsumes = this.getConsumes(snapshotStep, snapshotCostAccount);

      snapshotConsumes.setActualCost(masterConsumes.getActualCost());
      snapshotConsumes.setPlannedCost(masterConsumes.getPlannedCost());

      consumes.add(snapshotConsumes);
    }

    this.consumesRepository.saveAll(consumes);
  }

  private void snapshotProperties(
    final Workpack workpack,
    final Workpack workpackSnapshot,
    final Baseline baseline
  ) {
    for(final Property property : getProperties(workpack)) {
      this.snapshotProperty(property, workpackSnapshot, baseline);
    }
  }

  private boolean workpackHasSnapshot(
    final Baseline baseline,
    final UpdateRequest updateRequest
  ) {
    return this.baselineRepository.workpackHasSnapshot(updateRequest.getIdWorkpack(), baseline.getId());
  }

  private Workpack findWorkpackById(final Long idWorkpack) {
    return this.workpackRepository.findWithPropertiesAndModelAndChildrenById(idWorkpack)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
  }

  private Optional<Workpack> getParentSnapshot(
    final Baseline baseline,
    final UpdateRequest updateRequest
  ) {
    return this.baselineRepository.findSnapshotOfParentByChildIdAndBaselineId(updateRequest.getIdWorkpack(), baseline.getId());
  }

  private Workpack getSnapshotWithProperties(
    final Baseline baseline,
    final UpdateRequest updateRequest
  ) {
    return this.baselineRepository.findSnapshotWithChildrenAndPropertiesByWorkpackIdAndBaselineId(
        updateRequest.getIdWorkpack(),
        baseline.getId()
      )
      .orElseThrow(() -> new NegocioException(ApplicationMessage.SNAPSHOT_NOT_FOUND));
  }

  private Optional<Workpack> getSnapshot(
    final Baseline baseline,
    final UpdateRequest updateRequest
  ) {
    return this.baselineRepository.findSnapshotByMasterIdAndBaselineId(updateRequest.getIdWorkpack(), baseline.getId());
  }

  private void snapshotViaActiveBaseline(
    final Baseline baseline,
    final Workpack workpack
  ) {
    final Baseline activeBaseline = this.getActiveBaseline(workpack);
    final Workpack snapshot = this.getSnapshot(workpack, activeBaseline);

    final Workpack newSnapshot = this.baselineHelper.createSnapshot(snapshot, this.workpackRepository);
    this.baselineHelper.createBaselineSnapshotRelationship(baseline, newSnapshot, this.workpackRepository);
    this.createMasterSnapshotRelationship(this.getWorpackMaster(snapshot), newSnapshot);
    this.snapshotProperties(snapshot, newSnapshot, baseline);
    this.createScheduleWorkpackRelationship(baseline, snapshot, newSnapshot);
    this.snapshotChildren(baseline, snapshot, newSnapshot);
  }

  private Baseline getActiveBaseline(final Workpack workpack) {
    return this.baselineRepository.findActiveBaselineByWorkpackId(workpack.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_HAS_NO_ACTIVE_BASELINE_INVALID_STATE_ERROR));
  }

  private Workpack getSnapshot(
    final Workpack workpack,
    final Baseline activeBaseline
  ) {
    return this.baselineRepository.findSnapshotWithChildrenAndPropertiesByWorkpackIdAndBaselineId(
        workpack.getId(),
        activeBaseline.getId()
      )
      .orElseThrow(() -> new NegocioException(ApplicationMessage.SNAPSHOT_NOT_FOUND));
  }

  private void snapshotChildren(
    final Baseline baseline,
    final Workpack snapshot,
    final Workpack parent
  ) {
    if(snapshot.getChildren() == null) {
      return;
    }

    for(final Workpack child : snapshot.getChildren()) {
      this.snapshot(baseline, child, parent);
    }
  }

  private void snapshot(
    final Baseline baseline,
    final Workpack child,
    final Workpack parent
  ) {
    final Workpack newSnapshot = this.baselineHelper.createSnapshot(child, this.workpackRepository);
    this.baselineHelper.createBaselineSnapshotRelationship(baseline, newSnapshot, this.workpackRepository);
    this.createMasterSnapshotRelationship(this.getWorpackMaster(child), newSnapshot);
    this.linkChildAndParentSnapshots(newSnapshot, parent);
    this.snapshotProperties(child, newSnapshot, baseline);
    this.createScheduleWorkpackRelationship(baseline, child, newSnapshot);
    this.snapshotChildren(baseline, child, newSnapshot);
  }

  private void linkChildAndParentSnapshots(
    final Workpack childSnapshot,
    final Workpack parentSnapshot
  ) {
    this.baselineHelper.createIsInRelationship(childSnapshot, parentSnapshot);
  }

  private Workpack getWorpackMaster(final Workpack snapshot) {
    return this.baselineRepository.findMasterBySnapshotId(snapshot.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.SNAPSHOT_NOT_FOUND));
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
    this.scheduleRepository.findScheduleByWorkpackId(workpack.getId()).ifPresent(schedule -> {
      final Schedule scheduleSnapshot = this.baselineHelper.createSnapshot(schedule, this.scheduleRepository);
      this.baselineHelper.createBaselineSnapshotRelationship(baseline, scheduleSnapshot, this.scheduleRepository);
      this.createMasterSnapshotRelationship(this.getScheduleMaster(schedule), scheduleSnapshot);
      this.createFeatureRelationship(workpackSnapshot, scheduleSnapshot);
      this.createStepScheduleRelationship(baseline, schedule, scheduleSnapshot, workpackSnapshot);
    });
  }

  private void snapshotProperty(
    final Property property,
    final Workpack workpackSnapshot,
    final Baseline baseline
  ) {
    final Property snapshot = this.baselineHelper.createSnapshot(property, this.propertyRepository);
    this.baselineHelper.createBaselineSnapshotRelationship(baseline, snapshot, this.propertyRepository);
    this.createMasterSnapshotRelationship(this.getPropertyMaster(property), snapshot);
    this.createFeatureRelationship(workpackSnapshot, snapshot);
  }

  private Schedule getScheduleMaster(final Schedule snapshot) {
    return this.scheduleRepository.findMasterBySnapshotId(snapshot.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.SCHEDULE_NOT_FOUND));
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

  private void createFeatureRelationship(
    final Workpack workpackSnapshot,
    final Schedule scheduleSnapshot
  ) {
    scheduleSnapshot.setWorkpack(workpackSnapshot);
    this.scheduleRepository.save(scheduleSnapshot);
  }

  private void createStepScheduleRelationship(
    final Baseline baseline,
    final Schedule schedule,
    final Schedule scheduleSnapshot,
    final Workpack workpackSnapshot
  ) {
    for(final Step step : this.stepRepository.findAllByScheduleId(schedule.getId())) {
      final Step stepSnapshot = this.baselineHelper.createSnapshot(step, this.stepRepository);
      this.baselineHelper.createBaselineSnapshotRelationship(baseline, stepSnapshot, this.stepRepository);
      this.baselineHelper.createComposesRelationship(scheduleSnapshot, stepSnapshot);
      this.createMasterSnapshotRelationship(this.getStepMaster(step), stepSnapshot);
      this.createCostAccountStepRelationship(baseline, step, stepSnapshot, workpackSnapshot);
    }
  }

  private void createCostAccountStepRelationship(
    final Baseline baseline,
    final Step step,
    final Step stepSnapshot,
    final Workpack workpackSnapshot
  ) {
    for(final CostAccount costAccount : this.costAccountRepository.findAllByStepId(step.getId())) {
      final Optional<CostAccount> account = this.getSnapshot(baseline, costAccount);

      if(account.isPresent()) {
        this.firstTimeSubmitBaselineService.createConsumesRelationship(step, costAccount, stepSnapshot, account.get());
        continue;
      }

      final CostAccount costAccountSnapshot = this.baselineHelper.createSnapshot(costAccount, this.costAccountRepository);
      this.baselineHelper.createBaselineSnapshotRelationship(baseline, costAccountSnapshot, this.costAccountRepository);
      this.baselineHelper.createAppliesToRelationship(workpackSnapshot, costAccountSnapshot);
      this.firstTimeSubmitBaselineService.createConsumesRelationship(step, costAccount, stepSnapshot, costAccountSnapshot);
      this.createMasterSnapshotRelationship(costAccount, costAccountSnapshot);
    }
  }

  private Optional<CostAccount> getSnapshot(final Baseline baseline, final CostAccount costAccount) {
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
    this.propertyRepository.save(propertySnapshot);
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

}
