package br.gov.es.openpmo.service.schedule;

import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.repository.ScheduleRepository;
import br.gov.es.openpmo.repository.StepRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
public class GetEquivalentStepSnapshot implements IGetEquivalentStepSnapshot {

  private final StepRepository stepRepository;
  private final ScheduleRepository scheduleRepository;


  public GetEquivalentStepSnapshot(
    final StepRepository stepRepository,
    final ScheduleRepository scheduleRepository
  ) {
    this.stepRepository = stepRepository;
    this.scheduleRepository = scheduleRepository;
  }


  @Override
  public Optional<Step> execute(final Step master) {
    Objects.requireNonNull(master.getSchedule());

    final Optional<Schedule> scheduleSnapshot = this.getScheduleSnapshot(master.getScheduleId());

    if(!scheduleSnapshot.isPresent()) return Optional.empty();

    final Set<Step> stepSnapshots = scheduleSnapshot.get().getSteps();

    final Optional<Step> first = stepSnapshots.stream()
      .filter(snapshot -> snapshot.equivalent(master))
      .findFirst();
    return first;
  }

  private Optional<Schedule> getScheduleSnapshot(final Long idSchedule) {
    return this.scheduleRepository.findSnapshotByMasterId(idSchedule);
  }

}
