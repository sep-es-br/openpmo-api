package br.gov.es.openpmo.service.baselines.calculators;

import br.gov.es.openpmo.dto.baselines.ccbmemberview.StepCollectedData;
import br.gov.es.openpmo.model.relations.Consumes;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class StepDataCollector implements IStepDataCollector {

  private final ScheduleRepository scheduleRepository;

  @Autowired
  public StepDataCollector(final ScheduleRepository scheduleRepository) {
    this.scheduleRepository = scheduleRepository;
  }

  private static BigDecimal getTotalPlannedWorkOfStep(final Collection<? extends Step> steps) {
    if(steps.isEmpty()) {
      return null;
    }
    return steps.stream()
      .map(Step::getPlannedWork)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private static BigDecimal getTotalCostOfStep(final Collection<? extends Consumes> consumes) {
    if(consumes.isEmpty()) {
      return null;
    }
    return consumes.stream()
      .map(Consumes::getPlannedCost)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Override
  public StepCollectedData collect(
    final Long idBaseline,
    final Long idBaselineReference,
    final Long idSchedule
  ) {
    final StepCollectedData stepCollectedData = new StepCollectedData();

    final Optional<Schedule> maybeScheduleSnapshot = this.maybeGetScheduleSnapshot(idBaseline, idSchedule);
    final Optional<Schedule> maybeScheduleReferenceSnapshot = this.maybeGetScheduleSnapshot(idBaselineReference, idSchedule);

    final Set<Consumes> consumesProposed = maybeScheduleSnapshot.map(Schedule::getSteps)
      .map(Set::stream)
      .map(s -> s.flatMap(s2 -> s2.getConsumes().stream()))
      .map(c -> c.collect(Collectors.toSet()))
      .orElse(new HashSet<>());
    final Set<Consumes> consumesCurrent = maybeScheduleReferenceSnapshot.map(Schedule::getSteps)
      .map(Set::stream)
      .map(s -> s.flatMap(s2 -> s2.getConsumes().stream()))
      .map(c -> c.collect(Collectors.toSet()))
      .orElse(new HashSet<>());

    final Set<Step> stepProposed = maybeScheduleSnapshot.map(Schedule::getSteps).orElse(new HashSet<>());
    final Set<Step> stepCurrent = maybeScheduleReferenceSnapshot.map(Schedule::getSteps).orElse(new HashSet<>());

    stepCollectedData.work.addCurrentValue(getTotalPlannedWorkOfStep(stepCurrent));
    stepCollectedData.work.addProposedValue(getTotalPlannedWorkOfStep(stepProposed));
    stepCollectedData.cost.addCurrentValue(getTotalCostOfStep(consumesCurrent));
    stepCollectedData.cost.addProposedValue(getTotalCostOfStep(consumesProposed));

    return stepCollectedData;
  }

  private Optional<Schedule> maybeGetScheduleSnapshot(
    final Long idBaselineReference,
    final Long idSchedule
  ) {
    return this.scheduleRepository.findSnapshotByMasterIdAndBaselineId(idSchedule, idBaselineReference);
  }

}
