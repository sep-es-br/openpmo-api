package br.gov.es.openpmo.dto.baselines.ccbmemberview;

import br.gov.es.openpmo.model.schedule.Schedule;

import java.time.LocalDate;

public class ScheduleInterval {

  private final LocalDate initialDate;
  private final LocalDate endDate;

  public ScheduleInterval(final LocalDate initialDate, final LocalDate endDate) {
    this.initialDate = initialDate;
    this.endDate = endDate;
  }

  public static ScheduleInterval ofSchedule(final Schedule schedule) {
    return new ScheduleInterval(
      schedule.getStart(),
      schedule.getEnd()
    );
  }

  public LocalDate getInitialDate() {
    return this.initialDate;
  }

  public LocalDate getEndDate() {
    return this.endDate;
  }

  public ScheduleInterval newChangedInterval(final ScheduleInterval interval) {
    return new ScheduleInterval(
      this.getChangedInitialDate(interval),
      this.getChangedEndDate(interval)
    );
  }

  public LocalDate getChangedInitialDate(final ScheduleInterval interval) {
    if(!interval.initialDate.isEqual(this.initialDate)) {
      return this.initialDate;
    }
    return null;
  }

  public LocalDate getChangedEndDate(final ScheduleInterval interval) {
    if(!interval.endDate.isEqual(this.endDate)) {
      return this.endDate;
    }
    return null;
  }
}
