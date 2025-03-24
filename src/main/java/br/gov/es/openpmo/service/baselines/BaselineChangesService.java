package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.relations.Consumes;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.ConsumesRepository;
import br.gov.es.openpmo.repository.CostAccountRepository;
import br.gov.es.openpmo.repository.PropertyRepository;
import br.gov.es.openpmo.repository.ScheduleRepository;
import br.gov.es.openpmo.repository.StepRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static br.gov.es.openpmo.utils.PropertyInstanceTypeDeprecated.TYPE_MODEL_NAME_DATE;

@Service
public class BaselineChangesService implements IBaselineChangesService {

  private final PropertyRepository propertyRepository;

  private final ScheduleRepository scheduleRepository;

  private final StepRepository stepRepository;

  private final ConsumesRepository consumesRepository;

  private final CostAccountRepository costAccountRepository;

  @Autowired
  public BaselineChangesService(
    final PropertyRepository propertyRepository,
    final ScheduleRepository scheduleRepository,
    final StepRepository stepRepository,
    final ConsumesRepository consumesRepository,
    final CostAccountRepository costAccountRepository
  ) {
    this.propertyRepository = propertyRepository;
    this.scheduleRepository = scheduleRepository;
    this.stepRepository = stepRepository;
    this.consumesRepository = consumesRepository;
    this.costAccountRepository = costAccountRepository;
  }

  private static Set<Workpack> getChildren(final Workpack workpack) {
    return Optional.ofNullable(workpack.getChildren()).orElse(Collections.emptySet());
  }

  private static boolean isDate(final Property property) {
    return TYPE_MODEL_NAME_DATE.equals(property.getClass().getTypeName());
  }

  public boolean hasChanges(
    final Baseline baseline,
    final Workpack workpack,
    final boolean isSnapshot
  ) {
    return this.hasPropertiesChanges(baseline, workpack, isSnapshot)
           || this.hasRelationshipsChanges(baseline, workpack, isSnapshot);
  }

  private boolean hasRelationshipsChanges(
    final Baseline baseline,
    final Workpack workpack,
    final boolean isSnapshot
  ) {
    return this.getSchedule(workpack).map(schedule -> this.hasScheduleChanges(baseline, schedule, isSnapshot)).orElse(false)
           || getChildren(workpack).stream().anyMatch(child -> this.hasRelationshipsChanges(baseline, child, isSnapshot));
  }

  private boolean hasRelationshipsChanges(
    final CostAccount costAccount,
    final CostAccount costAccountSnapshot,
    final Step step,
    final Step stepSnapshot
  ) {
    final Consumes consumes = this.findConsumesByStepIdAndCostAccountId(costAccount, step);
    final Consumes consumesSnapshot = this.findConsumesByStepIdAndCostAccountId(costAccountSnapshot, stepSnapshot);

    return consumes.hasActualCostChanges(consumesSnapshot)
           || consumes.hasPlannedCostChanges(consumesSnapshot);
  }

  private boolean hasRelationshipsChanges(
    final Baseline baseline,
    final Schedule schedule,
    final boolean isSnapshot
  ) {
    return this.findAllStepByScheduleId(schedule).stream()
      .anyMatch(step -> this.hasStepChanges(baseline, step, isSnapshot));
  }

  private boolean hasScheduleChanges(
    final Baseline baseline,
    final Schedule schedule,
    final boolean isSnapshot
  ) {
    final Optional<Schedule> maybeSnapshot = this.getScheduleSnapshot(baseline, schedule, isSnapshot);

    if(maybeSnapshot.isPresent()) {
      return schedule.hasChanges(maybeSnapshot.get())
             || this.hasRelationshipsChanges(baseline, schedule, isSnapshot);
    }

    if(isSnapshot) {
      return false;
    }

    throw new NegocioException(ApplicationMessage.SCHEDULE_HAS_NO_SNAPSHOT_INVALID_STATE_ERROR);
  }

  private Consumes findConsumesByStepIdAndCostAccountId(
    final CostAccount costAccount,
    final Step step
  ) {
    return this.consumesRepository.findByStepIdAndCostAccountId(step.getId(), costAccount.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.STEP_DOES_NOT_CONSUME_COST_ACCOUNT_INVALID_STATE_ERROR));
  }

  private boolean hasRelationshipsChanges(
    final Baseline baseline,
    final Step step,
    final Step stepSnapshot,
    final boolean isSnapshot
  ) {
    return this.findAllCostAccountByStepId(step).stream()
      .anyMatch(costAccount -> this.hasCostAccountChanges(baseline, costAccount, step, stepSnapshot, isSnapshot));
  }

  private List<CostAccount> findAllCostAccountByStepId(final Step step) {
    return this.costAccountRepository.findAllByStepId(step.getId());
  }

  private boolean hasCostAccountChanges(
    final Baseline baseline,
    final CostAccount costAccount,
    final Step step,
    final Step stepSnapshot,
    final boolean isSnapshot
  ) {
    return this.findSnapshotByMasterIdAndBaselineId(baseline, costAccount, isSnapshot)
      .map(snapshot -> costAccount.hasChanges(snapshot) || this.hasRelationshipsChanges(costAccount, snapshot, step,
                                                                                        stepSnapshot))
      .orElseThrow(() -> new NegocioException(ApplicationMessage.COST_ACCOUNT_HAS_NO_SNAPSHOT_INVALID_STATE_ERROR));
  }

  private boolean hasStepChanges(
    final Baseline baseline,
    final Step step,
    final boolean isSnapshot
  ) {
    return this.getStepSnapshot(baseline, step, isSnapshot)
      .map(snapshot -> step.hasChanges(snapshot) || this.hasRelationshipsChanges(baseline, step, snapshot, isSnapshot))
      .orElseThrow(() -> new NegocioException(ApplicationMessage.STEP_HAS_NO_SNAPSHOT_INVALID_STATE_ERROR));
  }

  private List<Step> findAllStepByScheduleId(final Schedule schedule) {
    return this.stepRepository.findAllByScheduleId(schedule.getId());
  }

  private Optional<Schedule> getSchedule(final Workpack workpack) {
    return this.scheduleRepository.findScheduleByWorkpackId(workpack.getId());
  }

  private boolean hasPropertyChanges(
    final Baseline baseline,
    final Property property,
    final boolean isSnapshot
  ) {
    if(!isDate(property)) {
      return false;
    }

    return this.getPropertySnapshot(baseline, property, isSnapshot)
      .map(snapshot -> property.hasChanges(snapshot))
      .orElse(false);
  }

  private Optional<CostAccount> findSnapshotByMasterIdAndBaselineId(
    final Baseline baseline,
    final CostAccount costAccount,
    final boolean isSnapshot
  ) {
    return isSnapshot
      ? this.costAccountRepository.findAnotherSnapshotOfMasterBySnapshotIdAndAnotherBaselineId(costAccount.getId(),
                                                                                               baseline.getId())
      : this.costAccountRepository.findSnapshotByMasterIdAndBaselineId(costAccount.getId(), baseline.getId());
  }

  private Optional<Property> getPropertySnapshot(
    final Baseline baseline,
    final Property property,
    final boolean isSnapshot
  ) {
    return isSnapshot
      ? this.propertyRepository.findAnotherSnapshotOfMasterBySnapshotIdAndAnotherBaselineId(property.getId(), baseline.getId())
      : this.propertyRepository.findSnapshotByMasterIdAndBaselineId(property.getId(), baseline.getId());
  }

  private Optional<Schedule> getScheduleSnapshot(
    final Baseline baseline,
    final Schedule schedule,
    final boolean isSnapshot
  ) {
    return isSnapshot
      ? this.scheduleRepository.findAnotherSnapshotOfMasterBySnapshotIdAndAnotherBaselineId(schedule.getId(), baseline.getId())
      : this.scheduleRepository.findSnapshotByMasterIdAndBaselineId(schedule.getId(), baseline.getId());
  }

  private Optional<Step> getStepSnapshot(
    final Baseline baseline,
    final Step step,
    final boolean isSnapshot
  ) {
    return isSnapshot
      ? this.stepRepository.findAnotherSnapshotOfMasterBySnapshotIdAndAnotherBaselineId(step.getId(), baseline.getId())
      : this.stepRepository.findSnapshotByMasterIdAndBaselineId(step.getId(), baseline.getId());
  }

  private boolean hasPropertiesChanges(
    final Baseline baseline,
    final Workpack workpack,
    final boolean isSnapshot
  ) {

    return workpack.getProperties() != null && workpack.getProperties()
      .stream()
      .anyMatch(property -> this.hasPropertyChanges(baseline, property, isSnapshot));
  }

}
