package br.gov.es.openpmo.service.schedule;

import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.model.schedule.Step;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecalculateStepsAfterRemove {

  private final GetScheduleById getScheduleById;

  public RecalculateStepsAfterRemove(final GetScheduleById getScheduleById) {this.getScheduleById = getScheduleById;}

  private static boolean isStart(
    final Step step,
    final Schedule schedule
  ) {
    return isSameStartYear(step, schedule) && isSameStartMonth(step, schedule);
  }

  private static void setStart(
    final Schedule schedule,
    final Long periodFromStart
  ) {
    final LocalDate localDate = schedule.getStart().plusMonths(periodFromStart);
    schedule.setStart(localDate);
  }

  private static void setEnd(
    final Schedule schedule,
    final Long periodFromStart
  ) {
    final LocalDate localDate = schedule.getStart().plusMonths(periodFromStart);
    schedule.setEnd(localDate);
  }

  private static boolean isSameStartYear(
    final Step step,
    final Schedule schedule
  ) {
    return step.getPeriodFromStartDate().getYear() == schedule.getStart().getYear();
  }

  private static boolean isSameStartMonth(
    final Step step,
    final Schedule schedule
  ) {
    return step.getPeriodFromStartDate().getMonthValue() == schedule.getStart().getMonthValue();
  }

  private static void handleMoreThanOneStep(
    final Schedule schedule,
    final boolean start,
    final List<? extends Step> steps
  ) {
    if(start) {
      steps.remove(0);
      setStart(schedule, steps.get(0).getPeriodFromStart());
      recalculate(steps);
    }
    else {
      Collections.reverse(steps);
      steps.remove(0);
      setEnd(schedule, steps.get(0).getPeriodFromStart());
    }
    schedule.setSteps(new HashSet<>(steps));
  }

  private static void handleOneStep(
    final Schedule schedule,
    final Step step,
    final boolean start
  ) {
    if(start) {
      setStart(schedule, step.getPeriodFromStart() + 1);
    }
    else {
      setEnd(schedule, step.getPeriodFromStart() - 1);
    }
  }

  private static List<Step> getSortedStep(final Schedule schedule) {
    return schedule.getSteps().stream()
      .sorted(Comparator.comparing(Step::getPeriodFromStart))
      .collect(Collectors.toList());
  }

  private static void recalculate(final Iterable<? extends Step> steps) {
    long periodCounter = 0;
    for(final Step step : steps) {
      step.setPeriodFromStart(periodCounter);
      periodCounter++;
    }
  }

  public List<Step> execute(final Step removedStep) {
    final Long idSchedule = removedStep.getScheduleId();
    final Schedule schedule = this.getScheduleById.execute(idSchedule);
    final boolean start = isStart(removedStep, schedule);

    final List<Step> steps = getSortedStep(schedule);

    if(steps.size() > 1) {
      handleMoreThanOneStep(schedule, start, steps);
    }
    else {
      handleOneStep(schedule, removedStep, start);
    }

    return steps;
  }

}
